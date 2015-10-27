package com.lxy.test.whv.ui.discover;

import android.os.Bundle;

import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.base_activity.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by LXY on 2015/10/27.
 */
public class CompanyNewPostActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_company_newpost_activity);
        ButterKnife.inject(this);
        initActionBar("添加结伴");
    }

}
