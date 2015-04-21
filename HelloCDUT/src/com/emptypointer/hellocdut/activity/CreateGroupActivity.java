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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CreateGroupActivity extends BaseActivity implements OnClickListener {
    private EditText mEtName, mEtIntroduction;
    private CheckBox mCbPubilicable, mCbInvitable;
    private LinearLayout mLayoutPublicable, mLayoutInvitable;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_create_group);
        mEtName = (EditText) findViewById(R.id.editText_group_name);
        mEtIntroduction = (EditText) findViewById(R.id.editText_group_introduction);
        mCbPubilicable = (CheckBox) findViewById(R.id.checkbox_public_group);
        mCbInvitable = (CheckBox) findViewById(R.id.cheakBox_invitation_by_menber);
        mLayoutPublicable = (LinearLayout) findViewById(R.id.layout_public_group);
        mLayoutInvitable = (LinearLayout) findViewById(R.id.layout_invitation_by_menber);
//		mCbPubilicable.setOnClickListener(this);
//		mCbInvitable.setOnClickListener(this);
        mLayoutPublicable.setOnClickListener(this);
        mLayoutInvitable.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.layout_public_group:
                if (mCbPubilicable.isChecked()) {
                    mCbPubilicable.setChecked(false);
                    mLayoutInvitable.setVisibility(View.VISIBLE);
                } else {
                    mCbPubilicable.setChecked(true);
                    mLayoutInvitable.setVisibility(View.GONE);
                }
                break;
            case R.id.layout_invitation_by_menber:
                if (mCbInvitable.isChecked()) {
                    mCbInvitable.setChecked(false);
                } else {
                    mCbInvitable.setChecked(true);
                }
                break;

            default:
                break;
        }

    }

    private void commitData() {
        String name = mEtName.getText().toString();
        String des = mEtIntroduction.getText().toString();
        boolean publicable = mCbPubilicable.isChecked();
        boolean invitable = mCbInvitable.isChecked();
        if (name.length() == 0) {
            CommonUtils.showCustomToast(Toast.makeText(CreateGroupActivity.this, R.string.message_wrong_group_length, Toast.LENGTH_LONG));
            return;
        }
//		params.add(new BasicNameValuePair("desc", paramin[0]));
//		params.add(new BasicNameValuePair("public", paramin[1]));
//		params.add(new BasicNameValuePair("groupname", paramin[2]));
//		params.add(new BasicNameValuePair("approval", paramin[3]));
        new CommitTask().execute(des, String.valueOf(publicable), name, String.valueOf(invitable));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == R.id.action_ok) {
            commitData();
        }
        ;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class CommitTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(CreateGroupActivity.this);
            mDialog.setMessage(getString(R.string.str_on_creating_group));
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialog.dismiss();
            if (result) {
                CommonUtils.showCustomToast(Toast.makeText(CreateGroupActivity.this, mMessage,
                        Toast.LENGTH_SHORT));
                CreateGroupActivity.this.finish();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(CreateGroupActivity.this, mMessage,
                        Toast.LENGTH_LONG));
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(CreateGroupActivity.this,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();
                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }
        }

        @Override
        protected Boolean doInBackground(String... paramin) {
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("action", "createGroups"));
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("desc", paramin[0]));
            params.add(new BasicNameValuePair("public", paramin[1]));
            params.add(new BasicNameValuePair("groupname", paramin[2]));
            params.add(new BasicNameValuePair("approval", paramin[3]));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);
                JSONObject object = JSONObject.parseObject(str);
                mMessage = object.getString("message");
                boolean result = object.getBooleanValue("result");
                if (result) {

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
