package com.tbt.app.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbt.app.ListItems.PenPointListItem;
import com.tbt.app.R;

import java.util.List;

/**
 * Created by bradley on 07-03-2017.
 */

public class PenPointRecyclerViewAdapter extends RecyclerView.Adapter<PenPointRecyclerViewAdapter.MyViewHolder> {

    Context context;
    List<PenPointListItem> list;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, preview, authorName, likesCounter;
        public ImageView likeButton, authorImg;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.pen_point_list_article_title);
            preview = (TextView) itemView.findViewById(R.id.pen_point_list_article_preview);
            authorName = (TextView) itemView.findViewById(R.id.pen_point_list_author_name);
            likesCounter = (TextView) itemView.findViewById(R.id.pen_point_list_like_counter);
            authorImg = (ImageView) itemView.findViewById(R.id.pen_point_list_author_img);
            likeButton = (ImageView) itemView.findViewById(R.id.pen_point_list_like_button);
        }
    }

    public PenPointRecyclerViewAdapter(Context context, List<PenPointListItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.pen_point_item_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PenPointListItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.preview.setText(item.getPreview());
        holder.authorName.setText(item.getAuthorName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
