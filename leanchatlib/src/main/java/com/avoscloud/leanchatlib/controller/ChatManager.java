package com.avoscloud.leanchatlib.controller;

import android.content.Context;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.Room;
import com.avoscloud.leanchatlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 该类来负责处理接收消息、聊天服务连接状态管理、查找对话、获取最近对话列表最后一条消息
 * Created by lzw on 15/2/10.
 */
public class ChatManager extends AVIMClientEventHandler {
  private static final String KEY_UPDATED_AT = "updatedAt";
  private static ChatManager chatManager;
  private static Context context;

  /**
   * 默认的聊天连接状态监听器
   */
  private static ConnectionListener defaultConnectListener = new ConnectionListener() {
    @Override
    public void onConnectionChanged(boolean connect) {
    }
  };

  private ConnectionListener connectionListener = defaultConnectListener;
  private volatile AVIMClient imClient;
  private volatile String selfId;
  private volatile boolean connect = false;
  private RoomsTable roomsTable;

  private ChatManager() {
  }

  /**
   * 获取 ChatManager 单例
   *
   * @return
   */
  public static synchronized ChatManager getInstance() {
    if (chatManager == null) {
      chatManager = new ChatManager();
    }
    return chatManager;
  }

  public static Context getContext() {
    return context;
  }

  /**
   * 设置是否打印 leanchatlib 的日志，发布应用的时候要关闭
   * 日志 TAG 为 leanchatlib，可以获得一些异常日志
   *
   * @param debugEnabled
   */
  public static void setDebugEnabled(boolean debugEnabled) {
    LogUtils.debugEnabled = debugEnabled;
  }

  /**
   * 请在应用一启动(Application onCreate)的时候就调用，因为 SDK 一启动，就会去连接聊天服务器
   * 如果没有调用此函数设置 messageHandler ，就可能丢失一些消息
   *
   * @param context
   */
  public void init(Context context) {
    this.context = context;
    AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new MessageHandler(context));
    AVIMClient.setClientEventHandler(this);
    //签名
    //AVIMClient.setSignatureFactory(new SignatureFactory());
  }

  /**
   * 设置 AVIMConversationEventHandler，用来处理对话成员变更回调
   *
   * @param eventHandler
   */
  public void setConversationEventHandler(AVIMConversationEventHandler eventHandler) {
    AVIMMessageManager.setConversationEventHandler(eventHandler);
  }

  /**
   * 请在登录之后，进入 MainActivity 之前，调用此函数，因为此时可以拿到当前登录用户的 ID
   *
   * @param userId 应用用户系统当前用户的 userId
   */
  public void setupManagerWithUserId(String userId) {
    this.selfId = userId;
    roomsTable = RoomsTable.getInstanceByUserId(userId);
  }

  /**
   * 监听聊天服务连接状态 ，这里不用 SDK 的 AVIMClientHandler
   * 是因为 SDK 在 open 的时候没有回调 onConnectResume ，不方便统一处理
   *
   * @param connectionListener
   */
  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
  }

  public String getSelfId() {
    return selfId;
  }

  public RoomsTable getRoomsTable() {
    return roomsTable;
  }

  public AVIMClient getImClient() {
    return imClient;
  }

  /**
   * 连接聊天服务器，用 userId 登录，在进入MainActivity 前调用
   *
   * @param callback AVException 常发生于网络错误、签名错误
   */
  public void openClient(final AVIMClientCallback callback) {
    if (this.selfId == null) {
      throw new IllegalStateException("please call setupManagerWithUserId() first");
    }
    imClient = AVIMClient.getInstance(this.selfId);
    imClient.open(new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (e != null) {
          setConnectAndNotify(false);
        } else {
          setConnectAndNotify(true);
        }
        if (callback != null) {
          callback.done(avimClient, e);
        }
      }
    });
  }

  /**
   * 用户注销的时候调用，close 之后消息不会推送过来，也不可以进行发消息等操作
   *
   * @param callback AVException 常见于网络错误
   */
  public void closeWithCallback(final AVIMClientCallback callback) {
    imClient.close(new AVIMClientCallback() {

      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        if (e != null) {
          LogUtils.logException(e);
        }
        if (callback != null) {
          callback.done(avimClient, e);
        }
      }
    });
    imClient = null;
    selfId = null;
  }

  /**
   * 获取和 userId 的对话，先去服务器查之前两人有没创建过对话，没有的话，创建一个
   *
   * @param userId
   * @param callback
   */
  public void fetchConversationWithUserId(final String userId, final AVIMConversationCreatedCallback callback) {
    AVIMConversationQuery query = imClient.getQuery();
    query.withMembers(Arrays.asList(userId, selfId));
    query.whereEqualTo(ConversationType.ATTR_TYPE_KEY, ConversationType.Single.getValue());
    query.orderByDescending(KEY_UPDATED_AT);
    query.limit(1);
    query.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> conversations, AVIMException e) {
        if (e != null) {
          callback.done(null, e);
        } else {
          if (conversations.size() > 0) {
            callback.done(conversations.get(0), null);
          } else {
            Map<String, Object> attrs = new HashMap<>();
            attrs.put(ConversationType.TYPE_KEY, ConversationType.Single.getValue());
            imClient.createConversation(Arrays.asList(userId, selfId), attrs, callback);
          }
        }
      }
    });
  }

  /**
   * 获取 AVIMConversationQuery，用来查询对话
   *
   * @return
   */
  public AVIMConversationQuery getConversationQuery() {
    return imClient.getQuery();
  }

  /**
   * 创建对话，为了不暴露 AVIMClient，这里封装一下
   *
   * @param members    成员，需要包含自己
   * @param attributes 对话的附加属性
   * @param callback   AVException 聊天服务断开时抛出
   */
  public void createConversation(List<String> members, Map<String, Object> attributes,
                                 AVIMConversationCreatedCallback callback) {
    imClient.createConversation(members, attributes, callback);
  }

  public AVIMConversation getConversation(String conversationId) {
    return imClient.getConversation(conversationId);
  }

  @Override
  public void onConnectionPaused(AVIMClient client) {
    setConnectAndNotify(false);
  }

  @Override
  public void onConnectionResume(AVIMClient client) {
    setConnectAndNotify(true);
  }

  public void setConnectAndNotify(boolean connect) {
    this.connect = connect;
    connectionListener.onConnectionChanged(connect);
  }

  /**
   * 是否连上聊天服务
   *
   * @return
   */
  public boolean isConnect() {
    return connect;
  }

  //ChatUser
  public List<Room> findRecentRooms() {
    return ChatManager.getInstance().getRoomsTable().selectRooms();
  }

  public interface ConnectionListener {
    void onConnectionChanged(boolean connect);
  }

  /**
   * msgId 、time 共同使用，防止某毫秒时刻有重复消息
   */
  public void queryMessages(AVIMConversation conversation, final String msgId, long time, final int limit,
                            final AVIMTypedMessagesArrayCallback callback) {
    final AVIMMessagesQueryCallback queryCallback = new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVIMException e) {
        if (e != null) {
          callback.done(Collections.EMPTY_LIST, e);
        } else {
          callback.done(filterTypedMessages(list), null);
        }
      }
    };

    if (time == 0) {
      // 第一次加载
      conversation.queryMessages(limit, queryCallback);
    } else {
      // 上拉
      conversation.queryMessages(msgId, time, limit, queryCallback);
    }
  }

  List<AVIMTypedMessage> filterTypedMessages(List<AVIMMessage> messages) {
    List<AVIMTypedMessage> resultMessages = new ArrayList<>();
    for (AVIMMessage msg : messages) {
      if (msg instanceof AVIMTypedMessage) {
        resultMessages.add((AVIMTypedMessage) msg);
      } else {
        LogUtils.i("unexpected message " + msg.getContent());
      }
    }
    return resultMessages;
  }


  /**
   * 查找对话的最后一条消息，如果已查找过，则立即返回
   *
   * @param conversation
   * @return 当向服务器查找失败时或无历史消息时，返回 null
   */
  public synchronized AVIMTypedMessage queryLatestMessage(AVIMConversation conversation) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final List<AVIMTypedMessage> typeMessages = new ArrayList<>();
    conversation.queryMessages(null, 0, 1, new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVIMException e) {
        if (e == null) {
          typeMessages.addAll(filterTypedMessages(list));
        }
        latch.countDown();
      }
    });
    latch.await();
    if (typeMessages.size() > 0) {
      return typeMessages.get(0);
    } else {
      return null;
    }
  }

}
