package com.jp.autoanswercalls.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.jp.autoanswercalls.R;
import com.jp.autoanswercalls.utils.Constants;
import com.jp.autoanswercalls.utils.ToastUtils;

public class AnswerCallActivity extends Activity {
    public static String durationBeforeAnswerCall;
    public static boolean isAutoAnswerCallServiceEnabled;
    private CheckBox mCheckboxAutoAnswerCallEnableDisable;
    private EditText mDurationBeforeAnswerCallEditText;

    public AnswerCallActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_call);
        SharedPreferences settings = getSharedPreferences(Constants.SETTING_PREF, 0);
        mCheckboxAutoAnswerCallEnableDisable = (CheckBox) findViewById(R.id.checkboxAutoAnswerCallEnableDisable);
        mCheckboxAutoAnswerCallEnableDisable.setChecked(settings.getBoolean(Constants.AUTO_ANSWER_CALL_SERVICE, false));
        mCheckboxAutoAnswerCallEnableDisable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCheckboxAutoAnswerCallEnableDisable.isChecked()) {
                    mDurationBeforeAnswerCallEditText.setEnabled(false);
                    enableAutoAnswerCallService();
                    return;
                }
                mDurationBeforeAnswerCallEditText.setEnabled(true);
                disableAutoAnswerCallService();
            }
        });
        mDurationBeforeAnswerCallEditText = (EditText) findViewById(R.id.durationBeforeAnswerCallEditText);
        mDurationBeforeAnswerCallEditText.setKeyListener(new DigitsKeyListener());
        mDurationBeforeAnswerCallEditText.setText(settings.getString(Constants.ANSWER_CALL_DURATION, "1"));
        mDurationBeforeAnswerCallEditText.setEnabled(!settings.getBoolean(Constants.AUTO_ANSWER_CALL_SERVICE, false));
        mDurationBeforeAnswerCallEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    mDurationBeforeAnswerCallEditText.setText("0");
                    mDurationBeforeAnswerCallEditText.selectAll();
                }
                if (s.length() > 1 && "0".equals(String.valueOf(s.charAt(0)))) {
                    mDurationBeforeAnswerCallEditText.setText("0");
                    ToastUtils.showToast(AnswerCallActivity.this, "The waiting time should no start with 0.");
                    mDurationBeforeAnswerCallEditText.selectAll();
                }
                if (Integer.parseInt(mDurationBeforeAnswerCallEditText.getText().toString()) > 60) {
                    mDurationBeforeAnswerCallEditText.setText("60");
                    ToastUtils.showToast(AnswerCallActivity.this, "等候时间不能大于60秒");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void enableAutoAnswerCallService() {
        Log.d("AutoAnswer", "Start call service!");
        isAutoAnswerCallServiceEnabled = true;
        durationBeforeAnswerCall = this.mDurationBeforeAnswerCallEditText.getText().toString();
        Editor editor = getSharedPreferences(Constants.SETTING_PREF, 0).edit();
        editor.putBoolean(Constants.AUTO_ANSWER_CALL_SERVICE, isAutoAnswerCallServiceEnabled);
        editor.putString(Constants.ANSWER_CALL_DURATION, durationBeforeAnswerCall);
        if (editor.commit()) {
            addNotificaction();
            ToastUtils.showToast(this, "自动接听已打开");
        }
    }

    private void disableAutoAnswerCallService() {
        Log.d("AutoAnswer", "Stop call service!");
        isAutoAnswerCallServiceEnabled = false;
        Editor editor = getSharedPreferences(Constants.SETTING_PREF, 0).edit();
        editor.putBoolean(Constants.AUTO_ANSWER_CALL_SERVICE, isAutoAnswerCallServiceEnabled);
        if (editor.commit()) {
            removeNotificaction();
            ToastUtils.showToast(this, "自动接听已关闭");
        }
    }


    private void addNotificaction() {
        NotificationManager manager = (NotificationManager) getSystemService("notification");
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = "自动接听已打开";
        notification.defaults = 1;
        notification.audioStreamType = -1;
        notification.setLatestEventInfo(this, "自动接听电话", "自动接听已打开", PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        manager.notify(0, notification);
    }

    private void removeNotificaction() {
        ((NotificationManager) getSystemService("notification")).cancel(0);
    }

}
