package com.lxy.whv.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.lxy.whv.R;
import com.lxy.whv.constant.Constant;
import com.lxy.whv.service.AddRequestManager;
import com.lxy.whv.service.CacheService;
import com.lxy.whv.service.event.ContactRefreshEvent;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.ui.chat.ChatRoomActivity;
import com.lxy.whv.util.AgeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.greenrobot.event.EventBus;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 用户详情页，从对话详情页面和发现页面跳转过来
 */
public class ContactPersonInfoActivity extends BaseActivity implements OnClickListener {
    public static final String USER_ID = "userId";

    //    @InjectView(R.id.all_layout)
//    LinearLayout allLayout;
    @InjectView(R.id.avatar_view)
    ImageView avatarView;
    @InjectView(R.id.avatar_arrow)
    ImageView avatarArrowView;
    @InjectView(R.id.username_view)
    TextView usernameView;
    @InjectView(R.id.head_layout)
    RelativeLayout avatarLayout;

    @InjectView(R.id.chatBtn)
    Button chatBtn;
    @InjectView(R.id.deleteBtn)
    Button deleteBtn;
    @InjectView(R.id.addFriendBtn)
    Button addFriendBtn;

    @InjectView(R.id.contact_gender_tv)
    TextView tv_gender;
    @InjectView(R.id.contact_age_tv)
    TextView tv_age;
    @InjectView(R.id.contact_aboutme_tv)
    TextView tv_aboutme;
    @InjectView(R.id.contact_state_tv)
    TextView tv_state;
    @InjectView(R.id.contact_wechat_tv)
    TextView tv_wechat;
    @InjectView(R.id.contact_weibo_tv)
    TextView tv_weibo;
    @InjectView(R.id.contact_line_tv)
    TextView tv_line;

    @InjectView(R.id.layout_social_wechat)
    RelativeLayout rl_social_wechat;
    @InjectView(R.id.layout_social_weibo)
    RelativeLayout rl_social_weibo;
    @InjectView(R.id.layout_social_line)
    RelativeLayout rl_social_line;

    String userId = "";
    LeanchatUser user;

    private int applyState = -1;
    private int gender = -1; // -1 unset 0 female 1 male
    private String wechatID = "";
    private String weiboID = "";
    private String lineID = "";
    private String aboutMe = "";
    private String birthdate = "";


    public static void goPersonInfo(Context ctx, String userId) {
        Intent intent = new Intent(ctx, ContactPersonInfoActivity.class);
        intent.putExtra(USER_ID, userId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int currentApiVersion = Build.VERSION.SDK_INT;
//        if (currentApiVersion >= 14) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
        setContentView(R.layout.contact_person_info_activity);
        ButterKnife.inject(this);

        initData();
        initView();
    }

    private void initData() {
        userId = getIntent().getStringExtra(USER_ID);
        user = CacheService.lookupUser(userId);

        applyState = user.getInt("applyState");
        gender = user.getInt("gender");
        wechatID = user.getString("wechatID");
        weiboID = user.getString("weiboID");
        lineID = user.getString("lineID");
        aboutMe = user.getString("aboutMe");
        birthdate = user.getString("birthdate");
    }


    private void initView() {
        AVUser curUser = AVUser.getCurrentUser();

        if (gender >= 0) {
            tv_gender.setText(Constant.genderTextId[gender]);
        }
        int age = AgeUtils.getAgeByBirthday(birthdate);
        String ageStr = "未知";
        if (age > 0) {
            ageStr = String.valueOf(age);
        }
        tv_age.setText(ageStr);
        tv_aboutme.setText(aboutMe);
        if (applyState >= 0) {
            tv_state.setText(Constant.applicationStateTextId[applyState]);
        }

        if (curUser.equals(user)) {
            initActionBar(R.string.contact_personalInfo);
            avatarLayout.setOnClickListener(this);
            avatarArrowView.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            addFriendBtn.setVisibility(View.GONE);
            setSocialView(true);
        } else {
            initActionBar(R.string.contact_detailInfo);
            avatarArrowView.setVisibility(View.INVISIBLE);

            List<String> cacheFriends = CacheService.getFriendIds();
            boolean isFriend = cacheFriends.contains(user.getObjectId());
            if (isFriend) {
                chatBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
                chatBtn.setOnClickListener(this);
                deleteBtn.setOnClickListener(this);
                setSocialView(true);

            } else {
                chatBtn.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
                addFriendBtn.setVisibility(View.VISIBLE);
                addFriendBtn.setOnClickListener(this);
                setSocialView(false);
            }
        }
        updateView(user);
    }

    private void setSocialView(boolean show) {
        if (show) {
            if (weiboID != null && !weiboID.isEmpty()) {
                rl_social_weibo.setVisibility(View.VISIBLE);
                tv_weibo.setText(weiboID);
            } else {
                rl_social_weibo.setVisibility(View.GONE);
            }
            if (lineID != null && !lineID.isEmpty()) {
                rl_social_line.setVisibility(View.VISIBLE);
                tv_line.setText(lineID);
            } else {
                rl_social_line.setVisibility(View.GONE);
            }
            if (wechatID != null && !wechatID.isEmpty()) {
                rl_social_wechat.setVisibility(View.VISIBLE);
                tv_wechat.setText(wechatID);
            } else {
                rl_social_wechat.setVisibility(View.GONE);
            }
        } else {
            rl_social_wechat.setVisibility(View.GONE);
            rl_social_weibo.setVisibility(View.GONE);
            rl_social_line.setVisibility(View.GONE);
        }

    }

    private void updateView(AVUser user) {
        ImageLoader.getInstance().displayImage(((LeanchatUser) user).getAvatarUrl(), avatarView, PhotoUtils.avatarImageOptions);
        usernameView.setText(user.getUsername());
    }

    private void refresh() {
        //TODO 更新朋友列表
        List<String> cacheFriends = CacheService.getFriendIds();
        boolean isFriend = cacheFriends.contains(user.getObjectId());
        if (isFriend) {
            chatBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            chatBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
        } else {
            chatBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            addFriendBtn.setVisibility(View.VISIBLE);
            addFriendBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatBtn:// 发起聊天
                Intent intent = new Intent(ContactPersonInfoActivity.this, ChatRoomActivity.class);
                intent.putExtra(Constants.MEMBER_ID, userId);
                startActivity(intent);
                finish();
                break;
            case R.id.deleteBtn:// 发起聊天
                deleteFriend();
                break;
            case R.id.addFriendBtn:// 添加好友
                AddRequestManager.getInstance().createAddRequestInBackground(this, user);
                break;
        }
    }

    private void deleteFriend() {
        // TODO 单向删除，怎样双向删除呢?
        AVUser.getCurrentUser(LeanchatUser.class).removeFriend(user.getObjectId(), new SaveCallback() {
            @Override
            public void done(AVException e) {

                if (filterException(e)) {

                    CacheService.removeFriend(user);
                    refresh();
                    ContactRefreshEvent event = new ContactRefreshEvent();
                    EventBus.getDefault().post(event);
                } else {
                    toast("sorry,delete friends error. Try later.");
                    e.printStackTrace();
                }
            }
        });
    }
}
