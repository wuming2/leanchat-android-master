package com.lxy.whv.ui.discover.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVGeoPoint;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.view.ViewHolder;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.CompanyPost;
import com.lxy.whv.service.PreferenceMap;
import com.lxy.whv.ui.adapter.BaseListAdapter;
import com.lxy.whv.util.DateUtils;
import com.lxy.whv.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

/**
 * Created by LXY on 2015/10/15.
 */
public class CompanyPostAdapter extends BaseListAdapter<CompanyPost> {

    // 计算距离使用
    PrettyTime prettyTime;

    public CompanyPostAdapter(Context ctx) {
        super(ctx);
        init();
    }

    public CompanyPostAdapter(Context ctx, List<CompanyPost> datas) {
        super(ctx, datas);
        init();
    }

    private void init() {
        prettyTime = new PrettyTime();
        PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(ctx);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO 同行样式
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discover_company_post_item, null, false);
        }
        final CompanyPost post = (CompanyPost) datas.get(position);
        TextView nameView = ViewHolder.findViewById(convertView, R.id.user_name_text);
        TextView planedTimeView = ViewHolder.findViewById(convertView, R.id.time_text);
        TextView postTimeView = ViewHolder.findViewById(convertView, R.id.posttime_text);
        TextView titleView = ViewHolder.findViewById(convertView, R.id.title_text);
        TextView destView = ViewHolder.findViewById(convertView, R.id.dest_text);
        ImageView avatarView = ViewHolder.findViewById(convertView, R.id.avatar_view);

        LeanchatUser user = (LeanchatUser) post.getPublisher();
//
        if (user != null) {
            //TODO 取不到?  查询需要include 但是会导致其他用户无法保存
//            user = CacheService.lookupUser(user.getObjectId());
            ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView,
                    com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
            LogUtils.d("userName = " + user.getUsername() + " avatar = "
                    + user.getAvatarUrl() + " id = " + user.getObjectId());
            nameView.setText(user.getUsername());
        }

        titleView.setText(post.getTitle());
        destView.setText(post.getDestination());

        Date date = post.getDateplanned();
        if (date != null) {
            LogUtils.d("date != null  true");
            String datePlanned = DateUtils.dateToStr(date, "yyyy-MM-dd");
            planedTimeView.setText(datePlanned);
        }
        LogUtils.e("!!!!!!date == null  true");
        Date updatedAt = post.getUpdatedAt();
        String prettyTimeStr = this.prettyTime.format(updatedAt);
        postTimeView.setText(" " + prettyTimeStr);
        return convertView;
    }
}
