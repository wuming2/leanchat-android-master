package com.lxy.test.whv.ui.discover;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.avobject.CompanyPost;
import com.lxy.test.whv.service.PreferenceMap;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.contact.ContactPersonInfoActivity;
import com.lxy.test.whv.ui.discover.adapter.CompanyAdapter;
import com.lxy.test.whv.ui.discover.adapter.CompanyPostAdapter;
import com.lxy.test.whv.ui.discover.adapter.MyPagerAdapter;
import com.lxy.test.whv.ui.view.BaseListView;
import com.lxy.test.whv.ui.view.DatePickerDialog;
import com.lxy.test.whv.util.DateUtils;
import com.lxy.test.whv.util.LogUtils;
import com.lxy.test.whv.util.UserDAOUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wuming on 2015/10/25.
 */
public class CompanyActivity extends BaseActivity {

    LayoutInflater inflater = null;
    String datePlanned = "";
    String destination = "";

    //TODO 先使用单选
    HashSet<String> cityCodeSelect = new HashSet<>();
    HashSet<String> citySelect = new HashSet<>();
    HashMap<String, String> cityMap;

    @InjectView(R.id.discover_company_tv_destination)
    TextView tv_destination;
    @InjectView(R.id.discover_company_tv_time)
    TextView tv_time;
    @InjectView(R.id.discover_company_viewpager)
    ViewPager mPager;//页卡内容

//    private int pageSelected = 0;

    PreferenceMap preferenceMap;
    LeanchatUser user;
    List<View> listViews; // Tab页面列表

    BaseListView<LeanchatUser> listViewWhver;
    BaseListView<CompanyPost> listViewPost;
    List<LeanchatUser> whvers = new ArrayList<>();
    List<CompanyPost> posts = new ArrayList<>();
    CompanyAdapter companyAdapter;
    CompanyPostAdapter companyPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_company_activity);
        inflater = this.getLayoutInflater();

        initActionBar("同行");
        // cityMap = Constant.getCityMap(CompanyActivity.this.getApplication());
        ButterKnife.inject(this);
        getInfo();
        companyAdapter = new CompanyAdapter(ctx, whvers);
        companyPostAdapter = new CompanyPostAdapter(ctx, posts);
        InitViewPager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    public void goNewPostActivity(View view) {
        Intent i = new Intent(this, CompanyNewPostActivity.class);
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
        //TODO 使用侧边按钮
    }

    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        listViews = new ArrayList<>();
        listViews.add(inflater.inflate(R.layout.discover_company_whver, null));
        listViews.add(inflater.inflate(R.layout.discover_company_post, null));
        listViewWhver = (BaseListView) listViews.get(0).findViewById(R.id.list_discover_company_whv);
        listViewPost = (BaseListView) listViews.get(1).findViewById(R.id.list_discover_company_post);
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
                showCompanyInfos(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        showCompanyInfos(0);
    }

    // 获取并显示同行者
    private void showCompanyInfos(int position) {

        switch (position) {
            case 0:
                initWHVXListView(companyAdapter, listViewWhver, whvers);
                break;
            case 1:
                initPostXListView(companyPostAdapter, listViewPost, posts);
                break;
        }

    }

    private void initWHVXListView(CompanyAdapter adapter, BaseListView<LeanchatUser> listView, List<LeanchatUser> nears) {

        listView.init(new BaseListView.DataFactory<LeanchatUser>() {
            @Override
            public List<LeanchatUser> getDatasInBackground(int skip, int limit, List<LeanchatUser> currentDatas) throws Exception {
                String dest = destination;
                if (destination.equalsIgnoreCase(getString(R.string.city_australia))) {
                    dest = "";
                }
                return UserDAOUtils.findCompany(skip, limit, datePlanned, dest);
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
        listView.onRefresh();
    }

    private void initPostXListView(CompanyPostAdapter adapter, BaseListView<CompanyPost> listView, List<CompanyPost> nears) {

        listView.init(new BaseListView.DataFactory<CompanyPost>() {
            @Override
            public List<CompanyPost> getDatasInBackground(int skip, int limit, List<CompanyPost> currentDatas) throws Exception {
                String dest = destination;
                if (destination.equalsIgnoreCase(getString(R.string.city_australia))) {
                    dest = "";
                }
                return CompanyPost.findCompanyPost(skip, limit, datePlanned, dest);
            }
        }, adapter);

        listView.setItemListener(new BaseListView.ItemListener<CompanyPost>() {
            @Override
            public void onItemSelected(CompanyPost item) {
                Intent i = new Intent(CompanyActivity.this, CompanyPostInfoActivity.class);
                i.putExtra("post", item);
                CompanyActivity.this.startActivity(i);
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
                findViewById(R.id.discover_company_selectview_post).setBackgroundColor(Color.parseColor("#00000000"));
                findViewById(R.id.discover_company_selectview_whver).setBackgroundColor(Color.parseColor("#2C97E8"));
                break;
            case 1:
                findViewById(R.id.discover_company_selectview_post).setBackgroundColor(Color.parseColor("#2C97E8"));
                findViewById(R.id.discover_company_selectview_whver).setBackgroundColor(Color.parseColor("#00000000"));
                break;
        }

    }

    @OnClick(R.id.discover_company_ll_post)
    public void onPostSelect(View view) {
        mPager.setCurrentItem(1);
    }

    @OnClick(R.id.discover_company_ll_whver)
    public void onWhverSelect(View view) {
        mPager.setCurrentItem(0);
    }

    @OnClick(R.id.discover_company_time_clear)
    public void clearDestination(View view) {
        datePlanned = "";
        tv_time.setText(datePlanned);
        refreshListView();
    }

    @OnClick(R.id.discover_company_destination_clear)
    public void clearTime(View view) {
        destination = "";
        tv_destination.setText(destination);
        refreshListView();
    }

    private void refreshListView() {
        int currentPageIndex = mPager.getCurrentItem();
        switch (currentPageIndex) {
            case 0:
                if (listViewWhver != null) {
                    listViewWhver.onRefresh();
                }

                break;
            case 1:
                if (listViewPost != null) {
                    listViewPost.onRefresh();
                }

                break;
        }
    }


    private void getInfo() {
        user = LeanchatUser.getCurrentUser();
        boolean planSetted = false;
        planSetted = user.getBoolean("planSetted");
        if (!planSetted) {
            goCompanyInfoEditActivity();
//            finish();
        }
        Date date = user.getDate("datePlanned");
        if (date != null) {
            datePlanned = DateUtils.dateToStr(date, "yyyy-MM-dd");
        }
        destination = user.getString("destination");
        if (destination == null || destination.isEmpty()) {
            destination = "";
        }

        tv_time.setText(datePlanned);
        tv_destination.setText(destination);
    }

    public void goDestinationSelectRadio(View view) {
        final AlertDialog dialog;
        final View destinationSelectView = inflater.inflate(R.layout.discover_company_city_radio, null);

        RadioGroup group = (RadioGroup) destinationSelectView.findViewById(R.id.radioGroup);

        String title = "城市选择";
        // final int viewId = view.getId();
        dialog = new AlertDialog.Builder(this).setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(destinationSelectView)
                .show();

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // 获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();

                // 根据ID获取RadioButton的实例
                String cityName = ((RadioButton) destinationSelectView.findViewById(radioButtonId)).getText().toString();
                toast(cityName);
                destination = cityName;
                tv_destination.setText(destination);
                refreshListView();
                dialog.dismiss();
            }
        });
    }

    //TODO 当前先支持单选，复选相对复杂一些
    public void goDestinationSelectCheckbox(final View view) {

        final AlertDialog dialog;
        View stateSelectView = inflater.inflate(R.layout.discover_company_city_checkbox, null);

        CheckBox cb_city_sydney = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_sydney);
        CheckBox cb_city_melb = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_melb);
        CheckBox cb_city_brisbane = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_brisbane);
        CheckBox cb_city_perth = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_perth);
        CheckBox cb_city_adelaide = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_adelaide);
        CheckBox cb_city_canberra = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_canberra);
        CheckBox cb_city_hobart = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_hobart);
        CheckBox cb_city_other = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_other);
        CheckBox cb_city_australia = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_australia);
        CheckBox cb_city_newzealand = (CheckBox) stateSelectView.findViewById(R.id.checkbox_city_newzealand);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                CheckBox box = (CheckBox) buttonView;
                String cityName = box.getText().toString();
                String code = cityMap.get(cityName);
                if (isChecked) {
                    cityCodeSelect.add(code);
                    citySelect.add(cityName);
                } else {
                    cityCodeSelect.remove(code);
                    citySelect.remove(cityName);
                }
            }
        };

        cb_city_sydney.setOnCheckedChangeListener(listener);
        cb_city_melb.setOnCheckedChangeListener(listener);
        cb_city_brisbane.setOnCheckedChangeListener(listener);
        cb_city_perth.setOnCheckedChangeListener(listener);
        cb_city_adelaide.setOnCheckedChangeListener(listener);
        cb_city_canberra.setOnCheckedChangeListener(listener);
        cb_city_hobart.setOnCheckedChangeListener(listener);
        cb_city_other.setOnCheckedChangeListener(listener);
        cb_city_australia.setOnCheckedChangeListener(listener);
        cb_city_newzealand.setOnCheckedChangeListener(listener);

//        if (applyState >= 0) {
////            int checkedId = applicationStateTextId[applyState];
////            rg_app_state.check(checkedId);
//            ((RadioButton) rg_app_state.getChildAt(applyState)).setChecked(true);
//        }
        String title = "城市选择";
////        final int viewId = view.getId();
        dialog = new AlertDialog.Builder(this).setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(stateSelectView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //onInfoSet(viewId, input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void showDatePacker(final View view) {
        Calendar c = Calendar.getInstance();
        int myear = 0;
        int mmonth = 0;
        int mday = 0;
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (!datePlanned.isEmpty()) {
                Date date = fmt.parse(datePlanned);
                myear = date.getYear() + 1900;
                mmonth = date.getMonth();
                mday = date.getDate();
                LogUtils.d("birthdate = " + datePlanned + " myear" + myear + " " + mmonth + " " + mday);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (myear == 0) {
            myear = c.get(Calendar.YEAR);
        }
        if (mmonth == 0) {
            mmonth = c.get(Calendar.MONTH);
        }
        if (mday == 0) {
            mday = c.get(Calendar.DATE);
        }

        DatePickerDialog dialog = new DatePickerDialog(CompanyActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int day) {
                String textString = String.format("%d-%d-%d", year,
                        month + 1, day);
                tv_time.setText(textString);
                datePlanned = textString;
                refreshListView();
            }
        }, myear, mmonth, mday, true);

        // 出发时间只能设置为今天以后
        dialog.setMinDate(new Date());
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.discover_company_ativity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int menuId = item.getItemId();
        LogUtils.d("onMenuItemSelected" + featureId + menuId);
        if (menuId == R.id.company_userinfo_setting) {
            //TODO 编辑个人计划信息
            goCompanyInfoEditActivity();

        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void goCompanyInfoEditActivity() {
        Intent i = new Intent(CompanyActivity.this, CompanyInfoEditActivity.class);
        CompanyActivity.this.startActivity(i);
    }
}
