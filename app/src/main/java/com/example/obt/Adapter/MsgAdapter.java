package com.example.obt.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.obt.Class.MsgClass;
import com.example.obt.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<MsgClass> msgClasses;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View msgview;
        CircleImageView userImage;
        TextView userName;
        TextView msgInfo;

        public ViewHolder(View view) {
            super(view);
            msgview = view;
            userImage = view.findViewById(R.id.user_image);
            userName = view.findViewById(R.id.user_name);
            msgInfo = view.findViewById(R.id.msg_info);
        }
    }

    public MsgAdapter(List<MsgClass> msgClasses) {
        this.msgClasses = msgClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.msg_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 添加点击事件
        holder.msgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MsgClass msgClass = msgClasses.get(position);
                Context context = view.getContext();
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MsgClass msgClass = msgClasses.get(position);
        holder.userImage.setImageBitmap(msgClass.getUserImage());
        holder.userName.setText(msgClass.getUserName());
        holder.msgInfo.setText(msgClass.getMsg());
    }

    @Override
    public int getItemCount() {
        return msgClasses.size();
    }
}
