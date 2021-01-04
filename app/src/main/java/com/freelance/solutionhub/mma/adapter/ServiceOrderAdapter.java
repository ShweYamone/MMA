package com.freelance.solutionhub.mma.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.NFCReadingActivity;
import com.freelance.solutionhub.mma.delegate.HomeFragmentCallback;
import com.freelance.solutionhub.mma.model.ServiceInfoModel;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freelance.solutionhub.mma.util.AppConstant.ACK;
import static com.freelance.solutionhub.mma.util.AppConstant.APPR;
import static com.freelance.solutionhub.mma.util.AppConstant.INPRG;
import static com.freelance.solutionhub.mma.util.AppConstant.MONTHLY;
import static com.freelance.solutionhub.mma.util.AppConstant.QUARTERLY;
import static com.freelance.solutionhub.mma.util.AppConstant.WEEKLY;
import static com.freelance.solutionhub.mma.util.AppConstant.WSCH;
import static com.freelance.solutionhub.mma.util.AppConstant.YEARLY;
import static com.freelance.solutionhub.mma.util.AppConstant.cm;
import static com.freelance.solutionhub.mma.util.AppConstant.pm;

public class ServiceOrderAdapter extends RecyclerView.Adapter<ServiceOrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<ServiceInfoModel> serviceInfoModelList;
    private HomeFragmentCallback callback;
    private SharePreferenceHelper sharePreferenceHelper;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMSONumber)
        TextView tvMsoNumber;

        @BindView(R.id.tvStatus)
        TextView tvStatus;

        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.tv_panel_id)
        TextView tvPanelId;

        @BindView(R.id.tvReportedProblem)
        TextView tvReportedProblem;

        @BindView(R.id.tvPriority)
        TextView tvPriority;

        @BindView(R.id.ivLanding)
        ImageView ivLanding;

        @BindView(R.id.tvProblemorSchedule)
        TextView tvProblemORSchedule;

        @BindView(R.id.layoutPriority)
        LinearLayout layoutPriority;

        @BindView(R.id.layoutBack)
        LinearLayout layoutBack;

        private ServiceInfoModel service;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (tvStatus.getText().toString().equals(APPR)) {
                        if (callback.hasUserId()) {
                            callback.update_ARRP_To_ACK(service.getId(), getAdapterPosition());
                        }
                    }
                    else {
                        if (callback.hasUserId()) {
                            Intent intent = new Intent(mContext, NFCReadingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("id", service.getId());
                            mContext.startActivity(intent);
                        }
                    }

                }
            });

        }

        public void bindView(ServiceInfoModel service, int position){
            this.service = service;

            if (service.getId().equals(sharePreferenceHelper.getCurrentMSOID()))
                layoutBack.setBackground(mContext.getDrawable(R.drawable.bg_mso_selected_border));
            else
                layoutBack.setBackground(mContext.getDrawable(R.drawable.bg_mso_border));
            if (service.getId().startsWith(pm)) {
                layoutPriority.setVisibility(View.GONE);
                tvProblemORSchedule.setText("Schedule Type");
                String checkType = service.getPreventativeMaintenanceCheckType()+"";
                tvReportedProblem.setText(service.getPreventativeMaintenanceCheckType());
                if (checkType.equals(WEEKLY))
                    tvReportedProblem.setTextColor(mContext.getResources().getColor(R.color.green));
                else if (checkType.equals(MONTHLY))
                    tvReportedProblem.setTextColor(mContext.getResources().getColor(R.color.blue));
                else if (checkType.equals(QUARTERLY))
                    tvReportedProblem.setTextColor(mContext.getResources().getColor(R.color.colorVioletDark));
                else if (checkType.equals(YEARLY))
                    tvReportedProblem.setTextColor(mContext.getResources().getColor(R.color.pink));
                else
                    tvReportedProblem.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            } else {
                layoutPriority.setVisibility(View.VISIBLE);
                tvProblemORSchedule.setText("Reported Problem");
                tvReportedProblem.setText(service.getReportedProblemDescription()+"");
                tvReportedProblem.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }

            tvTime.setText(service.getCreationDate());
            tvMsoNumber.setText(service.getId());

            String status = service.getServiceOrderStatus();
            tvStatus.setText(status);
            tvLocation.setText(service.getBusStopLocation());
            tvPanelId.setText(service.getPanelId());

            tvPriority.setText(service.getPriorityLevel()+"");
            if (service.getId().startsWith(cm)) {
                if (status.equals(APPR)){
                    Glide.with(mContext).load(R.drawable.ack).into(ivLanding);
                } else
                    Glide.with(mContext).load(R.drawable.ic_landing_cm).into(ivLanding);

            } else {
                Glide.with(mContext).load(R.drawable.ic_landing_pm).into(ivLanding);
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

    public ServiceOrderAdapter(Context mContext, List<ServiceInfoModel> serviceInfoModelList, HomeFragmentCallback callback, SharePreferenceHelper sharePreferenceHelper) {
        this.mContext = mContext;
        this.serviceInfoModelList = serviceInfoModelList;
        this.callback = callback;
        this.sharePreferenceHelper = sharePreferenceHelper;
    }

    @Override
    public ServiceOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maintenance, parent, false);

        return new ServiceOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServiceOrderAdapter.MyViewHolder holder, int position) {
        ServiceInfoModel service = serviceInfoModelList.get(position);
        holder.bindView(service, position);
    }

    @Override
    public int getItemCount() {
        return serviceInfoModelList.size();
    }

}
