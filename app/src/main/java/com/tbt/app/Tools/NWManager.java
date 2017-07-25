package com.tbt.app.Tools;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by bradley on 05-03-2017.
 */

public class NWManager {
    public static boolean isNetworkConnected (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
