package com.avoscloud.chat.ui.entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.avoscloud.chat.R;
import com.avoscloud.chat.ui.MainActivity;
import com.avoscloud.chat.ui.base_activity.BaseActivity;
import com.avoscloud.leanchatlib.model.LeanchatUser;

public class EntrySplashActivity extends BaseActivity {
  public static final int SPLASH_DURATION = 2000;
  private static final int GO_MAIN_MSG = 1;
  private static final int GO_LOGIN_MSG = 2;

  private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case GO_MAIN_MSG:
          MainActivity.goMainActivityFromActivity(EntrySplashActivity.this);
          finish();
          break;
        case GO_LOGIN_MSG:
          Intent intent = new Intent(ctx, EntryLoginActivity.class);
          ctx.startActivity(intent);
          finish();
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.entry_splash_layout);
    if (LeanchatUser.getCurrentUser() != null) {
      LeanchatUser.getCurrentUser(LeanchatUser.class).updateUserInfo();
      handler.sendEmptyMessageDelayed(GO_MAIN_MSG, SPLASH_DURATION);
    } else {
      handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, SPLASH_DURATION);
    }
  }
}
