package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.freelance.solutionhub.mma.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CMCompletionActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvAcknowledgeDateTime)
    TextView tvAcknowledgeDateTime;

    @BindView(R.id.tvArrivalDateTime)
    TextView tvArrivalDateTime;

    @BindView(R.id.tvJobCompletionDateTime)
    TextView tvJobCompleteDataTime;

    @BindView(R.id.tvRemarks)
    TextView tvRemarks;

    @BindView(R.id.btnClose)
    Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmcompletion);
        ButterKnife.bind(this);

        tvArrivalDateTime.setText(getIntent().getStringExtra("start_time"));
        tvJobCompleteDataTime.setText(getIntent().getStringExtra("end_time"));
        tvRemarks.setText(getIntent().getStringExtra("remarks"));
        tvAcknowledgeDateTime.setText(getIntent().getStringExtra("acknowledge_time"));
        btnClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClose:
                Intent intent = new Intent(CMCompletionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
    }
}
