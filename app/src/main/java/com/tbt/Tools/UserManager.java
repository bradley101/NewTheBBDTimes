package com.tbt.Tools;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bradley on 20-03-2017.
 */

public class UserManager {
    private JSONObject userJson;
    public UserManager(Context context) {
        SPManager spManager = new SPManager(context);
        try {
            userJson = new JSONObject(spManager.getEntity("logJSON"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return get("id");
    }

    public String getUserProfilePicUrl() {
        return get("profile_pic_url");
    }

    private String get(String key) {
        String ans = null;
        try {
            ans = userJson.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }
}
