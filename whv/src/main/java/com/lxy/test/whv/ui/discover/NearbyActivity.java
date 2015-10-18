package com.lxy.test.whv.ui.discover;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.LogUtils;
import com.lxy.test.whv.App;
import com.lxy.test.whv.R;
import com.lxy.test.whv.service.CacheService;
import com.lxy.test.whv.service.PreferenceMap;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.contact.ContactPersonInfoActivity;
import com.lxy.test.whv.ui.view.BaseListView;
import com.lxy.test.whv.ui.view.HeaderLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NearbyActivity extends BaseActivity {

    @InjectView(R.id.nearby_headerlayout)
    protected HeaderLayout headerLayout;

    private final SortDialogListener distanceListener = new SortDialogListener(Constants.ORDER_DISTANCE);
    private final SortDialogListener updatedAtListener = new SortDialogListener(Constants.ORDER_UPDATED_AT);
    @InjectView(R.id.list_near)
    BaseListView<LeanchatUser> listView;
    DiscoverFragmentUserAdapter adapter;
    List<LeanchatUser> nears = new ArrayList<LeanchatUser>();
    int orderType;
    PreferenceMap preferenceMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_nearby_activity);
        ButterKnife.inject(this);

        headerLayout.showTitle(R.string.discover_title);
        //TODO 过滤按钮 逻辑功能  设置过去n天
        headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NearbyActivity.this);
                builder.setTitle(R.string.discover_fragment_sort).setPositiveButton(R.string.discover_fragment_loginTime,
                        updatedAtListener).setNegativeButton(R.string.discover_fragment_distance, distanceListener).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferenceMap = PreferenceMap.getCurUserPrefDao(this);
        orderType = preferenceMap.getNearbyOrder();

        initXListView();
        listView.onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_ativity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int menuId = item.getItemId();

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferenceMap.setNearbyOrder(orderType);
    }

    private void initXListView() {
        adapter = new DiscoverFragmentUserAdapter(ctx, nears);
        listView = (BaseListView<LeanchatUser>) findViewById(R.id.list_near);
        listView.init(new BaseListView.DataFactory<LeanchatUser>() {
            @Override
            public List<LeanchatUser> getDatasInBackground(int skip, int limit, List<LeanchatUser> currentDatas) throws Exception {
                return findNearbyPeople(orderType, skip, limit);
            }
        }, adapter);

        listView.setItemListener(new BaseListView.ItemListener<LeanchatUser>() {
            @Override
            public void onItemSelected(LeanchatUser item) {
                ContactPersonInfoActivity.goPersonInfo(ctx, item.getObjectId());
            }
        });

        PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
                true, true);
        listView.setOnScrollListener(listener);
    }


    public List<LeanchatUser> findNearbyPeople(int orderType, int skip, int limit) throws AVException {
        PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(App.ctx);
        AVGeoPoint geoPoint = preferenceMap.getLocation();
        if (geoPoint == null) {
            LogUtils.i("geo point is null");
            return new ArrayList<>();
        }
        AVQuery<LeanchatUser> q = LeanchatUser.getQuery(LeanchatUser.class);
        AVUser user = AVUser.getCurrentUser();
        q.whereNotEqualTo(Constants.OBJECT_ID, user.getObjectId());
        if (orderType == Constants.ORDER_DISTANCE) {
            q.whereNear(LeanchatUser.LOCATION, geoPoint);
        } else {
            q.orderByDescending(Constants.UPDATED_AT);
        }
        q.skip(skip);
        q.limit(limit);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<LeanchatUser> users = q.find();
        CacheService.registerUsers(users);
        return users;
    }

    public class SortDialogListener implements DialogInterface.OnClickListener {
        int orderType;

        public SortDialogListener(int orderType) {
            this.orderType = orderType;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            NearbyActivity.this.orderType = orderType;
            listView.onRefresh();
        }
    }
}
