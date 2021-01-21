package com.digisoft.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.digisoft.mma.R;
import com.digisoft.mma.util.CryptographyUtils;
import com.digisoft.mma.util.SharePreferenceHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.digisoft.mma.util.AppConstant.PID;
import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class FullScreenActivity extends AppCompatActivity {

    @BindView(R.id.imgDisplay)
    ImageView imgDisplay;

    @BindView(R.id.imgClose)
    ImageView imgClose;

    private Runnable r;
    private Handler handler;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;
    private String url;
    private GlideUrl glideUrl;
    private RequestOptions options;
    private final String IMAGE = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.setLock(false);

        ButterKnife.bind(this);

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        Bundle extras = getIntent().getExtras();
        boolean isRejected = extras.getBoolean("isRejected");
        if(isRejected){
            Log.i(IMAGE,extras.getString(IMAGE));
            url = "http://alb-java-apps-776075049.ap-southeast-1.elb.amazonaws.com:9000"+extras.get(IMAGE);
            glideUrl = new GlideUrl(url,
                    new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + sharePreferenceHelper.getToken())
                            .build());


            Glide.with(this)
                    .load(glideUrl)
                    .transition(withCrossFade())
                    .thumbnail(0.5f)
                    .apply(options)
                    .into(imgDisplay);

        }else {
            imgDisplay.setImageBitmap(CryptographyUtils.getDecodedBitmap(extras.getString(IMAGE), extras.getString(PID)));
        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharePreferenceHelper.setLock(false);
                finish();
            }
        });


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
    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        return img;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharePreferenceHelper.setLock(false);
    }
}