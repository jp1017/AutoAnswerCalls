package com.jp.autoanswercalls.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;


import com.android.internal.telephony.ITelephony;
import com.jp.autoanswercalls.utils.Constants;

import java.lang.reflect.Method;

public class AutoAnswerCallIntentService extends IntentService {

    public AutoAnswerCallIntentService() {
        super("AutoAnswerCallIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        Context context = getBaseContext();
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTING_PREF, 0);
        Log.d("AutoAnswer", "I'm now in call service!");
        if (((TelephonyManager) context.getSystemService("phone")).getCallState() != 1) {
            Log.d("AutoAnswer", "CALL_STATE_RINGING didn't detected!");
            return;
        }
        Log.d("AutoAnswer", "Phone receive an incoming call!");
        try {
            Thread.sleep((long) (Integer.parseInt(settings.getString(Constants.ANSWER_CALL_DURATION, "1")) * 1000));
            answerPhoneAidl(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answerPhoneAidl(Context context) throws Exception {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService("phone");
            Method m = Class.forName(tm.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm, new Object[0]);
            Log.d("AutoAnswer", "Phone answer incoming call now!");
            telephonyService.answerRingingCall();
        } catch (Exception e) {
            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(0, 79));
            context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
            intent = new Intent("android.intent.action.MEDIA_BUTTON");
            intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(1, 79));
            context.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
        }
    }

}
