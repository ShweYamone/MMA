package com.digisoft.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digisoft.mma.R;
import com.digisoft.mma.model.ComponentDetails;
import com.digisoft.mma.model.QRReturnBody;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.SharePreferenceHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class AssetInformationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_component_id)
    TextView tvComponentId;

    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;

    @BindView(R.id.tv_component_type)
    TextView tvComponentType;

    @BindView(R.id.tv_component_state)
    TextView tvComponentState;

    @BindView(R.id.tvRemarks)
    TextView tvDescription;

    @BindView(R.id.btnClose)
    Button btnClose;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharedPreference;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient(this);
        mSharedPreference = new SharePreferenceHelper(this);
        mSharedPreference.setLock(false);

        setContentView(R.layout.activity_asset_information);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //intializing scan object
        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("Scan QR Code");
        qrScan.setCameraId(0);  // Use a specific camera of the device
        qrScan.setOrientationLocked(true);
        qrScan.setBeepEnabled(true);
        qrScan.setCaptureActivity(CaptureActivityPotrait.class);
        qrScan.initiateScan();

        btnClose.setOnClickListener(this);

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(AssetInformationActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(AssetInformationActivity.this, PasscodeActivity.class);
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
        mSharedPreference.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
      //  Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
        Log.i("LOCKSCREEN", "onResume: " + mSharedPreference.getLock());
        if (mSharedPreference.getLock()) {

            Intent intent = new Intent(AssetInformationActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSharedPreference.setLock(false);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
             //   Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                Call<QRReturnBody> call = apiInterface.getAssetInformation("Bearer " + mSharedPreference.getToken(), result.getContents());
                call.enqueue(new Callback<QRReturnBody>() {
                    @Override
                    public void onResponse(Call<QRReturnBody> call, Response<QRReturnBody> response) {
                        if (response.isSuccessful()) {
                            QRReturnBody qrReturnBody = response.body();
                            ComponentDetails componentDetails = qrReturnBody.getComponentDetails();
                            tvComponentId.setText(result.getContents() + "");
                            tvSerialNumber.setText(componentDetails.getSerialNo());
                            tvComponentType.setText(componentDetails.getComponentType());
                            tvComponentState.setText(componentDetails.getStatus());
                            tvDescription.setText(componentDetails.getDescription());
                        } else {
                            Log.i("ASSET", "onResponse: " + response.code());
                        }

                    }

                    @Override
                    public void onFailure(Call<QRReturnBody> call, Throwable t) {

                    }
                });

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(AssetInformationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
