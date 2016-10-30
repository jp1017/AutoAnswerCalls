package com.jp.autoanswercalls.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.jp.autoanswercalls.utils.Constants;

public class AutoAnswerMessageIntentService extends IntentService {
    private static final String ACTION_SMS_SEND = "com.SMSDemo.SMS.send";

    public AutoAnswerMessageIntentService() {
        super("AutoAnswerMessageIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Context context = getBaseContext();
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTING_PREF, 0);
        Log.d("AutoAnswer", "I'm now in message service!");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (TextUtils.isEmpty(bundle.getString("msgBody"))
                    || bundle.getString("msgBody").contains(settings.getString(Constants.REPLY_MESSAGE_TRIGGER, Constants.AUTO_ANSWER_TIGGER))) {
                try {
                    Thread.sleep((long) (Integer.parseInt(settings.getString(Constants.ANSWER_MESSAGE_DURATION, "3")) * 1000));
                    sendSMS(context, bundle.getString("msgFrom"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void sendSMS(Context context, String phoneNumber) throws Exception {
        Log.d("AutoAnswer", "Start to send SMS!");
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTING_PREF, 0);
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_SMS_SEND), 0);
        smsManager.sendTextMessage(phoneNumber, null, settings.getString(Constants.REPLY_MESSAGE_CONTENT, Constants.AUTO_ANSWER_MESSAGE), pi, null);
    }
}
