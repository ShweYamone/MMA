package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.NotificationModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationViewActivity extends AppCompatActivity {

    @BindView(R.id.tvMessageHead)
    TextView tvMessageHead;

    @BindView(R.id.tvMessageBody)
    TextView tvMessageBody;

    @BindView(R.id.tvDateTime)
    TextView tvDataTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NotificationModel notificationModel = (NotificationModel)getIntent().getSerializableExtra("noti");
        tvMessageHead.setText(notificationModel.getMessageHead());
        tvMessageBody.setText(notificationModel.getMessageBody());
        tvDataTime.setText(notificationModel.getMessageDateTime());

    }
}
