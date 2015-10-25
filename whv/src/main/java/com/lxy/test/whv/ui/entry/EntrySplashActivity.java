package com.lxy.test.whv.ui.entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.RefreshCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.R;
import com.lxy.test.whv.service.CacheService;
import com.lxy.test.whv.ui.MainActivity;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.bootstrap.BootstrapActivity;
import com.lxy.test.whv.util.LogUtils;

import java.lang.ref.WeakReference;


public class EntrySplashActivity extends BaseActivity {
    public static final int SPLASH_DURATION = 2000;
    private static final int GO_MAIN_MSG = 1;
    private static final int GO_LOGIN_MSG = 2;
    LeanchatUser user;

    private MyHandler handler = new MyHandler(EntrySplashActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_splash_layout);
        user = LeanchatUser.getCurrentUser();

        if (LeanchatUser.getCurrentUser() != null) {

            //user.put("test","hahah");
            // 更新数据库中updatedAt参数
            user.updateUserInfo();
            LogUtils.d("refreshInBackground");
            //TODO 额需要refresh一下才能获取到其他数据 要不要修改leanclound demo项目中相关代码? 后续删除下无用代码
            // 如果不更新，put保存数据&avatar等数据均获取不到！
            user.refreshInBackground(new RefreshCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (e != null) {
                        LogUtils.e("error = " + e.getLocalizedMessage());
                        handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
                    } else {
                        // TODO
                        CacheService.cacheFriends();
                        handler.sendEmptyMessageDelayed(GO_MAIN_MSG, SPLASH_DURATION);
                    }
                }
            });
        } else {
            handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
        }
    }

    private void goBootstrapActivity() {
        Intent intent = new Intent(ctx, BootstrapActivity.class);
        ctx.startActivity(intent);
    }

    static class MyHandler extends Handler {

        WeakReference<EntrySplashActivity> mActivity;

        MyHandler(EntrySplashActivity activity) {
            mActivity = new WeakReference<EntrySplashActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            EntrySplashActivity theActivity = mActivity.get();
            if (theActivity == null)
                return;
            switch (msg.what) {
                case GO_MAIN_MSG:

                    boolean bootstraped = false;
                    //TODO 编辑中 默认进入
                    bootstraped = theActivity.user.getInt("applyState") >= 0;
                    if (!bootstraped) {

                        theActivity.goBootstrapActivity();
                        theActivity.finish();
                        return;
                    }

                    MainActivity.goMainActivityFromActivity(theActivity);
                    theActivity.finish();
                    break;
                case GO_LOGIN_MSG:
                    Intent intent = new Intent(theActivity, EntryLoginActivity.class);
                    theActivity.startActivity(intent);
                    theActivity.finish();
                    break;
            }
        }
    }
}
