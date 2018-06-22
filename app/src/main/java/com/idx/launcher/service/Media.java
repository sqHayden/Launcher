package com.idx.launcher.service;

/**
 * Created by derik on 18-5-19.
 */

public class Media {

    public static final String ACTION_PLAY = "com.idx.launcher.action.play";
    public static final String ACTION_PAUSE = "com.idx.launcher.action.pause";
    public static final String ACTION_STOP = "com.idx.launcher.action.stop";
    public static final String ACTION_ERROR = "com.idx.launcher.action.error";

    public enum States {
        PLAY,
        STOP,
        PAUSE,
        ERROR
    }
}
