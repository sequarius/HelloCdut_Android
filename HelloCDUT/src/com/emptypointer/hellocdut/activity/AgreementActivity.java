package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class AgreementActivity extends BaseActivity {
    private WebView mWebView;
    private PtrFrameLayout mPtrFrame;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_aao_news_detail);
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);


        mWebView = (WebView) findViewById(R.id.webView_show);
//		mWebView.loadDataWithBaseURL(null, mNoTittleHtml, "text/html",
//				HTTP.UTF_8, null);
        mWebView.setWebViewClient(new WebViewClient() {


            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mPtrFrame.refreshComplete();
            }
        });
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, dp2px(10), 0, dp2px(4));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);

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

        mPtrFrame.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPtrFrame.autoRefresh(true);
                mWebView.loadUrl(GlobalVariables.SERVICE_HOST_AGREEMENT);
            }
        }, 150);


    }


}
