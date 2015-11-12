package com.lxy.whv.ui.discover.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.view.ViewHolder;
import com.lxy.whv.App;
import com.lxy.whv.R;
import com.lxy.whv.entity.avobject.PostComment;
import com.lxy.whv.ui.adapter.BaseListAdapter;
import com.lxy.whv.ui.contact.ContactPersonInfoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

/**
 * Created by wuming on 2015/10/31.
 */
public class PostCommentAdapter extends BaseListAdapter<PostComment> {

    PrettyTime prettyTime;
    Context ctx;

    public PostCommentAdapter(Context ctx) {
        super(ctx);
        this.ctx = ctx;
        init();
    }

    public PostCommentAdapter(Context ctx, List<PostComment> datas) {
        super(ctx, datas);
        this.ctx = ctx;
        init();
    }

    private void init() {
        prettyTime = new PrettyTime();
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO 同行样式
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discover_company_post_comment_item, null, false);
        }

        PostComment post = datas.get(position);
        final LeanchatUser user = (LeanchatUser) post.getCreator();

        TextView contentView = ViewHolder.findViewById(convertView, R.id.content_text);
//        TextView distanceView = ViewHolder.findViewById(convertView, R.id.distance_text);
        TextView loginTimeView = ViewHolder.findViewById(convertView, R.id.comment_time_text);
        ImageView avatarView = ViewHolder.findViewById(convertView, R.id.avatar_view);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactPersonInfoActivity.goPersonInfo(ctx, user.getObjectId());
            }
        });

        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), avatarView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);

        contentView.setText(post.getContent());
        Date updatedAt = post.getUpdatedAt();
        String prettyTimeStr = this.prettyTime.format(updatedAt);
        loginTimeView.setText(App.ctx.getString(R.string.company_post_comment_time) + prettyTimeStr);
        return convertView;
    }
}
