package com.syslab.caddyless.filemanager.adpter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.syslab.caddyless.filemanager.utils.Message;
import com.syslab.caddyless.filemanager.R;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by caddyless on 2017/4/21.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> mMessageList;
    public MessageAdapter(List<Message> messageList){
        mMessageList=messageList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message=mMessageList.get(position);
        holder.icon.setImageResource(message.getIconId());
        Log.d(TAG, "onBindViewHolder: "+message.getAppName());
        holder.appName.setText(message.getAppName());
        holder.fileName.setText(message.getFileName());
        holder.time.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView time;
        TextView fileName;
        TextView appName;
        public ViewHolder(View itemView) {
            super(itemView);
            icon= (ImageView) itemView.findViewById(R.id.icon);
            time= (TextView) itemView.findViewById(R.id.time);
            fileName= (TextView) itemView.findViewById(R.id.fileName);
            appName= (TextView) itemView.findViewById(R.id.appName);
        }
    }
}
