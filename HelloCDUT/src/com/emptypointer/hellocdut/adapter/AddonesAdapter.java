package com.emptypointer.hellocdut.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.domain.Addone;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddonesAdapter extends BaseAdapter {
    private static final String TAG = "AddonesAdapter";
    private static final String DOWNLOAD_FOLDER_NAME = "hello_cdut/addones";
    private List<Addone> mAddones;
    private Context mContext;
    private DisplayImageOptions options;
    private List<String> mPackageNames;

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mAddones.size();
    }

    public AddonesAdapter(List<Addone> mAddones, Context mContext) {
        super();
        this.mAddones = mAddones;
        this.mContext = mContext;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.ic_error_loaded)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        List<PackageInfo> mApps = new ArrayList<PackageInfo>();
        PackageManager pManager = mContext.getPackageManager();
        mApps = pManager.getInstalledPackages(0);
        mPackageNames = new ArrayList<>();
        for (PackageInfo info : mApps) {
            String packageName = info.packageName;
            mPackageNames.add(packageName);
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mAddones.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.row_addone, null);
        }
        final Addone addone = mAddones.get(position);
        ((TextView) convertView.findViewById(R.id.textView_name))
                .setText(addone.getName());
        ((TextView) convertView.findViewById(R.id.textView_author))
                .setText(mContext.getString(R.string.str_format_author,
                        addone.getAuthor()));
        String updateTime = addone.getUpdateTime();
        ((TextView) convertView.findViewById(R.id.textView_update_time))
                .setText(mContext.getString(R.string.str_format_publish_time,
                        updateTime.substring(0, 10)));
        ((TextView) convertView.findViewById(R.id.textView_version))
                .setText(mContext.getString(R.string.str_format_adone_version,
                        addone.getVersion()));
        ((TextView) convertView.findViewById(R.id.textView_introduction))
                .setText(mContext.getString(R.string.str_format_introduction,
                        addone.getIntroduction()));
        final ImageView image = (ImageView) convertView
                .findViewById(R.id.imageView_avatar);

        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ImageLoader.getInstance().displayImage(addone.getIcUrl(),
                        image, options);
            }
        });

        Button button = (Button) convertView.findViewById(R.id.button_function);
        LinearLayout layout = (LinearLayout) convertView
                .findViewById(R.id.layout_list_item);
        // 如果已经安装
        if (hasInstall(addone.getPackageName())) {
            layout.setClickable(true);
            layout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(addone.getAction());

                    try {
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            });
            button.setText(R.string.uninst);
            button.setBackgroundDrawable((mContext.getResources()
                    .getDrawable(R.drawable.selector_btn_red_color)));
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        Uri packageURI = Uri.parse("package:"
                                + addone.getPackageName());

                        Intent uninstallIntent = new Intent(
                                Intent.ACTION_DELETE, packageURI);
                        mContext.startActivity(uninstallIntent);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            });
        } else {
            layout.setClickable(false);

            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (CommonUtils.isExitsSdcard()) {
                        // TODO Auto-generated method stub
                        downloadApp(addone.getAddoneUrl(), addone.getVersion(),
                                addone.getName());
                    } else {
                        CommonUtils.customToast(
                                R.string.message_wrong_unexsisted_sd_card,
                                mContext, true);
                    }
                }
            });
        }

        return convertView;
    }

    private boolean hasInstall(String packageName) {
        return mPackageNames.contains(packageName);
    }

    public void downloadApp(String url, String version, String fileName) {
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

        req.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, fileName
                + ".apk");
        // req.setDestinationInExternalPublicDir(mContext, DOWNLOAD_FOLDER_NAME,
        // mContext.getString(R.string.str_app_file_name, version));
        // 再通知欄顯示
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // req.setShowRunningNotification(true);
        // 设置一些基本显示信息
        req.setTitle(mContext.getString(R.string.str_on_dowanload_addone,
                fileName));
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
