package com.emptypointer.hellocdut.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.activity.LoginActivity;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class EPUpdateService {
    private EPApplication mApplication;
    private Context mContext;
    private String DOWNLOAD_FOLDER_NAME = "hello_cdut/apk";

    public EPUpdateService(EPApplication mApplication, Context mContext) {
        super();
        this.mApplication = mApplication;
        this.mContext = mContext;
    }

    /**
     * 前臺更新
     */
    public void cheakVersionFront() {

        new QueryFrontTask().execute(true);

    }

    /**
     * 後臺更新
     */
    public void cheakVersionBack() {

        new QueryOnBackTask().execute(false);

    }

    private class QueryFrontTask extends QueryOnBackTask {

        private ProgressDialog imDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            imDialog = new ProgressDialog(mContext);
            imDialog.setCanceledOnTouchOutside(false);
            ;
            imDialog.setMessage(mContext.getString(R.string.str_cheaking_update));
            imDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            imDialog.dismiss();
            super.onPostExecute(result);
        }

    }

    private class QueryOnBackTask extends AsyncTask<Boolean, Void, Boolean> {

        private JSONObject imJsonObject;
        private String imMessage;
        private boolean imDoToast;

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                Builder builder = new Builder(mContext);
                builder.setTitle(mContext.getString(
                        R.string.str_format_has_new_version,
                        imJsonObject.getString("version")));
                StringBuffer sb = new StringBuffer();
                sb.append(mContext.getString(R.string.str_update_detail))
                        .append("\n")
                        .append(imJsonObject.getString("update_details"))
                        .append("\n")
                        .append(mContext.getString(R.string.str_update_time))
                        .append("\n")
                        .append(imJsonObject.getString("update_time"));
                builder.setMessage(sb.toString());
                builder.setNegativeButton(
                        mContext.getString(R.string.str_update_delay),
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                            }
                        });
                builder.setPositiveButton(
                        mContext.getString(R.string.str_update_immediately),
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                downloadApp(
                                        imJsonObject.getString("client_url"),
                                        imJsonObject.getString("version"));
                                CommonUtils.showCustomToast(Toast.makeText(
                                        mContext,
                                        mContext.getString(R.string.str_on_start_updating),
                                        Toast.LENGTH_SHORT));
                            }
                        });
                CommonUtils.dialogTitleLineColor(builder.show());

            } else {
                if (imDoToast) {
                    CommonUtils.showCustomToast(Toast.makeText(mContext, imMessage, Toast.LENGTH_LONG)
                    );
                    if (imMessage.equals(GlobalVariables.ERRO_MESSAGE_RELOG)) {
                        Intent intent = new Intent(mContext,
                                LoginActivity.class);
                        EPApplication.getInstance().clearAcitivy();

                        EPApplication.getInstance().logout();
                        mContext.startActivity(intent);
                    }
                }
            }

        }

        @Override
        protected Boolean doInBackground(Boolean... params1) {
            imDoToast = params1[0];
            // TODO Auto-generated method stub

            try {

                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("user_name", EPSecretService
                        .encryptByPublic(mApplication.getUserName())));
                params.add(new BasicNameValuePair(
                        "user_login_token",
                        EPSecretService.encryptByPublic(mApplication.getToken())));
                String appVersion = getAppVersion(mContext);
                params.add(new BasicNameValuePair("version", appVersion));
                params.add(new BasicNameValuePair("client", "0"));
                String str = EPHttpService.customerPostString(
                        GlobalVariables.SERVICE_HOST_UPDATE, params);
                JSONObject obj = JSONObject.parseObject(str);
                boolean result = obj.getBooleanValue("result");
                if (result) {
                    imJsonObject = obj.getJSONObject("message");
                } else {
                    imMessage = obj.getString("message");
                }

                return result;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                imMessage = mContext.getString(R.string.message_weak_internet);

                return false;
            }

        }

    }

    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public void downloadApp(String url, String version) {
        // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
        if (!CommonUtils.isExitsSdcard()) {
            Intent intentByBrowser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            mContext.startActivity(intentByBrowser);
            return;
        }
        DownloadManager.Request req = new DownloadManager.Request(
                Uri.parse(url));

        // 通过setAllowedNetworkTypes方法可以设置允许在何种网络下下载，
        // 也可以使用setAllowedOverRoaming方法，它更加灵活
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE);

        // 此方法表示在下载过程中通知栏会一直显示该下载，在下载完成后仍然会显示，
        // 直到用户点击该通知或者消除该通知。还有其他参数可供选择
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 设置下载文件存放的路径，同样你可以选择以下方法存放在你想要的位置。
        // setDestinationUri
        // setDestinationInExternalPublicDir

        File folder = Environment
                .getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        // 先删除
        if (folder.exists()) {
            folder.delete();
        }

        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        req.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME,
                mContext.getString(R.string.str_app_file_name, version));
        // req.setDestinationInExternalPublicDir(mContext, DOWNLOAD_FOLDER_NAME,
        // mContext.getString(R.string.str_app_file_name, version));
        // 再通知欄顯示
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // req.setShowRunningNotification(true);
        // 设置一些基本显示信息
        req.setTitle(mContext.getString(R.string.str_format_app_name, version));
        req.setDescription(mContext.getString(R.string.str_updating_compelet,
                version));
        req.setMimeType("application/vnd.android.package-archive");

        // Ok go!
        DownloadManager dm = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = dm.enqueue(req);
        SharedPreferences preferences = mContext.getSharedPreferences(
                "setting", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putLong("download_id", downloadId);
        editor.commit();
    }

}
