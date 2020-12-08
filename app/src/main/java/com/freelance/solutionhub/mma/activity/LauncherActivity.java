package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

public class LauncherActivity extends AppCompatActivity {

    SharePreferenceHelper mSharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharePreferenceHelper = new SharePreferenceHelper(this);
        if (mSharePreferenceHelper.isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
