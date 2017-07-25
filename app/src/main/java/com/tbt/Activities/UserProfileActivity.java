package com.tbt.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tbt.Adapters.UserSubmissionRecyclerViewAdapter;
import com.tbt.ListItems.UserSubmissionListItem;
import com.tbt.R;
import com.tbt.Tools.SPManager;
import com.tbt.Tools.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.tbt.Constants.Config.APP_DIR;
import static com.tbt.Constants.Config.GET_UPLOADS_FILE;
import static com.tbt.Constants.Config.SERVER;
import static com.tbt.Constants.Config.SUB_DIR;

/**
 * Created by bradley on 22-03-2017.
 */

public class UserProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    ImageView pic;
    Bitmap picBmp;
    RecyclerView submissionRecyclerView;
    List<UserSubmissionListItem> list;
    Button logoutButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_layout);
        initializeElements();
        loadActivityContents();
    }

    private void loadActivityContents() {
        UserManager userManager = new UserManager(this);
        final String picurl = userManager.getUserProfilePicUrl();
        final ProgressDialog progress = new ProgressDialog(this);
        final String userId = userManager.getUserId();
        progress.setMessage("Loading profile..");
        progress.setCancelable(false);
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    LOAD PROFILE PICTURE
                    picBmp = BitmapFactory.decodeStream(new URL(picurl).openConnection().getInputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pic.setImageBitmap(picBmp);
                        }
                    });

//                    LOAD USER SUBMISSIONS
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("user_id", userId)
                            .build();
                    Request request = new Request.Builder()
                            .url(SERVER + SUB_DIR + APP_DIR + GET_UPLOADS_FILE)
                            .post(requestBody)
                            .build();
                    String response = client.newCall(request).execute().body().string();
                    JSONObject resJSON = new JSONObject(response);
                    if (resJSON.getString("code").equals("200")) {
                        JSONArray uploadArray = new JSONArray(resJSON.getString("uploads"));
                        list = new ArrayList<>();
                        UserSubmissionListItem item;
                        JSONObject object;
                        for (int i = 0 ; i < uploadArray.length() ; i++) {
                            object = uploadArray.getJSONObject(i);
                            item = new UserSubmissionListItem();
                            item.setId(object.getString("id"));
                            item.setUserId(object.getString("user_id"));
                            item.setTime(object.getString("time"));
                            item.setOriginalFileName(object.getString("orig_file_name"));
                            item.setType(object.getString("type"));
                            item.setBaseLocation(object.getString("location"));
                            item.setStatus(object.getString("status"));
                            item.setCaption(object.getString("caption"));
                            list.add(item);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserSubmissionRecyclerViewAdapter adapter = new UserSubmissionRecyclerViewAdapter(getApplicationContext(), list);
                                submissionRecyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                progress.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                onBackPressed();
                                Toast.makeText(UserProfileActivity.this, "Cannot load user submissions.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            onBackPressed();
                            Toast.makeText(UserProfileActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private void initializeElements() {
        toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.user_collapsing_toolbar);
        collapsingToolbar.setTitle("Profile");
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);

        pic = (ImageView) findViewById(R.id.user_profile_pic);

        submissionRecyclerView = (RecyclerView) findViewById(R.id.user_uploads_recycler_view);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getApplicationContext());
        submissionRecyclerView.setLayoutManager(layout);
        submissionRecyclerView.setHasFixedSize(true);

        logoutButton = (Button) findViewById(R.id.user_profile_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        SPManager spManager = new SPManager(UserProfileActivity.this);
        spManager.edit();
        spManager.addEntity("isLoggedIn", false);
        spManager.removeEntity("logJSON");
        spManager.commit();
        startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
        finish();
    }
}
