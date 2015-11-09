package com.lxy.test.whv.ui.discover.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVGeoPoint;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.view.ViewHolder;
import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.avobject.Post;
import com.lxy.test.whv.service.PreferenceMap;
import com.lxy.test.whv.ui.adapter.BaseListAdapter;
import com.lxy.test.whv.util.DateUtils;
import com.lxy.test.whv.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

/**
 * Created by LXY on 2015/10/15.
 */
public class PostAdapter extends BaseListAdapter<Post> {

    // 计算距离使用
    private static final double EARTH_RADIUS = 6378137;
    PrettyTime prettyTime;
    AVGeoPoint location;

    public PostAdapter(Context ctx) {
        super(ctx);
        init();
    }

    public PostAdapter(Context ctx, List<Post> datas) {
        super(ctx, datas);
        init();
    }

    private void init() {
        prettyTime = new PrettyTime();
        PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(ctx);
        location = preferenceMap.getLocation();
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 距离：单位为米
     */
    public static double DistanceOfTwoPoints(double lat1, double lng1,
                                             double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO 同行样式
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discover_post_item, null, false);
        }
        final Post post = (Post) datas.get(position);
        TextView nameView = ViewHolder.findViewById(convertView, R.id.user_name_text);
        TextView contentView = ViewHolder.findViewById(convertView, R.id.content_text);
        TextView planedTimeView = ViewHolder.findViewById(convertView, R.id.time_text);
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
        contentView.setText(post.getContent());

        Date date = post.getDateplanned();
        if (date != null) {
            LogUtils.d("date != null  true");
            String datePlanned = DateUtils.dateToStr(date, "yyyy-MM-dd");
            planedTimeView.setText(datePlanned);
        }
        LogUtils.e("!!!!!!date == null  true");
//        Date updatedAt = post.getUpdatedAt();
//        String prettyTimeStr = this.prettyTime.format(updatedAt);
        return convertView;
    }
}
