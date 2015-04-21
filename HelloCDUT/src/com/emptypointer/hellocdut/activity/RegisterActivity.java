package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {
    private EditText mEtUserName;
    private EditText mEtPassWord;
    private EditText mEtPassWordConfirm;
    private Button mBtnRegist;
    private CheckBox mCbAgreement;
    private TextView mTvAgreement;
    private String mUserName;
    private String mPassWord;

    private static final String INTERFACE_REGISTER = "";
    public static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_register);

        mEtUserName = (EditText) findViewById(R.id.editText_user_name);
        mEtPassWord = (EditText) findViewById(R.id.editText_password);
        mEtPassWordConfirm = (EditText) findViewById(R.id.editText_password_2nd);
        mCbAgreement = (CheckBox) findViewById(R.id.checkBox_agreement);
        mTvAgreement = (TextView) findViewById(R.id.textView_agrement);
        mTvAgreement.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, AgreementActivity.class));
            }
        });


        mBtnRegist = (Button) findViewById(R.id.button_register);

        mBtnRegist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                register();
            }
        });

    }

    /**
     * 注册
     */
    private void register() {
        if (!mCbAgreement.isChecked()) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_donnot_accpet_agreement),
                    Toast.LENGTH_LONG));
            return;
        }

        mUserName = mEtUserName.getText().toString().trim();
        if (!(StringChecker.isLegalUserName(mUserName))) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_username_toast),
                    Toast.LENGTH_LONG));
            return;
        }
        mPassWord = mEtPassWord.getText().toString().trim();
        String str_passWordConfirm = mEtPassWordConfirm.getText().toString()
                .trim();

        if (!mPassWord.equals(str_passWordConfirm)) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_password_confirm_toast),
                    Toast.LENGTH_LONG));
            return;
        }

        if (!(StringChecker.isLegalPassword(mPassWord))) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_password_toast),
                    Toast.LENGTH_LONG));
            return;
        }

        new RegisterTask().execute();

    }

    private class RegisterTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(RegisterActivity.this);
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.setMessage(getString(R.string.str_regging));
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params1) {
            TelephonyManager tm = (TelephonyManager) RegisterActivity.this
                    .getSystemService(TELEPHONY_SERVICE);
            String _deviceID = EPSecretService.encryptByPublic(tm.getDeviceId());
            String _userName = EPSecretService.encryptByPublic(mUserName);
            String _passWord = EPSecretService.encryptByPublic(mPassWord);

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            params.add(new BasicNameValuePair("user_name", _userName));
            params.add(new BasicNameValuePair("user_password", _passWord));
            params.add(new BasicNameValuePair("user_device_code", _deviceID));
            params.add(new BasicNameValuePair("action", "registerUser"));
            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM + INTERFACE_REGISTER,
                        params);
                Log.i(TAG, str);
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
                EPApplication appliaction = EPApplication.getInstance();
                appliaction.setUserName(mUserName);
                appliaction.setPassWord(null);
                CommonUtils.showCustomToast(Toast.makeText(RegisterActivity.this,
                        mMessage,
                        Toast.LENGTH_SHORT));
                finish();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(RegisterActivity.this,
                        mMessage,
                        Toast.LENGTH_LONG));
            }
        }

    }

}
