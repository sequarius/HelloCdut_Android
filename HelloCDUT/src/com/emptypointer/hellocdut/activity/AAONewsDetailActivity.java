package com.emptypointer.hellocdut.activity;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.service.EPHttpService;
import com.emptypointer.hellocdut.service.EPShareService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class AAONewsDetailActivity extends BaseActivity {
    private static final String INIT_AAO_TIPS = "init_aao_tips";

    private WebView mWebView;

    private PtrFrameLayout mPtrFrame;

    private EPShareService mService;
    private String mOriginalHtml;
    private String mNoTittleHtml;
    private String mSuitScreenHtml;

    private String mNewsUrl;

    private final byte MODE_SUIT_SCREEN = 0;
    private final byte MODE_NO_TITTLE = 1;
    private final byte MODE_ORIGINAL = 2;

    private byte mReadMode = MODE_SUIT_SCREEN;

    private SharedPreferences mPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aao_detail, menu);
        return true;
    }

    /**
     * 改变浏览器的缩放配置
     */
    private void changeZoomMode(boolean canZoom) {

        WebSettings settings = mWebView.getSettings();
        if (canZoom) {
            settings.setSupportZoom(true);
            // settings.setDisplayZoomControls(true);
            settings.setBuiltInZoomControls(true);

        } else {
            mWebView.setInitialScale(147);
            settings.setSupportZoom(false);
            // settings.setBuiltInZoomControls(false);
            settings.setDisplayZoomControls(false);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_aao_tail_read_mode:
                choseReadMode();

                return true;
            case R.id.action_aao_open_with_sytem_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mNewsUrl));
                startActivity(intent);
                return true;
            case R.id.action_aao_detail_explain:
                showTips();

                return true;
            case R.id.action_aao_detail_share:
                mService.shareContent(
                        getIntent().getStringExtra(
                                GlobalVariables.INTENT_EXTRA_AAONEWS_TITLE),
                        getIntent().getStringExtra(
                                GlobalVariables.INTENT_EXTRA_AAONEWS_DETAIL), null,
                        null);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_aao_news_detail);
        mWebView = (WebView) findViewById(R.id.webView_show);
        mPtrFrame = (PtrFrameLayout) findViewById(R.id.material_style_ptr_frame);
        mService = new EPShareService(this);
        WebSettings settings = mWebView.getSettings();
        mPreferences = getSharedPreferences("read_setting", MODE_PRIVATE);
        mReadMode = (byte) mPreferences.getInt("read_mode", 0);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDisplayZoomControls(true);


        // mWebView.setWebViewClient(new WebViewClient() {
        //
        //
        // public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // view.loadUrl(url);
        // return true;
        // }
        //
        // @Override
        // public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // super.onPageStarted(view, url, favicon);
        //
        // }
        //
        // @Override
        // public void onPageFinished(WebView view, String url) {
        // super.onPageFinished(view, url);
        // mPtrFrame.refreshComplete();
        // }
        // });

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
                new LoadMoreDateTask().execute();
            }
        }, 150);

        if (mReadMode == 0) {
            changeZoomMode(false);
        } else {
            changeZoomMode(true);
        }

        if (!getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_INIT,
                MODE_PRIVATE).getBoolean(INIT_AAO_TIPS, false)) {
            showTips();
            getSharedPreferences(GlobalVariables.SHARED_PERFERENCR_INIT,
                    MODE_PRIVATE).edit().putBoolean(INIT_AAO_TIPS, true)
                    .commit();

        }
        // mWebView.loadUrl(url);
    }

    /*
     * 获取数据线程
     */
    private class LoadMoreDateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mNewsUrl = getIntent().getStringExtra(
                    GlobalVariables.INTENT_EXTRA_AAONEWS_DETAIL);
            try {
                mOriginalHtml = EPHttpService.customerGetString(mNewsUrl, null);
                int size = (int) (getScreenWith() * 0.65);
                String[] strs = mOriginalHtml.split("\\r\\n");
                for (int i = 0; i < strs.length; i++) {
                    if (strs[i].trim().equals("<div id=\"text\">")) {
                        mNoTittleHtml = "<br>" + strs[++i];
                        mSuitScreenHtml = "<div style=\"width:" + size
                                + "px\">" + mNoTittleHtml + "</div>";
                        // str="<div style=\"word-break:break-all\">"+"<br>"+strs[++i]+"</div>"
                        // ;
                        break;
                    }

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            mPtrFrame.refreshComplete();
            mReadMode = (byte) mPreferences.getInt("read_mode", 0);
            switch (mReadMode) {
                case MODE_SUIT_SCREEN:
                    mWebView.loadDataWithBaseURL(null, mSuitScreenHtml,
                            "text/html", HTTP.UTF_8, null);
                    break;
                case MODE_NO_TITTLE:
                    mWebView.loadDataWithBaseURL(null, mNoTittleHtml, "text/html",
                            HTTP.UTF_8, null);
                    break;
                case MODE_ORIGINAL:
                    mWebView.loadUrl(mNewsUrl);
                    break;

                default:
                    break;
            }

        }

        // @Override
        // protected void onPostExecute(Void.. pa) {
        // // TODO Auto-generated method stub
        // mWebView.loadDataWithBaseURL(null, str, "text/html", HTTP.UTF_8,
        // null);
        //
        // super.onPostExecute(str);
        // }
    }

    /*
     * 切换阅读模式
     */
    private void choseReadMode() {
        Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.str_read_mode));
        final String[] strs = getResources().getStringArray(
                R.array.str_array_aao_detail_chose_dialog);
        mReadMode = (byte) mPreferences.getInt("read_mode", 0);
        dialog.setSingleChoiceItems(strs, mReadMode, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                Editor editor = mPreferences.edit();
                switch (which) {
                    case 0:
                        editor.putInt("read_mode", 0);
                        mWebView.loadDataWithBaseURL(null, mSuitScreenHtml,
                                "text/html", HTTP.UTF_8, null);
                        changeZoomMode(false);
                        break;
                    case 1:
                        editor.putInt("read_mode", 1);
                        mWebView.loadDataWithBaseURL(null, mNoTittleHtml,
                                "text/html", HTTP.UTF_8, null);
                        changeZoomMode(true);
                        break;
                    case 2:
                        editor.putInt("read_mode", 2);
                        mWebView.loadDataWithBaseURL(null, mOriginalHtml,
                                "text/html", HTTP.UTF_8, null);
                        new LoadMoreDateTask().execute();
                        changeZoomMode(true);
                        break;

                    default:
                        break;
                }
                editor.commit();
                dialog.dismiss();
            }

        });
        dialog.create().show();
    }

    /**
     * 显示说明dialog
     */
    private boolean showTips() {
        Builder dialog = new Builder(this);
        dialog.setTitle(getString(R.string.str_explain));
        dialog.setMessage(getString(R.string.message_aao_detail));
        dialog.setPositiveButton(getString(R.string.str_knowed),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        CommonUtils.dialogTitleLineColor(dialog.show());
        return true;
    }
}
