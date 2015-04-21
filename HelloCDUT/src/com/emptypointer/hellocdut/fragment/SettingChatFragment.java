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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingChatFragment extends Fragment implements
        OnCheckedChangeListener {
    private static final String TAG = "SettingChatFragment";
    private SwitchButton mSwitchPlayBySpeaker, mSwithSendByEntry;
    private SharedPreferences mPreferences;
    private EMChatOptions chatOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(), R.layout.fragment_setting_chat,
                null);
        mSwitchPlayBySpeaker = (SwitchButton) view
                .findViewById(R.id.switch_play_with_loud_speaker);
        mSwithSendByEntry = (SwitchButton) view
                .findViewById(R.id.switch_send_with_enter);

        mPreferences = getActivity().getSharedPreferences("setting",
                Context.MODE_PRIVATE);
        mSwitchPlayBySpeaker.setChecked(mPreferences.getBoolean("void_message_by_speaker", true));
        mSwitchPlayBySpeaker.setOnCheckedChangeListener(this);
        mSwithSendByEntry.setOnCheckedChangeListener(this);

        chatOptions = EMChatManager.getInstance().getChatOptions();
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

        Editor editor = mPreferences.edit();
        switch (buttonView.getId()) {
            case R.id.switch_play_with_loud_speaker:
                editor.putBoolean("void_message_by_speaker", isChecked);
                chatOptions.setUseSpeaker(isChecked);
                break;
            case R.id.switch_send_with_enter:
                editor.putBoolean("send_with_enter", isChecked);
                break;

            default:
                break;
        }
        editor.commit();
    }
}
