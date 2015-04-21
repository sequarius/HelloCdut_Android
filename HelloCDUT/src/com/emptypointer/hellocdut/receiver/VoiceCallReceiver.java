package com.emptypointer.hellocdut.receiver;

import com.easemob.chat.EMChatManager;
import com.emptypointer.hellocdut.activity.VoiceCallActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VoiceCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(EMChatManager.getInstance().getIncomingVoiceCallBroadcastAction()))
            return;

        //拨打方username
        String from = intent.getStringExtra("from");
        context.startActivity(new Intent(context, VoiceCallActivity.class).
                putExtra("username", from).putExtra("isComingCall", true).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
