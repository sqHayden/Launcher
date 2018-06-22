package com.idx.jakku.figure;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.idx.jakku.BaseActivity;
import com.idx.jakku.R;
import com.bumptech.glide.Glide;
import com.idx.jakku.figure.data.Card;
import com.idx.jakku.figure.data.Figure;
import com.idx.jakku.figure.data.ImoranResponseFigure;
import com.idx.jakku.figure.data.Personal;
import com.idx.jakku.music.utils.ToastUtil;
import com.idx.jakku.news.util.MyTextView;
import com.idx.jakku.service.IService;
import com.idx.jakku.service.JakkuService;
import com.idx.jakku.utils.RoundImageUtils;
import com.idx.jakku.weather.utils.ImoranResponseToBeanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FigureActivity extends BaseActivity {

    private IService mIService;
    private List<Figure> mFigures = new ArrayList<>();
    private List<Personal> mPersonals = new ArrayList<>();
    private List<TextView> mTextViews = new ArrayList<>();
    private List<String> figureInfo = new ArrayList<>();
    private ImoranResponseFigure mImoranResponseFigure;//人物数据封装

    private LinearLayout mLinearLayout;
    private NestedScrollView mScrollView;
    private ImageView mFigureImageView;
    private TextView mFigureName;
    private MyTextView mFigureExperience;
    private TextView mLoading;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;
    private TextView mTextView;

    private String figureJson;//人物json
    private String constellation;
    private String bloodType;
    private String height;
    private String weight;
    private String bornDay;
    private String mFigureBirthday;
    private String info;
    private String figureExperience;//人物简介
    //控制自动滚动的线程
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    handler.removeCallbacks(ScrollRunnable);
                    break;
                default:
                    break;
            }
        }
    };
    private int i;
    //获取手指在屏幕上下滑动
    private float y1;
    private float y2;
    //每一行播报平均时间长
    private int averageTime;
    //计算自动滑动的总高度
    private int sun;
    //人物图片下方信息个数
    private int count;
    //临时json
    private String temporaryJson;
    //拿到service的连接
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIService = (IService) iBinder;
            mIService.setDataListener(FigureActivity.this);
            //拿到的人物json为空时的处理
            if ((mIService.getJson()).equals("") || mIService.getJson() == null) {
                if (temporaryJson != null) {
                    mScrollView.scrollTo(0, 0);
                }
            } else {
                figureJson = mIService.getJson();
                //人物json不为空对json分类处理
                mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(figureJson);
                if (mImoranResponseFigure.getFigureData().getDomain().equals("people")) {
                    if (mImoranResponseFigure != null && !mImoranResponseFigure.getFigureData().getFigureContent().getType().equals("")) {
                        if (mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure() != null) {
                            //存在person_info，长度为2时无内容，不为2时有内容
                            if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure().toString()).length() == 2) {
                                //无内容
                                oneMessage();
                            } else {
                                //person_info处理内容
                                parseToFigure(figureJson);
                                //插入数据
                                initData();
                            }
                        } else {
                            oneMessage();
                        }
                    } else {
                        oneMessage();
                    }
                }else {
                    mScrollView.scrollTo(0, 0);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onJsonReceived(String json) {
        //拿到service的json文件
        figureJson = json;
        mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(figureJson);
        //当json内容不为空时，判断是指令还是人物信息
        if (mImoranResponseFigure != null && !mImoranResponseFigure.getFigureData().getFigureContent().getType().equals("")) {
            //当domain为cmd时收到的是指令
            if (mImoranResponseFigure.getFigureData().getDomain().equals("cmd")) {
                chargeCmd(figureJson);
            } else {
                //收到的是人物信息，则清空之前的人物信息
                removeFigureInfo();
                if (ImoranResponseToBeanUtils.isImoranFigureNull(mImoranResponseFigure)) {
                    //当为person_info且长度为2时，无内容；否则有数据则解析数据，并将数据放入view
                    if ((mImoranResponseFigure.getFigureData().getFigureContent().getFigureReply().getFigure()).toString().length() == 2) {
                        oneMessage();
                    } else {
                        parseToFigure(figureJson);
                        initData();
                    }

                } else {
                    oneMessage();
                }

            }
        }else {
            oneMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_figure);
        initView();
    }
    private void initView() {
        mLinearLayout = findViewById(R.id.figure_layout);
        mLoading = findViewById(R.id.figure_loading);
        mScrollView = findViewById(R.id.figure_scroll);
        mFigureName = findViewById(R.id.figure_name);
        mFigureExperience = findViewById(R.id.figure_experience);
        mFigureImageView = findViewById(R.id.figure_pic);
        //将显示人物属性的组件绑定
        mTextView1 = findViewById(R.id.figure_info1);
        mTextViews.add(mTextView1);
        mTextView2 = findViewById(R.id.figure_info2);
        mTextViews.add(mTextView2);
        mTextView3 = findViewById(R.id.figure_info3);
        mTextViews.add(mTextView3);
        mTextView4 = findViewById(R.id.figure_info4);
        mTextViews.add(mTextView4);
        mTextView = findViewById(R.id.figure_info);
    }

    private void initData() {
        figureExperience = mPersonals.get(0).getBrief();
        //人物姓名
        mFigureName.setText(mPersonals.get(0).getName());
        //人物详情
        mFigureExperience.setText(figureExperience);
        //加载人物图片
        Glide.with(this).load(mPersonals.get(0).getPic())
                .transform(new RoundImageUtils(this, getResources().getDimensionPixelSize(R.dimen.figure_image_radius)))
                .error(R.drawable.figure_defult)
                .into(mFigureImageView);
        mLoading.setVisibility(View.GONE);
        mFigureName.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        setFigureInfo();
        //动态插入人物信息，按信息个数动态显示TextView的个数
        if (count == 0){
            mTextView1.setVisibility(View.GONE);
            mTextView2.setVisibility(View.GONE);
            mTextView3.setVisibility(View.GONE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 1){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.GONE);
            mTextView3.setVisibility(View.GONE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 2){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
            mTextView3.setVisibility(View.GONE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 3){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
            mTextView3.setVisibility(View.VISIBLE);
            mTextView4.setVisibility(View.GONE);
        }else if (count == 4){
            mTextView1.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
            mTextView3.setVisibility(View.VISIBLE);
            mTextView4.setVisibility(View.VISIBLE);
        }
        if (mTextView == null){
            mTextView .setVisibility(View.GONE);
        }else {
            mTextView.setVisibility(View.VISIBLE);
        }
        //播放进度相关状态初始化
        mScrollView.scrollTo(0,0);
        sun = 0;
        i = 0;
        //将上一次Handler移除
        handler.removeCallbacksAndMessages(null);
        //延迟执行当前Handler
        handler.postDelayed(ScrollRunnable,10000);
        //监听NestedScrollView滑动状态。动态改变自定义标题栏的背景色
        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >0) {
                    mFigureName.setBackgroundColor(getResources().getColor(R.color.figure_name_background_color));
                }else {
                    mFigureName.setBackgroundColor(0);
                }
            }
        });
        //监听手指在NestedScrollView的滑动
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    y1 = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP){
                    y2 = event.getY();
                    if ((y2 - y1) > 5 || (y1 - y2) > 5) {
                        i++;
                        handler.removeCallbacksAndMessages(null);
                        if (i == 1) {
                            ToastUtil.showToast(FigureActivity.this, getString(R.string.auto_reader_stop));
                        }
                    }

                }
                return false;
            }
        });
    }

    /**
     * 解析json数据
     * @param json 拿到json数据
     */
    private void parseToFigure(String json) {
        mImoranResponseFigure = ImoranResponseToBeanUtils.handleFigureData(json);
        if (ImoranResponseToBeanUtils.isImoranFigureNull(mImoranResponseFigure)) {
            mFigures = mImoranResponseFigure.getFigureData()
                    .getFigureContent().getFigureReply().getFigure();
            mPersonals = mFigures.get(0).getPersonal();
            mFigureBirthday = mPersonals.get(0).getBirthday();
            info = mPersonals.get(0).getBaikeInfo();
            temporaryJson = json;
            //提取人物百科中 生日、星座、身高、血型、体重信息
            if (info != null && info.length() > 0) {
                JSONObject object = null;
                List<Card> cards = new ArrayList<>();
                count = 0;
                try {
                    object = new JSONObject(info);
                    JSONArray array = object.getJSONArray("card");
                    //遍历拿到的array
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object1 = array.getJSONObject(i);
                        Card card = new Card();
                        card.setKey(object1.getString("key"));
                        card.setValue(object1.getString("value"));
                        cards.add(card);
                    }

                    for (Card card : cards) {
                        switch (card.getKey()) {
                            //星座
                            case "m1_constellation":
                                constellation = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //血型
                            case "m1_bloodtype":
                                bloodType = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //身高
                            case "m1_height":
                                height = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //体重
                            case "m1_weight":
                                weight = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                count++;
                                break;
                            //出生日期
                            case "m1_bornDay":
                                bornDay = card.getValue().subSequence(2, card.getValue().length() - 2).toString();
                                boolean status = bornDay.contains("（");
                                if (status) {
                                    String b = bornDay.substring(bornDay.indexOf("（"), bornDay.indexOf("）") + 1);
                                    bornDay = bornDay.replace(b, "");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理指令
     * @param json 指令json
     */
    private void chargeCmd(String json) {
        try {
            //获取指令
            String type = ImoranResponseToBeanUtils.handleNewsType(json);
            switch (type) {
                case "continue":
                    //继续播放
                    resume();
                    break;
                case "pause":
                    //暂停
                    pause();
                    break;
                case "back":
                    //返回
                    back();
                    break;
                default:
                    //不支持指令
                    //unDo();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //返回功能
    private void back(){
        finish();
    }

    //不支持指令
    private void unDo() {
        Toast.makeText(FigureActivity.this, getString(R.string.no_support_cmd), Toast.LENGTH_SHORT).show();
    }

    //提示已暂停
    private void pause() {
//        Toast.makeText(FigureActivity.this, getString(R.string.figure_pause), Toast.LENGTH_SHORT).show();
        handler.removeCallbacksAndMessages(null);
    }

    //继续播报新闻
    private void resume() {
        mScrollView.scrollTo(0, sun);
        handler.post(ScrollRunnable);
    }

    //加入人物百科中 生日、星座、身高、血型、体重信息
    private void setFigureInfo(){
        if (height != null){
            figureInfo.add(height);
        }
        if (weight != null){
            figureInfo.add(weight);
        }
        if(constellation != null){
            figureInfo.add(constellation);
        }
        if (bloodType != null){
            figureInfo.add(bloodType);
        }
        if (count > 0){
            for (int i=0; i<count; i++){
                mTextViews.get(i).setText(figureInfo.get(i));
            }
        }
        if (bornDay != null) {
            mTextView.setText(bornDay);
        }else if (mFigureBirthday != null){
            mTextView.setText(mFigureBirthday);
        }
    }
    //清空人物信息
    private void removeFigureInfo(){
        for (int i=0; i<count; i++){
            mTextViews.get(i).setText("");
        }
        mTextView.setText("");
        count = 0;
        constellation = null;
        height = null;
        weight = null;
        bloodType = null;
        bornDay = null;
        mFigureBirthday = null;
        figureInfo.clear();
        mFigureName.setText("");
        mFigureExperience.setText("");
    }

    /**
     * 返回的人物信息只有一句话
     */
    private void oneMessage(){
        String s = mImoranResponseFigure.getFigureData().getFigureContent().getDisplay();
        mLoading.setText(s);
        mFigureName.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.VISIBLE);
    }

    //实现scrollview的自动滚动
    private Runnable ScrollRunnable = new Runnable() {

        @Override
        public void run() {
            //计算需要滚动的高度
            int off = mFigureExperience.getMeasuredHeight() - mScrollView.getHeight();
            //计算一行内容的高度
            int scrollY = mFigureExperience.getMeasuredHeight()/mFigureExperience.getLineCount();
            //读一行需要花的大概时间
            averageTime = mFigureExperience.length()/mFigureExperience.getLineCount()*200;
            //当高度大于0时，进入

            if (off > 0) {
                mScrollView.scrollBy(0, 1);
                sun = sun + 1;
                //当滚动高度大于等于高度差时，退出线程；否则继续滚动
                if (sun >= off) {
                    Message message = new Message();
                    message.what = -1;
                    handler.sendMessage(message);
                } else {
                    handler.postDelayed(this, (averageTime / scrollY));
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, JakkuService.class);
        if (!isServiceRunning(getBaseContext(), JakkuService.class)) {
            startService(intent);
        }
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 解绑连接
     * 移除线程
     */
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        handler.removeCallbacksAndMessages(null);
    }
}