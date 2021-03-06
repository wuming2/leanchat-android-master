package com.lxy.whv.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.Post;
import com.lxy.whv.entity.avobject.PostComment;
import com.lxy.whv.service.CacheService;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.ui.contact.ContactPersonInfoActivity;
import com.lxy.whv.ui.discover.adapter.PostCommentAdapter;
import com.lxy.whv.ui.view.BaseListView;
import com.lxy.whv.util.DateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wuming on 2015/10/31.
 * <p/>
 * TODO 帖子内容的Activity
 */
public class PostInfoActivity extends BaseActivity {

    private Post post;
    private LeanchatUser user;

    View headerView;
    @InjectView(R.id.company_post_comment_listview)
    BaseListView<PostComment> listViecomwComment;

    List<PostComment> comments = new ArrayList<>();
    PostCommentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_postinfo_activity);
        ButterKnife.inject(this);
        initActionBar("帖子详情");
        initDate();
        showComment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listViecomwComment.onRefresh();
    }

    private void showComment() {
        //显示评论
        adapter = new PostCommentAdapter(ctx, comments);
        //TODO
        listViecomwComment.addHeaderView(headerView);
        initXListView(adapter, listViecomwComment, comments);
    }

    private void initXListView(PostCommentAdapter adapter, BaseListView<PostComment> listView, List<PostComment> comments) {

        listView.init(new BaseListView.DataFactory<PostComment>() {
            @Override
            public List<PostComment> getDatasInBackground(int skip, int limit, List<PostComment> currentDatas) throws Exception {

                return PostComment.findCompanyPostComment(skip, limit, post.getObjectId());
            }
        }, adapter);

        listView.setItemListener(new BaseListView.ItemListener<PostComment>() {
            @Override
            public void onItemSelected(PostComment item) {
                //TODO
//                ContactPersonInfoActivity.goPersonInfo(ctx, item.getObjectId());
            }
        });

        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
                true, true);
        listView.setOnScrollListener(listener);
        listView.onRefresh();
    }

    private void initDate() {
        post = this.getIntent().getParcelableExtra("post");
        user = (LeanchatUser) post.getPublisher();
        //TODO 就是这里  会获取到空的user  看看在适当的时候去更新一下缓存吧！
        user = CacheService.lookupUser(user.getObjectId());

        headerView = getLayoutInflater().inflate(
                R.layout.discover_postinfo_header, null);

        TextView tv_post_title = (TextView) headerView.findViewById(R.id.company_post_title);
        TextView tv_post_content = (TextView) headerView.findViewById(R.id.company_post_content);
        TextView tv_posttime = (TextView) headerView.findViewById(R.id.company_post_posttime);
        TextView tv_username = (TextView) headerView.findViewById(R.id.company_post_username);
        ImageView imageView_avatar = (ImageView) headerView.findViewById(R.id.avatar_view);

        tv_post_title.setText(post.getTitle());
        tv_post_content.setText(post.getContent());
        tv_username.setText(user.getUsername());
        tv_posttime.setText(getString(R.string.company_post_publish_time) + " " + DateUtils.dateToStr(post.getUpdatedAt(), "yyyy-MM-dd HH:mm"));
//        LogUtils.d("user avatar = " + user.getAvatarUrl());
        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), imageView_avatar, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);

    }

    public void gotUserActivity(View view) {
        ContactPersonInfoActivity.goPersonInfo(PostInfoActivity.this, user.getObjectId());
    }

    @OnClick(R.id.button_post_comment)
    public void gotPostCommentActivity(View view) {
        Intent i = new Intent(PostInfoActivity.this, PostCommentActivity.class);
        i.putExtra("post", post);
        startActivity(i);
    }
}
