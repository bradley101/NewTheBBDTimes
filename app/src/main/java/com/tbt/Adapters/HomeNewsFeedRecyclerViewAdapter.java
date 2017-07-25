package com.tbt.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tbt.ListItems.HomeNewsFeedItem;
import com.tbt.R;
import com.tbt.Tools.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static com.tbt.Constants.Config.APP_DIR;
import static com.tbt.Constants.Config.HOME_FEED_LIKE_URL;
import static com.tbt.Constants.Config.SERVER;
import static com.tbt.Constants.Config.SUB_DIR;

/**
 * Created by bradley on 04-04-2017.
 */

public class HomeNewsFeedRecyclerViewAdapter extends RecyclerView.Adapter<HomeNewsFeedRecyclerViewAdapter.MyViewHolder> {
    public List<HomeNewsFeedItem> list;
    Context context;
    public HomeNewsFeedRecyclerViewAdapter(Context context, List<HomeNewsFeedItem> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_news_item_layout, parent, false);
        return new MyViewHolder(v);
    }

    HomeNewsFeedItem item;

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        item = list.get(position);
        holder.userName.setText(item.getName());
        Picasso.with(context)
                .load(item.getProfilePicURL())
                .placeholder(R.drawable.progress_animation)
                .into(holder.userProfilePicture);
        Picasso.with(context)
                .load(SERVER + SUB_DIR + APP_DIR + "/" + item.getLocation())
                .placeholder(R.drawable.progress_animation)
                .into(holder.userImage);
        holder.imgCaption.setText(item.getCaption());

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    URL url = new URL(SERVER + SUB_DIR + APP_DIR + "/" + item.getLocation());
                    Bitmap b = BitmapFactory.decodeStream(url.openStream());
                    Intent shareChooser = new Intent(Intent.ACTION_SEND);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "", null);
                    Uri imgUri = Uri.parse(path);
                    shareChooser.setType("image/*");
                    shareChooser.putExtra(Intent.EXTRA_STREAM, imgUri);
                    shareChooser.putExtra(Intent.EXTRA_TEXT, item.getCaption() + "\n\n -Sent from The BBD Times App. Download us from Play Store. \n http://bit.ly/tbtapp");
                    context.startActivity(shareChooser);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LikePost(new UserManager(context).getUserId(), item.getPostId(), item.getLike().equals("1") ? "0" : "1", holder)
                        .execute();
            }
        });

        if (item.getLike().equals("1")) {
            holder.likeButton.setText("UNLIKE");
            holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_like_active), null);
        } else {
            holder.likeButton.setText("LIKE");
            holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_like_inactive), null);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView userProfilePicture, userImage;
        TextView userName, imgCaption;
        Button shareButton, likeButton;
        public MyViewHolder(View itemView) {
            super(itemView);
            userProfilePicture = (ImageView) itemView.findViewById(R.id.home_user_image);
            userName = (TextView) itemView.findViewById(R.id.home_feed_user_name);
            userImage = (ImageView) itemView.findViewById(R.id.home_feed_image);
            imgCaption = (TextView) itemView.findViewById(R.id.home_feed_image_caption);
            shareButton = (Button) itemView.findViewById(R.id.home_image_share);
            likeButton = (Button) itemView.findViewById(R.id.home_feed_like_button);
        }
    }

    class LikePost extends AsyncTask<Object, Object, Object> {
        String userId, postId, like;
        MyViewHolder holder;
        String response;
        public LikePost(String userId, String postId, String like, MyViewHolder holder) {
            this.userId = userId;
            this.postId = postId;
            this.like = like;
            this.holder = holder;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", postId)
                    .addFormDataPart("user_id", userId)
                    .addFormDataPart("like", like)
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(SERVER + SUB_DIR + APP_DIR + HOME_FEED_LIKE_URL)
                    .post(requestBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                response = client.newCall(request).execute().body().string();
            } catch (IOException e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (response == null) {
                Snackbar.make(((Activity) context).findViewById(R.id.main_frame_layout), "Error during like", Snackbar.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("code").equals("200")) {
                        item.setLike(item.getLike().equals("1") ? "0" : "1");
                        if (item.getLike().equals("1")) {
                            holder.likeButton.setText("UNLIKE");
                            holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_like_active), null);
                        } else {
                            holder.likeButton.setText("LIKE");
                            holder.likeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.ic_like_inactive), null);
                        }
                    }
                } catch (JSONException e) {
                    Snackbar.make(((Activity) context).findViewById(R.id.main_frame_layout), "Error during like", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}
