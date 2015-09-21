package com.emptypointer.hellocdut.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.activity.QueryCampusCardActivity;
import com.emptypointer.hellocdut.activity.RegisterActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.domain.UserInfo;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BindBasicFragment extends Fragment {
    private static final String TAG = "BindBasicFragment";
    private EditText mEtUserName;
    private EditText mEtPassWord;
    private Button mButton;
    private String mUserName, mPassWord;
    private String bindAction;
    public static final String ACTION_BIND_AAO = "bindAAO";
    public static final String ACTION_BIND_LIB = "bindLib";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(), R.layout.fragment_bind_basic,
                null);
        mEtUserName = (EditText) view.findViewById(R.id.editText_user_name);
        mEtPassWord = (EditText) view.findViewById(R.id.editText_password);
        mButton = (Button) view.findViewById(R.id.button_bind);
        if (bindAction.equals(ACTION_BIND_LIB)) {
            mEtUserName.setHint(getString(R.string.hint_input_lib_account));
            mEtPassWord.setHint(getString(R.string.hint_input_lib_password));
        }
        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                bind();
            }
        });
        return view;
    }

    @SuppressLint("ValidFragment")
    public BindBasicFragment(String bindAction) {
        super();
        this.bindAction = bindAction;
    }

    public BindBasicFragment() {
        super();
    }

    private void bind() {
        mUserName = mEtUserName.getText().toString().trim();
        if (!StringChecker.isLegalStudentID(mUserName)) {
            CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                    getString(R.string.message_wrong_student_id_toast),
                    Toast.LENGTH_LONG));
            return;
        }
        mPassWord = mEtPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(mPassWord)) {
            CommonUtils.showCustomToast(Toast.makeText(getActivity(),
                    getString(R.string.message_wrong_empty_password_toast),
                    Toast.LENGTH_LONG));
            return;
        }

        new CommitTask().execute();

    }

    private class CommitTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mDialog;
        private String mMessage;
        private JSONObject mObject;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setCanceledOnTouchOutside(false);

            mDialog.setMessage(getString(R.string.str_str_loading_binding));
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params1) {

            String _userAccount = EPSecretService.encryptByPublic(mUserName);
            String _passWord = EPSecretService.encryptByPublic(mPassWord);
            String token = EPApplication.getInstance().getToken();
            String _token = EPSecretService.encryptByPublic(token);
            String _userName = EPSecretService.encryptByPublic(EPApplication
                    .getInstance().getUserName());
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

            params.add(new BasicNameValuePair("user_login_token", _token));
            params.add(new BasicNameValuePair("user_name", _userName));
            params.add(new BasicNameValuePair("account", _userAccount));
            params.add(new BasicNameValuePair("password", _passWord));
            params.add(new BasicNameValuePair("action", bindAction));

            try {
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_BIND_SYSTEM, params);
                Log.i("TAG","response=="+str);
                mObject = JSONObject.parseObject(str);
                boolean result = mObject.getBooleanValue("result");
                mMessage = mObject.getString("message");
                return result;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                mMessage = getString(R.string.message_weak_internet);
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            mDialog.dismiss();
            if (result) {
                Log.i(TAG, mObject.toString());
                if (bindAction.equals(ACTION_BIND_AAO)) {
                    EPApplication.getInstance().setUserStatus(
                            EPApplication.USER_STATUS_CERTIFICATE);
                    UserInfo info = UserInfo.getInstance(getActivity());
                    info.setRealName(mObject.getString("user_real_name"));
                    info.setGender(mObject.getIntValue("user_gender"));
                    info.setBirthDate(mObject.getString("user_birthdate"));
                    info.setStudentID(mObject.getString("user_stu_id"));
                    info.setInstituteName(mObject.getString("user_institute"));
                    info.setMajorName(mObject.getString("user_major"));
                    info.setClassID(mObject.getString("user_class_id"));
                    info.setEntryYear(mObject.getString("user_entrance_year"));

                } else if (bindAction.equals(ACTION_BIND_LIB)) {
                    EPApplication.getInstance().setUserLibStatus(
                            EPApplication.USER_STATUS_CERTIFICATE);

                }
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
