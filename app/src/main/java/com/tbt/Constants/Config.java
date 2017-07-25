package com.tbt.Constants;

import android.util.Log;

/**
 * Created by bradley on 02-03-2017.
 */

public class Config {
    public static final int GS_RC = 100;
    public static final String LOG_TAG = "TBT-LOG";
    public static void log (String log) {
        Log.i (LOG_TAG, log);
    }
    public static final String SERVER = "http://thebbdtimes-project.000webhostapp.com";
    public static final String SUB_DIR = "/internal";
    public static final String APP_DIR = "/app";
    public static final String ADD_USER_FILE = "/add_user.php";
    public static final String SHARED_PREFS = "com.tbt.SHAREDPREFS";
    public static final int TBT_LOGO_COLOR = 0x2A3890;
    public static final String FB_GRAPH = "https://graph.facebook.com";
    public static final String UPLOAD_FILE = "/file_upload.php";
    public static final String UPLOAD_DIR = "/uploads";
    public static final String GET_UPLOADS_FILE = "/get_user_uploads.php";
    public static final String[] DOC_TYPES = {
            "doc",
            "rtf",
            "docx",
            "txt",
            "pdf"
    };
    public static final String[] IMG_TYPES = {
            "png",
            "jpg",
            "jpeg",
            "bmp",
            "gif"
    };
    public static final String HOME_FEED_URL = "/get_news_feed.php";
    public static final String HOME_FEED_LIKE_URL = "/home_news_feed_like.php";
    public static final String TRENDING_NOW_URL = "/home_news_feed_max_like.php";
}
