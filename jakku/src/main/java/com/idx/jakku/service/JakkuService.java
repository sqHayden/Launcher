package com.idx.jakku.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.idx.jakku.Intents;
import com.idx.jakku.MainActivity;
import com.idx.jakku.NabooActions;
import com.idx.jakku.calendar.CalenderActivity;
import com.idx.jakku.data.JsonData;
import com.idx.jakku.data.JsonUtil;
import com.idx.jakku.figure.FigureActivity;
import com.idx.jakku.music.MusicActivity;
import com.idx.jakku.news.NewsActivity;
import com.idx.jakku.weather.WeatherActivity;

import java.util.ArrayList;

public class JakkuService extends Service {

    private static final String TAG = JakkuService.class.getSimpleName();
    private static final int CONSTANT_JSON_RECEIVED = 0x001;
    private static final int CONSTANT_UDP_REQUEST_PORT = 0x002;
    private static final int CONSTANT_UDP_REQUEST_CLOSE_TCP_PORT = 0x003;
    private UDPServer mUdpServer;
    private DataListener mDataListener;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONSTANT_JSON_RECEIVED:
                    Log.d(TAG, "handleMessage: " + msg.obj);
                    try {
                        handleMoranResponse((String) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case CONSTANT_UDP_REQUEST_PORT:
                    if (!isActivityRunning(MainActivity.class.getName())) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    Log.i("TestDemo", "呼叫是否要建立连接...");
                    sendBroadcast(new Intent(Intents.ACTION_REQUEST_TCP_PORT));
                    break;
                case CONSTANT_UDP_REQUEST_CLOSE_TCP_PORT:
                    Log.i("TestDemo", "呼叫关闭连接...");
                    mUdpServer.sendMsg("disconnect success");
                    //先在这儿处理naboo的开启工作
                    sendBroadcast(new Intent("com.idx.launcher.jakku.open"));
                    if (mDataListener != null) {
                        mDataListener.onFinish();
                    }
                    break;
                default:

            }
        }
    };
    private String mJson = null;
    private TCPServer mTcpServer;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new JakkuBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("TestDemo", "onCreate: 服务已创建....");

        initUDPServer();
        initTCPServer();
    }

    private void initUDPServer() {
        Log.d(TAG, "initUDPServer: ");
        mUdpServer = new UDPServer("224.255.20.0", "224.255.10.0", 9910);
        mUdpServer.receive();
        mUdpServer.setDataListener(new UDPDataListener() {
            @Override
            public void onReceived(String ip, String msg) {
                if (msg.equals("请求port")) {
                    Log.i("TestDemo", "onReceived: 接收到了UDP连接请求");
                    sendMessage(CONSTANT_UDP_REQUEST_PORT, msg);
                } else if (msg.equals("close tcp")){
                    Log.i("TestDemo", "onReceived: 接收到了UDP关闭请求");
                    sendMessage(CONSTANT_UDP_REQUEST_CLOSE_TCP_PORT, msg);
                }
            }
        });
        mUdpServer.startServer();
    }

    private void initTCPServer() {
        mTcpServer = TCPServer.newInstance(new TCPServer.CallBack() {
            @Override
            public void onSuccess(String s) {
                sendMessage(CONSTANT_JSON_RECEIVED, s);

            }

            @Override
            public void onError(String error) {

            }
        });

        mTcpServer.startServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage(int what, Object object) {
        sendMessageDelayed(what, object, 0);
    }

    private void sendMessageDelayed(int what, Object object, int delay) {
        if (mHandler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            if (object != null) {
                msg.obj = object;
            }
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUdpServer.stopServer();
        mTcpServer.stopServer();
    }

    public class JakkuBinder extends Binder implements IService {

        @Override
        public void setDataListener(DataListener listener) {
            mDataListener = listener;
        }

        @Override
        public String getJson() {
            return mJson;
        }

        @Override
        public void sendTcpPort() {
            String port = mTcpServer.getPort() + "";
            Log.d(TAG, "send TCP port=" + port);
            mUdpServer.sendMsg(port);
        }

        @Override
        public void rejectRequestTcpPort() {
            mUdpServer.sendMsg("reject");
        }
    }

    private void handleMoranResponse(String json) {
        if (json == null || json.equals("")) {
            return;
        }
        Log.d(TAG, "handleMoranResponse: " + json.length());
        Log.d(TAG, "handleMoranResponse: start=" + json.substring(0, 100) + " ,\nend=" + json.substring(json.length() - 200, json.length()));
        JsonData jsonData = JsonUtil.createJsonData(json);
        String domain = jsonData.getDomain();
        String tts = jsonData.getTts();
        String type = jsonData.getType();
        Log.d(TAG, "TTS=" + tts);
        switch (domain) {
            case NabooActions.Weather.TARGET_WEATHER:
                startTarget(WeatherActivity.class, json);
                break;
            case NabooActions.Music.TARGET_MUSIC:
                startTarget(MusicActivity.class, json);
                break;
            case NabooActions.Calendar.TARGET_CALENDAR:
                startTarget(CalenderActivity.class, json);
                break;
//            case NabooActions.Video.TARGET_VIDEO:
//                String intention = jsonData.getIntention();
//                if (intention.equals("watching") || intention.equals("detailing")) {
//                    startTarget(VideoDetailActivity.class, json);
//                } else if (intention.equals("searching")) {
//                    startTarget(VideoActivity.class, json);
//                }
//                break;
//            case NabooActions.Dish.TARGET_DISH:
//                startTarget(DishActivity.class, json);
//                break;
//            case NabooActions.Map.TARGET_MAP:
//            case NabooActions.Map.TARGET_RESTAURANT:
//            case NabooActions.Map.TARGET_VIEWSPOT:
//                startTarget(MapActivity.class, json);
//                break;
//            case NabooActions.TakeOut.TARGET_TAKEOUT:
//                if (type.equals(NabooActions.TakeOut.TARGET_TYPE_SHOP)) {
//                    startTarget(TakeoutActivity.class, json);
//                } else if (type.equals(NabooActions.TakeOut.TARGET_TYPE_MENU)) {
//                    startTarget(TakeoutSellerActivity.class, json);
//                }
//                break;
//            case NabooActions.TakeOut.TARGET_TAKEOUT_CAR:
//                startTarget(TakeoutCarActivity.class, json);
//                break;
            case NabooActions.Figure.TARGET_FIGURE:
                startTarget(FigureActivity.class, json);
                break;
            case NabooActions.News.TARGET_NEWS:
                startTarget(NewsActivity.class, json);
                break;
//            case NabooActions.Phone.TARGET_PHONE:
//                CallUtil.openVideoCall(getApplicationContext(), json);
//                break;
            case NabooActions.Cmd.TARGET_CMD:
                cmdType(type, json);
                break;
//            case NabooActions.Calendar.TARGET_CALENDAR:
//                startTarget(CalenderActivity.class, json);
//                break;
//            case NabooActions.Order.TARGET_ORDER_LIST:
//                sharedPreferencesUtil = new SharedPreferencesUtil(this);
//                sharedPreferencesUtil.saveUUID("Order", "Order");
//                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
//                    startTarget(LoginActivity.class, json);
//                    android.util.Log.d(TAG, "LoginActivity: " + json);
//                } else if (!EMClient.getInstance().isLoggedInBefore()) {
//                    startTarget(EaseLoginActivity.class, json);
//                    android.util.Log.d(TAG, "EaseLoginActivity: " + json);
//
//                } else {
//                    startTarget(OrderTimeActivity.class, json);
//                }
//                break;
//            default:
//                // 无界面部分
//                if (tts.contains("再见")) {
//                    tts = mVoiceBye[MathTool.randomValue(mVoiceBye.length)];
//                    isSessionEnd = true;
//                } else if (tts.equals("")) {
//                    tts = mVoiceRepeat[MathTool.randomValue(mVoiceRepeat.length)];
//                }
//                mTTSManager.speak(tts, true);
        }

    }

    /**
     * 启动目的领域，或执行领域中的指令
     *
     * @param clz  启动目标的类名
     * @param json 领域所需要的json
     */
    private void startTarget(Class<?> clz, String json) {
        if (isActivityRunning(clz.getName())) {
            if (mDataListener != null) {
                mDataListener.onJsonReceived(json);
            }
        } else {
            //部分json数据过大，不能使用intent携带，需getJson接口主动获取
            mJson = json;
            Intent intent = new Intent(getBaseContext(), clz);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * 通用指令
     *
     * @param type 指令类型
     * @param json 通用指令所需json
     */
    private void cmdType(String type, String json) {
        String name = topActivity();
        switch (type) {
//            case "up":
//            case "down":
//            case "to":
//            case "unmute":
//            case "mute":
//            case "max":
//            case "min":
//                AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//                int sysVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//                Log.d(TAG, "cmdType: 调前" + sysVoice);
//                sysVoice = AdjustVolume.adjustVolume(this, json, sysVoice);
//                Log.d(TAG, "cmdType: 调后" + sysVoice);
//                break;
//            case "main_page"://回到主页
//                startTarget(HomeActivity.class, "");
//                break;
//            case "show_version":
//                sharedPreferencesUtil = new SharedPreferencesUtil(this);
//                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
//                    startTarget(LoginActivity.class, json);
//                } else if (!EMClient.getInstance().isLoggedInBefore()) {
//                    startTarget(EaseLoginActivity.class, json);
//                } else {
//                    startTarget(Personal_center.class, json);
//                }
//
//                break;
//            case "usercenter": //打开个人中心
//                sharedPreferencesUtil = new SharedPreferencesUtil(this);
//                if (TextUtils.isEmpty(sharedPreferencesUtil.getUUID("uuid"))) {
//                    startTarget(LoginActivity.class, json);
//                } else if (!EMClient.getInstance().isLoggedInBefore()) {
//                    startTarget(EaseLoginActivity.class, json);
//                } else {
//                    startTarget(Personal_center.class, json);
//                }

//                break;
            default:
                if (!name.equals("")) {
                    if (mDataListener != null) {
                        mDataListener.onJsonReceived(json);
                    }
                }
                break;
        }
    }

    /**
     * 判断对应的Activity是否正在前台运行
     *
     * @param name Activity 名字
     * @return
     */
    public boolean isActivityRunning(String name) {
        boolean result;
        result = name.equals(topActivity());
        Log.d(TAG, "isActivityRunning: " + name + ", " + result);
        return result;
    }

    /**
     * 获取当前栈顶Activity的类名
     *
     * @return
     */
    private String topActivity() {
        String result = "";
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningTaskInfo> lists = (ArrayList<ActivityManager.RunningTaskInfo>) manager.getRunningTasks(1);
        if (lists != null && lists.size() > 0) {
            ComponentName cpn = lists.get(0).topActivity;
            result = cpn.getClassName();
        }
        Log.d(TAG, "topActivity: " + result);
        return result;
    }
}
