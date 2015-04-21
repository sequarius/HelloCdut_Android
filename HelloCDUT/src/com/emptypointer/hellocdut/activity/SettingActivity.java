package com.emptypointer.hellocdut.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class SettingActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = "SettingActivity";
    RelativeLayout mLayoutMessage, mLayoutChat, mLayoutSlience, mLayoutAccount,
            mLayoutFeedBack, mLayoutabout;
    Button mButton;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_setting);
        mButton = (Button) findViewById(R.id.button_quit);
        mLayoutMessage = (RelativeLayout) findViewById(R.id.layout_message_and_notify_setting);
        mLayoutChat = (RelativeLayout) findViewById(R.id.layout_chat_setting);
        mLayoutSlience = (RelativeLayout) findViewById(R.id.layout_scilence_setting);
        mLayoutAccount = (RelativeLayout) findViewById(R.id.layout_account_setting);
        mLayoutFeedBack = (RelativeLayout) findViewById(R.id.layout_feed_back);
        mLayoutabout = (RelativeLayout) findViewById(R.id.layout_about);
        mButton.setOnClickListener(this);
        mLayoutMessage.setOnClickListener(this);
        mLayoutChat.setOnClickListener(this);
        mLayoutSlience.setOnClickListener(this);
        mLayoutAccount.setOnClickListener(this);
        mLayoutFeedBack.setOnClickListener(this);
        mLayoutabout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() != R.id.button_quit
                && v.getId() != R.id.layout_quit_account
                && v.getId() != R.id.layout_quit_app) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(GlobalVariables.ACTION_SETTING_DETAIL);
            switch (v.getId()) {
                case R.id.layout_message_and_notify_setting:
                    intent.putExtra(GlobalVariables.INTENT_EXTRA_SETTINGDETAIL,
                            SettingDetailActivity.MODE_MESSAGE);
                    break;
                case R.id.layout_chat_setting:
                    intent.putExtra(GlobalVariables.INTENT_EXTRA_SETTINGDETAIL,
                            SettingDetailActivity.MODE_CHAT);

                    break;
                case R.id.layout_scilence_setting:
                    intent.putExtra(GlobalVariables.INTENT_EXTRA_SETTINGDETAIL,
                            SettingDetailActivity.MODE_SILENCE);

                    break;
                case R.id.layout_account_setting:
                    intent.putExtra(GlobalVariables.INTENT_EXTRA_SETTINGDETAIL,
                            SettingDetailActivity.MODE_ACCOUNT);

                    break;
                case R.id.layout_feed_back:
                    intent.putExtra(GlobalVariables.INTENT_EXTRA_SETTINGDETAIL,
                            SettingDetailActivity.MODE_FEEDBACK);

                    break;
                case R.id.layout_about:
                    intent.putExtra(GlobalVariables.INTENT_EXTRA_SETTINGDETAIL,
                            SettingDetailActivity.MODE_ABOUT);

                    break;

                default:
                    return;

            }
            startActivity(intent);
        } else {
            switch (v.getId()) {
                case R.id.button_quit:
                    createQuitDialog();
                    break;
                case R.id.layout_quit_account:
                    mDialog.dismiss();
                    EPApplication.getInstance().logout();
                    SettingActivity.this.finish();
                    break;
                case R.id.layout_quit_app:

                    break;

                default:
                    break;
            }
        }
    }

    private void createQuitDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.str_quit);
        // View contentView = View.inflate(this, R.layout.diaglog_quit_app,
        // null);
        // LinearLayout layoutQuitApp = (LinearLayout) contentView
        // .findViewById(R.id.layout_quit_app);
        // LinearLayout layoutQuitAcount = (LinearLayout) contentView
        // .findViewById(R.id.layout_quit_account);
        // layoutQuitAcount.setOnClickListener(this);
        // layoutQuitApp.setOnClickListener(this);
        // builder.setView(contentView);
        // builder.setPositiveButton(R.string.cancel,
        // new DialogInterface.OnClickListener() {
        //
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // // TODO Auto-generated method stub
        // dialog.dismiss();
        // }
        // });
        builder.setMessage(R.string.hint_quit_message);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        mDialog.dismiss();
                        EPApplication.getInstance().logout();

                        Intent intent = new Intent(SettingActivity.this,
                                LoginActivity.class);
                        EPApplication.getInstance().clearAcitivy();

                        EPApplication.getInstance().logout();
                        startActivity(intent);

                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
        mDialog = builder.show();
        CommonUtils.dialogTitleLineColor(mDialog);
    }

}
