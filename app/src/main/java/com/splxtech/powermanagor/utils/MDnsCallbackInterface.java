package com.splxtech.powermanagor.utils;

/**
 * Created by li300 on 2016/10/24 0024.
 */
import org.json.JSONObject;

public interface MDnsCallbackInterface {
    void onDeviceResolved(JSONObject deviceJSON);
}
