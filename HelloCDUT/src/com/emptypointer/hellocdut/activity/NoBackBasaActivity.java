package com.emptypointer.hellocdut.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;

public class NoBackBasaActivity extends FragmentActivity {
    private int mScreenWith;
    private int mScreenHeight;
    private float mDensity;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);

        EPApplication.getInstance().pushActivity(this);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mScreenWith = mDisplayMetrics.widthPixels;
        mScreenHeight = mDisplayMetrics.heightPixels;
        mDensity = mDisplayMetrics.density;


        // enable navigation bar tint
//		tintManager.setNavigationBarTintEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);

        tintManager
                .setTintColor(getResources().getColor(R.color.color_ep_actionbar_blue));
//		 set a custom navigation bar resource
//		 tintManager.setNavigationBarTintResource(R.drawable.bg_);
        // // set a custom status bar drawable
        // tintManager.setStatusBarTintDrawable(MyDrawable);

    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EPApplication.getInstance().removeActivity(this);
    }

    public int dp2px(float dp) {
        final float scale = mDensity;
        return (int) (dp * scale + 0.5f);
    }

    // 获取屏幕的宽度
    protected int getScreenWith() {
        return mScreenWith;
    }

    // 获取屏幕的高度
    protected int getScreenHeight() {
        return mScreenHeight;
    }

    public void createTObindDiglog(String BindType, Context context) {
        Builder builder = new Builder(context);
        builder.setTitle(R.string.str_need_to_bind);
        builder.setMessage(getString(R.string.message_need_to_bind, BindType));
        builder.setPositiveButton(R.string.str_bind_immediately,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(
                                GlobalVariables.ACTION_ACCOUNTMANAGE);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(R.string.str_bind_delay,
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        CommonUtils.dialogTitleLineColor(builder.show());
    }
}
