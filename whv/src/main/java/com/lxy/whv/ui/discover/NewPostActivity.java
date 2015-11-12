package com.lxy.whv.ui.discover;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.Post;
import com.lxy.whv.ui.base_activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LXY on 2015/10/27.
 */
public class NewPostActivity extends BaseActivity {

    LayoutInflater inflater = null;
    ProgressDialog dialog;

    @InjectView(R.id.editText_title)
    TextView et_title;
    @InjectView(R.id.editText_content)
    TextView et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_newpost_activity);
        ButterKnife.inject(this);
        inflater = this.getLayoutInflater();
        initActionBar("新帖子");
    }

    public void submitPost(View view) {
        // 发布文章
        String title = et_title.getText().toString();
        if (title == null || title.isEmpty()) {
            toast("请输入标题");
            return;
        }
        String content = et_content.getText().toString();
        if (content == null || content.isEmpty()) {
            toast("请输入内容");
            return;
        }

        Post post = new Post(title, content, LeanchatUser.getCurrentUser());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    if (dialog != null) {
                        dialog.hide();
                    }
                    toast("发布成功");
                    NewPostActivity.this.finish();
                } else {
                    toast("发布失败 error:" + e.getMessage());
                }
            }
        });
    }
}
