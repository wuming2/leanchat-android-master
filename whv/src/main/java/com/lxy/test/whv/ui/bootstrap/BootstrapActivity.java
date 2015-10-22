package com.lxy.test.whv.ui.bootstrap;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.MainActivity;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.view.HeaderLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wuming on 2015/10/22.
 */
public class BootstrapActivity extends BaseActivity {

    @InjectView(R.id.nearby_headerlayout)
    protected HeaderLayout headerLayout;

    @InjectView(R.id.rl_container)
    protected RelativeLayout containerLayout;

    LayoutInflater inflater = null;
    Button button_set_app_state;
    Button button_prefile_submit;
    RadioGroup rg_app_state;
    private int applyState = -1;

    private final int[] applicationStateButtonIndex = {R.id.radiobutton_want_to_know, R.id.radiobutton_preparing,
            R.id.radiobutton_submiting, R.id.radiobutton_granted, R.id.radiobutton_abroad,
            R.id.radiobutton_returned, R.id.radiobutton_pr};
    private final int[] applicationState = {0, 1, 2, 3, 4, 5, 6};
    private final int[] applicationStateTextId = {R.string.bootstrap_state_want_to_know, R.string.bootstrap_state_preparing,
            R.string.bootstrap_state_submiting, R.string.bootstrap_state_granted, R.string.bootstrap_state_abroad,
            R.string.bootstrap_state_returned, R.string.bootstrap_state_pr};

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootstrap_activity);
        ButterKnife.inject(this);

        inflater = this.getLayoutInflater();
        headerLayout.showTitle(R.string.bootstrap_title);
//        headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        showApplicationStateSet();
    }

    private void showApplicationStateSet() {

        View appStateView = inflater.inflate(R.layout.bootstrap_application_state, containerLayout);
//        containerLayout.removeAllViews();
//        containerLayout.addView(appStateView);

        button_set_app_state = (Button) appStateView.findViewById(R.id.button_application_state_submit);
        button_set_app_state.setOnClickListener(listener);
        rg_app_state = (RadioGroup) appStateView.findViewById(R.id.radiogroup_application_state);

        rg_app_state.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                for (int i = 0; i < applicationStateButtonIndex.length; i++) {
                    if (radioButtonId == radioButtonId) {
                        applyState = applicationState[i];
                    }
                }
                //根据ID获取RadioButton的实例
                //更新文本内容，以符合选中项
            }
        });
    }

    private void updateUserInfo() {

        dialog = showSpinnerDialog();

        //TODO
        MainActivity.goMainActivityFromActivity(BootstrapActivity.this);
        finish();
    }

    private void showPerfileInfoSet() {
        containerLayout.removeAllViews();
        View perfileInfoSet = inflater.inflate(R.layout.bootstrap_perfile_set, containerLayout);

        button_prefile_submit = (Button) perfileInfoSet.findViewById(R.id.button_prefile_submit);
        button_prefile_submit.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_application_state_submit:
                    if (applyState >= 0) {
                        showPerfileInfoSet();
                    } else {
                        toast(R.string.bootstrap_state_tip_select);
                    }
                    break;
                case R.id.button_prefile_submit:
                    updateUserInfo();
                    break;

            }
        }
    };


}
