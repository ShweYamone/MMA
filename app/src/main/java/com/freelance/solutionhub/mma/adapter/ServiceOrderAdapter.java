package com.freelance.solutionhub.mma.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import com.bumptech.glide.Glide;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.CMActivity;
import com.freelance.solutionhub.mma.activity.LoadingActivity;
import com.freelance.solutionhub.mma.activity.NFCReadingActivity;
import com.freelance.solutionhub.mma.activity.PMActivity;
import com.freelance.solutionhub.mma.delegate.HomeFragmentCallback;
import com.freelance.solutionhub.mma.fragment.First_Step_CM_Fragment;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freelance.solutionhub.mma.util.AppConstant.ACK;
import static com.freelance.solutionhub.mma.util.AppConstant.APPR;
import static com.freelance.solutionhub.mma.util.AppConstant.CM;
import static com.freelance.solutionhub.mma.util.AppConstant.INPRG;
import static com.freelance.solutionhub.mma.util.AppConstant.WSCH;
import static com.freelance.solutionhub.mma.util.AppConstant.cm;

public class ServiceOrderAdapter extends RecyclerView.Adapter<ServiceOrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<PMServiceInfoModel> serviceInfoModelList;
    private HomeFragmentCallback callback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMSONumber)
        TextView tvMsoNumber;

        @BindView(R.id.tvStatus)
        TextView tvStatus;

        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.ivLanding)
        ImageView ivLanding;

        private PMServiceInfoModel service;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   Toast.makeText(v.getContext(), "click", Toast.LENGTH_SHORT).show();
                }
            });

        }

        public void bindView(PMServiceInfoModel service, int position){
            this.service = service;
            tvTime.setText(service.getCreationDate());
            tvMsoNumber.setText(service.getId());
            String status = service.getServiceOrderStatus();
            tvStatus.setText(status);
            tvLocation.setText(service.getBusStopLocation());

            if (service.getId().startsWith(cm)) {
                if (status.equals(APPR)){
                    Glide.with(mContext).load(R.drawable.ack).into(ivLanding);
                    ivLanding.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (callback.hasUserId()) {
                                callback.update_ARRP_To_ACK(service.getCreationDate(), service.getId(), position);
                              //  Glide.with(mContext).load(R.drawable.landing_cm).into(ivLanding);
                                ivLanding.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(mContext, NFCReadingActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("id", service.getId());

                                        mContext.startActivity(intent);

                                    }
                                });
                            }

                        }
                    });
                } else {
                    Glide.with(mContext).load(R.drawable.landing_cm).into(ivLanding);
                    ivLanding.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (callback.hasUserId()) {
                                Intent intent = new Intent(mContext, NFCReadingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("id", service.getId());
                                mContext.startActivity(intent);
                            }

                        }
                    });
                }
            } else {
                Glide.with(mContext).load(R.drawable.landing_pm).into(ivLanding);
                ivLanding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback.hasUserId()) {
                            Intent intent = new Intent(mContext, NFCReadingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("id", service.getId());
                            mContext.startActivity(intent);
                        }
                    }
                });
            }

            if (status.equals(APPR))
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
            else if (status.equals(INPRG))
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorOrange));
            else if (status.equals(WSCH))
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_green_text));
            else if (status.equals(ACK)) {
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.color_blue));
            } else {
                tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }

        }
    }

    public ServiceOrderAdapter(Context mContext, List<PMServiceInfoModel> serviceInfoModelList, HomeFragmentCallback callback) {
        this.mContext = mContext;
        this.serviceInfoModelList = serviceInfoModelList;
        this.callback = callback;
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
        holder.bindView(service, position);
    }

    @Override
    public int getItemCount() {
        return serviceInfoModelList.size();
    }
}
