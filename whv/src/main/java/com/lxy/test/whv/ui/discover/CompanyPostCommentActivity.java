package com.lxy.test.whv.ui.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.avobject.CompanyPost;
import com.lxy.test.whv.entity.avobject.PostComment;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.contact.ContactPersonInfoActivity;
import com.lxy.test.whv.ui.discover.adapter.CompanyAdapter;
import com.lxy.test.whv.ui.discover.adapter.CompanyPostCommentAdapter;
import com.lxy.test.whv.ui.view.BaseListView;
import com.lxy.test.whv.util.LogUtils;
import com.lxy.test.whv.util.UserDAOUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.w3c.dom.Comment;

import java.util.List;

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
        comt.setContent(edittext.getText().toString());
        comt.setCreator(AVUser.getCurrentUser());
        comt.setPostId(post.getObjectId());
        comt.saveInBackground(new SaveCallback() {
            public void done(com.avos.avoscloud.AVException arg0) {
                if (null != arg0) {
                    toast("保存失败 " + arg0.getLocalizedMessage());
                } else {
                    finish();
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
