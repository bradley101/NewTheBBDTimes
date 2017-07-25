package com.tbt.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tbt.Constants.Config;
import com.tbt.R;
import com.tbt.Tools.SPManager;

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
 * Created by bradley on 06-03-2017.
 */

public class FeedbackFragment extends Fragment {
    View v;
    EditText message;
    Button submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.feedback_fragment, container, false);

        initializeElements();
        initializeListeners();
        return v;
    }

    private void initializeElements() {
        message = (EditText) v.findViewById(R.id.feedback_message);
        submitButton = (Button) v.findViewById(R.id.feedback_submit_button);
    }

    private void initializeListeners() {

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (message.getText().length() < 20) {
                    message.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_text_area_border));
                } else {
                    message.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_text_area_border));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPManager spManager = new SPManager(getContext());
                String user = null;
                try {
                    user = new JSONObject(spManager.getEntity("logJSON")).getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String msg = message.getText().toString();
                submitFeedback(user, msg);
            }
        });
    }

    public void submitFeedback(final String user, final String msg) {
        new AsyncTask<Object, Object, Object>() {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            String response;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setMessage("Please wait while we submit your feedback..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected Object doInBackground(Object... objects) {
                HttpPost post = new HttpPost(Config.SERVER + Config.SUB_DIR + Config.APP_DIR + "/submit_feedback.php");
                HttpClient client = new DefaultHttpClient();
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("user", user));
                list.add(new BasicNameValuePair("message", msg));
                try {
                    post.setEntity(new UrlEncodedFormEntity(list));
                    response = EntityUtils.toString(client.execute(post).getEntity());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                progressDialog.hide();
                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    if (object.getString("code").equals("200")) {
                        alert.setMessage("Thank you for providing the feedbaack!");
                    } else {
                        alert.setMessage("Some error occurred!");
                    }
                    alert.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
