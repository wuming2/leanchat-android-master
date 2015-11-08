package com.lxy.test.whv.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.avobject.News;
import com.lxy.test.whv.service.PreferenceMap;
import com.lxy.test.whv.ui.WebViewActivity;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.discover.adapter.PostAdapter;
import com.lxy.test.whv.ui.view.BaseListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostActivity extends BaseActivity {

    @InjectView(R.id.list_near)
    BaseListView<News> listView;
    PostAdapter adapter;
    List<News> newses = new ArrayList<>();
    PreferenceMap preferenceMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_post_activity);
        ButterKnife.inject(this);
        initActionBar(R.string.discover_post);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferenceMap = PreferenceMap.getCurUserPrefDao(this);

        initXListView();
        listView.onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_ativity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.info) {
            // TODO 提示信息
            toast("信息来自于微信，如果有损您的利益请联系我进行删除");
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initXListView() {
        adapter = new PostAdapter(ctx, newses);
        listView = (BaseListView<News>) findViewById(R.id.list_near);
        listView.init(new BaseListView.DataFactory<News>() {
            @Override
            public List<News> getDatasInBackground(int skip, int limit, List<News> currentDatas) throws Exception {
                return News.getPost(skip, limit);
            }
        }, adapter);

        listView.setItemListener(new BaseListView.ItemListener<News>() {
            @Override
            public void onItemSelected(News item) {
                //TODO 点击跳转
                Intent intent = new Intent(PostActivity.this, WebViewActivity.class);
                intent.putExtra("url", item.getUrl());
                intent.putExtra("title", item.getTitle());
                startActivity(intent);
            }
        });

        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
                true, true);
        listView.setOnScrollListener(listener);
    }
}
