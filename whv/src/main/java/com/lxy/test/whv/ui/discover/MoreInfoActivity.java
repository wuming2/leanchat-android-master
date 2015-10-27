package com.lxy.test.whv.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.WebViewActivity;
import com.lxy.test.whv.ui.base_activity.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LXY on 2015/10/27.
 * <p/>
 * 更多信息 貌似用处也不大哈-。-
 */
public class MoreInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_moreinfo_activity);
        ButterKnife.inject(this);

        initActionBar(R.string.discover_more);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.discover_more_imagineaustralia_weibo)
    public void goImagineWeibo(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", "http://m.weibo.cn/u/1918101143");
        intent.putExtra("title", "澳大利亚驻华使领馆微博");
        startActivity(intent);
    }

    @OnClick(R.id.discover_more_douban)
    public void goDouban(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", "http://m.douban.com/group/whvers/");
        intent.putExtra("title", "豆瓣澳大利亚WHV小组");
        startActivity(intent);
    }

    @OnClick(R.id.discover_more_visa_application_quick)
    public void goAuApplicationQuick(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", "https://www.visaservices.org.in/DIAC-China-Appointment/AppScheduling/AppWelcome.aspx?p=Gta39GFZnstZVCxNVy83zTlkvzrXE95fkjmft28XjNg%3d");
        intent.putExtra("title", "预约申请快速入口");
        startActivity(intent);
    }

    @OnClick(R.id.discover_more_visa_application)
    public void goAuApplication(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", "http://www.vfsglobal.com/Australia/China/");
        intent.putExtra("title", "预约申请入口");
        startActivity(intent);
    }

    @OnClick(R.id.discover_more_backpackers_au)
    public void goBackpackers(View view) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", "http://www.backpackers.com.tw/forum/forumdisplay.php?f=121");
        intent.putExtra("title", getString(R.string.discover_more_backpackers_au));
        startActivity(intent);
    }
}