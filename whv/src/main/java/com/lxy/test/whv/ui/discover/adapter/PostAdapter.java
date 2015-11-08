package com.lxy.test.whv.ui.discover.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avoscloud.leanchatlib.view.ViewHolder;
import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.avobject.News;
import com.lxy.test.whv.ui.adapter.BaseListAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by LXY on 2015/10/15.
 */
public class PostAdapter extends BaseListAdapter<News> {

    int screenWidth;


    public PostAdapter(Context ctx) {
        super(ctx);
        init(ctx);
    }

    public PostAdapter(Context ctx, List<News> datas) {
        super(ctx, datas);
        init(ctx);
    }

    private void init(Context ctx) {

        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
//        prettyTime = new PrettyTime();
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discover_post_item, null, false);
            //screenWidth = convertView.getWidth();
        }
        News news = (News) datas.get(position);
        TextView nameView = ViewHolder.findViewById(convertView, R.id.post_title_tv);
        ImageView imageView = ViewHolder.findViewById(convertView, R.id.post_iv);

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = screenWidth;
        lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        imageView.setLayoutParams(lp);

        imageView.setMaxWidth(screenWidth);
        imageView.setMaxHeight(screenWidth * 5 / 9);

        nameView.setText(news.getTitle());
        String imgUrl = news.getImgurl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(imgUrl, imageView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
            nameView.setBackgroundColor(Color.parseColor("#90000000"));
            nameView.setTextColor(Color.WHITE);
        } else {
            imageView.setVisibility(View.GONE);
            nameView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            nameView.setTextColor(Color.BLACK);
        }
        return convertView;
    }
}
