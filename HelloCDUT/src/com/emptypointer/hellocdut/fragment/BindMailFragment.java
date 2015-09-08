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
import android.os.Handler;
import android.os.Message;
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
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

public class BindMailFragment extends Fragment {
    public static final String TAG = "BindMailFragment";
//    private RefreshBtnStatusTask mRefreshBtnStatusTask;
    private EditText mETmail,mEtToken;
    private Button mBtnCommit;
    private Button mBtnGetToekn;
    private long mLastGetTokenTime;
    private static String PRE_KEY_LAST_GET_TIME = "bind_token_time";
    private static String PRE_KEY_BIND_EMAIL = "bind_email";
    private String mLastEmail;
    private Handler handler;
    private boolean isActivityDestroy=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mLastGetTokenTime = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).getLong(PRE_KEY_LAST_GET_TIME, Integer.MAX_VALUE);
        mLastEmail = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).getString(PRE_KEY_BIND_EMAIL, "");
        View view = View.inflate(getActivity(), R.layout.fragment_bind_mail, null);
        mETmail = (EditText) view.findViewById(R.id.editText_email);
        mEtToken = (EditText) view.findViewById(R.id.editText_token);
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
//        mRefreshBtnStatusTask = new RefreshBtnStatusTask();
//        mRefreshBtnStatusTask.execute();
        handler=new getBtnHandeler();
        handler.post(runnable);
        return view;
    }

    @Override
    public void onDestroy() {
//        Log.i(TAG,"destryo"+mRefreshBtnStatusTask.getStatus());
//        if (mRefreshBtnStatusTask.getStatus() != AsyncTask.Status.FINISHED) {
//            mRefreshBtnStatusTask.isContinue=false;
//            mRefreshBtnStatusTask.cancel(true);
//        }
        isActivityDestroy=true;
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void CommitRequest() {
        if (!StringChecker.isMail(mETmail.getText().toString())) {
            CommonUtils.customToast(R.string.str_message_wrong_email_format, getActivity(), true);
            return;
        }
        new CommitTask().execute();
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


    private class CommitTask extends AsyncTask<Void, Integer, Boolean> {
        private String mMessage;
        private ProgressDialog mDialog;
        private String imMail;
        private String imToken;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(getString(R.string.str_str_loading_binding));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
            imMail = mETmail.getText().toString();
            imToken=mEtToken.getText().toString();
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
            params.add(new BasicNameValuePair("action", "bindEmail"));
            params.add(new BasicNameValuePair("active_token", EPSecretService.encryptByPublic(imToken)));

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
                UserInfo userInfo=UserInfo.getInstance(getActivity());
                userInfo.setMail(imMail);
                EPApplication.getInstance().setMAILsStatus(EPApplication.USER_STATUS_CERTIFICATE);
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
//                if (mRefreshBtnStatusTask.getStatus() == Status.FINISHED) {
//                    mRefreshBtnStatusTask = new RefreshBtnStatusTask();
//                }
//                mRefreshBtnStatusTask.execute();
                handler.post(runnable);

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

//    private class RefreshBtnStatusTask extends AsyncTask<Void, Long, Boolean> {
//        public boolean isContinue = true;
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            long timeDiffer = (mLastGetTokenTime - System.currentTimeMillis()) / 1000;
//            while (timeDiffer < 60 && timeDiffer > 0 && isContinue) {
//                publishProgress(timeDiffer);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                timeDiffer = (mLastGetTokenTime - System.currentTimeMillis()) / 1000;
//            }
//            publishProgress(timeDiffer);
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//        }
//
//        @Override
//        protected void onProgressUpdate(Long... values) {
//            super.onProgressUpdate(values);
//            if (values[0] <= 0) {
//                mBtnGetToekn.setClickable(true);
//                mBtnGetToekn.setEnabled(true);
//                mBtnGetToekn.setText("发送验证码");
//            } else {
//                mBtnGetToekn.setClickable(false);
//                mBtnGetToekn.setEnabled(false);
//
//                mBtnGetToekn.setText(getString(R.string.str_format_last_time, values[0]));
//            }
//        }
//    }



    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            long timeDiffer = (mLastGetTokenTime - System.currentTimeMillis()) / 1000;
            Message message=new Message();
            message.what=(int)timeDiffer;
            handler.sendMessage(message);
            if (timeDiffer < 60 && timeDiffer > 0&&!isActivityDestroy) {
                handler.postDelayed(this, 1000);
            }
        }
    };
    private class getBtnHandeler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isActivityDestroy) {
                if (msg.what<= 0){
                    mBtnGetToekn.setClickable(true);
                    mBtnGetToekn.setEnabled(true);
                    mBtnGetToekn.setText("发送验证码");
                } else {
                    mBtnGetToekn.setClickable(false);
                    mBtnGetToekn.setEnabled(false);
                    mBtnGetToekn.setText(getString(R.string.str_format_last_time, msg.what));
                }
            }
        }
    }

}
