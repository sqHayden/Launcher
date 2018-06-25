package com.idx.jakku.weather;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idx.jakku.BaseActivity;
import com.idx.jakku.R;
import com.idx.jakku.data.JsonData;
import com.idx.jakku.data.JsonUtil;
import com.idx.jakku.service.IService;
import com.idx.jakku.service.JakkuService;
import com.idx.jakku.weather.adapter.WeatherAdapter;
import com.idx.jakku.weather.data.ContentWeather;
import com.idx.jakku.weather.data.ImoranResponse;
import com.idx.jakku.weather.data.Summary;
import com.idx.jakku.weather.data.Weather;
import com.idx.jakku.weather.utils.HandlerWeatherUtil;
import com.idx.jakku.weather.utils.ImoranResponseToBeanUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends BaseActivity {
    private static final String TAG="shaoqiWeather";
    private IService mIService;
    private ImoranResponse imoranResponse;
    private List<Weather> mWeather = new ArrayList<>();
    private TextView weather_current_time, weather_current_week;
    private ImageView weather_now_icon;
    private TextView weather_city;
    private TextView weather_now_cond_txt, weather_current_temp;
    private TextView weather_current_aqi, weather_current_rays;
    private RelativeLayout relativeLayout;
    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private int[] mCurrentDate;
    private Summary mSummary;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private String mCity;
    private String mJson;
    private boolean flag;
    private Handler handler = new Handler();
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(WeatherActivity.this);
            mJson = mIService.getJson();
            if (mJson != null && (!mJson.equals(""))) {
                pauseJson(mJson);
                if (mWeather != null && mWeather.size() > 0) {
                    findCurrentDates(imoranResponse);
                    updateView(imoranResponse.getDataWeather().getContent());
                    updateAdapter();
                    if (flag &&mCurrentDate.length > 1) {
                        Log.i(TAG, "onJsonReceived: 切换周日界面");
                        handler.postDelayed(runnable, 8000);
                    }
                } else {
                    noMessage();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.jakku_weather);
        setListener();
        flag = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, JakkuService.class);
        if (!isServiceRunning(getBaseContext(), JakkuService.class)) {
            startService(intent);
        }
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConn);
    }

    @Override
    public void onJsonReceived(String json) {
        mJson = json;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (type.equals("weather")) {
                pauseJson(json);
                if (mWeather != null && mWeather.size() > 0) {
                    findCurrentDates(imoranResponse);
                    dialogDismiss();
                    updateAdapter();
                    updateView(imoranResponse.getDataWeather().getContent());
                    if (flag &&mCurrentDate.length > 1) {
                        Log.i(TAG, "onJsonReceived: 切换周日界面");
                        handler.postDelayed(runnable, 8000);
                    }
                }
            } else if (domain.equals("cmd") && type.equals("back")) {
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        weather_current_time = findViewById(R.id.weather_current_time);
        weather_current_week = findViewById(R.id.weather_current_week);
        weather_now_icon = findViewById(R.id.weather_now_icon);
        weather_city = findViewById(R.id.weather_city);
        weather_now_cond_txt = findViewById(R.id.weather_now_cond_txt);
        weather_current_temp = findViewById(R.id.weather_current_temp);
        weather_current_aqi = findViewById(R.id.weather_current_aqi);
        weather_current_rays = findViewById(R.id.weather_current_rays);
        recyclerView = findViewById(R.id.weather_recyclerView);
        relativeLayout = findViewById(R.id.weather_background);
    }

    //解析json
    private void pauseJson(String json) {
        imoranResponse = ImoranResponseToBeanUtils.handleWeatherData(json);
        if (ImoranResponseToBeanUtils.isImoranWeatherNull(imoranResponse)) {
            mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
        }
    }

    private void updateView(ContentWeather contentWeather) {
        try {
            if (contentWeather.getReply().getWeather() != null && contentWeather.getReply().getWeather().size() > 0) {
                if (mWeather != null && mWeather.size() > 0 && mCurrentDate != null) {
                    weather_current_time.setText((contentWeather.getReply().getWeather().get(mCurrentDate[0]).getDate()));
                    weather_current_week.setText(HandlerWeatherUtil.parseDateWeek(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[0]).getDate()));
                    weather_now_icon.setImageResource(HandlerWeatherUtil.getWeatherImageResource(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[0]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_city.setText(contentWeather.getReply().getWeather().get(mCurrentDate[0]).getCity());
                    weather_now_cond_txt.setText(contentWeather.getReply().getWeather().get(mCurrentDate[0]).getWeatherDetail()
                            .getWeatherStateCode().getTxt_d());
                    relativeLayout.setBackgroundResource(HandlerWeatherUtil.getWeatherBackground(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[0]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_current_temp.setText(contentWeather.getReply().getWeather().get(mCurrentDate[0]).getWeatherDetail().getTemperature().getMin()
                            + "℃ ~ " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getWeatherDetail().getTemperature()
                            .getMax() + "℃");
                    weather_current_aqi.setText(getResources().getString(R.string.weather_aqi) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getAqiQuality().getAqi()
                            + " " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getAqiQuality().getQlty());
                    weather_current_rays.setText(getResources().getString(R.string.weather_rays) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[0]).getSuggestion().getUv().getBrf());
                }
            } else {
                noMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //拿到返回的日期信息
    private void findCurrentDates(ImoranResponse imoranResponse) {
        try {
            mSummary = imoranResponse.getDataWeather().getContent().getSummary();
            mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
            mCurrentDate = new int[mSummary.getDates().length];
            for (int i = 0; i < mWeather.size(); i++) {
                for (int j = 0; j < mSummary.getDates().length; j++) {
                    if (mSummary.getDates()[j].equals(mWeather.get(i).getDate())) {
                        mCurrentDate[j] = i;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "run: ");
            if (flag && mCurrentDate.length > 1) {
                Log.i(TAG, "run: 切换周日界面");
                update(imoranResponse.getDataWeather().getContent());
                updateAdapter();
            }
        }
    };

    //对周末天气情况,进行第二次更新
    private void update(ContentWeather contentWeather) {
        try {
            if (contentWeather != null) {
                if (mWeather != null && mWeather.size() > 0) {
                    weather_current_time.setText((contentWeather.getReply().getWeather().get(mCurrentDate[1]).getDate()));
                    weather_current_week.setText(HandlerWeatherUtil.parseDateWeek(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[1]).getDate()));
                    weather_now_icon.setImageResource(HandlerWeatherUtil.getWeatherImageResource(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[1]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_city.setText(contentWeather.getReply().getWeather().get(mCurrentDate[1]).getCity());
                    weather_now_cond_txt.setText(contentWeather.getReply().getWeather().get(mCurrentDate[1]).getWeatherDetail()
                            .getWeatherStateCode().getTxt_d());
                    relativeLayout.setBackgroundResource(HandlerWeatherUtil.getWeatherBackground(Integer.parseInt(contentWeather.getReply().getWeather()
                            .get(mCurrentDate[1]).getWeatherDetail().getWeatherStateCode().getCodeDay())));
                    weather_current_temp.setText(contentWeather.getReply().getWeather().get(mCurrentDate[1]).getWeatherDetail().getTemperature().getMin()
                            + "℃ ~ " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getWeatherDetail().getTemperature()
                            .getMax() + "℃");
                    weather_current_aqi.setText(getResources().getString(R.string.weather_aqi) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getAqiQuality().getAqi()
                            + " " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getAqiQuality().getQlty());
                    weather_current_rays.setText(getResources().getString(R.string.weather_rays) + " " + contentWeather.getReply().getWeather().get(mCurrentDate[1]).getSuggestion().getUv().getBrf());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //数据为空时的界面处理
    private void noMessage() {
        JsonData jsonData = JsonUtil.createJsonData(mJson);
        getLocations(jsonData);
//        Log.i(TAG, "noMessage: " + mCity);
        if (mCity != null) {
            builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.weather_not_find1) + mCity +
                    getResources().getString(R.string.weather_not_find2));
            builder.setPositiveButton(getResources().getString(R.string.weather_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
            relativeLayout.setBackgroundResource(R.mipmap.weather_bg);
        }
    }

    //状态栏消失
    private void dialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    //得到城市名
    private void getLocations(JsonData jsonData) {
        try {
            JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
            JSONArray locations = semantic.getJSONArray("locations");
            mCity = locations.get(0).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateAdapter() {
        //设置横向滑动
        if (mWeather != null && mWeather.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(WeatherActivity.this, LinearLayoutManager.HORIZONTAL, false));
            if (imoranResponse.getDataWeather().getContent().getReply().getWeather() != null) {
                mWeather = imoranResponse.getDataWeather().getContent().getReply().getWeather();
                weatherAdapter = new WeatherAdapter(mWeather);
                recyclerView.setAdapter(weatherAdapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        if (handler != null) {
            handler = null;
        }
        if (runnable != null) {
            runnable = null;
        }
    }
}
