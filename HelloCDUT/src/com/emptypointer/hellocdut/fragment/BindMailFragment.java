package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

public class BindMailFragment extends Fragment {
    public static final String TAG = "BindMailFragment";
    private EditText mETmail;
    private Button mBtnCommit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(), R.layout.fragment_bind_mail, null);
        mETmail = (EditText) view.findViewById(R.id.editText_email);
        mBtnCommit = (Button) view.findViewById(R.id.button_commit);
        mBtnCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommitRequest();
            }
        });
        return view;
    }

    private void CommitRequest() {
        if (!StringChecker.isMail(mETmail.getText().toString())) {
            CommonUtils.customToast(R.string.str_message_wrong_email_format, getActivity(), true);
            return;
        }
        new CommitTask().execute();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }


    private class CommitTask extends AsyncTask<Void, Void, Boolean> {
        private String mMessage;
        private ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getString(R.string.str_str_loading_binding));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... param) {

            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());

            String mail = mETmail.getText().toString();
            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "bindEmail"));
            params.add(new BasicNameValuePair("user_email", EPSecretService.encryptByPublic(mail)));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_BIND_SYSTEM, params);
                Log.i(TAG, str);
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
//				EPApplication.getInstance().setUser(
//						EPApplication.USER_STATUS_CERTIFICATE);
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT)
                );
                getActivity().finish();

            } else {
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
