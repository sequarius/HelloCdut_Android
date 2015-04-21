package com.emptypointer.hellocdut.customer;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.CommonUtils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

public class EPUpdataBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadID = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        SharedPreferences sPreferences = context.getSharedPreferences(
                "setting", 0);

        long referneceID = sPreferences.getLong("download_id", Context.MODE_PRIVATE);

        if (referneceID == downloadID) {
            String serviceString = Context.DOWNLOAD_SERVICE;
            DownloadManager dManager = (DownloadManager) context
                    .getSystemService(serviceString);
            Intent install = new Intent(Intent.ACTION_VIEW);
            Uri downloadFileUri = dManager
                    .getUriForDownloadedFile(downloadID);
//			install.setAction("android.intent.action.VIEW");
//			install.addCategory("android.intent.category.DEFAULT");
            install.setDataAndType(downloadFileUri,
                    "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(install);
            } catch (Exception e) {
                CommonUtils.showCustomToast(Toast.makeText(context, context.getString(R.string.message_update_failed), Toast.LENGTH_LONG));
                e.printStackTrace();
            }
        }

    }


}
