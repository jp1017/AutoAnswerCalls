package com.jp.autoanswercalls.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.jp.autoanswercalls.utils.Constants;

public class AutoAnswerBootReceiver extends BroadcastReceiver {

    public AutoAnswerBootReceiver() {
    }

    public void onReceive(Context context, Intent intent) {
        Editor editor = context.getSharedPreferences(Constants.SETTING_PREF, 0).edit();
        editor.putBoolean(Constants.AUTO_ANSWER_CALL_SERVICE, false);
        editor.putBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, false);
        editor.apply();
    }
}
