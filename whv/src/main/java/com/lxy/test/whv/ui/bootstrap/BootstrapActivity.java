package com.lxy.test.whv.ui.bootstrap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.test.whv.R;
import com.lxy.test.whv.ui.MainActivity;
import com.lxy.test.whv.ui.base_activity.BaseActivity;
import com.lxy.test.whv.ui.view.HeaderLayout;
import com.lxy.test.whv.util.LogUtils;
import com.lxy.test.whv.util.PathUtils;
import com.lxy.test.whv.util.PhotoUtils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wuming on 2015/10/22.
 */
public class BootstrapActivity extends BaseActivity {

    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    @InjectView(R.id.nearby_headerlayout)
    protected HeaderLayout headerLayout;

    @InjectView(R.id.rl_container)
    protected ScrollView containerLayout;

    LayoutInflater inflater = null;
    Button button_set_app_state;
    Button button_prefile_submit;
    RadioGroup rg_app_state;

    //TODO 进入界面更新 & 保存 information
    private int applyState = -1;
    private int gender = -1; // -1 unset 0 female 1 male
    private String wechatID = "";
    private String weiboID = "";
    private String lineID = "";
    private String aboutMe = "";

    private final int[] applicationStateButtonIndex = {R.id.radiobutton_want_to_know, R.id.radiobutton_preparing,
            R.id.radiobutton_submiting, R.id.radiobutton_granted, R.id.radiobutton_abroad,
            R.id.radiobutton_returned, R.id.radiobutton_pr};
    private final int[] applicationState = {0, 1, 2, 3, 4, 5, 6};
    private final int[] applicationStateTextId = {R.string.bootstrap_state_want_to_know, R.string.bootstrap_state_preparing,
            R.string.bootstrap_state_submiting, R.string.bootstrap_state_granted, R.string.bootstrap_state_abroad,
            R.string.bootstrap_state_returned, R.string.bootstrap_state_pr};

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootstrap_activity);
        ButterKnife.inject(this);

        inflater = this.getLayoutInflater();
        headerLayout.showTitle(R.string.bootstrap_title);
//        headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        //TODO 获取当前用户信息撒

        showApplicationStateSet();
    }

    private void showApplicationStateSet() {

        View appStateView = inflater.inflate(R.layout.bootstrap_application_state, containerLayout);
//        containerLayout.removeAllViews();
//        containerLayout.addView(appStateView);

        button_set_app_state = (Button) appStateView.findViewById(R.id.button_application_state_submit);
        button_set_app_state.setOnClickListener(listener);
        rg_app_state = (RadioGroup) appStateView.findViewById(R.id.radiogroup_application_state);

        rg_app_state.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                for (int i = 0; i < applicationStateButtonIndex.length; i++) {
                    if (radioButtonId == radioButtonId) {
                        applyState = applicationState[i];
                    }
                }
                //根据ID获取RadioButton的实例
                //更新文本内容，以符合选中项
            }
        });
    }

    private void updateUserInfo() {

        dialog = showSpinnerDialog();

        //TODO
        MainActivity.goMainActivityFromActivity(BootstrapActivity.this);
        finish();
    }

    private void showPerfileInfoSet() {
        containerLayout.removeAllViews();
        View perfileInfoSet = inflater.inflate(R.layout.bootstrap_perfile_set, containerLayout);

        button_prefile_submit = (Button) perfileInfoSet.findViewById(R.id.button_prefile_submit);
        button_prefile_submit.setOnClickListener(listener);

        LinearLayout profile_avatar_layout = (LinearLayout) perfileInfoSet.findViewById(R.id.profile_avatar_layout);
        profile_avatar_layout.setOnClickListener(listener);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        // 绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // 获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                // 根据ID获取RadioButton的实例
                if (radioButtonId == R.id.radioMale) {
                    gender = 1;
                } else {
                    gender = 0;
                }
            }
        });
    }

    //TODO 头像编辑
    public void onAvatarClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_application_state_submit:
                    if (applyState >= 0) {
                        showPerfileInfoSet();
                    } else {
                        toast(R.string.bootstrap_state_tip_select);
                    }
                    break;
                case R.id.button_prefile_submit:
                    updateUserInfo();
                    break;
                case R.id.profile_avatar_layout:
                    onAvatarClick();
                    break;

            }
        }
    };


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
                            //TODO 刷新
                            //BootstrapActivity.this.refresh();
                        } else {
                            Toast.makeText(BootstrapActivity.this, "更新失败", Toast.LENGTH_LONG).show();
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

    public void showSocialEditDialog(final View view) {
        final EditText et = new EditText(this);

        String title = "";
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.bootstrap_ll_social_weibo:
                title = "微博";
                et.setText(weiboID);
                break;
            case R.id.bootstrap_ll_social_line:
                title = "Line";
                et.setText(lineID);
                break;
            case R.id.bootstrap_ll_social_wechat:
                title = "WeChat";
                et.setText(wechatID);
                break;
        }

        new AlertDialog.Builder(this).setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {

                        } else {
                            onInfoSet(viewId, input);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void shoAboutEditDialog(final View view) {
        final EditText et = new EditText(this);
        et.setMaxLines(4);
        et.setText(aboutMe);
        String title = "个性签名";
        final int viewId = view.getId();
        new AlertDialog.Builder(this).setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (input.equals("")) {

                        } else {
                            onInfoSet(viewId, input);
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void onInfoSet(int viewId, String text) {
        switch (viewId) {
            case R.id.bootstrap_ll_social_weibo:
                weiboID = text;
                ((TextView) findViewById(R.id.profile_social_weibo)).setText(text);
                break;
            case R.id.bootstrap_ll_social_line:
                lineID = text;
                ((TextView) findViewById(R.id.profile_social_line)).setText(text);
                break;
            case R.id.bootstrap_ll_social_wechat:
                wechatID = text;
                ((TextView) findViewById(R.id.profile_social_wechat)).setText(text);
                break;
            case R.id.bootstrap_ll_aboutme:
                aboutMe = text;
                ((TextView) findViewById(R.id.profile_info)).setText(text);
                break;
        }
    }

}
