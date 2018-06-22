package com.idx.jakku.service;

/**
 * Created by derik on 18-5-18.
 */

public interface UDPDataListener {
    void onReceived(String ip, String message);
}
