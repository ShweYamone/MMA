package com.freelance.solutionhub.mma.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.NotificationViewActivity;
import com.freelance.solutionhub.mma.model.NotificationModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context mContext;
    private List<NotificationModel> notificationModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMessageHead)
        TextView tvMessageHead;

        @BindView(R.id.tvMessageBody)
        TextView tvMessageBody;

        @BindView(R.id.tvDateTime)
        TextView tvDateTime;

        private NotificationModel notification;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NotificationViewActivity.class);
                    intent.putExtra("noti", notification);
                    mContext.startActivity(intent);
                }
            });

        }

        public void bindView(NotificationModel notification){

            this.notification = notification;
            tvMessageHead.setText(notification.getMessageHead());
            tvMessageBody.setText(notification.getMessageBody());
            tvDateTime.setText(notification.getMessageDateTime());

        }
    }

    public NotificationAdapter(Context mContext, List<NotificationModel> notificationModelList) {
        this.mContext = mContext;
        this.notificationModelList = notificationModelList;
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, int position) {
        NotificationModel movie = notificationModelList.get(position);
        holder.bindView(movie);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
}
