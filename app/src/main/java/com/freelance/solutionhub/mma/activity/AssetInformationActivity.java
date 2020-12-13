package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.ComponentDetails;
import com.freelance.solutionhub.mma.model.QRReturnBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient(this);
        mSharedPreference = new SharePreferenceHelper(this);
        setContentView(R.layout.activity_asset_information);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

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
        switch (view.getId()) {
            case R.id.btnClose:
                Intent intent = new Intent(AssetInformationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
        }
    }
}
