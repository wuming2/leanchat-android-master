package com.lxy.test.whv.ui.discover;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.avobject.CompanyPost;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.view.DatePickerDialog;
import com.lxy.test.whv.util.DateUtils;
import com.lxy.test.whv.util.LogUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by LXY on 2015/10/27.
 */
public class CompanyNewPostActivity extends BaseActivity {

    LayoutInflater inflater = null;
    ProgressDialog dialog;

    String datePlanned = "";
    String destination = "";

    @InjectView(R.id.discover_company_newpost_tv_destination)
    TextView tv_destination;

    @InjectView(R.id.discover_company_newpost_tv_time)
    TextView tv_time;
    @InjectView(R.id.editText_title)
    TextView et_title;
    @InjectView(R.id.editText_content)
    TextView et_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_company_newpost_activity);
        ButterKnife.inject(this);
        inflater = this.getLayoutInflater();
        initActionBar("添加结伴");
    }

    public void submitPost(View view) {
        //TODO 发布文章
        if (datePlanned == null || datePlanned.isEmpty()) {
            toast("请输入计划出发时间");
            return;
        }
        if (destination == null || destination.isEmpty()) {
            toast("请输入目标地");
            return;
        }

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

        Date date = null;
        boolean dateParseSuccess = false;
        try {
            date = DateUtils.toDate(datePlanned, "yyyy-MM-dd");
            dateParseSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!dateParseSuccess) {
            toast("时间错误，请反馈问题谢谢");
            return;
        }

        CompanyPost companyPost = new CompanyPost(title, content, date, destination, LeanchatUser.getCurrentUser());
        companyPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    if (dialog != null) {
                        dialog.hide();
                    }
                    toast("发布成功");
                    CompanyNewPostActivity.this.finish();
                } else {
                    toast("发布失败 error:" + e.getMessage());
                }
            }
        });
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

        DatePickerDialog dialog = new DatePickerDialog(CompanyNewPostActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
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
