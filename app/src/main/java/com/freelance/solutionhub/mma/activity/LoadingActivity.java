package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.transition.CircularPropagation;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.token;
import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class LoadingActivity extends AppCompatActivity {

    @BindView(R.id.circularProgressBar)
    CircularProgressBar progressBar;

    @BindView(R.id.tvProgressPercent)
    TextView tvPercent;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePrefrence;
    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean lockScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient(this);
        mSharePrefrence = new SharePreferenceHelper(this);
        mSharePrefrence.setLock(false);

        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        getServieOrderbyId(getIntent().getStringExtra("id"));

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(LoadingActivity.this, "user is inactive from last 1 minute",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(LoadingActivity.this, PasscodeActivity.class);
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
        else
            mSharePrefrence.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
        if (mSharePrefrence.getLock()) {
            Intent intent = new Intent(LoadingActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        mSharePrefrence.setLock(true);
    }

    private void getServieOrderbyId(String id) {
        Call<PMServiceInfoDetailModel> call = apiInterface.getPMServiceOrderByID("Bearer " + mSharePrefrence.getToken() , id);
        call.enqueue(new Callback<PMServiceInfoDetailModel>() {
            @Override
            public void onResponse(Call<PMServiceInfoDetailModel> call, Response<PMServiceInfoDetailModel> response) {

                if (response.isSuccessful()) {
                    Intent intent;
                    if (id.startsWith("CM")) {
                        intent = new Intent(LoadingActivity.this, CMActivity.class);
                    } else {
                        intent = new Intent(LoadingActivity.this, PMActivity.class);
                    }
                    intent.putExtra("start_time", getIntent().getStringExtra("start_time"));
                    intent.putExtra("object", response.body());
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("SERVICEORDER", response.code()+"");
                }
            }
            @Override
            public void onFailure(Call<PMServiceInfoDetailModel> call, Throwable t) {
                Log.i("SERVICEORDER", id+"failure");
            }
        });
    }
}
