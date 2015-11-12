package com.lxy.test.whv.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.R;
import com.lxy.test.whv.service.PushManager;
import com.lxy.test.whv.service.UpdateService;
import com.lxy.test.whv.ui.base_activity.BaseFragment;
import com.lxy.test.whv.ui.entry.EntryLoginActivity;
import com.lxy.test.whv.util.LogUtils;
import com.lxy.test.whv.util.PathUtils;
import com.lxy.test.whv.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by LXY on 2015/10/14.
 */
public class ProfileFragment extends BaseFragment {

    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    @InjectView(R.id.profile_avatar_view)
    ImageView avatarView;

    // username
    @InjectView(R.id.profile_username_view)
    TextView userNameView;

    ChatManager chatManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.inject(this, view);

        refresh();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        headerLayout.showTitle(R.string.profile_title);
        chatManager = ChatManager.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refresh() {

        LeanchatUser curUser = LeanchatUser.getCurrentUser();
        LogUtils.d("refresh getAvatarUrl= " + curUser.getAvatarUrl());
        userNameView.setText(curUser.getUsername());
        if (curUser.getAvatarUrl() != null && !curUser.getAvatarUrl().equalsIgnoreCase("null")) {
            ImageLoader.getInstance().displayImage(curUser.getAvatarUrl(), avatarView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
        }
    }

    @OnClick(R.id.profile_avatar_layout)
    public void onAvatarClick() {
        Intent intent = new Intent(this.getActivity(), ProfileEditActivity.class);
        this.getActivity().startActivity(intent);
    }

    @OnClick(R.id.profile_about)
    public void onTrackingClick() {
        Intent i = new Intent(this.getActivity(), AboutActivity.class);
        this.startActivity(i);
    }

    @OnClick(R.id.profile_notifysetting_view)
    public void onNotifySettingClick() {
        Intent intent = new Intent(ctx, ProfileNotifySettingActivity.class);
        ctx.startActivity(intent);
    }


    @OnClick(R.id.profile_logout_btn)
    public void onLogoutClick() {
        chatManager.closeWithCallback(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
            }
        });
        PushManager.getInstance().unsubscribeCurrentUserChannel();
        LeanchatUser.logOut();
        getActivity().finish();
        Intent intent = new Intent(ctx, EntryLoginActivity.class);
        ctx.startActivity(intent);
    }

    @OnClick(R.id.profile_checkupdate_view)
    public void onCheckUpdateClick(View view) {
        UpdateService updateService = UpdateService.getInstance(getActivity());
        updateService.showSureUpdateDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("onActivityResult result = " + resultCode + " code = " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                //TODO 大图片保存
                startImageCrop(uri, 200, 200, CROP_REQUEST);
            } else if (requestCode == CROP_REQUEST) {
                final String path = saveCropAvatar(data);
//                ImageLoader.getInstance().displayImage(path, avatarView, com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);
                LeanchatUser user = LeanchatUser.getCurrentUser();
                //TODO 删除历史图片
                user.saveAvatar(path, new SaveCallback() {

                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            ProfileFragment.this.refresh();
                        } else {
                            Toast.makeText(ProfileFragment.this.getActivity(), "更新失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                LogUtils.d("path = " + path);
            }
        }
    }

    public Uri startImageCrop(Uri uri, int outputX, int outputY,
                              int requestCode) {
        Intent intent = null;
        intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        String outputPath = PathUtils.getAvatarTmpPath();
        Uri outputUri = Uri.fromFile(new File(outputPath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false); // face detection
        startActivityForResult(intent, requestCode);
        return outputUri;
    }

    private String saveCropAvatar(Intent data) {
        Bundle extras = data.getExtras();
        String path = null;
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                bitmap = PhotoUtils.toRoundCorner(bitmap, 10);
                path = PathUtils.getAvatarCropPath();
                PhotoUtils.saveBitmap(path, bitmap);
                if (bitmap != null && bitmap.isRecycled() == false) {
                    bitmap.recycle();
                }
            }
        }
        return path;
    }

}
