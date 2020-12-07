package com.freelance.solutionhub.mma.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.PMActivity;
import com.freelance.solutionhub.mma.model.MaintenanceInfoModel;
import com.freelance.solutionhub.mma.model.NotificationModel;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceOrderAdapter extends RecyclerView.Adapter<ServiceOrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<PMServiceInfoModel> serviceInfoModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMSONumber)
        TextView tvMsoNumber;

        @BindView(R.id.tvStatus)
        TextView tvStatus;

        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.tvTime)
        TextView tvTime;

        private PMServiceInfoModel service;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public void bindView(PMServiceInfoModel service){
            this.service = service;
            tvTime.setText(service.getCreationDate());
            tvMsoNumber.setText(service.getId());
            String status = service.getServiceOrderStatus();
            if (status.equals("APPR"))
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            else if (status.equals("INPRG"))
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            else if (status.equals("ACK"))
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_blue));
            tvStatus.setText(status);
            tvLocation.setText(service.getBusStopLocation());

        }
    }

    public ServiceOrderAdapter(Context mContext, List<PMServiceInfoModel> serviceInfoModelList) {
        this.mContext = mContext;
        this.serviceInfoModelList = serviceInfoModelList;
    }

    @Override
    public ServiceOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maintenance, parent, false);

        return new ServiceOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceOrderAdapter.MyViewHolder holder, int position) {
        PMServiceInfoModel service = serviceInfoModelList.get(position);
        holder.bindView(service);
    }

    @Override
    public int getItemCount() {
        return serviceInfoModelList.size();
    }
}