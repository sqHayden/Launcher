package net.imoran.tv.sdk.network.param.iot;

import net.imoran.tv.sdk.network.gson.GsonObjectDeserializer;
import net.imoran.tv.sdk.network.param.BaseParams;
import net.imoran.tv.sdk.network.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by bobge on 2018/4/13.
 */

public class IotTyoeListParams extends BaseParams {
    private String uid;
    private String key;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", getUid());
        params.put("key", getKey());
        LogUtils.i("iot", "IotTyoeListParams:" + GsonObjectDeserializer.produceGson().toJson(this));
        return params;
    }
}
