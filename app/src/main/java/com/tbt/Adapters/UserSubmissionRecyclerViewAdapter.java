package com.tbt.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbt.ListItems.UserSubmissionListItem;
import com.tbt.R;

import java.util.List;

import static com.tbt.Constants.Config.DOC_TYPES;
import static com.tbt.Constants.Config.IMG_TYPES;

/**
 * Created by bradley on 23-03-2017.
 */

public class UserSubmissionRecyclerViewAdapter extends RecyclerView.Adapter<UserSubmissionRecyclerViewAdapter.MyViewHolder> {
    Context context;
    List<UserSubmissionListItem> list;
    public UserSubmissionRecyclerViewAdapter(Context context, List<UserSubmissionListItem> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView subType, subStatus;
        TextView subName;
        public MyViewHolder(View itemView) {
            super(itemView);
            subType = (ImageView) itemView.findViewById(R.id.user_submission_type_img);
            subStatus = (ImageView) itemView.findViewById(R.id.user_submission_status);
            subName = (TextView) itemView.findViewById(R.id.user_submission_file_name);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_submission_list_item_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserSubmissionListItem item = list.get(position);
        holder.subName.setText(item.getCaption());

        if (item.getStatus().equals("0")) {
            holder.subStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pending));
        } else if (item.getStatus().equals("1")) {
            holder.subStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_accepted));
        } else if (item.getStatus().equals("2")){
            holder.subStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rejected));
        } else {
            holder.subStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pending));
        }

        switch (getFileType(item.getOriginalFileName())) {
            case 0:
                holder.subType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_text));
                break;
            case 1:
                holder.subType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_image));
                break;
        }
    }

    int getFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        for (String s : DOC_TYPES) {
            if (s.equals(extension)) return 0;
        }
        for (String s : IMG_TYPES) {
            if (s.equals(extension)) return 1;
        }
        return 0;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
