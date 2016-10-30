package com.jp.autoanswercalls.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.jp.autoanswercalls.R;
import com.jp.autoanswercalls.activity.AnswerCallActivity;
import com.jp.autoanswercalls.activity.AnswerMessageActivity;

import static com.jp.autoanswercalls.utils.Constants.SETTING_PREF;

public class MainActivity extends ActivityGroup {
    public static TabHost mTabHost;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabHost = (TabHost) findViewById(R.id.edit_item_tab_host);
        mTabHost.setup(getLocalActivityManager());

        TabSpec tabCall = mTabHost.newTabSpec("TAB_Call");
        tabCall.setIndicator("电话");
        tabCall.setContent(new Intent(this, AnswerCallActivity.class));
        mTabHost.addTab(tabCall);

        TabSpec tabMessage = mTabHost.newTabSpec("TAB_Message");
        tabMessage.setIndicator("短信");
        tabMessage.setContent(new Intent(this, AnswerMessageActivity.class));
        mTabHost.addTab(tabMessage);

        mTabHost.setCurrentTab(0);
    }
}
