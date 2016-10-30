package com.jp.autoanswercalls.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.jp.autoanswercalls.service.AutoAnswerMessageIntentService;
import com.jp.autoanswercalls.utils.Constants;

public class AutoAnswerMessageReceiver extends BroadcastReceiver {
    public AutoAnswerMessageReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("AutoAnswer", "I am now in message receiver");
        if (context.getSharedPreferences(Constants.SETTING_PREF, 0).getBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, false)) {
            try {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    String msgFrom = messages[pdus.length - 1].getDisplayOriginatingAddress();
                    String msgBody = messages[pdus.length - 1].getDisplayMessageBody();
                    Log.d("AutoAnswer", "Msg From: " + msgFrom);
                    Log.d("AutoAnswer", "Msg Body: " + msgBody);
                    Intent receiveSMSIntent = new Intent();
                    receiveSMSIntent.setClass(context, AutoAnswerMessageIntentService.class);
                    receiveSMSIntent.putExtra("msgFrom", msgFrom);
                    receiveSMSIntent.putExtra("msgBody", msgBody);
                    context.startService(receiveSMSIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
