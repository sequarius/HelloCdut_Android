package com.emptypointer.hellocdut.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.service.EPJsonHttpBaseResponseHandler;
import com.emptypointer.hellocdut.service.EPJsonHttpResponseHandler;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.widget.ClearableEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResetPwdByMailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResetPwdByMailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPwdByMailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ResetPwdByMailFragment";

    private long mLastGetTokenTime;
    private static String PRE_KEY_LAST_GET_TIME = "reset_token_time";
    private static String PRE_KEY_BIND_EMAIL = "reseet_email";

    // TODO: Rename and change types of parameters
    private Handler handler;

    private boolean isActivityDestroy=false;

    private AsyncHttpClient client;

    private OnFragmentInteractionListener mListener;
    private com.emptypointer.hellocdut.widget.ClearableEditText etMail;
    private com.emptypointer.hellocdut.widget.ClearableEditText etToken;
    private android.widget.Button buttonget;
    private com.emptypointer.hellocdut.widget.ClearableEditText etNewPwd;
    private android.widget.Button buttoncommit;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResetPwdByMailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPwdByMailFragment newInstance() {
        ResetPwdByMailFragment fragment = new ResetPwdByMailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ResetPwdByMailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client=new AsyncHttpClient();
    }

    @Override
    public void onDestroy() {
        isActivityDestroy=true;
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_pwd_by_mail, container, false);
        this.buttoncommit = (Button) view.findViewById(R.id.button_commit);
        buttoncommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitTask();
            }
        });
        this.etNewPwd = (ClearableEditText) view.findViewById(R.id.etNewPwd);
        this.buttonget = (Button) view.findViewById(R.id.button_get);
        buttonget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTokenTask();
            }
        });
        this.etToken = (ClearableEditText) view.findViewById(R.id.etToken);
        this.etMail = (ClearableEditText) view.findViewById(R.id.etMail);
        handler=new getBtnHandeler();
        handler.post(runnable);

        mLastGetTokenTime = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).getLong(PRE_KEY_LAST_GET_TIME, Integer.MAX_VALUE);
        String lastEmail = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).getString(PRE_KEY_BIND_EMAIL, "");
        etMail.setText(lastEmail);
        return view;
    }

    private void getTokenTask() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("action", "getResetUserPasswordTokenByEmail");
        String mail = etMail.getText().toString();
        if(!StringChecker.isMail(mail)){
            CommonUtils.customToast(R.string.str_message_wrong_email_format,getActivity(),true);
            return;
        }
        SharedPreferences preferences = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE);
        if (!preferences.edit().putString(PRE_KEY_BIND_EMAIL, mail).commit()) {
            return;
        }
        requestParams.add("user_email", EPSecretService.encryptByPublic(mail));
        client.post(GlobalVariables.SERVICE_HOST, requestParams, new EPJsonHttpResponseHandler(getActivity(),true) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());
                super.onSuccess(statusCode, headers, response);
                if(result){
                    SharedPreferences preferences = getActivity().getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE);
                    long timeMillis = System.currentTimeMillis() + 1000 * 60;
                    if (preferences.edit().putLong(PRE_KEY_LAST_GET_TIME, timeMillis).commit()) {
                        mLastGetTokenTime = timeMillis;
                    }
                    handler.post(runnable);
                }

            }

        });

    }

    private void commitTask() {
        RequestParams requestParams = new RequestParams();
        String mail = etMail.getText().toString();
        String token=etToken.getText().toString();
        String newPwd=etNewPwd.getText().toString();
        if(!StringChecker.isMail(mail)){
            CommonUtils.customToast(R.string.str_message_wrong_email_format,getActivity(),true);
            return;
        }
        if(token.length()!=4){
            CommonUtils.customToast(R.string.message_wrong_format_captcha,getActivity(),true);
            return;
        }
        if(!StringChecker.isLegalPassword(newPwd)){
            CommonUtils.customToast(R.string.message_wrong_password_toast,getActivity(),true);
            return;
        }
        requestParams.add("action", "resetUserPasswordByEmail");
        requestParams.add("user_email", EPSecretService.encryptByPublic(mail));
        requestParams.add("validate_code", EPSecretService.encryptByPublic(token));
        requestParams.add("new_password", EPSecretService.encryptByPublic(newPwd));
        client.post(GlobalVariables.SERVICE_HOST,requestParams,new EPJsonHttpBaseResponseHandler(getActivity(),true){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(result){
                    getActivity().finish();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class getBtnHandeler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isActivityDestroy) {
                if (msg.what<= 0){
                    buttonget.setClickable(true);
                    buttonget.setEnabled(true);
                    buttonget.setText("发送验证码");
                } else {
                    buttonget.setClickable(false);
                    buttonget.setEnabled(false);
                    buttonget.setText(getString(R.string.str_format_last_time, msg.what));
                }
            }
        }
    }

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

}
