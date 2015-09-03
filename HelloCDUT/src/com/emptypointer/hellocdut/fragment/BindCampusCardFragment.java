package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.activity.QueryCampusCardActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class BindCampusCardFragment extends Fragment {
    private EditText mEtUserName, mEtPassWord, mEtCaptcha;
    private Button mButtonCommit, mButtonRefresh;
    private ImageView mImgeView;
    private boolean isOnLoadCaptcha = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(),
                R.layout.fragment_bind_campus_card, null);
        mEtUserName = (EditText) view.findViewById(R.id.editText_user_name);
        mEtPassWord = (EditText) view.findViewById(R.id.editText_password);
        mEtCaptcha = (EditText) view.findViewById(R.id.editText_captcha);
        mButtonCommit = (Button) view.findViewById(R.id.button_bind);
        mButtonCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new CommitTask().execute();
            }
        });
        mButtonRefresh = (Button) view.findViewById(R.id.button_refresh);
        mButtonRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isOnLoadCaptcha) {
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                            getString(R.string.message_is_on_load),
                            Toast.LENGTH_LONG));
                } else {
                    new GetChaptchaTask().execute();
                }

            }
        });
        mImgeView = (ImageView) view.findViewById(R.id.imageView_captcha);
        new GetChaptchaTask().execute();
        return view;
    }

    private class GetChaptchaTask extends AsyncTask<Void, Void, Boolean> {
        private String mStrEncropy;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mButtonRefresh.setClickable(false);
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            isOnLoadCaptcha = true;

            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "bindCampus"));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_BIND_SYSTEM, params);
                JSONObject object = JSONObject.parseObject(str);
                boolean result = object.getBooleanValue("result");
                if (result) {
                    mStrEncropy = object.getString("captcha");

                } else {
                    mMessage = object.getString("message");
                }

                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                mMessage = getString(R.string.message_weak_internet);
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            if (result) {
                Bitmap bitmap = CommonUtils.decodeBase64(mStrEncropy);
                mImgeView.setImageBitmap(bitmap);
                isOnLoadCaptcha = false;
                mButtonRefresh.setClickable(true);

            } else {
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage,
                        Toast.LENGTH_LONG));
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(getActivity(),
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();

                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }
        }
    }

    private class CommitTask extends AsyncTask<Void, Void, Boolean> {
        private String mMessage;
        private ProgressDialog mDialog;
        private String account;
        private String password;
        private String captcha;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getString(R.string.str_str_loading_binding));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            account = mEtUserName.getText().toString();
            password = mEtPassWord.getText().toString();
            captcha = mEtCaptcha.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... param) {

            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());


            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "bindCampus"));
            params.add(new BasicNameValuePair("flag", "true"));
            params.add(new BasicNameValuePair("account", EPSecretService
                    .encryptByPublic(account)));
            params.add(new BasicNameValuePair("password", EPSecretService
                    .encryptByPublic(password)));
            params.add(new BasicNameValuePair("captcha", captcha));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_BIND_SYSTEM, params);

                JSONObject object = JSONObject.parseObject(str);
                boolean result = object.getBooleanValue("result");
                mMessage = object.getString("message");
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
            mDialog.dismiss();
            if (result) {
                EPApplication.getInstance().setUserCampusStatus(
                        EPApplication.USER_STATUS_CERTIFICATE);
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT)
                );
                getActivity().finish();

            } else {
                new GetChaptchaTask().execute();
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_LONG)
                );
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(getActivity(),
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();

                    EPApplication.getInstance().logout();
                    startActivity(intent);
                }
            }
        }
    }


}
