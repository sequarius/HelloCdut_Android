package com.emptypointer.hellocdut.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.emptypointer.hellocdut.R;

import org.apache.http.protocol.HTTP;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class QueryNationalExamActivity extends BaseActivity {
    private static final String TAG = "QueryNationalExam" ;
    private WebView mWebView;

    private PtrFrameLayout mPtrFrame;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_query_national_exam);
        mWebView = (WebView) findViewById(R.id.webView_show);
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);
        WebSettings settings = mWebView.getSettings();
//        settings.set
        settings.setUseWideViewPort(false);
//        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setDisplayZoomControls(false);

        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(10), 0, dp2px(4));
        header.setPtrFrameLayout(mPtrFrame);

        mWebView.clearCache(true);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        mPtrFrame.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPtrFrame.autoRefresh(true);
            }
        }, 150);

        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame,
                                             View content, View header) {

                return false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG,"progress=="+newProgress);
                if (newProgress == 100) {
                    mPtrFrame.refreshComplete();
                }
            }


        });
        mWebView.loadUrl("http://www.hellocdut.com/h5/");
//        mWebView.loadDataWithBaseURL("http://www.hellocdut.com/h5/", null,
//                "text/html", HTTP.UTF_8, null);
//        mWebView.loadDataWithBaseURL();

    }

}
