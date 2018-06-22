package com.idx.jakku;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idx.jakku.data.JsonData;
import com.idx.jakku.data.JsonUtil;
import com.idx.jakku.service.DataListener;
import com.idx.jakku.service.UDPDataListener;
import com.idx.jakku.service.IService;
import com.idx.jakku.service.JakkuService;
import com.idx.jakku.utils.NetStatusUtils;

public class MainActivity extends BaseActivity implements DataListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private IService mIService;
    private FrameLayout frameLayout;
    private ImageView wifi_image;           //信号图片显示
    private AlertDialog mDialog;

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        frameLayout = findViewById(R.id.right);
        frameLayout.setBackgroundResource(R.mipmap.bg_70);

        //WiFi信号强度变化
        wifi_image = (ImageView) findViewById(R.id.wifi_image);
        wifi_image.setImageResource(R.mipmap.wifi_5);
        wifi_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);

            }
        });
    }

    private void showDialog() {
        if (mIService == null) {
            return;
        }
        if (mDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            mDialog = builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mIService.rejectRequestTcpPort();
                    mDialog.dismiss();
                }
            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    String port = mTcpServer.getPort() + "";
//                    Log.d(TAG, "send TCP port=" + port);
//                    mUdpServer.sendMsg(port);
                    mIService.sendTcpPort();
                    mDialog.dismiss();
                }
            }).setTitle("是否同意连接？").create();
        }
        mDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, JakkuService.class);
        if (!isServiceRunning(getBaseContext(), JakkuService.class)) {
            startService(intent);
        }
        bindService(intent, mConn, BIND_AUTO_CREATE);
        // 注册wifi消息处理器
        IntentFilter intentFilter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(Intents.ACTION_REQUEST_TCP_PORT);
        registerReceiver(wifiIntentReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConn);
        unregisterReceiver(wifiIntentReceiver);

    }

    @Override
    public void onJsonReceived(String json) {
        try {
            JsonData jsonData = JsonUtil.createJsonData(json);
            String stringBuffer = "domain=" +
                    jsonData.getDomain() +
                    "\n" +
                    "type=" +
                    jsonData.getType() +
                    "\n" +
                    "queryId=" +
                    jsonData.getQueryId() +
                    "\n" +
                    "tts=" +
                    jsonData.getTts() +
                    "\n";
//            textView.setText(stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            textView.append(json);
        }
    }

    // Wifi的连接速度及信号强度：
    private int obtainWifiInfo(Context context) {
        int strength = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度，5为获取的信号强度值在5以内
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
        }
        return strength;
    }

    private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case Intents.ACTION_REQUEST_TCP_PORT:
                    showDialog();
                    break;
                case WifiManager.RSSI_CHANGED_ACTION:
                    NetStatusUtils.isNetWorkAvailableOfGet("https://www.baidu.com/", new Comparable<Boolean>() {
                        @Override
                        public int compareTo(Boolean available) {
                            if (available) {
                                int s = obtainWifiInfo(context);
                                android.util.Log.d(TAG, "onReceive:有网");
                                if (s == 0) {
                                    wifi_image.setImageResource(R.mipmap.wifi_1);
                                } else if (s == 1) {
                                    wifi_image.setImageResource(R.mipmap.wifi_2);
                                } else if (s == 2) {
                                    wifi_image.setImageResource(R.mipmap.wifi_3);
                                } else if (s == 3) {
                                    wifi_image.setImageResource(R.mipmap.wifi_4);
                                } else if (s == 4) {
                                    wifi_image.setImageResource(R.mipmap.wifi_5);
                                }

                            } else {
                                android.util.Log.d(TAG, "onReceive:无网");
                                wifi_image.setImageResource(R.mipmap.wifi_error);
                            }
                            return 0;
                        }

                    });
            }
        }
    };
}
