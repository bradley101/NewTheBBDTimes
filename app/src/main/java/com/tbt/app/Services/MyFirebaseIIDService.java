package com.tbt.app.Services;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tbt.app.Constants.Config;
import com.tbt.app.Tools.NWManager;
import com.tbt.app.Tools.SPManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradley on 05-03-2017.
 */

public class MyFirebaseIIDService extends FirebaseInstanceIdService {

    @Override
    public void onCreate() {
        onTokenRefresh();
    }

    @Override
    public void onTokenRefresh() {
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken == null) return;
        Config.log(refreshedToken);
        SPManager spManager = new SPManager(getApplicationContext());
        if (spManager.hasEntity("isLoggedIn") && spManager.hasEntity("logJSON")) {
            if (spManager.getEntityBoolean("isLoggedIn") && NWManager.isNetworkConnected(getApplicationContext())) {
                try {
                    JSONObject object = new JSONObject(spManager.getEntity("logJSON"));
                    final String id = object.getString("id");
                    final String name = object.getString("name");
                    final String email = object.getString("email");

                    new Thread() {
                        @Override
                        public void run() {
                            HttpPost post = new HttpPost(Config.SERVER + Config.SUB_DIR + Config.APP_DIR + "/update_instance_id.php");
                            HttpClient client = new DefaultHttpClient();
                            List<NameValuePair> list = new ArrayList<>();
                            list.add(new BasicNameValuePair("id", id));
                            list.add(new BasicNameValuePair("name", name));
                            list.add(new BasicNameValuePair("email", email));
                            list.add(new BasicNameValuePair("iid", refreshedToken));
                            try {
                                post.setEntity(new UrlEncodedFormEntity(list));
                                String response = EntityUtils.toString(client.execute(post).getEntity());
                                Config.log(response);
                                if (new JSONObject(response).getString("code").equals("200")) {
                                    // TODO  handle new token
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
