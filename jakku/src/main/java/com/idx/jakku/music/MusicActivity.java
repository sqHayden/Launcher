package com.idx.jakku.music;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.idx.jakku.BaseActivity;
import com.idx.jakku.R;
import com.idx.jakku.data.JsonData;
import com.idx.jakku.data.JsonUtil;
import com.idx.jakku.music.adapter.MusicAdapter;
import com.idx.jakku.music.data.ContentMusic;
import com.idx.jakku.music.data.ImoranResponseMusic;
import com.idx.jakku.music.data.Song;
import com.idx.jakku.music.utils.ToastUtil;
import com.idx.jakku.service.DataListener;
import com.idx.jakku.service.IService;
import com.idx.jakku.service.JakkuService;
import com.idx.jakku.weather.utils.ImoranResponseToBeanUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends BaseActivity {
    private static final String TAG = MusicActivity.class.getSimpleName();
    private IService mIService;
    //开始时间，音乐时长，音乐名，歌手
    private TextView mStartText, mEndText, mTitle, mArtist;
    private SeekBar mSeekbar;
    //音乐暂停播放按钮，音乐播放模式按钮
    private ImageView mPlayPause, mNext, mPrevious;
    //背景图片
    private ImageView mBackground;
    //解析蓦然json数据
    private ImoranResponseMusic imoranResponseMusic;
    private ContentMusic contentMusic;
    private String mJson;
    //音乐对象
    private List<Song> mSong = new ArrayList<>();
    //音乐播放列表
    private int mSongIndex;
    private MusicAdapter mMusicAdapter;
    private ListView mListView;
    private static final int MSG_VIEW_UPDATE = 0X001;
    private static final int MSG_PAUSE = 0X002;

    //线程标志位
    private boolean mFlag;
    //蓦然json数据
    private JsonData jsonData;
    private String mIndexNumber;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIService = (IService) service;
            mIService.setDataListener(MusicActivity.this);
            mJson = mIService.getJson();
            if (mJson != null && (!mJson.equals(""))) {
                getDomainType(mJson);
                parseToMusic(mJson);
                if (mSong != null && mSong.size() > 0) {
                    mSongIndex=0;
                    sendMessageUpdateView();
                }else {
                    noMessage();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFlag) {
                switch (msg.what) {
                    case MSG_VIEW_UPDATE:
                        updateMusicList();
                        updateImageView();
                        updateView();
                        mPlayPause.setImageResource(R.mipmap.music_play);
                        mListView.smoothScrollToPosition(mSongIndex);
                        mListView.setSelection(mSongIndex);
                        break;
                    case MSG_PAUSE:
                        mPlayPause.setImageResource(R.mipmap.music_pause);
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        setListener();
        mFlag = true;
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
        jsonData = JsonUtil.createJsonData(json);
        mJson = json;
        try {
            JSONObject jsonObject = new JSONObject(json);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (type.equals("song")) {
                getDomainType(json);
                getIndexNumber(jsonData);
                Log.i(TAG, "onJsonReceived: 进入music");
            } else if (domain.equals("cmd")) {
                Log.i(TAG, "cmd");
                dealwithAction();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        mTitle = findViewById(R.id.title);
        mArtist = findViewById(R.id.artist);
//        mStartText = findViewById(R.id.startText);
//        mEndText = findViewById(R.id.endText);
//        mSeekbar = findViewById(R.id.seekBar1);
        mPlayPause = findViewById(R.id.play_pause);
        mBackground = findViewById(R.id.background_image);
        mNext = findViewById(R.id.next);
        mPrevious = findViewById(R.id.prev);
        mListView = findViewById(R.id.music_list);
    }

    //解析json数据
    private void parseToMusic(String json) {
        imoranResponseMusic = ImoranResponseToBeanUtils.handleMusicData(json);
        if (ImoranResponseToBeanUtils.isImoranMusicNull(imoranResponseMusic)) {
            contentMusic = imoranResponseMusic.getMusicData().getMusicContent();
            if (contentMusic.getMusicReply().getSongs() != null && contentMusic.getMusicReply().getSongs().size() > 0) {
                //拿到音乐列表，存入List中
                mSong = contentMusic.getMusicReply().getSongs();
            }
        }
    }

    private void updateMusicList() {
        if (mSong != null && mSong.size() > 0) {
            mMusicAdapter = new MusicAdapter(mSong);
            mListView.setAdapter(mMusicAdapter);
            mMusicAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(mSongIndex);
            mListView.setSelection(mSongIndex);
            Log.i(TAG, "mSongIndex= " + mSongIndex);
        }
    }

    //界面显示
    private void updateView() {
        try {
            if (mSong != null && mSong.size() > 0) {
                mMusicAdapter.setClick(true);
                mMusicAdapter.setCurrentItem(mSongIndex);
                mMusicAdapter.notifyDataSetChanged();
                mTitle.setText(mSong.get(mSongIndex).getName());
                Log.i(TAG, "mSongIndex= " + mSongIndex);
                mTitle.setSelected(true);
                mTitle.setFocusable(true);
                mTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTitle.setMarqueeRepeatLimit(-1);
                if (mSong.get(mSongIndex).getSinger() != null && mSong.get(mSongIndex).getSinger().length > 0) {
                    mArtist.setText(mSong.get(mSongIndex).getSinger()[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新专辑图片
    private void updateImageView() {
        if (mSong != null && mSong.size() > 0) {
            Log.i(TAG, "mSongIndex= " + mSongIndex);
            String url = mSong.get(mSongIndex).getPicUrl();
            Glide.with(this).load(url)
                    .placeholder(R.drawable.music_default)
                    .error(R.drawable.music_default)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(347, 347)
                    .into(mBackground);
        }
    }

    //无数据时处理
    private void noMessage() {
        ToastUtil.showToast(this, getResources().getString(R.string.music_not_find));
    }

    //对domain是音乐,但是返回是movie的情况进行规避
    private void getDomainType(String mJson) {
        try {
            JSONObject jsonObject = new JSONObject(mJson);
            String domain = jsonObject.getJSONObject("data").getString("domain");
            String type = jsonObject.getJSONObject("data").getJSONObject("content").getString("type");
            if (domain.equals("music") && type.equals("movie")) {
                noMessage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //判断播放指令
    private void dealwithAction() {
        String type = jsonData.getType();
        Log.i(TAG, "dealwithAction: type=" + type);
        switch (type) {
            case "next_song":
                //下一首
                nextPrevious(mSongIndex+1);
                break;
            case "last_song":
                //上一首
                nextPrevious(mSongIndex-1);
                break;
            case "pause":
            case "end":
                //暂停
                sendMessagePause();
                break;
            case "continue":
            case "play":
                sendMessageUpdateView();
                break;
            case "next":
                //换一组
                break;
            case "skipto_start":
                //从头播放
                break;
            case "back":
                finish();
                break;
            default:
                break;
        }
    }
    //下一首
    private void nextPrevious(int position){
        if (mSong.size()<=0){
            return;
        }
        if (position < 0) {
            position = mSong.size() - 1;
        } else if (position >= mSong.size()) {
            position = 0;
        }
        mSongIndex = position;
        sendMessageUpdateView();
    }

    //播放第几首
    private void getIndexNumber(JsonData jsonData) {
        try {
            JSONObject semantic = jsonData.getContent().getJSONObject("semantic");
            mIndexNumber = semantic.getString("index_number");
            //拿到位置信息，播放该音乐
            mSongIndex = Integer.parseInt(mIndexNumber) - 1;
            Log.i(TAG, "getIndexNumber: mSongIndex="+mSongIndex);
            mFlag=true;
            sendMessageUpdateView();
        } catch (JSONException e) {
            parseToMusic(mJson);
            if (mSong != null && mSong.size() > 0) {
                mSongIndex=0;
                mFlag = true;
                sendMessageUpdateView();
            }
            e.printStackTrace();
        }
    }

    private void sendMessageUpdateView() {
        Message msg = Message.obtain();
        msg.what = MSG_VIEW_UPDATE;
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    private void sendMessagePause() {
        Message msg = Message.obtain();
        msg.what = MSG_PAUSE;
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFlag = false;
        if (handler != null) {
            handler = null;
        }
    }
}
