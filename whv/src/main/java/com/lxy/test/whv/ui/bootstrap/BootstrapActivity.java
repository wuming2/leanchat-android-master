package com.lxy.test.whv.ui.bootstrap;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.view.HeaderLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wuming on 2015/10/22.
 */
public class BootstrapActivity extends BaseActivity implements ApplicationStateFragment.OnFragmentInteractionListener {

    @InjectView(R.id.nearby_headerlayout)
    protected HeaderLayout headerLayout;

    ApplicationStateFragment stateFragment = ApplicationStateFragment.newInstance(null, null);

    private static final String FRAGMENT_TAG_APP_STATE = "applicationstate";
    private static final String[] fragmentTags = new String[]{FRAGMENT_TAG_APP_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootstrap_activity);
        ButterKnife.inject(this);

        headerLayout.showTitle(R.string.bootstrap_title);
//        headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
        showFragments();
    }


    //TODO 隐藏fragment
    private void hideFragments(FragmentManager fragmentManager, FragmentTransaction transaction) {
//        for (int i = 0; i < fragmentTags.length; i++) {
//            Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags[i]);
//            if (fragment != null && fragment.isVisible()) {
//                transaction.hide(fragment);
//            }
//        }
    }

    //TODO 显示fragment
    private void showFragments() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(manager, transaction);
        transaction.add(R.id.fragment_container, stateFragment, FRAGMENT_TAG_APP_STATE);
        transaction.show(stateFragment);
//        if (id == R.id.btn_message) {
//
//            if (conversationRecentFragment == null) {
//                conversationRecentFragment = new ConversationRecentFragment();
//                transaction.add(R.id.fragment_container, conversationRecentFragment, FRAGMENT_TAG_CONVERSATION);
//            }
//            transaction.show(conversationRecentFragment);
//        }
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
