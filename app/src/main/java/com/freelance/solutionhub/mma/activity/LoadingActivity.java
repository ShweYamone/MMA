package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.CircularPropagation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.freelance.solutionhub.mma.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingActivity extends AppCompatActivity {

    @BindView(R.id.circularProgressBar)
    CircularProgressBar progressBar;

    @BindView(R.id.tvProgressPercent)
    TextView tvPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        progressBar.setProgress(70);
        tvPercent.setText("70%");
    }
}
