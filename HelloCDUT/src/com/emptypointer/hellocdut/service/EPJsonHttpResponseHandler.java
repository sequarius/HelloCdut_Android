package com.emptypointer.hellocdut.service;

import android.content.Context;
import android.util.Log;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Sequarius on 2015/9/9.
 */
public class EPJsonHttpResponseHandler extends EPJsonHttpBaseResponseHandler {
    private static final String TAG = "EPJsonHttpResponse";

    public EPJsonHttpResponseHandler(Context mContext) {
        super(mContext);
    }

    public EPJsonHttpResponseHandler(Context mContext, boolean showWaitingDialog) {
        super(mContext, showWaitingDialog);
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        Log.i(TAG,"get Json Success responsecode="+statusCode+"..content=="+response.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }
}
