package com.emptypointer.hellocdut.service;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Sequarius on 2015/9/9.
 */
public class EPJsonHttpBaseResponseHandler extends JsonHttpResponseHandler {

    private static final String TAG ="EPJsonHttpBaseResponse" ;
    private Context mContext;
    public boolean result=true;
    private boolean showWaitingDialog=false;
    private ProgressDialog mDialog;




    public EPJsonHttpBaseResponseHandler(Context mContext) {
        this.mContext = mContext;
    }

    public EPJsonHttpBaseResponseHandler(Context mContext,boolean showWaitingDialog) {
        this.mContext = mContext;
        this.showWaitingDialog=showWaitingDialog;
        if(showWaitingDialog){
            mDialog = new ProgressDialog(mContext);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        if (mContext!=null) {
            CommonUtils.customToast(mContext.getString(R.string.message_weak_internet) + ":" + statusCode, mContext, true);
        }
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        try {
            result=response.getBoolean("result");
            if(!result){
                String message = response.getString("message");
                CommonUtils.customToast(message,mContext,true);
                if (message.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                    Intent intent = new Intent(mContext,
                            LoginActivity.class);
                    EPApplication.getInstance().clearAcitivy();
                    EPApplication.getInstance().logout();
                    mContext.startActivity(intent);
                }
                return;
            }
            Iterator<String> iterator = response.keys();
            while(iterator.hasNext()){
                if(iterator.next().equals("message")){
                    CommonUtils.customToast(response.getString("message"),mContext,false);
                    break;
                }
            }
        } catch (JSONException e) {
            result=false;
            e.printStackTrace();
            CommonUtils.customToast("server response cannot be parse！", mContext, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(showWaitingDialog){
            mDialog.setMessage(mContext.getString(R.string.message_loading));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if(showWaitingDialog){
            mDialog.dismiss();
        }
    }
}
