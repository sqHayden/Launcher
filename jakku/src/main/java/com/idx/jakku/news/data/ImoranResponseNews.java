package com.idx.jakku.news.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darkmi on 3/20/18.
 */

public class ImoranResponseNews {
    @SerializedName("data")
    private NewsData newsData;

    public NewsData getNewsData() {
        return newsData;
    }
}
