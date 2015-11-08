package com.lxy.test.whv.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lxy.test.whv.App;
import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.base_activity.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lzw on 14-9-17.
 */
public class DiscoverFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.discover_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    private void initHeader() {
        headerLayout.showTitle(App.ctx.getString(R.string.discover_title));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initHeader();
    }


    @OnClick(R.id.discover_post)
    public void goPostActivity() {
        Intent intent = new Intent(this.getActivity(), PostActivity.class);
        this.getActivity().startActivity(intent);
    }

    @OnClick(R.id.discover_nearby)
    public void goNearbyActivity() {
        Intent intent = new Intent(this.getActivity(), NearbyActivity.class);
        this.getActivity().startActivity(intent);
    }

    @OnClick(R.id.discover_together)
    public void goTogetherActivity() {
        Intent intent = new Intent(this.getActivity(), CompanyActivity.class);
        this.getActivity().startActivity(intent);
    }

    //TODO
    @OnClick(R.id.discover_meetup)
    public void goMeetUp() {
        Toast.makeText(this.getActivity(), "开发中", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this.getActivity(), NearbyActivity.class);
//        this.getActivity().startActivity(intent);
    }

    @OnClick(R.id.discover_more)
    public void goMoreInfoActivity() {
        Intent intent = new Intent(this.getActivity(), MoreInfoActivity.class);
        this.getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }


}
