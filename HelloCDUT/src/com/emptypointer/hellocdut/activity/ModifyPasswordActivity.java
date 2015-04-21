package com.emptypointer.hellocdut.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

public class ModifyPasswordActivity extends BaseActivity {
    private EditText mEtOldPwd, mEtNewPwd, mEtConfirmPwd;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_modify_password);
        mEtOldPwd = (EditText) findViewById(R.id.editText_old_password);
        mEtNewPwd = (EditText) findViewById(R.id.editText_new_password);
        mEtConfirmPwd = (EditText) findViewById(R.id.editText_password_2nd);
    }

    /**
     * 修改密码点击事件
     *
     * @param view
     */
    public void onSubmit(View view) {

        if (!EPApplication.getInstance().getPassWord()
                .equals(mEtOldPwd.getText().toString().trim())) {
            CommonUtils.showCustomToast(Toast.makeText(this, getString(R.string.message_wrong_password),
                    Toast.LENGTH_LONG));
            return;
        }
        String password = mEtNewPwd.getText().toString().trim();

        if (!password.equals(mEtConfirmPwd.getText().toString())) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_password_confirm_toast),
                    Toast.LENGTH_LONG));
            return;
        }

        if (!(StringChecker.isLegalPassword(password))) {
            CommonUtils.showCustomToast(Toast.makeText(this,
                    getString(R.string.message_wrong_password_toast),
                    Toast.LENGTH_LONG));
            return;
        }
        new ModifyPwd().execute(password);
    }

    private class ModifyPwd extends AsyncTask<String, Void, Boolean> {
        ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(ModifyPasswordActivity.this);
            mDialog.setMessage(getString(R.string.str_modify_password));
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialog.dismiss();
            if (result) {
                CommonUtils.showCustomToast(Toast.makeText(ModifyPasswordActivity.this, mMessage,
                        Toast.LENGTH_SHORT));
                ModifyPasswordActivity.this.finish();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(ModifyPasswordActivity.this, mMessage,
                        Toast.LENGTH_LONG));
            }
        }

        @Override
        protected Boolean doInBackground(String... paramin) {
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("action", "modifyUserPassword"));
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("user_new_password", EPSecretService.encryptByPublic(paramin[0])));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);
                JSONObject object = JSONObject.parseObject(str);
                mMessage = object.getString("message");
                boolean result = object.getBooleanValue("result");
                if (result) {
                    EPApplication.getInstance().setPassWord(
                            object.getString("user_password_hash"));
                    String returnV = EPSecretService.decryptByPublic(object.getString("user_password_hash"));
                }
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }
        }
    }
}
