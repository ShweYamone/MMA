package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

import com.freelance.solutionhub.mma.R;
import com.google.zxing.integration.android.IntentIntegrator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetInformationActivity extends AppCompatActivity {

    @BindView(R.id.tv_component_state)
    TextView tvComponentState;

    @BindView(R.id.tv_component_type)
    TextView tvComponentType;

    @BindView(R.id.tv_component_id)
    TextView tvComponentId;

    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    }
}
