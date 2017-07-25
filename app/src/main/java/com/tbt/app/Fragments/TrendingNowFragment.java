package com.tbt.app.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tbt.app.Adapters.HomeNewsFeedRecyclerViewAdapter;
import com.tbt.app.Constants.Config;
import com.tbt.app.ListItems.HomeNewsFeedItem;
import com.tbt.app.R;
import com.tbt.app.Tools.NWManager;
import com.tbt.app.Tools.UserManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.tbt.app.Constants.Config.APP_DIR;
import static com.tbt.app.Constants.Config.HOME_FEED_URL;
import static com.tbt.app.Constants.Config.SERVER;
import static com.tbt.app.Constants.Config.SUB_DIR;
import static com.tbt.app.Constants.Config.TRENDING_NOW_URL;

/**
 * Created by bradley on 29-05-2017.
 */

public class TrendingNowFragment extends Fragment {
    View v;
    Context context;
    RecyclerView homeRecyclerView;
    SwipeRefreshLayout homeSwipeRefreshLayout;
    LinearLayoutManager layoutManager;
    HomeNewsFeedRecyclerViewAdapter homeAdapter;
    List<HomeNewsFeedItem> list;
    LoadMoreData loadAsync;
    boolean isLoading;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = getContext();
        v = inflater.inflate(R.layout.home_fragment, container, false);
        initializeElements();

        loadMore(0);

        return v;
    }

    private void initializeElements() {
        homeRecyclerView = (RecyclerView) v.findViewById(R.id.home_feed_recycler_view);
        homeSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.home_swipe_refresh_layout);

        list = new ArrayList<>();

        layoutManager = new LinearLayoutManager(context);
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setHasFixedSize(true);
        homeAdapter = new HomeNewsFeedRecyclerViewAdapter(context, list);
        homeRecyclerView.setAdapter(homeAdapter);

        homeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItem = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && lastVisibleItem == totalItem - 1) {
                    loadMore(totalItem);
                }
            }
        });

        homeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                loadMore(0);
            }
        });
    }

    void addAdapter() {
        homeAdapter.list = list;
        homeAdapter.notifyDataSetChanged();
        homeSwipeRefreshLayout.setRefreshing(false);
    }

    void loadMore(int count) {
        loadAsync= new LoadMoreData();
        loadAsync.execute(String.valueOf(count));
    }

    class LoadMoreData extends AsyncTask<String, Object, String> {
        String userId;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!NWManager.isNetworkConnected(context)) {
                cancel(true);
            } else {
                if (!isLoading) {
                    isLoading = true;
                    userId = new UserManager(context).getUserId();
                    homeSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            homeSwipeRefreshLayout.setRefreshing(true);
                        }
                    });
                } else {
                    cancel(true);
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar.make(getActivity().findViewById(R.id.main_frame_layout), "Some error occurrred.", Snackbar.LENGTH_LONG).show();
            homeSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            isLoading = false;
            if (s != null) {
                addMoreData(s);
            } else {
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            int count = Integer.parseInt(strings[0]);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("post_num", String.valueOf(count))
                    .build();
            Request request = new Request.Builder()
                    .url(SERVER + SUB_DIR + APP_DIR + TRENDING_NOW_URL)
                    .post(requestBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            String response = null;
            try {
                response = client.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
                response = null;
            }
            return response;
        }
    }

    private void addMoreData(String s) {
        try {
            JSONObject object = new JSONObject(s);
            if (object.getString("code").equals("200")) {
                JSONArray array = object.getJSONArray("data");
                HomeNewsFeedItem item;
                for (int i = 0 ; i < array.length() ; i++) {
                    item = new HomeNewsFeedItem();
                    JSONObject ob = array.getJSONObject(i);
                    item.setPostId(ob.getString("id"));
                    item.setUserId(ob.getString("user_id"));
                    item.setName(ob.getString("name"));
                    item.setProfilePicURL(ob.getString("profile_pic_url"));
                    item.setLocation(ob.getString("location"));
                    item.setCaption(ob.getString("caption"));
                    item.setLike(ob.getString("ifnull(ulu.like, 0)"));
                    list.add(item);
                }
                addAdapter();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}