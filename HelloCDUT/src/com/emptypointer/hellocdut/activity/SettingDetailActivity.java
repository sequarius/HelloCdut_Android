package com.emptypointer.hellocdut.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.fragment.BindBasicFragment;
import com.emptypointer.hellocdut.fragment.BindCampusCardFragment;
import com.emptypointer.hellocdut.fragment.BindMailFragment;
import com.emptypointer.hellocdut.fragment.SettingAboutFragment;
import com.emptypointer.hellocdut.fragment.SettingAccountFragment;
import com.emptypointer.hellocdut.fragment.SettingChatFragment;
import com.emptypointer.hellocdut.fragment.SettingFeedBackFragment;
import com.emptypointer.hellocdut.fragment.SettingNotifyFragment;
import com.emptypointer.hellocdut.fragment.SettingSclienceFragment;
import com.emptypointer.hellocdut.utils.GlobalVariables;

public class SettingDetailActivity extends BaseActivity {

    public static final String TAG = "SettingDetailActivity";
    public static final int MODE_MESSAGE = 1;
    public static final int MODE_CHAT = 2;
    public static final int MODE_SILENCE = 3;
    public static final int MODE_ACCOUNT = 4;
    public static final int MODE_FEEDBACK = 5;
    public static final int MODE_ABOUT = 6;
    private int mBindCategory;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_support_fragment);
        Intent intent = getIntent();
        mBindCategory = intent.getIntExtra(
                GlobalVariables.INTENT_EXTRA_SETTINGDETAIL, -1);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        ActionBar actionBar = getActionBar();
        switch (mBindCategory) {
            case -1:
                transaction.commit();
                this.finish();
                break;
            case MODE_MESSAGE:

                transaction.replace(R.id.fragment_beach,
                        new SettingNotifyFragment());
                transaction.commit();
                actionBar.setTitle(R.string.str_message_and_notify_setting);
                break;
            case MODE_CHAT:
                transaction.replace(R.id.fragment_beach, new SettingChatFragment());
                transaction.commit();
                actionBar.setTitle(R.string.str_chat);
                break;
            case MODE_SILENCE:
                transaction.replace(R.id.fragment_beach,
                        new SettingSclienceFragment());
                transaction.commit();
                actionBar.setTitle(R.string.str_silence_mode);
                break;
            case MODE_ACCOUNT:
                transaction.replace(R.id.fragment_beach,
                        new SettingAccountFragment());
                transaction.commit();
                actionBar.setTitle(R.string.str_account);

                break;
            case MODE_FEEDBACK:
                transaction.replace(R.id.fragment_beach,
                        new SettingFeedBackFragment());
                transaction.commit();
                actionBar.setTitle(R.string.str_feed_back);

                break;
            case MODE_ABOUT:
                transaction
                        .replace(R.id.fragment_beach, new SettingAboutFragment());
                transaction.commit();
                actionBar.setTitle(R.string.str_about);

                break;

            default:
                transaction.commit();
                this.finish();
                break;
        }
    }

}
