package com.emptypointer.hellocdut.service;

import android.content.Context;

/**
 * Created by Sequarius on 2015/9/9.
 */
public class EPJsonHttpResponseHandler extends EPJsonHttpBaseResponseHandler {
    public EPJsonHttpResponseHandler(Context mContext) {
        super(mContext);
    }

    public EPJsonHttpResponseHandler(Context mContext, boolean showWaitingDialog) {
        super(mContext, showWaitingDialog);
    }
}
