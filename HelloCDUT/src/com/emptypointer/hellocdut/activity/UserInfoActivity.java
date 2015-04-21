package com.emptypointer.hellocdut.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.easemob.chat.EMChatManager;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.NetTask;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPHttpQueueService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

    private static final String USER_ENTRANCE_YEAR = "user_entrance_year";
    private static final String USER_CLASS_ID = "user_class_id";
    private static final String USER_MAJOR_ID = "user_major_id";
    private static final String USER_INSTITUTE_ID = "user_institute_id";
    private static final String USER_STU_NUM = "user_stu_num";
    private static final String USER_BIRTHDATE = "user_birthdate";
    private static final String USER_REAL_NAME = "user_real_name";
    public static final String USER_NICK_NAME = "user_nick_name";
    public static final String USER_MOTTO = "user_motto";
    public static final String RESULT_INTENT = "result";
    private static final int REQUEST_NICKNAME = 0xFF0;
    private static final int REQUEST_MOTTO = 0xFF1;

    private TextView tvUserName, tvNickName, tvMotto, tvLoveStatus,
            tvSexOrientation, tvRealName, tvGender, tvBirthDate,
            tvStundentNumber, tvInstitute, tvMajor, tvClass, tvEntranceDate;
    private UserInfo mInfo;

    private RelativeLayout mLayoutAvatar, mLayoutNickName, mLayoutMotto, mLayoutLoveStatus,
            mLayoutSexOrientation, mLayoutRealName, mLayoutBirthDate,
            mLayoutStudentID, mLayoutInstitute, mLayoutMajor,
            mLayoutEntranceDate, mLayoutClass;

    private ImageView mImageView;

    private LinearLayout mLayoutCertificate;
    private LinearLayout mLayoutUnCertificate;


    private String[] loveStatusTable;
    private String[] orientationTable;
    private String[] genderTable;
    private EPHttpQueueService mService;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        loveStatusTable = getResources().getStringArray(
                R.array.str_array_love_status);
        orientationTable = getResources().getStringArray(
                R.array.str_array_sex_orientation);
        genderTable = getResources().getStringArray(R.array.str_array_gender);
        mInfo = UserInfo.getInstance(this);
        setContentView(R.layout.activity_user_info);
        mLayoutAvatar = (RelativeLayout) findViewById(R.id.layout_avatar);
        tvUserName = (TextView) findViewById(R.id.textView_user_name);
        tvNickName = (TextView) findViewById(R.id.textView_nick_name);
        tvMotto = (TextView) findViewById(R.id.textView_motto);
        tvLoveStatus = (TextView) findViewById(R.id.textView_love_status);
        tvSexOrientation = (TextView) findViewById(R.id.textView_sex_orientation);
        tvRealName = (TextView) findViewById(R.id.textView_real_name);
        tvGender = (TextView) findViewById(R.id.textView_gender);
        tvBirthDate = (TextView) findViewById(R.id.textView_birth_date);
        tvStundentNumber = (TextView) findViewById(R.id.textView_student_number);
        tvInstitute = (TextView) findViewById(R.id.textView_institute);
        tvMajor = (TextView) findViewById(R.id.textView_major);
        tvClass = (TextView) findViewById(R.id.textView_class);
        tvEntranceDate = (TextView) findViewById(R.id.textView_entrance_date);

        mLayoutNickName = (RelativeLayout) findViewById(R.id.layout_nick_name);
        mLayoutMotto = (RelativeLayout) findViewById(R.id.layout_motto);
        mLayoutLoveStatus = (RelativeLayout) findViewById(R.id.layout_love_status);
        mLayoutSexOrientation = (RelativeLayout) findViewById(R.id.layout_sex_oriegin);

        mLayoutRealName = (RelativeLayout) findViewById(R.id.layout_real_name);
        mLayoutBirthDate = (RelativeLayout) findViewById(R.id.layout_birthdate);
        mLayoutStudentID = (RelativeLayout) findViewById(R.id.layout_stu_num);
        mLayoutInstitute = (RelativeLayout) findViewById(R.id.layout_institute);
        mLayoutMajor = (RelativeLayout) findViewById(R.id.layout_major);
        mLayoutClass = (RelativeLayout) findViewById(R.id.layout_class);
        mLayoutEntranceDate = (RelativeLayout) findViewById(R.id.layout_entrance_date);

        mImageView = (ImageView) findViewById(R.id.imageView_avatar);

        mLayoutAvatar.setOnClickListener(this);
        mLayoutNickName.setOnClickListener(this);
        mLayoutMotto.setOnClickListener(this);
        mLayoutLoveStatus.setOnClickListener(this);

        mLayoutSexOrientation.setOnClickListener(this);
        mLayoutRealName.setOnClickListener(this);
        mLayoutBirthDate.setOnClickListener(this);

        mLayoutStudentID.setOnClickListener(this);
        mLayoutInstitute.setOnClickListener(this);
        mLayoutMajor.setOnClickListener(this);
        mLayoutClass.setOnClickListener(this);

        mLayoutEntranceDate.setOnClickListener(this);

        tvUserName.setText(EPApplication.getInstance().getUserName());
        tvNickName.setText(EPApplication.getInstance().getNickName());

        tvMotto.setText(mInfo.getMetto());
        tvLoveStatus.setText(loveStatusTable[mInfo.getLoveStatus()]);
        tvSexOrientation.setText(orientationTable[mInfo.getSexOrientation()]);
        if (EPApplication.getInstance().getUserStatus() > EPApplication.USER_STATUS_NORMAL) {
            mLayoutCertificate = (LinearLayout) findViewById(R.id.layout_certificate);
            mLayoutCertificate.setVisibility(View.VISIBLE);
            tvRealName.setText(mInfo.getRealName());
            if (mInfo.getGender() > -1) {
                tvGender.setText(genderTable[mInfo.getGender()]);
            }
            tvBirthDate.setText(mInfo.getBirthDate());
            tvInstitute.setText(mInfo.getInstituteName());
            tvStundentNumber.setText(mInfo.getStudentID());
            tvMajor.setText(mInfo.getMajorName());
            tvClass.setText(mInfo.getClassID());
            tvEntranceDate.setText(mInfo.getEntryYear());

        } else {
            mLayoutUnCertificate = (LinearLayout) findViewById(R.id.layout_uncertificate);
            mLayoutUnCertificate.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.layout_avatar:
                Intent intent_avatar = new Intent(GlobalVariables.ACTION_UPLOAD_AVATAR);
                startActivity(intent_avatar);
                break;
            case R.id.layout_nick_name:
                Intent intent_nickname = new Intent(
                        GlobalVariables.ACTION_MODIFY_INFO);
                intent_nickname.putExtra(GlobalVariables.INTENT_EXTRA_MODIFY,
                        USER_NICK_NAME);
                startActivityForResult(intent_nickname, REQUEST_NICKNAME);
                break;
            case R.id.layout_motto:
                Intent intent_motto = new Intent(GlobalVariables.ACTION_MODIFY_INFO);
                intent_motto.putExtra(GlobalVariables.INTENT_EXTRA_MODIFY,
                        USER_MOTTO);
                startActivityForResult(intent_motto, REQUEST_MOTTO);
                break;
            case R.id.layout_love_status:
                createLoveStatusDialog();
                break;
            case R.id.layout_sex_oriegin:
                createSexOrientationDialog();
                break;
            case R.id.layout_real_name:
                CreatePermissionDialog(getString(R.string.str_real_name),
                        mInfo.getPermissionRealName(), USER_REAL_NAME);
                break;
            case R.id.layout_birthdate:
                CreatePermissionDialog(getString(R.string.str_birth_date),
                        mInfo.getPermissionBirthDate(), USER_BIRTHDATE);
                break;
            case R.id.layout_stu_num:
                CreatePermissionDialog(getString(R.string.str_student_number),
                        mInfo.getPermissionStudentID(), USER_STU_NUM);
                break;
            case R.id.layout_institute:
                CreatePermissionDialog(getString(R.string.str_institute),
                        mInfo.getPermissionInstitute(), USER_INSTITUTE_ID);
                break;
            case R.id.layout_major:
                CreatePermissionDialog(getString(R.string.str_major),
                        mInfo.getPermissionMajor(), USER_MAJOR_ID);
                break;
            case R.id.layout_class:
                CreatePermissionDialog(getString(R.string.str_class),
                        mInfo.getPermissionClass(), USER_CLASS_ID);
                break;
            case R.id.layout_entrance_date:
                CreatePermissionDialog(getString(R.string.str_entrance_date),
                        mInfo.getPermissionEntryYear(), USER_ENTRANCE_YEAR);
                break;
            default:
                break;
        }

    }

    /**
     * 创建单独的权限dialog
     *
     * @param title
     * @param mode
     * @param category
     */
    private void CreatePermissionDialog(final String title, int mode,
                                        final String category) {
        Builder dialog = new Builder(this);
        dialog.setTitle(getString(R.string.str_info_permission, title));
        dialog.setSingleChoiceItems(R.array.str_array_view_permission, mode,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        UpdateUserInfo(category, null, which);
                        dialog.dismiss();
                    }
                    // Log.i(filed, msg)
                });
        dialog.setNegativeButton(R.string.str_return,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        CommonUtils.dialogTitleLineColor(dialog.show());

    }

    /**
     * 恋爱状态dialog生成
     */
    private void createLoveStatusDialog() {
        final int permission = mInfo.getPermissionLoveStatus();
        final int value = mInfo.getLoveStatus();
        Builder dialog = new Builder(this);
        dialog.setTitle(getString(R.string.str_love_status));
        LinearLayout contentViewLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.layout_dialog_love_status, null);
        dialog.setView(contentViewLayout);
        final RadioGroup groupValue = (RadioGroup) contentViewLayout
                .findViewById(R.id.radioGroup_love_status);
        final RadioGroup groupPermission = (RadioGroup) contentViewLayout
                .findViewById(R.id.radioGroup_permission);
        RadioButton buttonSelf = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_self);
        RadioButton buttonSingle = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_single);
        RadioButton buttonInLove = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_in_love);
        switch (value) {
            case 0:
                buttonSelf.setChecked(true);

                break;
            case 1:
                buttonSingle.setChecked(true);
                break;
            case 2:
                buttonInLove.setChecked(true);
                break;
            default:
                break;
        }

        RadioButton buttonPermissionALL = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_all);
        RadioButton buttonPermissionFriend = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_friend);
        RadioButton buttonPermissionNone = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_none);
        switch (permission) {
            case 0:
                buttonPermissionALL.setChecked(true);

                break;
            case 1:
                buttonPermissionFriend.setChecked(true);
                break;
            case 2:
                buttonPermissionNone.setChecked(true);
                break;

            default:
                break;
        }
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        int valueCheckedID = groupValue
                                .getCheckedRadioButtonId();
                        int permissionCheckedID = groupPermission
                                .getCheckedRadioButtonId();
                        int valueChecked = -1;
                        int permissionChecked = -1;
                        switch (valueCheckedID) {
                            case R.id.radio_self:
                                valueChecked = 0;
                                break;
                            case R.id.radio_single:
                                valueChecked = 1;
                                break;
                            case R.id.radio_in_love:
                                valueChecked = 2;
                                break;
                            default:
                                break;
                        }
                        switch (permissionCheckedID) {
                            case R.id.radio_all:
                                permissionChecked = 0;
                                break;
                            case R.id.radio_friend:
                                permissionChecked = 1;
                                break;
                            case R.id.radio_none:
                                permissionChecked = 2;
                                break;
                            default:
                                break;
                        }
                        if (permissionChecked == -1
                                || valueChecked == -1
                                || (valueChecked == value && permissionChecked == permission)) {
                            return;
                        }
                        mInfo.setPermissionLoveStatus(permissionChecked);
                        mInfo.setLoveStatus(valueChecked);
                        UpdateUserInfo("user_love_status",
                                String.valueOf(valueChecked), permissionChecked);
                        tvLoveStatus.setText(loveStatusTable[valueChecked]);
                    }
                });
        dialog.setNegativeButton(R.string.str_return,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        CommonUtils.dialogTitleLineColor(dialog.show());
    }

    /**
     * 性取向dialog生成
     */
    private void createSexOrientationDialog() {
        final int permission = mInfo.getPermissionSexOrientation();
        final int value = mInfo.getSexOrientation();
        Builder dialog = new Builder(this);
        dialog.setTitle(getString(R.string.str_sex_orientation));
        LinearLayout contentViewLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.layout_dialog_sex_orientation, null);
        dialog.setView(contentViewLayout);
        final RadioGroup groupValue = (RadioGroup) contentViewLayout
                .findViewById(R.id.radioGroup_sex_orientation);
        final RadioGroup groupPermission = (RadioGroup) contentViewLayout
                .findViewById(R.id.radioGroup_permission);
        RadioButton buttonOppsitieSex = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_oppositie_sex);
        RadioButton buttonSameSex = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_same_sex);
        RadioButton buttonKeepAlone = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_keep_alone);
        RadioButton buttonDouleSex = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_double_sex);
        switch (value) {
            case 0:
                buttonOppsitieSex.setChecked(true);

                break;
            case 1:
                buttonSameSex.setChecked(true);
                break;
            case 2:
                buttonKeepAlone.setChecked(true);
                break;
            case 3:
                buttonDouleSex.setChecked(true);
                break;
            default:
                break;
        }

        RadioButton buttonPermissionALL = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_all);
        RadioButton buttonPermissionFriend = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_friend);
        RadioButton buttonPermissionNone = (RadioButton) contentViewLayout
                .findViewById(R.id.radio_none);
        switch (permission) {
            case 0:
                buttonPermissionALL.setChecked(true);

                break;
            case 1:
                buttonPermissionFriend.setChecked(true);
                break;
            case 2:
                buttonPermissionNone.setChecked(true);
                break;

            default:
                break;
        }
        dialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        int valueCheckedID = groupValue
                                .getCheckedRadioButtonId();
                        int permissionCheckedID = groupPermission
                                .getCheckedRadioButtonId();
                        int valueChecked = -1;
                        int permissionChecked = -1;
                        switch (valueCheckedID) {
                            case R.id.radio_oppositie_sex:
                                valueChecked = 0;
                                break;
                            case R.id.radio_same_sex:
                                valueChecked = 1;
                                break;
                            case R.id.radio_keep_alone:
                                valueChecked = 2;
                                break;
                            case R.id.radio_double_sex:
                                valueChecked = 3;
                                break;
                            default:
                                break;
                        }
                        switch (permissionCheckedID) {
                            case R.id.radio_all:
                                permissionChecked = 0;

                                break;
                            case R.id.radio_friend:
                                permissionChecked = 1;

                                break;
                            case R.id.radio_none:
                                permissionChecked = 2;

                                break;
                            default:
                                break;
                        }
                        if (permissionChecked == -1
                                || valueChecked == -1
                                || (valueChecked == value && permissionChecked == permission)) {
                            return;
                        }
                        mInfo.setPermissionSexOrientation(permissionChecked);
                        mInfo.setSexOrientation(valueChecked);
                        UpdateUserInfo("user_sex_orientation",
                                String.valueOf(valueChecked), permissionChecked);
                        tvSexOrientation
                                .setText(orientationTable[valueChecked]);
                    }
                });
        dialog.setNegativeButton(R.string.str_return,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        CommonUtils.dialogTitleLineColor(dialog.show());
    }

    public void UpdateUserInfo(String modifyCategory, String modifyValue,
                               int permission) {
        mService = EPHttpQueueService.getInstance(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("action", "modifyUserInfo");
        params.put("user_login_token", EPSecretService
                .encryptByPublic(EPApplication.getInstance().getToken()));
        params.put("user_name", EPSecretService.encryptByPublic(EPApplication
                .getInstance().getUserName()));
        params.put("modify_category", modifyCategory);
        if (modifyValue != null) {
            params.put("modify_value", modifyValue);
        }
        if (permission != -1) {
            params.put("modify_permission", String.valueOf(permission));
        }
        String finalParams = JSON.toJSONString(params);
        mService.saveRequest(new NetTask(
                GlobalVariables.SERVICE_HOST_USER_SYSTEM, finalParams));
        switch (modifyCategory) {
            case USER_REAL_NAME:
                mInfo.setPermissionRealName(permission);
                break;

            case USER_BIRTHDATE:
                mInfo.setPermissionBirthDate(permission);
                break;
            case USER_STU_NUM:
                mInfo.setPermissionStudentID(permission);
                break;
            case USER_INSTITUTE_ID:
                mInfo.setPermissionInstitute(permission);
                break;
            case USER_MAJOR_ID:
                mInfo.setPermissionMajor(permission);
                break;
            case USER_CLASS_ID:
                mInfo.setPermissionClass(permission);
                break;
            case USER_ENTRANCE_YEAR:
                mInfo.setPermissionEntryYear(permission);
                break;

            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        String pathName = getDir("user",
                Context.MODE_PRIVATE)
                + File.separator + EPApplication.getInstance().getUserName();
        File file = new File(pathName);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(pathName);
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, CoreService.class);
        startService(intent);
        super.onDestroy();
    }

    @Override
    /**
     * 修改用户名和资料的result回调
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        String result = data.getExtras().getString(RESULT_INTENT);
        if (requestCode == REQUEST_NICKNAME) {
            UpdateUserInfo(USER_NICK_NAME, result, -1);
            tvNickName.setText(result);
            EPApplication.getInstance().setNickName(result);
            EMChatManager.getInstance().updateCurrentUserNick(
                    EPApplication.getInstance().getNickName());
        } else if (requestCode == REQUEST_MOTTO) {
            UpdateUserInfo(USER_MOTTO, result, -1);
            tvMotto.setText(result);
            mInfo.setMetto(result);
        }
    }
}
