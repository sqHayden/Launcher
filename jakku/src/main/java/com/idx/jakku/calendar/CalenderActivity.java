package com.idx.jakku.calendar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.idx.calendarview.Calendar;
import com.idx.calendarview.CalendarLayout;
import com.idx.calendarview.CalendarView;
import com.idx.calendarview.LunarCalendar;
import com.idx.jakku.BaseActivity;
import com.idx.jakku.R;
import com.idx.jakku.calendar.presenter.Presenter;
import com.idx.jakku.data.JsonData;
import com.idx.jakku.data.JsonUtil;
import com.idx.jakku.service.IService;
import com.idx.jakku.service.JakkuService;




public class CalenderActivity extends BaseActivity implements View.OnClickListener, CalendarView.OnYearChangeListener, CalendarView.OnDateSelectedListener, Iview {

  //  private static final String TAG = CalendarFragment.class.getSimpleName();
  private static final String TAG="shaoqiCalender";


    private Presenter presenter;
    private Integer day;
    private TextView mYear;
    private TextView mWeek;
    private TextView mMonth;
    private TextView mDay;
    private TextView mlunar_calendar;
    private TextView mHoliday_calendar;
    int year;
    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mCurrentTime;
    TextView mTextDay;
    CalendarView mCalendarView;
    FrameLayout yearSelect;
    FrameLayout monthSelect;
    CalendarLayout mCalendarLayout;
    View mView;
    String time;
    private IService mIService;
    Boolean yearopen = false;
    String  yearNumber;
    private String monthnumber;
    private String json;
    private boolean Calendar_flag = false;
    private int year1;
    private int month;
    private int day1;
    private Gson gson;
    //农历天干
    private static String[] Gan={"甲","乙","丙","丁","戊","己","庚","辛","壬","癸"};

    //农历地支
    private static String[] Zhi={"子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"};

    private String date;
    private int day_date;
    private int month_date;
    private int year_date;


    private ServiceConnection conn = new ServiceConnection() {



        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(CalenderActivity.this);
            if(mIService.getJson()!=null&&!"".equals(mIService.getJson())) {
                Log.i("CalendarActivity", "onServiceConnected: 进入到Json获取");
                json = mIService.getJson();
                Log.d(TAG, "onServiceConnected: " + json);
                gson = new Gson();
                TimeBean root = gson.fromJson(json,TimeBean.class);
                //问时间
                if (root.getData().getContent().getReply().getTime() != null) {
                    if (!TextUtils.isEmpty(root.getData().getContent().getReply().getTime().get(0).getCity())) {
                        Log.d(TAG, "问时间: ");
                        date = root.getData().getContent().getReply().getTime().get(0).getDate();
                        showDate();

                    }
                }else if(root.getData().getContent().getReply().getDefaultX() != null) {
                    if (!TextUtils.isEmpty(root.getData().getContent().getReply().getDefaultX().get(0).getDay_val())) {
                        Log.d(TAG, "问节日: ");
                        date = root.getData().getContent().getReply().getDefaultX().get(0).getDay_val();
                        Log.d(TAG, "onServiceConnected: " + date);
                        showDate();
                    }
                    if (!TextUtils.isEmpty(root.getData().getContent().getReply().getDefaultX().get(0).getStr())) {
                        Log.d(TAG, "问天数: ");
                        if (!TextUtils.isEmpty(root.getData().getContent().getSemantic().getTIME().get(0))) {
                            date = root.getData().getContent().getSemantic().getTIME().get(0);
                            showDate();
                        }else {
                            presenter.selectmonth();
                            Log.d(TAG, "onServiceConnected: 点击月份");
                        }

                    }
                }else if (root.getData().getContent().getReply().getDate() != null){
                    if (!TextUtils.isEmpty(root.getData().getContent().getReply().getDate().get(0).getDate_str())){
                        Log.d(TAG, "问日期时间");
                        date = root.getData().getContent().getReply().getDate().get(0).getDate_str();
                        showDate();
                    }else {

                    }
                }

            }else {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //语音　显示　时间
    private void showDate(){
        year_date = Integer.parseInt(date.substring(0,4));
        month_date = Integer.parseInt(date.substring(5,7));
        day_date = Integer.parseInt(date.substring(8,10));

        Log.d(TAG, "showDate: " + String.valueOf(year_date));
        Log.d(TAG, "showDate: " + String.valueOf(month_date));
        Log.d(TAG, "showDate: " + String.valueOf(day_date));

        mTextYear.setText(String.valueOf(year_date));
        mTextMonthDay.setText(String.valueOf(month_date));
        mTextDay.setText(String.valueOf(day_date));
        mWeek.setText(getWeekByDateStr(year_date, month_date, day_date));
        mlunar_calendar.setText(getGanZhi(LunarCalendar.solarToLunar_year(year_date, month_date, day_date)) + convertNlMoeth(LunarCalendar.solarToLunar_month(year_date, month_date, day_date)) + LunarCalendar.cDay(LunarCalendar.solarToLunar(year_date, month_date, day_date)));
        mHoliday_calendar.setText(LunarCalendar.getLunarText_holiday(getBaseContext(), year_date, month_date, day_date));
        mCalendarView.selectCurrent(year_date, month_date, day_date);
        mCalendarView.scrollToCalendar(year_date, month_date, day_date);
    }



    @Override
    public void onJsonReceived(String json) {
        JsonData jsonUtil = JsonUtil.createJsonData(json);
        String tts = jsonUtil.getTts();
        if(json !=null&&!"".equals(json)) {
            Log.i("CalendarActivity", "onServiceConnected: 进入到Json获取");
            Log.d(TAG, "onServiceConnected: " + json);
            gson = new Gson();
            TimeBean root = gson.fromJson(json,TimeBean.class);
            //问时间
            if (root.getData().getContent().getReply().getTime() != null) {
                if (!TextUtils.isEmpty(root.getData().getContent().getReply().getTime().get(0).getCity())) {
                    Log.d(TAG, "问时间: ");
                    date = root.getData().getContent().getReply().getTime().get(0).getDate();
                    showDate();

                }
            }else if(root.getData().getContent().getReply().getDefaultX() != null) {
                if (!TextUtils.isEmpty(root.getData().getContent().getReply().getDefaultX().get(0).getDay_val())) {
                    Log.d(TAG, "问节日: ");
                    date = root.getData().getContent().getReply().getDefaultX().get(0).getDay_val();
                    showDate();
                }
                if (!TextUtils.isEmpty(root.getData().getContent().getReply().getDefaultX().get(0).getStr())) {
                    Log.d(TAG, "问天数: ");
                    if (!TextUtils.isEmpty(root.getData().getContent().getSemantic().getTIME().get(0))) {
                        date = root.getData().getContent().getSemantic().getTIME().get(0);
                        showDate();
                    }else {
                        presenter.selectmonth();
                        Log.d(TAG, "onServiceConnected: 点击月份");
                    }
                }
            }else if (root.getData().getContent().getReply().getDate() != null){
                if (!TextUtils.isEmpty(root.getData().getContent().getReply().getDate().get(0).getDate_str())){
                    Log.d(TAG, "问日期时间");
                    date = root.getData().getContent().getReply().getDate().get(0).getDate_str();
                    showDate();
                }
            }
        }else {
            Log.d(TAG, "onJsonReceived: 错误");
        }


        if (jsonUtil.getType().equals("back")){
            finish();
        }

        Log.d(TAG, "onJsonReceived: "+ tts);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jakku_calendar);
        Log.d(TAG, "onCreate: ");
        initView();
        if (savedInstanceState != null && savedInstanceState.getBoolean("year")){
            Log.d(TAG, "onActivityCreatedbb: ");
            yearNumber = savedInstanceState.getString("yearnumber");
            mCalendarView.showSelectLayout( Integer.valueOf(yearNumber).intValue());
            mTextMonthDay.setVisibility(View.GONE);
            mMonth.setVisibility(View.GONE);
            mTextYear.setText(yearNumber);
            yearopen = false;
        }else {
            Log.d(TAG, "onActivityCreatedcc: ");
            initData();
        }
        presenter = new Presenter(this,mCalendarView);
        initListener();

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(getBaseContext(), JakkuService.class), conn, BIND_AUTO_CREATE);
        if (Calendar_flag){
            Log.d(TAG, "onResume: 11111");
            presenter.selectmonth();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
        Calendar_flag = true;
    }

    private void initListener(){
        Log.d(TAG, "initListener: ");
        yearSelect.setOnClickListener(this);
        monthSelect.setOnClickListener(this);
        mCalendarView.setOnYearChangeListener(this);
        if (mTextMonthDay.getVisibility() != View.GONE){
            mCalendarView.setOnDateSelectedListener(this);
        }
        //date = String.valueOf(mCalendarView.getCurYear()) + String.valueOf(mCalendarView.getCurMonth());
        //day = mCalendarView.getCurDay();
    }
    private void initData() {
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
            mTextMonthDay.setText(String.valueOf(mCalendarView.getCurMonth()));
    }

    public void initView(){
        Log.d(TAG, "initView: ");
        mYear = findViewById(R.id.year);
        mMonth = findViewById(R.id.month);
        mDay = findViewById(R.id.day);
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextDay = findViewById(R.id.tv_day);
        mWeek = findViewById(R.id.week);
        mlunar_calendar = findViewById(R.id.lunar_calendar);
        mHoliday_calendar = findViewById(R.id.holiday_calendar);
        mCalendarView = findViewById(R.id.calendarView);
        yearSelect = findViewById(R.id.selectyear);
        monthSelect = findViewById(R.id.selectmonth);
        mCalendarLayout = findViewById(R.id.calendarLayout);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: " + yearopen);
        yearNumber = mTextYear.getText().toString();
        monthnumber = mTextMonthDay.getText().toString();
        outState.putBoolean("year",yearopen);
        outState.putString("yearnumber",yearNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (presenter != null){
            presenter.Destroy();
            presenter=null;
        }
        if (mCalendarView != null){
            mCalendarView.destroyDrawingCache();
            mCalendarView = null;
        }
        if (yearSelect != null){
            yearSelect = null;
        }
        if (monthSelect != null){
            monthSelect = null;
        }
        if (mCalendarLayout != null){
            mCalendarLayout = null;
        }
        if (mView != null){
            mView = null;
        }


    }
    @Override
    public void onClick(View view) {
            int id=view.getId();
            if(id==R.id.selectyear){
                presenter.selectyear();
            }
            if(id==R.id.selectmonth){
                presenter.selectmonth();
            }
    }


    /*
    * 点击年按钮
    * */
    @Override
    public void showyear(int year) {
        Log.d(TAG, "showyear: " + yearopen);
        this.year = year;
        mCalendarView.showSelectLayout(year);
        mTextMonthDay.setVisibility(View.GONE);
        mMonth.setVisibility(View.GONE);
        mTextDay.setVisibility(View.GONE);
        mDay.setVisibility(View.GONE);
        mWeek.setVisibility(View.GONE);
        mlunar_calendar.setVisibility(View.GONE);
        mHoliday_calendar.setVisibility(View.GONE);
        mTextYear.setText(String.valueOf(year));
        yearopen = false;
    }
    /*
    * 点击月按钮
    * */
    @Override
    public void showmonth(int year, int month, int day) {
        Log.d(TAG, "showmonth: " + yearopen);
        mCalendarView.selectCurrentMonth();
        mCalendarView.scrollToCalendar(year, month, day);
        mTextMonthDay.setVisibility(View.VISIBLE);
        mlunar_calendar.setVisibility(View.VISIBLE);
        mMonth.setVisibility(View.VISIBLE);
        mlunar_calendar.setVisibility(View.VISIBLE);
        mHoliday_calendar.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(String.valueOf(mCalendarView.getCurMonth()));
        mWeek.setText(getStringWeek(mCalendarView.getWeek()));
        mlunar_calendar.setText(getGanZhi(LunarCalendar.solarToLunar_year(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay())) + convertNlMoeth(LunarCalendar.solarToLunar_month(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay())) + LunarCalendar.cDay(LunarCalendar.solarToLunar(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay())));
        mHoliday_calendar.setText(LunarCalendar.getLunarText_holiday(getBaseContext(), mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay()));
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        }

    @Override
    public void onDateSelected(Calendar calendar) {
        
        Log.d(TAG, "onDateSelected: ");
        mTextYear.setVisibility(View.VISIBLE);
        mYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setVisibility(View.VISIBLE);
        mMonth.setVisibility(View.VISIBLE);
        mTextDay.setVisibility(View.VISIBLE);
        mDay.setVisibility(View.VISIBLE);
        mWeek.setVisibility(View.VISIBLE);
        mHoliday_calendar.setVisibility(View.VISIBLE);
        mlunar_calendar.setVisibility(View.VISIBLE);
        Log.d(TAG, "getResources().getConfiguration().locale.getCountry(): " + getResources().getConfiguration().locale.getCountry());

            Log.d(TAG, "点击的 day" + String.valueOf(calendar.getDay()));
            Log.d(TAG, "点击的 month" + String.valueOf(calendar.getMonth()));
            Log.d(TAG, "点击的 农历" + String.valueOf(calendar.getLunar()));
            Log.d(TAG, "点击的 week" + String.valueOf(calendar.getWeek()));

            mTextMonthDay.setText(String.valueOf(calendar.getMonth()));
            if (calendar.getDay() <= 31) {
                year1 = calendar.getYear();
                month = calendar.getMonth();
                day1 = calendar.getDay();
                mTextDay.setVisibility(View.VISIBLE);
                mDay.setVisibility(View.VISIBLE);
                mWeek.setVisibility(View.VISIBLE);
                mHoliday_calendar.setVisibility(View.VISIBLE);
                mlunar_calendar.setVisibility(View.VISIBLE);
                //在这里判断　天数显示是否正确
                getDaysByYearMonth(year1,month);
                Log.d(TAG, "onDateSelected:小于３１ 当前月的天数" + getDaysByYearMonth(year1,month));
                if (day1>getDaysByYearMonth(year1,month)){
                    day1 = getDaysByYearMonth(year1,month);
                }
                mTextDay.setText(String.valueOf(day1));

                mWeek.setText(getWeekByDateStr(year1, month, day1));
                int iYear = LunarCalendar.solarToLunar_year(year1, month, day1);   //获取农历年
                String ganzhi = getGanZhi(iYear);                //获得天干地支
                String iday = LunarCalendar.cDay(LunarCalendar.solarToLunar(year1, month, day1)); //获得农历日期
                String imonth = convertNlMoeth(LunarCalendar.solarToLunar_month(year1, month, day1)); //获得农历月份
                mlunar_calendar.setText(ganzhi + imonth + iday);
                Log.d(TAG, "onDateSelected: 节日" + LunarCalendar.getLunarText(getBaseContext(), calendar));
                mHoliday_calendar.setText(LunarCalendar.getLunarText_holiday(getBaseContext(), year1, month, day1));

            } else {
                Log.d(TAG, "滑动月份 " + year1 + " " + month + " " + day1);
                year1 = calendar.getYear();
                month = calendar.getMonth();
                day1 = calendar.getDay();
                Log.d(TAG, "onDateSelected　大于３１: 当前月的天数" + getDaysByYearMonth(year1,month));
                if (day1>getDaysByYearMonth(year1,month)){
                    day1 = getDaysByYearMonth(year1,month);
                }
                mTextDay.setText(String.valueOf(day1));
                mWeek.setText(getWeekByDateStr(year1, month, day1));
                int iYear = LunarCalendar.solarToLunar_year(year1, month, day1);   //获取农历年
                String ganzhi = getGanZhi(iYear);                //获得天干地支
                String iday = LunarCalendar.cDay(LunarCalendar.solarToLunar(year1, month, day1)); //获得农历日期
                String imonth = convertNlMoeth(LunarCalendar.solarToLunar_month(year1, month, day1)); //获得农历月份
                mlunar_calendar.setText(ganzhi + "年" + imonth + "月" + iday);
                mHoliday_calendar.setText(LunarCalendar.getLunarText_holiday(getBaseContext(), year1, month, day1));
            }

            mTextYear.setText(String.valueOf(calendar.getYear()));
    }

    @Override
    public void onYearChange(int year) {
            mTextYear.setText(String.valueOf(year));

    }
    public Integer ChineseEra(int iYear) {
        if ((iYear < 2050) && (iYear > 1901)) {
            return iYear;
        } else{
            return 1981;
        }
    }

    public String getGanZhi(int year) {
        int temp;
        temp = Math.abs(ChineseEra(year) - 1924);
        return Gan[temp % 10] + Zhi[temp % 12] + "年";
    }




    public static String getWeekByDateStr(int year,int month,int day) {


        java.util.Calendar c = java.util.Calendar.getInstance();

        c.set(java.util.Calendar.YEAR, year);
        c.set(java.util.Calendar.MONTH, month - 1 );
        c.set(java.util.Calendar.DAY_OF_MONTH, day);

        String week = "";
        int weekIndex = c.get(java.util.Calendar.DAY_OF_WEEK);

        switch (weekIndex)
        {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
        }
        return week;
    }

    private String getStringWeek(int week){
        switch (week){
            case 0:
                return "周日";

            case 1:
                return "周一";

            case 2:
                return "周二";

            case 3:
                return "周三";

            case 4:
                return "周四";

            case 5:
                return "周五";

            case 6:
                return "周六";

            case 7:
                return "周日";
        }
        return null;
    }


    public static String minCaseMax(String str){
        switch (Integer.parseInt(str)) {
            case 0:
                return "零";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";

            default:
                return "null";
        }
    }

    private static String[] monthNong = {"正", "二", "三", "四", "五", "六",
            "七", "八", "九", "十", "十一", "十二"};

    public static String convertNlMoeth(int month){
        String maxMonth = "";
        maxMonth = monthNong[month - 1];
        return maxMonth + "月";
    }


    public static int getDaysByYearMonth(int year, int month) {

        java.util.Calendar a = java.util.Calendar.getInstance();
                 a.set(java.util.Calendar.YEAR, year);
                 a.set(java.util.Calendar.MONTH, month - 1);
                 a.set(java.util.Calendar.DATE, 1);
                 a.roll(java.util.Calendar.DATE, -1);
                 int maxDate = a.get(java.util.Calendar.DATE);
                 return maxDate;
             }

    //根据语音进入日历　返回json数据　年月日　显示结果
    private void showCalendar(int year,int month , int day){

    }




}
