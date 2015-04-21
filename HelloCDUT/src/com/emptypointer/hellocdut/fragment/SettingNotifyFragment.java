package com.emptypointer.hellocdut.fragment;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.widget.SwitchButton;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingNotifyFragment extends Fragment implements
        OnCheckedChangeListener {

    private SwitchButton mSwitchNotify, mSwitchBrate, mSwitchRing;
    private SharedPreferences mPreferences;
    private EMChatOptions chatOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(),
                R.layout.fragment_setting_notify, null);
        mSwitchNotify = (SwitchButton) view.findViewById(R.id.switch_notify);
        mSwitchRing = (SwitchButton) view.findViewById(R.id.switch_ring);
        mSwitchBrate = (SwitchButton) view.findViewById(R.id.switch_beep);
        mSwitchBrate.setOnCheckedChangeListener(this);
        mSwitchRing.setOnCheckedChangeListener(this);
        mSwitchNotify.setOnCheckedChangeListener(this);
        chatOptions = EMChatManager.getInstance().getChatOptions();
        mPreferences = getActivity().getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        mSwitchNotify.setChecked(mPreferences.getBoolean(
                "message_notification", true));
        mSwitchBrate.setChecked(mPreferences.getBoolean("message_brate", true));
        mSwitchRing.setChecked(mPreferences.getBoolean("message_sound", false));
        return view;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mSwitchNotify.isChecked()) {
            mSwitchNotify.setChecked(mPreferences.getBoolean(
                    "message_notification", true));
        }
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        Editor editor = mPreferences.edit();
        switch (buttonView.getId()) {
            case R.id.switch_notify:
                editor.putBoolean("message_notification", isChecked);
                chatOptions.setNotificationEnable(isChecked);
                break;
            case R.id.switch_ring:
                editor.putBoolean("message_sound", isChecked);
                chatOptions.setNoticeBySound(isChecked);
                break;
            case R.id.switch_beep:
                editor.putBoolean("message_brate", isChecked);
                chatOptions.setNoticedByVibrate(isChecked);
                break;

            default:
                break;
        }
        editor.commit();
    }

}
