package com.lxy.whv.ui.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.CompanyPost;
import com.lxy.whv.entity.avobject.PostComment;
import com.lxy.whv.ui.base_activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wuming on 2015/10/31.
 */
public class CompanyPostCommentActivity extends BaseActivity {

    @InjectView(R.id.company_post_comment_edittext)
    TextView edittext;
    CompanyPost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_company_post_comment_activity);
        ButterKnife.inject(this);
        initActionBar("回复");

        post = getIntent().getParcelableExtra("post");
    }

    @OnClick(R.id.button_post_comment_submit)
    public void submit(View view) {

        PostComment comt = new PostComment();

        String content = edittext.getText().toString();
        if (content.trim().isEmpty()) {

            toast("好歹写点字吧-。-");
            return;
        }
        comt.setContent(content);
        comt.setCreator(AVUser.getCurrentUser());
        comt.setPostId(post.getObjectId());

        AVACL acl = new AVACL();
        acl.setPublicReadAccess(true);   //此处设置的是所有人的可读权限
        acl.setWriteAccess(AVUser.getCurrentUser(), true);   //而这里设置了文件创建者的写权限
        comt.setACL(acl);
        comt.saveInBackground(new SaveCallback() {
            public void done(com.avos.avoscloud.AVException arg0) {
                if (null != arg0) {
                    toast("保存失败 " + arg0.getLocalizedMessage());
                } else {
                    finish();
                    // 不再保存到CompanyPost中
//                    post.addComment(comt);
//                    //TODO 哎这里怎么办啊....
//                    post.saveInBackground(new SaveCallback() {
//                        public void done(com.avos.avoscloud.AVException arg0) {
//                            if (null == arg0) {
//                                //TODO
//                                LogUtils.d("save post");
//                                finish();
//                            } else {
//                                toast("保存失败 " + arg0.getLocalizedMessage());
//                                LogUtils.e("error " + arg0.getLocalizedMessage() + arg0.getMessage() + arg0.getCode());
//                            }
//                        }
//                    });

                }
            }
        });
    }

}
