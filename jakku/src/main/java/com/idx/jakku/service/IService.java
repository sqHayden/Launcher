package com.idx.jakku.service;

/**
 * Created by derik on 18-6-2.
 */

public interface IService {
    void setDataListener(DataListener listener);
    /**
     * getJson for news
     * @return
     */
    String getJson();

    void sendTcpPort();

    void rejectRequestTcpPort();
}
