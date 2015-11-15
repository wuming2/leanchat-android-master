package com.lxy.whv.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lxy.whv.R;
import com.lxy.whv.service.UpdateService;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.util.LogUtils;

/**
 * Created by wuming on 2015/11/1.
 * <p/>
 * 关于，嗯，随便写点什么吧
 */
public class AboutActivity extends BaseActivity {

    private int count = 0;
    private int index[] = {10, 20, 30, 50, 80, 100, 110};
    private String text[] = {"都叫你不要点了", "真不听话", "要qq在logcat里看，用DDMS", "你赢了", "后边没有了", "再点我就把他隐藏了……"};
    private int currentChecked = 0;

    Button button;
    TextView versionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initActionBar(R.string.profile_fragment_about);

        versionTv = (TextView) findViewById(R.id.about_versionName);
        versionTv.setText(getString(R.string.about_version_text) + UpdateService.getVersionName(AboutActivity.this));

        button = (Button) findViewById(R.id.button_douniwan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                setButtonText(count);
                LogUtils.d("qq 358919764 wuming");
            }
        });
    }

    private void setButtonText(int count) {

        if (count == index[currentChecked]) {
            LogUtils.d("text.length = " + text.length);
            if (currentChecked < text.length) {
                button.setText(text[currentChecked]);
            } else {
                button.setVisibility(View.GONE);
                toast(R.string.profile_fragment_for_fun);
            }
            currentChecked++;
        }
    }

}
