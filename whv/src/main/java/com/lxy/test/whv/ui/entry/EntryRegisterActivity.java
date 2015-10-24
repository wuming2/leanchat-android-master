package com.lxy.test.whv.ui.entry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.App;
import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.MainActivity;
import com.lxy.test.whv.ui.bootstrap.BootstrapActivity;
import com.lxy.test.whv.util.Utils;

public class EntryRegisterActivity extends EntryBaseActivity {
    View registerButton;
    EditText usernameEdit, passwordEdit, emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_register_activity);
        findView();
        initActionBar(App.ctx.getString(R.string.register));
        registerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                register();
            }
        });
    }

    private void findView() {
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        emailEdit = (EditText) findViewById(R.id.ensurePasswordEdit);
        registerButton = findViewById(R.id.btn_register);
    }

    private void register() {
        final String name = usernameEdit.getText().toString();
        final String password = passwordEdit.getText().toString();
        String againPassword = emailEdit.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Utils.toast(R.string.username_cannot_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Utils.toast(R.string.password_can_not_null);
            return;
        }
        if (!againPassword.equals(password)) {
            Utils.toast(R.string.password_not_consistent);
            return;
        }

        LeanchatUser.signUpByNameAndPwd(name, password, new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    Utils.toast(App.ctx.getString(R.string.registerFailed) + e.getMessage());
                } else {
                    Utils.toast(R.string.registerSucceed);
//                    MainActivity.goMainActivityFromActivity(EntryRegisterActivity.this);
                    goBootstrapActivity();
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
