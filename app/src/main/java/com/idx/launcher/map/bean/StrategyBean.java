package com.idx.launcher.map.bean;

/**
 * Created by hayden on 18-1-17.
 */
public class StrategyBean {
    private boolean mCongestion;
    private boolean mCost;
    private boolean mHightspeed;
    private boolean mAvoidhightspeed;

    public StrategyBean(boolean congestion, boolean cost, boolean highspeed, boolean avoidhighspeed){
        setCongestion(congestion);
        setCost(cost);
        setHightspeed(highspeed);
        setAvoidhightspeed(avoidhighspeed);
    }

    public boolean isCongestion() {
        return mCongestion;
    }

    public void setCongestion(boolean mCongestion) {
        this.mCongestion = mCongestion;
    }

    public boolean isCost() {
        return mCost;
    }

    public void setCost(boolean mCost) {
        this.mCost = mCost;
    }

    public boolean isHightspeed() {
        return mHightspeed;
    }

    public void setHightspeed(boolean mHightspeed) {
        this.mHightspeed = mHightspeed;
    }

    public boolean isAvoidhightspeed() {
        return mAvoidhightspeed;
    }

    public void setAvoidhightspeed(boolean mAvoidhightspeed) {
        this.mAvoidhightspeed = mAvoidhightspeed;
    }
}
