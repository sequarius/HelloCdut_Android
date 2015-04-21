package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.activity.ModifyPasswordActivity;
import com.emptypointer.hellocdut.activity.SettingActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingFeedBackFragment extends Fragment {
    private EditText mEtContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(),
                R.layout.fragment_setting_feedback, null);
        mEtContext = (EditText) view.findViewById(R.id.editText_content);
        view.findViewById(R.id.button_sent).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String context = mEtContext.getText().toString();
                if (context.length() < 10) {
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(), getString(R.string.message_wrong_feebback_context_length),
                            Toast.LENGTH_LONG));
                    return;
                }
                if (context.length() > 1000) {
                    CommonUtils.showCustomToast(Toast.makeText(getActivity(), getString(R.string.message_wrong_feebback_context_length_too_long),
                            Toast.LENGTH_LONG));
                    return;
                }
                new CommitTask().execute(context);
            }
        });
        return view;
    }

    private class CommitTask extends AsyncTask<String, Void, Boolean> {
        ProgressDialog mDialog;
        private String mMessage;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.setMessage(getString(R.string.str_feed_back));
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialog.dismiss();
            if (result) {
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage,
                        Toast.LENGTH_SHORT));
                getActivity().finish();
            } else {
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage,
                        Toast.LENGTH_LONG));
                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(getActivity(),
                            LoginActivity.class);
                    EPApplication.getInstance().logout();
                    EPApplication.getInstance().clearAcitivy();
                    startActivity(intent);
                }
            }
        }

        @Override
        protected Boolean doInBackground(String... paramin) {
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("action", "feedbackAdvice"));
            params.add(new BasicNameValuePair("user_name", EPSecretService
                    .encryptByPublic(EPApplication.getInstance().getUserName())));
            params.add(new BasicNameValuePair("user_login_token",
                    EPSecretService.encryptByPublic(EPApplication.getInstance()
                            .getToken())));
            params.add(new BasicNameValuePair("advice", paramin[0]));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_USER_SYSTEM, params);
                JSONObject object = JSONObject.parseObject(str);
                mMessage = object.getString("message");
                boolean result = object.getBooleanValue("result");
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
