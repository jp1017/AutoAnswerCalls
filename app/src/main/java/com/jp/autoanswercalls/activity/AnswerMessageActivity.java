package com.jp.autoanswercalls.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
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

public class AnswerMessageActivity extends Activity {
    public static String durationBeforeAnswerMessage;
    public static boolean isAutoAnswerMessageServiceEnabled;
    public static String replyMessageContent;
    public static String replyMessageTrigger;
    private CheckBox mCheckboxAutoAnswerMessageEnableDisable;
    private EditText mDurationBeforeAnswerMessageEditText;
    private EditText mReplyIncomingMessageContentEditText;
    private EditText mReplyIncomingMessageTriggerEditText;

    public AnswerMessageActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        boolean z = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_message);
        SharedPreferences settings = getSharedPreferences(Constants.SETTING_PREF, 0);
        mCheckboxAutoAnswerMessageEnableDisable = (CheckBox) findViewById(R.id.checkboxAutoAnswerMessageEnableDisable);
        mCheckboxAutoAnswerMessageEnableDisable.setChecked(settings.getBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, false));
        mCheckboxAutoAnswerMessageEnableDisable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCheckboxAutoAnswerMessageEnableDisable.isChecked()) {
                    mDurationBeforeAnswerMessageEditText.setEnabled(false);
                    mReplyIncomingMessageTriggerEditText.setEnabled(false);
                    mReplyIncomingMessageContentEditText.setEnabled(false);
                    enableAutoAnswerMessageService();
                    return;
                }
                mDurationBeforeAnswerMessageEditText.setEnabled(true);
                mReplyIncomingMessageTriggerEditText.setEnabled(true);
                mReplyIncomingMessageContentEditText.setEnabled(true);
                disableAutoAnswerMessageService();
            }
        });
        mDurationBeforeAnswerMessageEditText = (EditText) findViewById(R.id.durationBeforeAnswerMessageEditText);
        mDurationBeforeAnswerMessageEditText.setKeyListener(new DigitsKeyListener());
        mDurationBeforeAnswerMessageEditText.setText(settings.getString(Constants.ANSWER_MESSAGE_DURATION, "3"));
        mDurationBeforeAnswerMessageEditText.setEnabled(!settings.getBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, false));
        mDurationBeforeAnswerMessageEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if ("".equals(s.toString())) {
                    mDurationBeforeAnswerMessageEditText.setText("0");
                    mDurationBeforeAnswerMessageEditText.selectAll();
                }
                if (s.length() > 1 && "0".equals(String.valueOf(s.charAt(0)))) {
                    mDurationBeforeAnswerMessageEditText.setText("0");
                    ToastUtils.showToast(AnswerMessageActivity.this, "时间非0开头");
                    mDurationBeforeAnswerMessageEditText.selectAll();
                }
                if (Integer.parseInt(mDurationBeforeAnswerMessageEditText.getText().toString()) > 60) {
                    mDurationBeforeAnswerMessageEditText.setText("60");
                    ToastUtils.showToast(AnswerMessageActivity.this, "等候时间不能大于60秒");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mReplyIncomingMessageTriggerEditText = (EditText) findViewById(R.id.replyIncomingMessageTriggerEditText);
        mReplyIncomingMessageTriggerEditText.setText(settings.getString(Constants.REPLY_MESSAGE_TRIGGER, Constants.AUTO_ANSWER_TIGGER));
        mReplyIncomingMessageTriggerEditText.setEnabled(!settings.getBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, false));
        mReplyIncomingMessageTriggerEditText.selectAll();
        mReplyIncomingMessageContentEditText = (EditText) findViewById(R.id.replyIncomingMessageContentEditText);
        mReplyIncomingMessageContentEditText.setText(settings.getString(Constants.REPLY_MESSAGE_CONTENT, Constants.AUTO_ANSWER_MESSAGE));
        EditText editText = mReplyIncomingMessageContentEditText;
        if (!settings.getBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, false)) {
            z = true;
        }
        editText.setEnabled(z);
        mReplyIncomingMessageContentEditText.selectAll();
    }

    private void enableAutoAnswerMessageService() {
        Log.d("AutoAnswer", "Start message service!");
        isAutoAnswerMessageServiceEnabled = true;
        durationBeforeAnswerMessage = mDurationBeforeAnswerMessageEditText.getText().toString();
        replyMessageTrigger = mReplyIncomingMessageTriggerEditText.getText().toString();
        replyMessageContent = mReplyIncomingMessageContentEditText.getText().toString();
        Editor editor = getSharedPreferences(Constants.SETTING_PREF, 0).edit();
        editor.putBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, isAutoAnswerMessageServiceEnabled);
        editor.putString(Constants.ANSWER_MESSAGE_DURATION, durationBeforeAnswerMessage);
        editor.putString(Constants.REPLY_MESSAGE_TRIGGER, replyMessageTrigger);
        editor.putString(Constants.REPLY_MESSAGE_CONTENT, replyMessageContent);
        if (editor.commit()) {
            addNotificaction();
            ToastUtils.showToast(this, "自动短信回复已打开");
        }
    }

    private void disableAutoAnswerMessageService() {
        Log.d("AutoAnswer", "Stop message service!");
        isAutoAnswerMessageServiceEnabled = false;
        Editor editor = getSharedPreferences(Constants.SETTING_PREF, 0).edit();
        editor.putBoolean(Constants.AUTO_ANSWER_MESSAGE_SERVICE, isAutoAnswerMessageServiceEnabled);
        if (editor.commit()) {
            removeNotificaction();
            ToastUtils.showToast(this, "自动短信回复已关闭");
        }
    }


    private void addNotificaction() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("自动回复短信")
                .setContentText("自动短信回复已打开")
                .setTicker("自动短信回复已打开")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent
                        .getActivity(this, 0, new Intent(this, MainActivity.class), 0));

        Notification notification = builder.getNotification();
        manager.notify(0, notification);
    }

    private void removeNotificaction() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1);
    }

}
