package com.frostbytedev.wifiqr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;

public class FullScreenCode extends Activity implements OnClickListener {
    ImageView ivCode;
    LinearLayout parent;
    SharedPreferences prefs;
    TextView tv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0006R.layout.fullscreencode);
        getActionBar().hide();
        init();
        setQRImage();
    }

    private void setQRImage() {
        String message = getIntent().getStringExtra(HomeScreen.EXTRA_MESSAGE);
        QRUtil qrUtil = new QRUtil();
        this.parent.setBackgroundColor(Color.parseColor(this.prefs.getString("qrcodebackgroundcolor", "#FFFFFF")));
        this.ivCode.setBackgroundColor(Color.parseColor(this.prefs.getString("qrcodebackgroundcolor", "#FFFFFF")));
        this.ivCode.setImageBitmap(qrUtil.create(message, this.prefs));
        this.parent.setOnClickListener(this);
        this.ivCode.setOnClickListener(this);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(C0006R.anim.stationary, C0006R.anim.slide_down_from_middle);
    }

    private void init() {
        this.ivCode = (ImageView) findViewById(C0006R.id.ivCode);
        this.parent = (LinearLayout) findViewById(C0006R.id.llParent);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void onClick(View v) {
        finish();
        overridePendingTransition(C0006R.anim.stationary, C0006R.anim.slide_down_from_middle);
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }
}
