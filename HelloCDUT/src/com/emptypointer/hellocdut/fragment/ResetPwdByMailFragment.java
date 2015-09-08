package com.emptypointer.hellocdut.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.emptypointer.hellocdut.R;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        requestParams.add("user_name", EPSecretService.encryptByPublic(mail));
        client.post(GlobalVariables.SERVICE_HOST, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

        });

    }

    private void commitTask() {
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

}
