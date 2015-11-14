package com.lxy.whv.ui.entry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.App;
import com.lxy.whv.R;
import com.lxy.whv.ui.bootstrap.BootstrapActivity;
import com.lxy.whv.util.Utils;

public class ForgetPasswordActivity extends EntryBaseActivity {
    View submitButton;
    EditText usernameEdit, emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_forgetpassword_activity);
        findView();
        initActionBar(App.ctx.getString(R.string.forget_password));
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendRequest();
            }
        });
    }

    private void findView() {

        emailEdit = (EditText) findViewById(R.id.emailEdit);
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        submitButton = findViewById(R.id.btn_submit);
    }

    private void sendRequest() {
        final String name = usernameEdit.getText().toString();
        final String email = emailEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Utils.toast(R.string.username_cannot_null);
            return;
        } else if (name.length() <= 5) {
            Utils.toast(R.string.username_too_short);
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Utils.toast(R.string.email_cannot_null);
            return;
        }

        AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 已发送一份重置密码的指令到用户的邮箱
                    toast(R.string.forgetpassword_email_send_success);

                } else {
                    // 重置密码出错。
                    toast(getString(R.string.forgetpassword_email_send_error) + e.getLocalizedMessage());
                }
            }
        });
    }

    private void goBootstrapActivity() {
        Intent intent = new Intent(ctx, BootstrapActivity.class);
        ctx.startActivity(intent);
        finish();
    }
}
