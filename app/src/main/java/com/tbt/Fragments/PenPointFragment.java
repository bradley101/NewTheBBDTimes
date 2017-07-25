package com.tbt.Fragments;

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

import com.tbt.Adapters.PenPointRecyclerViewAdapter;
import com.tbt.Constants.Config;
import com.tbt.ListItems.PenPointListItem;
import com.tbt.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bradley on 07-03-2017.
 */

public class PenPointFragment extends Fragment {
    View v;
    List<PenPointListItem> list;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    PenPointRecyclerViewAdapter adapter;
    OkHttpClient client;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.pen_point_fragment, container, false);
        initializeElements();
        doJob();
        return v;
    }

    private void initializeElements() {
        recyclerView = (RecyclerView) v.findViewById(R.id.pen_point_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.pen_point_swipe_refresh_layout);
        client = new OkHttpClient();
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doJob();
            }
        });
    }

    void doJob() {
        run(Config.SERVER + Config.SUB_DIR + Config.APP_DIR + "/get_penpoint.php");
    }

    private void constructListData(String json) {
        try {
            JSONArray array = new JSONArray(json);
            list = new ArrayList<>();
            for (int i = 0 ; i < array.length() ; i++) {
                JSONObject ob = array.getJSONObject(i);
                PenPointListItem item = new PenPointListItem();
                item.setTitle(ob.getString("title"));
                item.setId(ob.getString("id"));
                item.setAuthorName(ob.getString("name"));
                item.setPreview(ob.getString("content").substring(0, 1) + "...");
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setAdapter() {
        adapter = new PenPointRecyclerViewAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    void run(final String url) {

        new AsyncTask<Object, Object, Object>() {
            String response;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (response == null) {
                    Snackbar.make(getActivity().findViewById(R.id.main_frame_layout), "Some error occurred", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Reload", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    doJob();
                                }
                            })
                            .setActionTextColor(0xFFFFFF)
                            .show();
                } else {
                    constructListData(response);
                    setAdapter();
                }
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            protected Object doInBackground(Object... objects) {
                Request request =  new Request.Builder()
                        .url(url)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    this.response = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
