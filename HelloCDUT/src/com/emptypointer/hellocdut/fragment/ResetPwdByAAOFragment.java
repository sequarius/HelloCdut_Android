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
import com.emptypointer.hellocdut.service.EPJsonHttpBaseResponseHandler;
import com.emptypointer.hellocdut.service.EPJsonHttpResponseHandler;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.widget.ClearableEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResetPwdByAAOFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResetPwdByAAOFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPwdByAAOFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ResetPwdByAAOFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private com.emptypointer.hellocdut.widget.ClearableEditText etUsername;
    private com.emptypointer.hellocdut.widget.ClearableEditText etPwd;
    private com.emptypointer.hellocdut.widget.ClearableEditText etNewPwd;
    private android.widget.Button buttonbind;
    private String stuID;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResetPwdByAAOFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPwdByAAOFragment newInstance() {
        ResetPwdByAAOFragment fragment = new ResetPwdByAAOFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ResetPwdByAAOFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_retrieve_by_aao, container, false);
        this.buttonbind = (Button) view.findViewById(R.id.button_bind);
        this.etNewPwd = (ClearableEditText) view.findViewById(R.id.etNewPwd);
        this.etPwd = (ClearableEditText) view.findViewById(R.id.etPwd);
        this.etUsername = (ClearableEditText) view.findViewById(R.id.etUsername);
        buttonbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });
        return view;
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

    private void loadDataFromServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("action", "resetUserPasswordByAAO");
        requestParams.add("aao_password", EPSecretService.encryptByPublic(etPwd.getText().toString()));
        stuID = etUsername.getText().toString();
        if(!StringChecker.isLegalStudentID(stuID)){
            CommonUtils.customToast(R.string.message_wrong_student_id_toast,getActivity(),true);
            return;
        }
        String account = EPSecretService.encryptByPublic(stuID);
        Log.d(TAG, account);
        requestParams.add("aao_account", account);
        String newPwd = etNewPwd.getText().toString();
        if(!StringChecker.isLegalPassword(newPwd)){
            CommonUtils.customToast(R.string.message_wrong_password_toast,getActivity(),true);
            return;
        }
        requestParams.add("new_password", EPSecretService.encryptByPublic(newPwd));
        client.post(GlobalVariables.SERVICE_HOST_ADDONES, requestParams, new EPJsonHttpResponseHandler(getActivity(),true){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, response.toString());
                if(result) {
                    getActivity().finish();
                }
            }
        });
    }

}
