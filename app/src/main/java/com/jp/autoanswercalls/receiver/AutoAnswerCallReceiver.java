package com.jp.autoanswercalls.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jp.autoanswercalls.service.AutoAnswerCallIntentService;
import com.jp.autoanswercalls.utils.Constants;

public class AutoAnswerCallReceiver extends BroadcastReceiver {

    public AutoAnswerCallReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("AutoAnswer", "I am now in call receiver");
        if (context.getSharedPreferences(Constants.SETTING_PREF, 0).getBoolean(Constants.AUTO_ANSWER_CALL_SERVICE, false)) {
            context.startService(new Intent(context, AutoAnswerCallIntentService.class));
        }
    }
}
