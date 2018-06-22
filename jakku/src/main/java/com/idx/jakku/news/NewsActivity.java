package com.idx.jakku.news;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idx.jakku.BaseActivity;
import com.idx.jakku.R;
import com.idx.jakku.data.JsonData;
import com.idx.jakku.data.JsonUtil;
import com.idx.jakku.music.utils.ToastUtil;
import com.idx.jakku.news.data.NewsDetail;
import com.idx.jakku.news.util.MyTextView;
import com.idx.jakku.service.DataListener;
import com.idx.jakku.service.IService;
import com.idx.jakku.service.JakkuService;
import com.idx.jakku.utils.HtmlUtils;
import com.idx.jakku.weather.utils.ImoranResponseToBeanUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends BaseActivity {

    private static final String TAG = "NewsActivity";
    private IService mIService;
    private int newsIndex;
    private int number;
    private String newsJson;
    private List<NewsDetail> mList=new ArrayList<>();
    private List<NewsDetail> srcList = null;
    private List<NewsDetail> list = new ArrayList<>();
    private String[] list1 = null;
    private RelativeLayout mRelativeLayout;
    private TextView title;
    private MyTextView content;
    private TextView nextNews;
    private TextView previousNews;
    private TextView source;
    private TextView date;
    private TextView toolBar;
    private TextView error;
    private RelativeLayout mLayout;
    private View line;
    private int i;
    //每一行播报平均时间长
    private int averageTime;
    //新闻标签
    private Button label;
    private android.support.v4.widget.NestedScrollView mScrollView;
    //播放的语句
    private String newsContent;

    //控制滚动进程
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    handler.removeCallbacks(NestedScrollView);
                    break;
                default:
                    break;
            }

        }
    };

    //获取手指在屏幕上下滑动
    private float y1;
    private float y2;

    //计算自动滑动的高度
    private int sun;
    //临时json
    private String temporaryJson;

    //拿到service的连接
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(NewsActivity.this);
            //json为空时（一般返回时进入）
            if ((mIService.getJson()).equals("") || mIService.getJson() == null){
                if (temporaryJson != null) {
                    mScrollView.scrollTo(0, 0);
                }
            } else{
                //拿json文件
                newsJson = mIService.getJson();

                //解析json
                parseToNews(newsJson);
                //判断解析后的结果，有数据插入数据；无数据显示无相关新闻
                if (list != null) {
                    if (list.toString().length() > 2 && mList.size() > 0) {
                        //清空内容
                        clearTextViewContent();
                        //插入数据
                        initData();
                    } else {
                        toolBar.setVisibility(View.INVISIBLE);
                        line.setVisibility(View.INVISIBLE);
                        mScrollView.setVisibility(View.INVISIBLE);
                        mRelativeLayout.setVisibility(View.INVISIBLE);
                        error.setVisibility(View.VISIBLE);
                    }
                }else {
                    mScrollView.scrollTo(0,0);
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.e(TAG, "onBindingDied: 有异常");
        }
    };

    @Override
    public void onJsonReceived(String json) {
        //拿到json文件
        newsJson = json;
        String cmd = ImoranResponseToBeanUtils.handleDomain(newsJson);
        //拿到的是指令，执行指令
        if (cmd.equals("cmd")){
            chargeCmd(newsJson);
        }
        //拿到的是新闻内容，刷新ListView内容，并回滚到顶部；内容为空显示暂无相关新闻
        else {
            list = ImoranResponseToBeanUtils.handleNewsData(newsJson);
            if (list != null && list.size() > 0) {
                if (list1 != null) {
                    label.setText("");
                    list1 = null;
                    label.setVisibility(View.INVISIBLE);
                }
                mScrollView.setVisibility(View.VISIBLE);
                mRelativeLayout.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                parseToNews(newsJson);
                clearTextViewContent();
                initData();
            } else {
                handler.removeCallbacksAndMessages(null);
                toolBar.setVisibility(View.INVISIBLE);
                label.setVisibility(View.INVISIBLE);
                line.setVisibility(View.INVISIBLE);
                mScrollView.setVisibility(View.INVISIBLE);
                mRelativeLayout.setVisibility(View.INVISIBLE);
                error.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        //绑定组件
        initView();
    }


    /**
     * 新闻类的控件的绑定
     */
    private void initView(){
        title = findViewById(R.id.news_new_title);
        content = findViewById(R.id.news_new_content);
        previousNews = findViewById(R.id.previous);
        nextNews = findViewById(R.id.next);
        source = findViewById(R.id.source);
        date = findViewById(R.id.publish_date);
        toolBar= findViewById(R.id.toolbar);
        line = findViewById(R.id.toolbar_line);
        label = findViewById(R.id.news_label);
        error = findViewById(R.id.news_error);
        mScrollView = findViewById(R.id.nested_scrollview);
        mLayout = findViewById(R.id.news_relative);
        mRelativeLayout = findViewById(R.id.navigation_bar_view);
    }

    /**
     * 将数据放入指定的控件中
     */
    private void initData(){
        //处理新闻内容
        newsContent = HtmlUtils.filterHtml(mList.get(0).getContent().replace("\u3000", "")).replace("&nbsp;", "");
        newsContent = (newsContent.replace(" ", "")).replace("\n","");
        //新闻标题
        title.setText(mList.get(0).getTitle());
        //新闻内容
        content.setText("\u3000\u3000"+newsContent);
        //新闻来源
        source.setText(getString(R.string.source)+mList.get(0).getSource());
        //新闻日期
        date.setText(mList.get(0).getPublishDate());
        toolBar.setText(mList.get(0).getTitle());
        mScrollView.scrollTo(0, 0);
        //移除上一个Handler
        handler.removeCallbacksAndMessages(null);
        //初始化参数
        sun = 0;
        i = 0;
        //Handler延迟
        handler.postDelayed(NestedScrollView,4600);
        /*
         * 监听手指的Touch事件
         */
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
                            ToastUtil.showToast(NewsActivity.this, getString(R.string.auto_reader_stop));
                        }
                    }
                }
                return false;
            }
        });
        //是否存在标签
        if (list1 != null){
            label.setText(list1[0]);
            label.setVisibility(View.VISIBLE);
        }
        //控制上一条的显示，及点击事件
        if (newsIndex == 0){
            previousNews.setVisibility(View.INVISIBLE);
        }else if(newsIndex > 0){
            previousNews.setVisibility(View.VISIBLE);

        }
        //控制下一条的显示，及点击事件
        if (newsIndex == (srcList.size()-1)){
            nextNews.setVisibility(View.INVISIBLE);
        }else if (newsIndex >= 0 && newsIndex < srcList.size()-1){
            nextNews.setVisibility(View.VISIBLE);
        }
        /*
         * scrollview 滚动的监听事件
         * 判断滚动高度，决定自定义title bar是否显示
         * 控制标签的显示
         */
        mScrollView.setOnScrollChangeListener(new android.support.v4.widget.NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(android.support.v4.widget.NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >60){
                    toolBar.setBackgroundColor(getResources().getColor(R.color.news_background));
                    toolBar.getBackground().setAlpha(0);
                    toolBar.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    toolBar.setText(title.getText());
                    label.setVisibility(View.INVISIBLE);
                }else if (scrollY < 60){
                    toolBar.setVisibility(View.INVISIBLE);
                    line.setVisibility(View.INVISIBLE);
                }
                if (scrollY == 0 && list1 != null){
                    label.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    /**
     * 1.拿到当前的内容；
     * 2.当新闻内容list大于1时存入srcList变量中
     * 3.当新闻内容list等于1时，判断有无NewIndex：
     * 有：根据拿到的NewIndex的值添加到mList中
     * 无：直接添加到mList中
     */
    private void parseToNews(String json) {
        list = ImoranResponseToBeanUtils.handleNewsData(json);
        if (ImoranResponseToBeanUtils.handleLabel(json) != null ){
            list1 = ImoranResponseToBeanUtils.handleLabel(json);
        }
        if (list != null && list.size() > 1){
            temporaryJson = json;
            //清空srcList操作
            if (srcList!=null){
                srcList.clear();
                mList.clear();
            }
            srcList = list;
            newsIndex = 0;
            mList.add(srcList.get(0));

        } else if(list != null && list.size() == 1){
            //找到其在srcList中的位置
            number = ImoranResponseToBeanUtils.handleNewsIndex(json);
            if (number == 0) {
                newsIndex = number;
                mList.add(list.get(0));
                temporaryJson = json;
            }else {
                newsIndex = number - 1;
                if (srcList != null && srcList.size() >= number) {
                    //清空mList操作
                    if (mList != null) {
                        mList.clear();
                    }
                    mList.add(srcList.get(newsIndex));
                }
            }

        }
    }

    /**
     * 获取指令，执行相应的方法
     */
    private void chargeCmd(String json) {
        try {
            //获取指令
            String type = ImoranResponseToBeanUtils.handleNewsType(json);
            switch (type) {
                case "next":
                    //下一条
                    next();
                    break;
                case "previous":
                    //上一条
                    previous();
                    break;
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
                    //无对应指令操作
//                    unDo();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unDo() {
        Toast.makeText(NewsActivity.this, getString(R.string.no_support_cmd), Toast.LENGTH_SHORT).show();
    }

    private void back() {
        finish();
    }

    //提示已暂停
    private void pause() {
//        Toast.makeText(NewsActivity.this, getString(R.string.news_pause), Toast.LENGTH_SHORT).show();
        handler.removeCallbacksAndMessages(null);
    }

    //继续播报新闻
    private void resume() {
        mScrollView.scrollTo(0,sun);
        handler.post(NestedScrollView);
    }

    //上一条新闻
    private void previous() {
        newsIndex--;
        if (newsIndex >= 0) {
            if (mList != null){
                mList.clear();
            }
            mList.add(srcList.get(newsIndex));
            initData();
        }else {
            newsIndex++;
            Toast.makeText(NewsActivity.this, getString(R.string.no_previous_news),Toast.LENGTH_SHORT).show();
        }
    }

    //下一条新闻
    private void next() {
        newsIndex++;
        if(newsIndex >=0 && newsIndex < srcList.size()) {
            if (mList != null) {
                mList.clear();
            }
            mList.add(srcList.get(newsIndex));
            initData();
        }else {
            newsIndex--;
            Toast.makeText(NewsActivity.this, getString(R.string.no_next_news),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 清空TextView中的内容
     */
    private void clearTextViewContent(){
        title.setText("");
        content.setText("");
        source.setText("");
        date.setText("");
        toolBar.setText("");
        label.setText("");
    }

    //实现scrollview的自动滚动
    private Runnable NestedScrollView = new Runnable() {

        @Override
        public void run() {
            int off = mLayout.getMeasuredHeight() - mScrollView.getHeight();
            int scrollY = 0;
            if (content.getLineCount() > 100){
                scrollY = content.getMeasuredHeight()/(content.getLineCount()-2);
            }else if (content.getLineCount() > 50){
                scrollY = content.getMeasuredHeight()/(content.getLineCount()-1);
            }else {
                scrollY = content.getMeasuredHeight()/content.getLineCount();
            }

            averageTime = content.length()/content.getLineCount()*200;
            if (off > 0) {
                mScrollView.scrollBy(0, 1);
                sun = sun + 1;
                if (sun >= off) {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } else {
                    handler.postDelayed(this, (averageTime/scrollY));
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

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        handler.removeCallbacksAndMessages(null);
    }
}