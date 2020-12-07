package com.freelance.solutionhub.mma.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.PMActivity;
import com.freelance.solutionhub.mma.model.MaintenanceInfoModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.MyViewHolder> {

    private Context mContext;
    private List<MaintenanceInfoModel> maintenanceInfoModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMSONumber)
        TextView tvMsoNumber;

        @BindView(R.id.tvStatus)
        TextView tvStatus;

        @BindView(R.id.tvLocation)
        TextView tvLocation;

        @BindView(R.id.ivPMLanding)
        ImageView ivPMLanding;

        private MaintenanceInfoModel maintenance;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public void bindView(MaintenanceInfoModel maintenance){

            this.maintenance = maintenance;
            tvMsoNumber.setText(maintenance.getMsoNumber());
            tvStatus.setText(maintenance.getStatus());
            //tvPanelHealthStatus.setText(maintenance.getPanelHealthStatus());
            tvLocation.setText(maintenance.getLocation());

            ivPMLanding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PMActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public MaintenanceAdapter(Context mContext, List<MaintenanceInfoModel> maintenanceInfoModelList) {
        this.mContext = mContext;
        this.maintenanceInfoModelList = maintenanceInfoModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maintenance, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MaintenanceInfoModel movie = maintenanceInfoModelList.get(position);
        holder.bindView(movie);
    }

    @Override
    public int getItemCount() {
        return maintenanceInfoModelList.size();
    }
}
