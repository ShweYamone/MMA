package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.freelance.solutionhub.mma.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etUserName)
    EditText etUserName;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

}
