package com.tbt.app.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tbt.app.Constants.Config;
import com.tbt.app.R;
import com.tbt.app.Tools.NWManager;
import com.tbt.app.Tools.SPManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tbt.app.Constants.Config.ADD_USER_FILE;
import static com.tbt.app.Constants.Config.APP_DIR;
import static com.tbt.app.Constants.Config.FB_GRAPH;
import static com.tbt.app.Constants.Config.GS_RC;
import static com.tbt.app.Constants.Config.SERVER;
import static com.tbt.app.Constants.Config.SUB_DIR;
import static com.tbt.app.Constants.Config.log;

/**
 * Created by bradley on 02-03-2017.
 */

public class LoginActivity extends AppCompatActivity {
    Button gSignInButton, fbSignInButton;
    GoogleSignInOptions gso;
    GoogleApiClient gpc;
    CallbackManager callback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        initializeSignIn();
        SPManager spManager = new SPManager(this);
        if (spManager.hasEntity("isLoggedIn") && spManager.hasEntity("logJSON") && spManager.getEntityBoolean("isLoggedIn")) {
            try {
                loginSuccess(new JSONObject(spManager.getEntity("logJSON")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.login_activity);

        initializeElements();
        initializeListeners();
    }

    private void initializeListeners() {
        gSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(gpc);
                startActivityForResult(intent, GS_RC);
            }
        });

        fbSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GS_RC) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Config.log("Google Login Success");
            new AddUserToDB(account.getDisplayName(), account.getEmail(), account.getPhotoUrl().toString()).execute();
        }
    }

    private void initializeSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        gpc = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    showErrorToast();
                }
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();

        callback = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Success
                Config.log("Facebook Login Success");
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            new AddUserToDB(object.getString("name"),
                                    object.getString("email"),
                                    FB_GRAPH + "/" + object.getString("id") + "/picture?type=large").execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, link, email, picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                showErrorToast();
            }

            @Override
            public void onError(FacebookException error) {
                showErrorToast();
            }
        });
    }

    private void initializeElements() {
        gSignInButton = (Button) findViewById(R.id.google_sign_in_button);
        fbSignInButton = (Button) findViewById(R.id.facebook_sign_in_button);
    }

    private void showErrorToast() {
        Toast.makeText(this, "Failed to Login!", Toast.LENGTH_LONG).show();
    }


    class AddUserToDB extends AsyncTask<Object, Object, Object> {
        private String name, email, photoURL;
        private String result;
        AddUserToDB(String name, String email, String photoURL) {
            this.name = name;
            this.email = email;
            this.photoURL = photoURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... objects) {
            HttpPost post = new HttpPost(SERVER + SUB_DIR + APP_DIR + ADD_USER_FILE);
            HttpClient client = new DefaultHttpClient();
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name", name));
            list.add(new BasicNameValuePair("email", email));
            list.add(new BasicNameValuePair("profile_pic_url", photoURL));
            try {
                post.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            log(result);
            try {
                JSONObject rJson = new JSONObject(result);
                if (rJson.getString("code").equals("200")) {
                    SPManager spManager = new SPManager(LoginActivity.this);
                    spManager.edit();
                    spManager.addEntity("isLoggedIn", true);
                    spManager.addEntity("logJSON", rJson.toString());
                    spManager.commit();
                    loginSuccess(rJson);
                } else {
                    loginFailure();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loginFailure() {
        if (NWManager.isNetworkConnected(LoginActivity.this)) {
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unable to connect to server", Toast.LENGTH_LONG).show();
        }
    }

    private void loginSuccess(JSONObject rJson) {
        Bundle parameters = new Bundle();
        try {
            parameters.putString("id", rJson.getString("id"));
            parameters.putString("name", rJson.getString("name"));
            parameters.putString("email", rJson.getString("email"));
            parameters.putString("profile_pic_url", rJson.getString("profile_pic_url"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtras(parameters);
        startActivity(intent);
        finish();
    }

}
