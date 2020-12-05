package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.MaintenanceAdapter;
import com.freelance.solutionhub.mma.adapter.NotificationAdapter;
import com.freelance.solutionhub.mma.model.MaintenanceInfoModel;
import com.freelance.solutionhub.mma.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview_NotiList)
    RecyclerView recyclerView;

    private NotificationAdapter mAdapter;
    private List<NotificationModel> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        mAdapter = new NotificationAdapter(this, notificationList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        prepareNotifications();

    }

    private void prepareNotifications() {
        NotificationModel obj = new NotificationModel("Announcement", "This is message. This is message. This is message. This is message. Fighting", "12/5/2020/ 05:54PM");
        notificationList.add(obj); notificationList.add(obj); notificationList.add(obj);
    }
}
