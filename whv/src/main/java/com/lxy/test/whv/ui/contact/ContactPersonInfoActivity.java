package com.lxy.test.whv.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.avoscloud.leanchatlib.utils.PhotoUtils;
import com.lxy.test.whv.R;
import com.lxy.test.whv.service.AddRequestManager;
import com.lxy.test.whv.service.CacheService;
import com.lxy.test.whv.service.event.ContactRefreshEvent;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.chat.ChatRoomActivity;
import com.lxy.test.whv.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 用户详情页，从对话详情页面和发现页面跳转过来
 */
public class ContactPersonInfoActivity extends BaseActivity implements OnClickListener {
    public static final String USER_ID = "userId";
    TextView usernameView, genderView;
    ImageView avatarView, avatarArrowView;
    LinearLayout allLayout;
    Button chatBtn, addFriendBtn, deleteBtn;
    RelativeLayout avatarLayout, genderLayout;

    String userId = "";
    LeanchatUser user;

    public static void goPersonInfo(Context ctx, String userId) {
        Intent intent = new Intent(ctx, ContactPersonInfoActivity.class);
        intent.putExtra(USER_ID, userId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= 14) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.contact_person_info_activity);
        initData();

        findView();
        initView();
    }

    private void initData() {
        userId = getIntent().getStringExtra(USER_ID);
        user = CacheService.lookupUser(userId);
    }

    private void findView() {
        allLayout = (LinearLayout) findViewById(R.id.all_layout);
        avatarView = (ImageView) findViewById(R.id.avatar_view);
        avatarArrowView = (ImageView) findViewById(R.id.avatar_arrow);
        usernameView = (TextView) findViewById(R.id.username_view);
        avatarLayout = (RelativeLayout) findViewById(R.id.head_layout);
        genderLayout = (RelativeLayout) findViewById(R.id.sex_layout);

        genderView = (TextView) findViewById(R.id.sexView);
        chatBtn = (Button) findViewById(R.id.chatBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
    }

    private void initView() {
        AVUser curUser = AVUser.getCurrentUser();
        if (curUser.equals(user)) {
            initActionBar(R.string.contact_personalInfo);
            avatarLayout.setOnClickListener(this);
            genderLayout.setOnClickListener(this);
            avatarArrowView.setVisibility(View.VISIBLE);
            chatBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            addFriendBtn.setVisibility(View.GONE);
        } else {
            initActionBar(R.string.contact_detailInfo);
            avatarArrowView.setVisibility(View.INVISIBLE);
            //TODO isFriend取不到朋友信息
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
        updateView(user);
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
                //TODO 删除好友
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
