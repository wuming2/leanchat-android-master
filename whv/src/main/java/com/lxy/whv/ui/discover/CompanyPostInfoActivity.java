package com.lxy.whv.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.CompanyPost;
import com.lxy.whv.entity.avobject.PostComment;
import com.lxy.whv.service.CacheService;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.ui.contact.ContactPersonInfoActivity;
import com.lxy.whv.ui.discover.adapter.CompanyPostCommentAdapter;
import com.lxy.whv.ui.view.BaseListView;
import com.lxy.whv.util.DateUtils;
import com.lxy.whv.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wuming on 2015/10/31.
 */
public class CompanyPostInfoActivity extends BaseActivity {

    private CompanyPost post;
    private LeanchatUser user;

    View headerView;

    @InjectView(R.id.company_post_comment_listview)
    BaseListView<PostComment> listViecomwComment;

    List<PostComment> comments = new ArrayList<>();
    CompanyPostCommentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_company_postinfo_activity);
        ButterKnife.inject(this);
        initActionBar("结伴详情");
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
        adapter = new CompanyPostCommentAdapter(ctx, comments);
        listViecomwComment.addHeaderView(headerView);
        initXListView(adapter, listViecomwComment, comments);
    }

    private void initXListView(CompanyPostCommentAdapter adapter, BaseListView<PostComment> listView, List<PostComment> comments) {

        listView.init(new BaseListView.DataFactory<PostComment>() {
            @Override
            public List<PostComment> getDatasInBackground(int skip, int limit, List<PostComment> currentDatas) throws Exception {
                return PostComment.findCompanyPostComment(skip, limit, post.getObjectId());
            }
        }, adapter);

        listView.setItemListener(new BaseListView.ItemListener<PostComment>() {
            @Override
            public void onItemSelected(PostComment item) {
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
        user = CacheService.lookupUser(user.getObjectId());

        headerView = getLayoutInflater().inflate(
                R.layout.discover_postinfo_header, null);

        TextView tv_post_title = (TextView) headerView.findViewById(R.id.company_post_title);
        TextView tv_post_time = (TextView) headerView.findViewById(R.id.company_post_time);
        TextView tv_post_destination = (TextView) headerView.findViewById(R.id.company_post_destination);
        TextView tv_post_content = (TextView) headerView.findViewById(R.id.company_post_content);
        TextView tv_posttime = (TextView) headerView.findViewById(R.id.company_post_posttime);
        TextView tv_username = (TextView) headerView.findViewById(R.id.company_post_username);
        ImageView imageView_avatar = (ImageView) headerView.findViewById(R.id.avatar_view);

        tv_post_title.setText(post.getTitle());
        tv_post_time.setText(DateUtils.dateToStr(post.getDateplanned(), "yyyy-MM-dd"));
        tv_post_destination.setText(post.getDestination());
        tv_post_content.setText(post.getContent());
        tv_username.setText(user.getUsername());
        tv_posttime.setText(getString(R.string.company_post_publish_time) + " " + DateUtils.dateToStr(post.getUpdatedAt(), "yyyy-MM-dd HH:mm"));
        LogUtils.d("user avatar = " + user.getAvatarUrl());
        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), imageView_avatar, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);

    }

    public void gotUserActivity(View view) {
        ContactPersonInfoActivity.goPersonInfo(CompanyPostInfoActivity.this, user.getObjectId());
    }

    @OnClick(R.id.button_post_comment)
    public void gotPostCommentActivity(View view) {
        Intent i = new Intent(CompanyPostInfoActivity.this, CompanyPostCommentActivity.class);
        i.putExtra("post", post);
        startActivity(i);
    }


}
