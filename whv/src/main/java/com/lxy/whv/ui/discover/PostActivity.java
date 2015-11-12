package com.lxy.whv.ui.discover;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.Post;
import com.lxy.whv.service.PreferenceMap;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.ui.discover.adapter.MyPagerAdapter;
import com.lxy.whv.ui.discover.adapter.PostAdapter;
import com.lxy.whv.ui.view.BaseListView;
import com.lxy.whv.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wuming on 2015/10/25.
 */
public class PostActivity extends BaseActivity {

    LayoutInflater inflater = null;

    @InjectView(R.id.discover_post_viewpager)
    ViewPager mPager;//页卡内容

    PreferenceMap preferenceMap;
    LeanchatUser user;
    List<View> listViews; // Tab页面列表

    BaseListView<Post> listViewPost;
    List<Post> posts = new ArrayList<>();
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_post_activity);
        inflater = this.getLayoutInflater();

        initActionBar("版聊");
        // cityMap = Constant.getCityMap(CompanyActivity.this.getApplication());
        ButterKnife.inject(this);
        getInfo();
        postAdapter = new PostAdapter(ctx, posts);
        InitViewPager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    //TODO
    public void goNewPostActivity(View view) {
        Intent i = new Intent(this, NewPostActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        ButterKnife.reset(this);
    }

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        listViews = new ArrayList<>();
        listViews.add(inflater.inflate(R.layout.discover_post, null));
        listViewPost = (BaseListView) listViews.get(0).findViewById(R.id.list_discover_post);
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        setViewPagerTitleView(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.d("onPageSelect position = " + position);
                setViewPagerTitleView(position);
                showPostInfo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showPostInfo(0);
    }

    // 获取并显示同行者
    private void showPostInfo(int position) {

        switch (position) {
            case 0:
                initPostXListView(postAdapter, listViewPost, posts);
                break;
        }

    }

    private void initPostXListView(PostAdapter adapter, BaseListView<Post> listView, List<Post> nears) {

        listView.init(new BaseListView.DataFactory<Post>() {
            @Override
            public List<Post> getDatasInBackground(int skip, int limit, List<Post> currentDatas) throws Exception {
                return Post.findPosts(skip, limit);
            }
        }, adapter);

        listView.setItemListener(new BaseListView.ItemListener<Post>() {
            @Override
            public void onItemSelected(Post item) {
                Intent i = new Intent(PostActivity.this, PostInfoActivity.class);
                i.putExtra("post", item);
                PostActivity.this.startActivity(i);
            }
        });

        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
                true, true);
        listView.setOnScrollListener(listener);
        listView.onRefresh();
    }

    private void setViewPagerTitleView(int index) {
        switch (index) {
            case 0:
                findViewById(R.id.discover_post_selectview).setBackgroundColor(Color.parseColor("#2C97E8"));
                break;
//            case 1:
//                findViewById(R.id.discover_company_selectview_post).setBackgroundColor(Color.parseColor("#2C97E8"));
//                findViewById(R.id.discover_company_selectview_whver).setBackgroundColor(Color.parseColor("#00000000"));
//                break;
        }

    }

    @OnClick(R.id.discover_company_ll_post)
    public void onPostSelect(View view) {
        mPager.setCurrentItem(0);
    }

    private void refreshListView() {
        int currentPageIndex = mPager.getCurrentItem();
        switch (currentPageIndex) {
            case 0:
                if (listViewPost != null) {
                    listViewPost.onRefresh();
                }

                break;
        }
    }


    private void getInfo() {
        user = LeanchatUser.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.discover_company_ativity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int menuId = item.getItemId();
        LogUtils.d("onMenuItemSelected" + featureId + menuId);
        if (menuId == R.id.company_userinfo_setting) {
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
