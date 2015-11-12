package com.lxy.whv.ui.discover;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.ui.view.DatePickerDialog;
import com.lxy.whv.util.DateUtils;
import com.lxy.whv.util.LogUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wuming on 2015/10/31.
 */
public class CompanyInfoEditActivity extends BaseActivity {

    LayoutInflater inflater = null;
    ProgressDialog dialog;
    LeanchatUser user;

    String datePlanned = "";
    String destination = "";
    boolean showMyPlan = false;

    @InjectView(R.id.discover_company_plan_tv_destination)
    TextView tv_destination;
    @InjectView(R.id.discover_company_plan_tv_time)
    TextView tv_time;
    @InjectView(R.id.switch_company_show_my_info)
    Switch switch_show_my_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_plan_edit_activity);
        ButterKnife.inject(this);
        inflater = this.getLayoutInflater();
        initActionBar(R.string.company_info_edit_title);
        initView();

    }

    private void initView() {
        user = LeanchatUser.getCurrentUser();
        Date date = user.getDate("datePlanned");
        if (date != null) {
            datePlanned = DateUtils.dateToStr(date, "yyyy-MM-dd");
        }
        destination = user.getString("destination");
        if (destination == null || destination.isEmpty()) {
            destination = "";
        }

        boolean planSetted = false;
        planSetted = user.getBoolean("planSetted");
        if (planSetted) {
            showMyPlan = user.getBoolean("showMyPlan");
        } else {
            showMyPlan = true;
        }
        switch_show_my_info.setChecked(showMyPlan);
        tv_time.setText(datePlanned);
        tv_destination.setText(destination);
        switch_show_my_info.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        showMyPlan = isChecked;
                        LogUtils.d("switch_show_my_info onCheckedChanged " + isChecked);
                    }
                }
        );
    }

    public void updateUserInfo(View view) {

        if (showMyPlan) {
            if (datePlanned == null || datePlanned.isEmpty()) {
                toast(R.string.company_info_edit_tip_time);
                return;
            }
            if (destination == null || destination.isEmpty()) {
                toast(R.string.company_info_edit_tip_destination);
                return;
            }
            Date date = null;
            boolean dateParseSuccess = false;
            try {
                date = DateUtils.toDate(datePlanned, "yyyy-MM-dd");
                dateParseSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!dateParseSuccess) {
                toast(R.string.company_info_edit_tip_time_error);
                return;
            }
            user.put("datePlanned", date);
            user.put("destination", destination);
        }
        user.put("planSetted", true);
        user.put("showMyPlan", showMyPlan);
        user.updateUserInfo();
        finish();
    }


    public void goDestinationSelectRadio(View view) {
        final AlertDialog dialog;
        final View destinationSelectView = inflater.inflate(R.layout.discover_company_city_radio, null);

        RadioGroup group = (RadioGroup) destinationSelectView.findViewById(R.id.radioGroup);
        String title = getString(R.string.company_info_edit_destination_dialog_title);
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
                dialog.dismiss();
            }
        });
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

        DatePickerDialog dialog = new DatePickerDialog(CompanyInfoEditActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int day) {
                String textString = String.format("%d-%d-%d", year,
                        month + 1, day);
                tv_time.setText(textString);
                datePlanned = textString;
            }
        }, myear, mmonth, mday, true);
        // 出发时间只能设置为今天以后
        dialog.setMinDate(new Date());
        dialog.show();
    }
}
