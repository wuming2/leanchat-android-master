package com.lxy.whv.ui.bootstrap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.lxy.whv.R;
import com.lxy.whv.constant.Constant;
import com.lxy.whv.ui.MainActivity;
import com.lxy.whv.ui.base_activity.BaseActivity;
import com.lxy.whv.ui.view.DatePickerDialog;
import com.lxy.whv.util.LogUtils;
import com.lxy.whv.util.PathUtils;
import com.lxy.whv.util.PhotoUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wuming on 2015/10/22.
 */
public class BootstrapActivity extends BaseActivity {

    private static final int IMAGE_PICK_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    @InjectView(R.id.rl_container)
    protected ScrollView containerLayout;

    LayoutInflater inflater = null;
    Button button_set_app_state;
    Button button_prefile_submit;
    RadioGroup rg_app_state;

    private int applyState = -1;
    private int gender = -1; // -1 unset 0 female 1 male
    private String wechatID = "";
    private String weiboID = "";
    private String lineID = "";
    private String aboutMe = "";
    private String birthdate = "";

    ProgressDialog dialog;
    LeanchatUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootstrap_activity);
        ButterKnife.inject(this);

        inflater = this.getLayoutInflater();
        initActionBar("完善资料", false);
//        headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        showUserInfo();
        //TODO 获取当前用户信息撒
        showPerfileInfoSet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    //获取显示当前用户信息
    private void showUserInfo() {
        user = LeanchatUser.getCurrentUser();
        applyState = user.getInt("applyState");
        gender = user.getInt("gender");
        wechatID = user.getString("wechatID");
        weiboID = user.getString("weiboID");
        lineID = user.getString("lineID");
        aboutMe = user.getString("aboutMe");
        birthdate = user.getString("birthdate");
    }

    private void showApplicationStateSet() {

        containerLayout.removeAllViews();
        View appStateView = inflater.inflate(R.layout.bootstrap_application_state, containerLayout);
//        containerLayout.removeAllViews();
//        containerLayout.addView(appStateView);

        button_set_app_state = (Button) appStateView.findViewById(R.id.button_application_state_submit);
        button_set_app_state.setOnClickListener(listener);
        rg_app_state = (RadioGroup) appStateView.findViewById(R.id.radiogroup_application_state);

        if (applyState >= 0) {
//            int checkedId = applicationStateTextId[applyState];
//            rg_app_state.check(checkedId);
            ((RadioButton) rg_app_state.getChildAt(applyState)).setChecked(true);
        }

        rg_app_state.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                for (int i = 0; i < Constant.applicationStateButtonIndex.length; i++) {
                    if (radioButtonId == Constant.applicationStateButtonIndex[i]) {
                        applyState = Constant.applicationState[i];
                    }
                }
                //根据ID获取RadioButton的实例
                //更新文本内容，以符合选中项
            }
        });
    }

    private void updateUserInfo() {

        dialog = showSpinnerDialog();
        LeanchatUser user = LeanchatUser.getCurrentUser();
        user.put("gender", gender);
        user.put("wechatID", wechatID);
        user.put("weiboID", weiboID);
        user.put("lineID", lineID);
        user.put("aboutMe", aboutMe);
        user.put("birthdate", birthdate);
        user.put("applyState", applyState);
        user.updateUserInfo(new SaveCallback() {
            @Override
            public void done(AVException e) {

                if (e != null) {
                    e.printStackTrace();
                    toast("更新出错，请稍后重试 " + e.getLocalizedMessage());

                } else {
                    MainActivity.goMainActivityFromActivity(BootstrapActivity.this);
                    finish();
                }
            }
        });
    }

    private void showPerfileInfoSet() {
        containerLayout.removeAllViews();
        View perfileInfoSet = inflater.inflate(R.layout.bootstrap_perfile_set, containerLayout);

        button_prefile_submit = (Button) perfileInfoSet.findViewById(R.id.button_prefile_submit);
        button_prefile_submit.setOnClickListener(listener);

        ImageLoader.getInstance().displayImage(user.getAvatarUrl(), (ImageView) perfileInfoSet.findViewById(R.id.profile_avatar_view),
                com.avoscloud.leanchatlib.utils.PhotoUtils.avatarImageOptions);

        LinearLayout profile_avatar_layout = (LinearLayout) perfileInfoSet.findViewById(R.id.profile_avatar_layout);
        profile_avatar_layout.setOnClickListener(listener);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        if (gender == 1) {
            ((RadioButton) group.getChildAt(0)).setChecked(true);
        } else if (gender == 0) {
            ((RadioButton) group.getChildAt(1)).setChecked(true);
        }
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

        ((TextView) findViewById(R.id.profile_birthdate)).setText(birthdate);
        ((TextView) findViewById(R.id.profile_aboutme)).setText(aboutMe);
        ((TextView) findViewById(R.id.profile_social_wechat)).setText(wechatID);
        ((TextView) findViewById(R.id.profile_social_weibo)).setText(weiboID);
        ((TextView) findViewById(R.id.profile_social_line)).setText(lineID);
    }

    // 头像编辑
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
                        updateUserInfo();
                    } else {
                        toast(R.string.bootstrap_state_tip_select);
                    }
                    break;
                case R.id.button_prefile_submit:
                    showApplicationStateSet();
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
                        onInfoSet(viewId, input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    public void showBirthdayDatePacker(final View view) {
        Calendar c = Calendar.getInstance();
        int myear = 0;
        int mmonth = 0;
        int mday = 0;
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (!birthdate.isEmpty()) {
                Date date = fmt.parse(birthdate);
                myear = date.getYear() + 1900;
                mmonth = date.getMonth();
                mday = date.getDate();
                LogUtils.d("birthdate = " + birthdate + " myear" + myear + " " + mmonth + " " + mday);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (myear == 0) {
            myear = 1990;
        }
        if (mmonth == 0) {
            mmonth = c.get(Calendar.MONTH);
        }
        if (mday == 0) {
            mday = c.get(Calendar.DATE);
        }

        new DatePickerDialog(BootstrapActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int day) {
                String textString = String.format("%d-%d-%d", year,
                        month + 1, day);
                ((TextView) findViewById(R.id.profile_birthdate)).setText(textString);
                birthdate = textString;
            }
        }, myear, mmonth, mday, true).show();

    }

    public void showAboutEditDialog(final View view) {
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
                ((TextView) findViewById(R.id.profile_aboutme)).setText(text);
                break;
        }
    }

}
