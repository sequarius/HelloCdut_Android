package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class AccountManageActivity extends BaseActivity implements
        OnClickListener {
    public static final String ACTION_UNBIND_LIB = "unbindLib";
    public static final String ACTION_UNBIND_AAO = "unbindAAO";
    public static final String ACTION_UNBIND_CAMPUS_CARD = "unbindCampus";
    private Button mBtnUnbindAAO, mBtnUnBindLib, mBtnUnbindCampusCard;
    private TextView mTvStuID, mTvLibID, mTvCampusID,mtvMailAccount;
    private TextView mTvBindHintCampus, mTvBindHintStuID, mTvBindHintLib,
            mTvBindHintMail;
    private LinearLayout mLayoutAAO;
    private LinearLayout mLayoutCampus;
    private LinearLayout mLayoutMail;
    private LinearLayout mLayoutLib;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_ralative_account);
        mLayoutAAO = (LinearLayout) findViewById(R.id.layout_bind_aao);
        mLayoutCampus = (LinearLayout) findViewById(R.id.layout_bind_campus_card);
        mLayoutMail = (LinearLayout) findViewById(R.id.layout_bind_mail);
        mLayoutLib = (LinearLayout) findViewById(R.id.layout_bind_lib);

        mTvBindHintCampus = (TextView) findViewById(R.id.textView_bind_hint_campus_card);
        mTvBindHintStuID = (TextView) findViewById(R.id.textView_bind_hint_stu_id);
        mTvBindHintLib = (TextView) findViewById(R.id.textView_bind_hint_lib);
        mTvBindHintMail = (TextView) findViewById(R.id.textView_bind_hint_mail);

        mBtnUnbindAAO = (Button) findViewById(R.id.button_unbind_aao);
        mBtnUnBindLib = (Button) findViewById(R.id.button_unbind_lib);
        mBtnUnbindCampusCard = (Button) findViewById(R.id.button_unbind_campus_card);
        mLayoutAAO.setOnClickListener(this);
        mLayoutCampus.setOnClickListener(this);
        mLayoutMail.setOnClickListener(this);
        mLayoutLib.setOnClickListener(this);

        mTvStuID = (TextView) findViewById(R.id.textView_stu_id);
        mTvLibID = (TextView) findViewById(R.id.textView_lib_id);
        mTvCampusID = (TextView) findViewById(R.id.textView_campus_id);
        mtvMailAccount=(TextView)findViewById(R.id.textView_mail_account);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (EPApplication.getInstance().getUserStatus() == EPApplication.USER_STATUS_CERTIFICATE) {
            mBtnUnbindAAO.setVisibility(View.VISIBLE);
            mBtnUnbindAAO.setOnClickListener(this);
            mTvStuID.setVisibility(View.VISIBLE);
            mTvStuID.setText(UserInfo.getInstance(getApplicationContext())
                    .getStudentID());
            mTvBindHintStuID.setVisibility(View.VISIBLE);
            mLayoutAAO.setClickable(false);

        }
        if (EPApplication.getInstance().getUserLibStatus() == EPApplication.USER_STATUS_CERTIFICATE) {
            mBtnUnBindLib.setVisibility(View.VISIBLE);
            mBtnUnBindLib.setOnClickListener(this);
            mTvLibID.setVisibility(View.VISIBLE);
            mTvLibID.setText(UserInfo.getInstance(getApplicationContext())
                    .getStudentID());
            mTvBindHintLib.setVisibility(View.VISIBLE);
            mLayoutLib.setClickable(false);
        }
        if (EPApplication.getInstance().getUserCampusStatus() == EPApplication.USER_STATUS_CERTIFICATE) {
            mTvCampusID.setVisibility(View.VISIBLE);
            mTvCampusID.setText(UserInfo.getInstance(getApplicationContext())
                    .getStudentID());
            mBtnUnbindCampusCard.setVisibility(View.VISIBLE);
            mBtnUnbindCampusCard.setOnClickListener(this);
            mTvBindHintCampus.setVisibility(View.VISIBLE);
            mLayoutCampus.setClickable(false);

        }
        if (EPApplication.getInstance().getMailStatus() == EPApplication.USER_STATUS_CERTIFICATE) {
            mTvBindHintMail.setVisibility(View.VISIBLE);
            mtvMailAccount.setText(UserInfo.getInstance(getApplicationContext())
                    .getMail());
//            mBtnUnbindCampusCard.setVisibility(View.VISIBLE);
//            mBtnUnbindCampusCard.setOnClickListener(this);
            mTvBindHintMail.setVisibility(View.VISIBLE);
            mLayoutMail.setClickable(false);

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(GlobalVariables.ACTION_BIND);
        switch (v.getId()) {
            case R.id.button_unbind_aao:
                craateUnbindDialog(ACTION_UNBIND_AAO);
                break;
            case R.id.button_unbind_lib:
                craateUnbindDialog(ACTION_UNBIND_LIB);
                break;
            case R.id.button_unbind_campus_card:
                craateUnbindDialog(ACTION_UNBIND_CAMPUS_CARD);
                break;
            case R.id.layout_bind_aao:
                intent.putExtra(GlobalVariables.INTENT_EXTRA_BIND_MODE,
                        BindActivity.MODE_AAO);
                startActivity(intent);
                break;
            case R.id.layout_bind_campus_card:
                intent.putExtra(GlobalVariables.INTENT_EXTRA_BIND_MODE,
                        BindActivity.MODE_CAMPUS_CARD);
                startActivity(intent);
                break;
            case R.id.layout_bind_mail:
                intent.putExtra(GlobalVariables.INTENT_EXTRA_BIND_MODE,
                        BindActivity.MODE_MAIL);
                startActivity(intent);
                break;
            case R.id.layout_bind_lib:
                intent.putExtra(GlobalVariables.INTENT_EXTRA_BIND_MODE,
                        BindActivity.MODE_LIB);
                startActivity(intent);
            default:
                break;
        }
    }

    private void craateUnbindDialog(final String action) {
        // TODO Auto-generated method stub
        Builder dialog = new Builder(this);
        dialog.setTitle(getString(R.string.hint_dangerous_operatation));
        if (action.equals(ACTION_UNBIND_AAO)) {
            dialog.setMessage(R.string.message_unbind_aao);
        } else if (action.equals(ACTION_UNBIND_LIB)) {
            dialog.setMessage(R.string.message_unbind_lib);
        } else if (action.equals(ACTION_UNBIND_CAMPUS_CARD)) {
            dialog.setMessage(R.string.message_unbind_campus);
        }
        View contentView = View.inflate(this, R.layout.dialog_check_box, null);
        final CheckBox checkBox = (CheckBox) contentView
                .findViewById(R.id.checkbox);
        checkBox.setText(getString(R.string.str_known_unbind_aftermath));
        dialog.setView(contentView);
        dialog.setNegativeButton(R.string.str_return,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // try {
                        // Field field = dialog.getClass().getSuperclass()
                        // .getDeclaredField("mShowing");
                        // field.setAccessible(false);
                        // field.set(dialog, true);
                        // } catch (Exception e) {
                        // e.printStackTrace();
                        // }
                        dialog.dismiss();
                    }
                });
        dialog.setNeutralButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 通过反射设置mShow避免dialog dismiss
                        if (!checkBox.isChecked()) {
                            // try {
                            // Field field = dialog.getClass().getSuperclass()
                            // .getDeclaredField("mShowing");
                            // field.setAccessible(true);
                            // field.set(dialog, false);
                            // } catch (Exception e) {
                            // e.printStackTrace();
                            // }
                            CommonUtils.showCustomToast(Toast.makeText(
                                    getApplicationContext(),
                                    getString(R.string.message_wrong_donnot_accpet_checkbox),
                                    Toast.LENGTH_LONG));
                            return;
                        } else {
                            new NetTask().execute(action);
                        }

                    }
                });
        CommonUtils.dialogTitleLineColor(dialog.show());
    }

    private class NetTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog mDialog;
        private String mMessage;
        private String mAction;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(AccountManageActivity.this);
            mDialog.setMessage(getString(R.string.str_unbinding));
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... inputParams) {
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            mAction = inputParams[0];
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("action", mAction));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_UNBIND_SYSTEM, params);
                JSONObject jsonObject = JSONObject.parseObject(str);
                boolean result = jsonObject.getBooleanValue("result");
                mMessage = jsonObject.getString("message");
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mDialog.dismiss();
            if (result) {
                CommonUtils.showCustomToast(Toast.makeText(AccountManageActivity.this, mMessage,
                        Toast.LENGTH_SHORT));
                if (mAction.equals(ACTION_UNBIND_AAO)) {
                    EPApplication.getInstance().setUserStatus(
                            EPApplication.USER_STATUS_NORMAL);
                    mBtnUnbindAAO.setVisibility(View.GONE);
                    mTvBindHintStuID.setVisibility(View.GONE);
                    mTvStuID.setVisibility(View.GONE);
                    mLayoutAAO.setClickable(true);

                    UserInfo info = UserInfo
                            .getInstance(getApplicationContext());
                    info.setRealName(UserInfo.INITED_STRING);
                    info.setBirthDate(UserInfo.INITED_STRING);
                    info.setStudentID(UserInfo.INITED_STRING);
                    info.setInstituteName(UserInfo.INITED_STRING);
                    info.setMajorName(UserInfo.INITED_STRING);
                    info.setClassID(UserInfo.INITED_STRING);
                    info.setEntryYear(UserInfo.INITED_STRING);
                } else if (mAction.equals(ACTION_UNBIND_LIB)) {
                    EPApplication.getInstance().setUserLibStatus(
                            EPApplication.USER_STATUS_NORMAL);
                    mBtnUnBindLib.setVisibility(View.GONE);
                    mTvLibID.setVisibility(View.GONE);
                    mTvBindHintLib.setVisibility(View.GONE);
                    mLayoutLib.setClickable(true);
                } else if (mAction.equals(ACTION_UNBIND_CAMPUS_CARD)) {
                    EPApplication.getInstance().setUserCampusStatus(
                            EPApplication.USER_STATUS_NORMAL);
                    mBtnUnbindCampusCard.setVisibility(View.GONE);
                    mTvBindHintCampus.setVisibility(View.GONE);
                    mTvCampusID.setVisibility(View.GONE);
                    mLayoutCampus.setClickable(true);

                }
            } else {
                CommonUtils.showCustomToast(Toast.makeText(AccountManageActivity.this, mMessage,
                        Toast.LENGTH_LONG));
            }
        }

    }

}
