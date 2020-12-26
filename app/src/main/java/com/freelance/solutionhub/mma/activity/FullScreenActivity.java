package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class FullScreenActivity extends AppCompatActivity {

    @BindView(R.id.imgDisplay)
    ImageView imgDisplay;

    @BindView(R.id.imgClose)
    ImageView imgClose;

    private Runnable r;
    private Handler handler;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.setLock(false);

        ButterKnife.bind(this);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");

        imgClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharePreferenceHelper.setLock(false);
                finish();
            }
        });


        imgDisplay.setImageBitmap(bmp );

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(FullScreenActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(FullScreenActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };

    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        sharePreferenceHelper.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
       // Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
        stopHandler();//stop first and then start
        if (startHandler)
            startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, user_inactivity_time); //for 3 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharePreferenceHelper.getLock()) {
            Intent intent = new Intent(FullScreenActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharePreferenceHelper.setLock(false);
    }
}