package com.frostbytedev.wifiqr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import com.google.analytics.tracking.android.EasyTracker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@TargetApi(14)
public class HomeScreen extends Activity implements OnClickListener {
    public static final String EXTRA_MESSAGE = "com.frostbytedev.wifiqr.MESSAGE";
    String PASS;
    String PASSTYPE;
    String SSID;
    String TAG;
    String WIFIQRCODE = "";
    Button bCreate;
    CheckBox cbShowPass;
    String currentSSID;
    Boolean didFirstRun = Boolean.valueOf(false);
    Editor edit;
    EditText etPASS;
    AutoCompleteTextView etSSID;
    boolean isCodeGenerated = false;
    Boolean isFirstRun = Boolean.valueOf(false);
    MenuItem item;
    ImageView ivCode;
    SharedPreferences pref2;
    SharedPreferences prefs;
    ShareActionProvider provider;
    QRUtil qrUtil = new QRUtil();
    String quotes;
    ArrayList<String> rar = new ArrayList();
    Spinner sTYPE;
    private Intent shareIntent = new Intent("android.intent.action.SEND");
    WifiManager wifiManager;
    ArrayList<String> wifiPass = new ArrayList();
    ArrayList<String> wifiSSID = new ArrayList();
    ArrayList<String> wifiType = new ArrayList();

    class C00001 implements DialogInterface.OnClickListener {
        C00001() {
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case -1:
                    HomeScreen.this.edit.putInt("wifissidsize", 0);
                    HomeScreen.this.wifiSSID.clear();
                    HomeScreen.this.edit.putInt("wifiPasssize", 0);
                    HomeScreen.this.wifiPass.clear();
                    HomeScreen.this.edit.putInt("wifiTypesize", 0);
                    HomeScreen.this.wifiType.clear();
                    HomeScreen.this.refreshACAdapter();
                    return;
                default:
                    return;
            }
        }
    }

    class C00012 implements OnLongClickListener {
        C00012() {
        }

        public boolean onLongClick(View v) {
            HomeScreen.this.connectToNetwork(HomeScreen.this.SSID, HomeScreen.this.PASS, HomeScreen.this.PASSTYPE);
            return true;
        }
    }

    class C00023 implements OnTouchListener {
        C00023() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            HomeScreen.this.etSSID.showDropDown();
            return false;
        }
    }

    class C00034 implements TextWatcher {
        C00034() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (HomeScreen.this.pref2.getBoolean("isautofillon", true)) {
                for (int n = 0; n < HomeScreen.this.wifiSSID.size(); n++) {
                    if (((String) HomeScreen.this.wifiSSID.get(n)).equalsIgnoreCase(HomeScreen.this.etSSID.getText().toString())) {
                        String str = HomeScreen.this.etSSID.getText().toString();
                        HomeScreen.this.etPASS.setText(String.valueOf(HomeScreen.this.wifiPass.get(HomeScreen.this.wifiSSID.indexOf(str))));
                        ArrayAdapter myAdap = (ArrayAdapter) HomeScreen.this.sTYPE.getAdapter();
                        String asdf = HomeScreen.this.wifiPass.toString();
                        HomeScreen.this.sTYPE.setSelection(myAdap.getPosition(HomeScreen.this.wifiType.get(HomeScreen.this.wifiSSID.indexOf(str))));
                    }
                }
            }
        }

        public void afterTextChanged(Editable s) {
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0006R.layout.generate);
        this.TAG = "WifiQR";
        this.SSID = "";
        this.PASS = "";
        this.PASSTYPE = "";
        uiInit();
        populateWifiList("wifissid", this.wifiSSID);
        populateWifiList("wifiPass", this.wifiPass);
        populateWifiList("wifiType", this.wifiType);
        this.wifiManager = (WifiManager) getSystemService("wifi");
        String ssidInfo = this.wifiManager.getConnectionInfo().getSSID().toString();
        if (ssidInfo.equalsIgnoreCase("<unknown ssid>")) {
            this.etSSID.setText("");
            this.etPASS.setText("");
        } else {
            if (ssidInfo.length() > 1 && ssidInfo.substring(0, 1).equals("\"")) {
                ssidInfo = ssidInfo.replaceAll(String.valueOf("\""), "");
            }
            this.etSSID.setText(ssidInfo);
        }
        refreshACAdapter();
    }

    private void refreshACAdapter() {
        if (this.pref2.getBoolean("isautosugon", true)) {
            ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367050, this.wifiSSID);
            this.etSSID.setThreshold(0);
            this.etSSID.setAdapter(adapter);
            return;
        }
        adapter = new ArrayAdapter(this, 17367050, new ArrayList());
        this.etSSID.setThreshold(10000);
        this.etSSID.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0006R.menu.action_bar_share_menu, menu);
        this.provider = (ShareActionProvider) menu.findItem(C0006R.id.menu_item_share).getActionProvider();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0006R.id.menu_item_scan:
                new IntentIntegrator(this).initiateScan();
                break;
            case C0006R.id.menu_item_settings:
                startActivity(new Intent(this, SettingActivity.class));
                overridePendingTransition(C0006R.anim.slide_down, C0006R.anim.stationary);
                break;
            case C0006R.id.menu_item_clear_networks:
                clearMessage();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public void clearMessage() {
        DialogInterface.OnClickListener dialogClickListener = new C00001();
        new Builder(this).setMessage("Are you sure you want to delete all saved networks?").setPositiveButton(IntentIntegrator.DEFAULT_YES, dialogClickListener).setNegativeButton(IntentIntegrator.DEFAULT_NO, dialogClickListener).show();
    }

    public void onClick(View view) {
        FileNotFoundException e;
        File file;
        Uri uri;
        Intent intent;
        int wifiIndex;
        IOException e2;
        switch (view.getId()) {
            case C0006R.id.cbShowPass:
                if (this.cbShowPass.isChecked()) {
                    this.etPASS.setInputType(128);
                    return;
                } else {
                    this.etPASS.setInputType(129);
                    return;
                }
            case C0006R.id.bcreate:
                this.SSID = this.etSSID.getText().toString();
                this.PASS = this.etPASS.getText().toString();
                this.PASSTYPE = this.sTYPE.getSelectedItem().toString();
                this.WIFIQRCODE = "WIFI:S:" + this.SSID + ";T:" + this.PASSTYPE + ";P:" + this.PASS + ";;";
                if (this.PASSTYPE.equalsIgnoreCase("Open")) {
                    this.WIFIQRCODE = "WIFI:S:" + this.SSID + ";P:" + this.PASS + ";;";
                } else if (this.PASSTYPE.equalsIgnoreCase("WPA/WPA2")) {
                    this.WIFIQRCODE = "WIFI:S:" + this.SSID + ";T:WPA;P:" + this.PASS + ";;";
                }
                Bitmap bmp = this.qrUtil.create(this.WIFIQRCODE, this.pref2);
                this.ivCode.setImageBitmap(bmp);
                try {
                    OutputStream stream = new FileOutputStream(getFilesDir() + "/QRCode.png");
                    OutputStream outputStream;
                    try {
                        bmp.compress(CompressFormat.PNG, 80, stream);
                        stream.close();
                        outputStream = stream;
                    } catch (FileNotFoundException e3) {
                        e = e3;
                        outputStream = stream;
                        e.printStackTrace();
                        file = new File(getFilesDir(), "QRCode.png");
                        file.setReadable(true, false);
                        uri = Uri.fromFile(file);
                        intent = new Intent("android.intent.action.SEND");
                        intent.setType("image/*");
                        intent.putExtra("android.intent.extra.STREAM", uri);
                        this.provider.setShareIntent(intent);
                        saveWifiInfo(this.wifiSSID, this.wifiPass, this.wifiType);
                        refreshACAdapter();
                        if (this.wifiSSID.contains(this.SSID)) {
                            this.wifiSSID.add(this.etSSID.getText().toString());
                            this.wifiPass.add(this.etPASS.getText().toString());
                            this.wifiType.add(this.sTYPE.getSelectedItem().toString());
                            return;
                        }
                        wifiIndex = this.wifiSSID.indexOf(this.SSID);
                        this.wifiPass.add(wifiIndex, this.PASS);
                        this.wifiType.add(wifiIndex, this.PASSTYPE);
                        return;
                    } catch (IOException e4) {
                        e2 = e4;
                        outputStream = stream;
                        e2.printStackTrace();
                        file = new File(getFilesDir(), "QRCode.png");
                        file.setReadable(true, false);
                        uri = Uri.fromFile(file);
                        intent = new Intent("android.intent.action.SEND");
                        intent.setType("image/*");
                        intent.putExtra("android.intent.extra.STREAM", uri);
                        this.provider.setShareIntent(intent);
                        saveWifiInfo(this.wifiSSID, this.wifiPass, this.wifiType);
                        refreshACAdapter();
                        if (this.wifiSSID.contains(this.SSID)) {
                            wifiIndex = this.wifiSSID.indexOf(this.SSID);
                            this.wifiPass.add(wifiIndex, this.PASS);
                            this.wifiType.add(wifiIndex, this.PASSTYPE);
                            return;
                        }
                        this.wifiSSID.add(this.etSSID.getText().toString());
                        this.wifiPass.add(this.etPASS.getText().toString());
                        this.wifiType.add(this.sTYPE.getSelectedItem().toString());
                        return;
                    }
                } catch (FileNotFoundException e5) {
                    e = e5;
                    e.printStackTrace();
                    file = new File(getFilesDir(), "QRCode.png");
                    file.setReadable(true, false);
                    uri = Uri.fromFile(file);
                    intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/*");
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    this.provider.setShareIntent(intent);
                    saveWifiInfo(this.wifiSSID, this.wifiPass, this.wifiType);
                    refreshACAdapter();
                    if (this.wifiSSID.contains(this.SSID)) {
                        this.wifiSSID.add(this.etSSID.getText().toString());
                        this.wifiPass.add(this.etPASS.getText().toString());
                        this.wifiType.add(this.sTYPE.getSelectedItem().toString());
                        return;
                    }
                    wifiIndex = this.wifiSSID.indexOf(this.SSID);
                    this.wifiPass.add(wifiIndex, this.PASS);
                    this.wifiType.add(wifiIndex, this.PASSTYPE);
                    return;
                } catch (IOException e6) {
                    e2 = e6;
                    e2.printStackTrace();
                    file = new File(getFilesDir(), "QRCode.png");
                    file.setReadable(true, false);
                    uri = Uri.fromFile(file);
                    intent = new Intent("android.intent.action.SEND");
                    intent.setType("image/*");
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    this.provider.setShareIntent(intent);
                    saveWifiInfo(this.wifiSSID, this.wifiPass, this.wifiType);
                    refreshACAdapter();
                    if (this.wifiSSID.contains(this.SSID)) {
                        wifiIndex = this.wifiSSID.indexOf(this.SSID);
                        this.wifiPass.add(wifiIndex, this.PASS);
                        this.wifiType.add(wifiIndex, this.PASSTYPE);
                        return;
                    }
                    this.wifiSSID.add(this.etSSID.getText().toString());
                    this.wifiPass.add(this.etPASS.getText().toString());
                    this.wifiType.add(this.sTYPE.getSelectedItem().toString());
                    return;
                }
                file = new File(getFilesDir(), "QRCode.png");
                file.setReadable(true, false);
                uri = Uri.fromFile(file);
                intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", uri);
                this.provider.setShareIntent(intent);
                saveWifiInfo(this.wifiSSID, this.wifiPass, this.wifiType);
                refreshACAdapter();
                if (this.wifiSSID.contains(this.SSID)) {
                    wifiIndex = this.wifiSSID.indexOf(this.SSID);
                    this.wifiPass.add(wifiIndex, this.PASS);
                    this.wifiType.add(wifiIndex, this.PASSTYPE);
                    return;
                }
                this.wifiSSID.add(this.etSSID.getText().toString());
                this.wifiPass.add(this.etPASS.getText().toString());
                this.wifiType.add(this.sTYPE.getSelectedItem().toString());
                return;
            case C0006R.id.imageView:
                Intent intent2 = new Intent(this, FullScreenCode.class);
                if (this.WIFIQRCODE != "") {
                    intent2.putExtra(EXTRA_MESSAGE, this.WIFIQRCODE);
                    startActivity(intent2);
                    overridePendingTransition(C0006R.anim.slide_up, C0006R.anim.stationary);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void saveWifiInfo(List<String> wifiSSID, List<String> wifiPass, List<String> wifiType) {
        if (this.pref2.getBoolean("isautosaveon", true)) {
            saveList("wifissid", wifiSSID);
            saveList("wifiPass", wifiPass);
            saveList("wifiType", wifiType);
        }
    }

    private void saveList(String spString, List<String> wifiList) {
        for (int i = 0; i < wifiList.size(); i++) {
            this.edit.putString(spString + i, (String) wifiList.get(i));
        }
        this.edit.putInt(spString + "size", wifiList.size());
        this.edit.commit();
    }

    private void populateWifiList(String spString, List<String> wifiList) {
        int size = this.prefs.getInt(spString + "size", 0);
        for (int j = 0; j < size; j++) {
            wifiList.add(this.prefs.getString(spString + j, null));
        }
    }

    private void uiInit() {
        this.shareIntent.setType("");
        this.wifiSSID = new ArrayList();
        this.wifiPass = new ArrayList();
        this.etSSID = (AutoCompleteTextView) findViewById(C0006R.id.etssid);
        this.etPASS = (EditText) findViewById(C0006R.id.etpass);
        this.sTYPE = (Spinner) findViewById(C0006R.id.spinner);
        this.bCreate = (Button) findViewById(C0006R.id.bcreate);
        this.cbShowPass = (CheckBox) findViewById(C0006R.id.cbShowPass);
        this.ivCode = (ImageView) findViewById(C0006R.id.imageView);
        this.bCreate.setOnClickListener(this);
        this.cbShowPass.setOnClickListener(this);
        this.ivCode.setOnClickListener(this);
        this.prefs = getSharedPreferences("MyPrefs", 0);
        this.pref2 = PreferenceManager.getDefaultSharedPreferences(this);
        this.edit = this.prefs.edit();
        this.ivCode.setOnLongClickListener(new C00012());
        this.etSSID.setOnTouchListener(new C00023());
        this.etSSID.addTextChangedListener(new C00034());
    }

    private void connectToNetwork(String ssid, String pass, String type) {
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
