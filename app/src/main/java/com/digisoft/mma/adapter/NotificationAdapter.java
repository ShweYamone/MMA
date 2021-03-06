package com.digisoft.mma.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.digisoft.mma.R;
import com.digisoft.mma.activity.NotificationViewActivity;
import com.digisoft.mma.model.NotificationModel;

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

        @BindView(R.id.iv_is_read)
        ImageView isRead;

        private NotificationModel notification;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRead.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.read_message));
                    Intent intent = new Intent(mContext, NotificationViewActivity.class);
                    intent.putExtra("noti", notification);
                    mContext.startActivity(intent);
                }
            });

        }

        public void bindView(NotificationModel notification){

            this.notification = notification;
            if(notification.isRead())
                isRead.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.read_message));
            tvMessageHead.setText(notification.getMessageHead());
            if(notification.getMessageBody().length() > 95){
                tvMessageBody.setText( Html.fromHtml( notification.getMessageBody().substring(0,95)+"<font color='#00558C'> read more...</font>"));
            }else {
                tvMessageBody.setText(notification.getMessageBody());
            }
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
