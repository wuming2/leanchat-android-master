package com.lxy.test.whv;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.baidu.mapapi.SDKInitializer;
import com.lxy.test.whv.entity.avobject.AddRequest;
import com.lxy.test.whv.service.PushManager;
import com.lxy.test.whv.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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

        String appId = "jBlSrnCLCJPo2CPIHDw1Pwph";//"x3o016bxnkpyee7e9pa5pre6efx2dadyerdlcez0wbzhw25g";
        String appKey = "rAl0HdKORza58S0yl1vf1BRC";//"057x24cfdzhffnl3dzk14jh9xo2rq6w1hy1fdzt5tv46ym78";

        // what this for?
        AVUser.alwaysUseSubUserClass(LeanchatUser.class);
        AVOSCloud.initialize(this, appId, appKey);

        AVObject.registerSubclass(AddRequest.class);

        PushManager.getInstance().init(ctx);

        LogUtils.debugEnabled = true;

        initImageLoader(ctx);
        initBaiduMap();
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
