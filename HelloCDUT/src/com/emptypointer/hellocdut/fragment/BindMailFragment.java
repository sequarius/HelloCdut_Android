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
import android.nfc.Tag;
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
    private RefreshBtnStatusTask mRefreshBtnStatusTask;
    private EditText mETmail;
    private Button mBtnCommit;
    private Button mBtnGetToekn;
    private long mLastGetTokenTime;
    private static String PRE_KEY_LAST_GET_TIME = "bind_token_time";
    private static String PRE_KEY_BIND_EMAIL = "bind_email";
    private String mLastEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mLastGetTokenTime = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).getLong(PRE_KEY_LAST_GET_TIME, Integer.MAX_VALUE);
        mLastEmail = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).getString(PRE_KEY_BIND_EMAIL, "");
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
        mBtnGetToekn = (Button) view.findViewById(R.id.button_get);
        mBtnGetToekn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBindToken();
            }
        });
        mETmail.setText(mLastEmail);
        mRefreshBtnStatusTask = new RefreshBtnStatusTask();
        mRefreshBtnStatusTask.execute();

        return view;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"destryo"+mRefreshBtnStatusTask.getStatus());
        if (mRefreshBtnStatusTask.getStatus() != AsyncTask.Status.FINISHED) {
            mRefreshBtnStatusTask.isContinue=false;
            mRefreshBtnStatusTask.cancel(true);
        }
        super.onDestroy();
    }

    private void CommitRequest() {
        if (!StringChecker.isMail(mETmail.getText().toString())) {
            CommonUtils.customToast(R.string.str_message_wrong_email_format, getActivity(), true);
            return;
        }
//        new CommitTask().execute();
    }


    private void getBindToken() {
        if (!StringChecker.isMail(mETmail.getText().toString())) {
            CommonUtils.customToast(R.string.str_message_wrong_email_format, getActivity(), true);
            return;
        }
        getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).edit().putString(PRE_KEY_BIND_EMAIL, mETmail.getText().toString()).commit();
        new GetBindTokenTask().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }


//    private class CommitTask extends AsyncTask<Void, Integer, Boolean> {
//        private String mMessage;
//        private ProgressDialog mDialog;
//
//        @Override
//        protected void onPreExecute() {
//            mDialog = new ProgressDialog(getActivity());
//            mDialog.setMessage(getString(R.string.str_str_loading_binding));
//            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... param) {
//
//            // TODO Auto-generated method stub
//            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//            String userName = EPSecretService.encryptByPublic(EPApplication
//                    .getInstance().getUserName());
//            String token = EPSecretService.encryptByPublic(EPApplication
//                    .getInstance().getToken());
//
//            String mail = mETmail.getText().toString();
//            params.add(new BasicNameValuePair("user_login_token", token));
//            params.add(new BasicNameValuePair("user_name", userName));
//            params.add(new BasicNameValuePair("action", "bindEmail"));
//            params.add(new BasicNameValuePair("user_email", EPSecretService.encryptByPublic(mail)));
//
//            try {
//                String str = EPHttpService.customerPostString(
//                        GlobalVariables.SERVICE_HOST_BIND_SYSTEM, params);
//                Log.i(TAG, str);
//                JSONObject object = JSONObject.parseObject(str);
//                boolean result = object.getBooleanValue("result");
//                mMessage = object.getString("message");
//                return result;
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            // TODO Auto-generated method stub
//            mDialog.dismiss();
//            if (result) {
////				EPApplication.getInstance().setUser(
////						EPApplication.USER_STATUS_CERTIFICATE);
//                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT)
//                );
//                getActivity().finish();
//
//            } else {
//                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_LONG)
//                );
//                if (mMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
//                    Intent intent = new Intent(getActivity(),
//                            LoginActivity.class);
//                    EPApplication.getInstance().clearAcitivy();
//
//                    EPApplication.getInstance().logout();
//                    startActivity(intent);
//                }
//            }
//        }
//    }

    private class GetBindTokenTask extends AsyncTask<Void, Void, Boolean> {
        private String mMessage;
        private ProgressDialog mDialog;
        private String imEmail;


        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("正在尝试发送验证邮件");
            mDialog.setCanceledOnTouchOutside(false);
            imEmail = mETmail.getText().toString();
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... param) {
            Log.i(TAG, "INTO");
            // TODO Auto-generated method stub
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            String userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            String token = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getToken());


            params.add(new BasicNameValuePair("user_login_token", token));
            params.add(new BasicNameValuePair("user_name", userName));
            params.add(new BasicNameValuePair("action", "getBindEmailToken"));
            params.add(new BasicNameValuePair("user_email", EPSecretService.encryptByPublic(imEmail)));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_BIND_SYSTEM, params);
                Log.i(TAG, str);
                JSONObject object = JSONObject.parseObject(str);
                boolean result = object.getBooleanValue("result");
                mMessage = object.getString("message");
                if (result) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE);
                    long timeMillis = System.currentTimeMillis() + 1000 * 60;
                    if (preferences.edit().putLong(PRE_KEY_LAST_GET_TIME, timeMillis).commit()) {
                        mLastGetTokenTime = timeMillis;
                    }
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
            mDialog.dismiss();
            if (result) {
//				EPApplication.getInstance().setUser(
//						EPApplication.USER_STATUS_CERTIFICATE);
                CommonUtils.showCustomToast(Toast.makeText(getActivity(), mMessage, Toast.LENGTH_SHORT)
                );
                if (mRefreshBtnStatusTask.getStatus() == Status.FINISHED) {
                    mRefreshBtnStatusTask = new RefreshBtnStatusTask();
                }
                mRefreshBtnStatusTask.execute();
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

    private class RefreshBtnStatusTask extends AsyncTask<Void, Long, Boolean> {
        public boolean isContinue=true;
        @Override
        protected Boolean doInBackground(Void... params) {
            long timeDiffer = (mLastGetTokenTime - System.currentTimeMillis()) / 1000;
            while (timeDiffer < 60 && timeDiffer > 0&&isContinue) {
                publishProgress(timeDiffer);
                Log.i(TAG,"timediffer=-="+timeDiffer);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                timeDiffer = (mLastGetTokenTime - System.currentTimeMillis()) / 1000;

            }
            publishProgress(timeDiffer);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.i(TAG, "result===" + aBoolean);
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            if (values[0] <= 0) {
                mBtnGetToekn.setClickable(true);
                mBtnGetToekn.setEnabled(true);
                mBtnGetToekn.setText("发送验证码");
            } else {
                mBtnGetToekn.setClickable(false);
                mBtnGetToekn.setEnabled(false);

                mBtnGetToekn.setText(getString(R.string.str_format_last_time, values[0]));
            }
        }
    }

}
