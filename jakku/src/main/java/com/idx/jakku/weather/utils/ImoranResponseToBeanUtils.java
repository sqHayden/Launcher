package com.idx.jakku.weather.utils;

import com.google.gson.Gson;
import com.idx.jakku.figure.data.ImoranResponseFigure;
import com.idx.jakku.music.data.ImoranResponseMusic;
import com.idx.jakku.news.data.ImoranResponseNews;
import com.idx.jakku.news.data.NewsDetail;
import com.idx.jakku.weather.data.ImoranResponse;


import java.util.ArrayList;
import java.util.List;


/**
 * 将返回的JSON数据解析成实体类
 * Created by sunny on 18-3-15.
 */

public class ImoranResponseToBeanUtils {
    private static final String TAG = ImoranResponseToBeanUtils.class.getSimpleName();

    //Imoran to music
    public static ImoranResponseMusic handleMusicData(String response) {
        try {
            Gson gson = new Gson();
            ImoranResponseMusic music = gson.fromJson(response, ImoranResponseMusic.class);
            return music;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Imoran music data is null
    public static boolean isImoranMusicNull(ImoranResponseMusic music){
        boolean flag = false;
        if (music!=null && music.getMusicData()!=null && music.getMusicData().getMusicContent()!=null) {
            flag = true;
        }
        return flag;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static ImoranResponse handleWeatherData(String weatherData) {
        try {
            Gson gson = new Gson();
            ImoranResponse weather = gson.fromJson(weatherData, ImoranResponse.class);
//            Log.i(TAG, "handleWeatherResponse: weather = " + weather);
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static boolean isImoranWeatherNull(ImoranResponse weather){
        boolean flag=false;
        if (weather!=null && weather.getDataWeather()!=null && weather.getDataWeather().getContent()!=null){
            flag=true;
        }
        return flag;
    }

    /**
     * 将返回的JSON数据解析成Figure实体类
     */
    //将接收到的json文件解析成Figure实体类
    public static ImoranResponseFigure handleFigureData(String response) {
        try {
            Gson gson = new Gson();
            ImoranResponseFigure figure = gson.fromJson(response, ImoranResponseFigure.class);
            return figure;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //判断数据是否为空
    public static boolean isImoranFigureNull(ImoranResponseFigure figure){
        boolean flag = false;
        if (figure != null && figure.getFigureData().getFigureContent().getFigureReply() != null &&
                figure.getFigureData().getFigureContent().getFigureReply().getFigure() != null ) {
            flag = true;
        }
        return flag;
    }

    /**
     * 将返回的JSON数据解析成News实体类
     */
    //将接收到的json文件解析成news实体类
    public static List<NewsDetail> handleNewsData(String response) {
        List<NewsDetail> list = null;
        try {
            list = new ArrayList<>();
            if (list != null){
                list.clear();
            }
            Gson gson = new Gson();
            ImoranResponseNews news = gson.fromJson(response, ImoranResponseNews.class);
            list = news.getNewsData().getNewsContent().getNewsReply().getNewsDetail();

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //拿到NewsIndex的值
    public static int handleNewsIndex(String response){
        int indexNumber = 0;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            indexNumber = responseNews.getNewsData().getNewsContent().getSemantic().getIndexNumber();
        }catch (Exception e){
            e.printStackTrace();
        }
        return indexNumber;
    }

    //拿到NewsLabel的值
    public static String[] handleLabel(String response){
        String[] list1 = null;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            list1 = responseNews.getNewsData().getNewsContent().getSemantic().getNewsType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list1;
    }

    public static String handleNewsType(String response){
        String type = null;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            type = responseNews.getNewsData().getNewsContent().getType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return type;
    }

    public static String handleDomain(String response){
        String domian = null;
        try {
            Gson gson = new Gson();
            ImoranResponseNews responseNews = gson.fromJson(response, ImoranResponseNews.class);
            domian = responseNews.getNewsData().getDomain();
        }catch (Exception e){
            e.printStackTrace();
        }
        return domian;
    }

}
