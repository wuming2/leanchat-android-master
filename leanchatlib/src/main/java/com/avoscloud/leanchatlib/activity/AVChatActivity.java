package com.avoscloud.leanchatlib.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avoscloud.leanchatlib.R;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.event.EmptyEvent;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.LogUtils;

/**
 * Created by wli on 15/9/18.
 */
public class AVChatActivity extends AVBaseActivity {

    protected ChatFragment chatFragment;
    protected AVIMConversation conversation;
    protected String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
        initByIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initByIntent(intent);
    }

    private void initByIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (null != extras) {
            if (extras.containsKey(Constants.MEMBER_ID)) {
                userId = extras.getString(Constants.MEMBER_ID);
                getConversation(userId);
            } else if (extras.containsKey(Constants.CONVERSATION_ID)) {
                String conversationId = extras.getString(Constants.CONVERSATION_ID);
                updateConversation(AVIMClient.getInstance(ChatManager.getInstance().getSelfId()).getConversation(conversationId));
            } else if (extras.containsKey(Constants.SQUARE_ID)) {
                String conversationId = extras.getString(Constants.SQUARE_ID);
                AVIMClient client = AVIMClient.getInstance(ChatManager.getInstance().getSelfId());
                conversation = client.getConversation(conversationId);
                //TODO 怎么判断有没有已经加入……
                joinSquare();
            }
        }
    }

    protected void initActionBar(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (title != null) {
                actionBar.setTitle(title);
            }
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            LogUtils.i("action bar is null, so no title, please set an ActionBar style for activity");
        }
    }

    public void onEvent(EmptyEvent emptyEvent) {
    }

    protected void updateConversation(AVIMConversation conversation) {
        if (null != conversation) {
            this.conversation = conversation;
            chatFragment.setConversation(conversation);
            chatFragment.showUserName(ConversationHelper.typeOfConversation(conversation) != ConversationType.Single);
            LogUtils.d("updateConversation before initActionBar");
            //TODO conversation 有问题阿啊啊，为什么每次type 为null
            initActionBar(ConversationHelper.titleOfConversation(conversation));
        }
    }

    /**
     * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
     * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
     */
    private void getConversation(final String memberId) {
        LogUtils.d("getConversation");
        ChatManager.getInstance().fetchConversationWithUserId(memberId, new AVIMConversationCreatedCallback() {
            @Override
            public void done(AVIMConversation conversation, AVIMException e) {
                if (filterException(e)) {
                    ChatManager.getInstance().getRoomsTable().insertRoom(conversation.getConversationId());
                    updateConversation(conversation);
                }
            }
        });
    }

    private void joinSquare() {
        conversation.join(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (filterException(e)) {
                    updateConversation(conversation);
                } else {
                    //TODO error
                    showToast("error");
                    finish();
                }
            }
        });
    }
}