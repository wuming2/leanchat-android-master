package com.lxy.whv.ui.entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.service.CacheService;
import com.lxy.whv.ui.MainActivity;
import com.lxy.whv.ui.bootstrap.BootstrapActivity;
import com.lxy.whv.util.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class EntryLoginActivity extends EntryBaseActivity {

    @InjectView(R.id.activity_login_et_username)
    public EditText userNameView;

    @InjectView(R.id.activity_login_et_password)
    public EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_login_activity);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.activity_login_btn_login)
    public void onLoginClick(View v) {
        login();
    }

    @OnClick(R.id.activity_login_btn_register)
    public void onRegisterClick(View v) {
        Intent intent = new Intent(ctx, EntryRegisterActivity.class);
        ctx.startActivity(intent);
    }

    private void login() {
        final String name = userNameView.getText().toString().trim();
        final String password = passwordView.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Utils.toast(R.string.username_cannot_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Utils.toast(R.string.password_can_not_null);
            return;
        }

        final ProgressDialog dialog = showSpinnerDialog();
        LeanchatUser.logInInBackground(name, password, new LogInCallback<LeanchatUser>() {
            @Override
            public void done(LeanchatUser avUser, AVException e) {
                dialog.dismiss();
                if (filterException(e)) {

                    CacheService.cacheFriends();

                    boolean bootstraped = false;
                    //TODO 编辑中 默认进入
                    bootstraped = avUser.getInt("applyState") >= 0;
                    if (!bootstraped) {

                        goBootstrapActivity();
                        finish();
                        return;
                    }
                    MainActivity.goMainActivityFromActivity(EntryLoginActivity.this);
                }
            }
        }, LeanchatUser.class);
    }

    private void goBootstrapActivity() {
        Intent intent = new Intent(ctx, BootstrapActivity.class);
        ctx.startActivity(intent);
        finish();
    }

    public void forgetPassword(View view) {
        Intent intent = new Intent(ctx, ForgetPasswordActivity.class);
        ctx.startActivity(intent);
        finish();
    }
}
