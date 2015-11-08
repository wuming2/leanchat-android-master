package com.lxy.test.whv;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.baidu.mapapi.SDKInitializer;
import com.lxy.test.whv.entity.avobject.AddRequest;
import com.lxy.test.whv.entity.avobject.CompanyPost;
import com.lxy.test.whv.entity.avobject.Post;
import com.lxy.test.whv.entity.avobject.PostComment;
import com.lxy.test.whv.service.ConversationManager;
import com.lxy.test.whv.service.PushManager;
import com.lxy.test.whv.util.LogUtils;
import com.lxy.test.whv.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.Collections;
import java.util.List;

/**
 * Created by wuming on 2015/10/11.
 */
public class App extends Application {

    public static App ctx;
    public static boolean debug = true;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        Utils.fixAsyncTaskBug();

        String appId = "jBlSrnCLCJPo2CPIHDw1Pwph";//"x3o016bxnkpyee7e9pa5pre6efx2dadyerdlcez0wbzhw25g";
        String appKey = "rAl0HdKORza58S0yl1vf1BRC";//"057x24cfdzhffnl3dzk14jh9xo2rq6w1hy1fdzt5tv46ym78";

        // what this for?
        AVUser.alwaysUseSubUserClass(LeanchatUser.class);
        AVOSCloud.initialize(this, appId, appKey);

        AVObject.registerSubclass(AddRequest.class);
        AVObject.registerSubclass(CompanyPost.class);
        AVObject.registerSubclass(PostComment.class);
        AVObject.registerSubclass(Post.class);

        PushManager.getInstance().init(ctx);
        LogUtils.debugEnabled = true;

        initImageLoader(ctx);
        initBaiduMap();

        initChatManager();
    }

    private void test() {
        // 测试创建聊天室
        AVIMClient tom = AVIMClient.getInstance("Tom");
        final List<String> members = Collections.emptyList();
        tom.open(new AVIMClientCallback() {

            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    //登录成功
                    //创建一个 名为 "HelloKitty PK 加菲猫" 的暂态对话
                    client.createConversation(members, "WHV广场", null, false,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation conv, AVIMException e) {

                                }
                            });
                }
            }
        });
    }

    private void initChatManager() {
        final ChatManager chatManager = ChatManager.getInstance();
        chatManager.init(this);
        if (LeanchatUser.getCurrentUser() != null) {
            chatManager.setupManagerWithUserId(LeanchatUser.getCurrentUser().getObjectId());
        }
        chatManager.setConversationEventHandler(ConversationManager.getEventHandler());
        ChatManager.setDebugEnabled(App.debug);
    }

    /**
     * 初始化ImageLoader
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                        //.memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initBaiduMap() {
        SDKInitializer.initialize(this);
    }
}
