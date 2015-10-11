package com.lxy.test.whv;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avoscloud.leanchatlib.model.LeanchatUser;

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
    }
}
