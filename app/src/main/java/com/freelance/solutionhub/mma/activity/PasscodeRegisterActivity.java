package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.arjsna.passcodeview.PassCodeView;

public class PasscodeRegisterActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pass_code_view1)
    PassCodeView pinCode;

    @BindView(R.id.pass_code_view2)
    PassCodeView rePinCode;

    @BindView(R.id.tv_pin_wrong)
    TextView textView;

    @BindView(R.id.iv_re_type)
    ImageView reType;



    private static final int MY_PERMISSION_REQUEST_CODE_PHONE_STATE = 1;

    private static final String LOG_TAG = "AndroidExample";

    private SharePreferenceHelper mSharePreferenceHelper;
    private String pinCodeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_register);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mSharePreferenceHelper = new SharePreferenceHelper(this);
        Log.i("PasscodeProblem", "onCreate: check" + mSharePreferenceHelper.getPinCode());
        if (mSharePreferenceHelper.hasPinCode()) {
            Log.i("PasscodeProblem", "onCreate: has PinCode " + mSharePreferenceHelper.getPinCode());
            startActivity(new Intent(PasscodeRegisterActivity.this, PasscodeActivity.class));
            finish();
        }else {
            askPermissionAndGetPhoneNumbers();
        }

        reType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinCode.setVisibility(View.VISIBLE);
                reType.setVisibility(View.INVISIBLE);
                rePinCode.setVisibility(View.GONE);
                textView.setText("Enter your new passcode");
                pinCode.reset();
            }
        });
        pinCode.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                Log.i("Passcode", "text");
                if(text.length() == 6){
                    pinCodeStr = text;
                    pinCode.setVisibility(View.GONE);
                    reType.setVisibility(View.VISIBLE);
                    rePinCode.setVisibility(View.VISIBLE);
                    textView.setText("Verify your passcode");
                }
            }
        });
        rePinCode.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                Log.i("PasscodeProblem", "text");
                if(text.length() == 6){
                    if(text.equals(pinCodeStr)){
                        mSharePreferenceHelper.setPinCode(text);
                        startActivity(new Intent(PasscodeRegisterActivity.this, LoginActivity.class));
                        finish();
                    }else {
                        rePinCode.setError(true);
                    }
                }
            }
        });
    }
    private void askPermissionAndGetPhoneNumbers() {

        // With Android Level >= 23, you have to ask the user
        // for permission to get Phone Number.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23

            // Check if we have READ_PHONE_STATE permission
            int readPhoneStatePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE);

            if ( readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA},
                        MY_PERMISSION_REQUEST_CODE_PHONE_STATE
                );
                return;
            }
        }
        this.getPhoneNumbers();
    }

    @SuppressLint("MissingPermission")
    private void getPhoneNumbers(){

        TelephonyManager telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephoneMgr.getLine1Number();

        Log.d("NETWORK OPERATOR", "EventSpy SIM Network Operator Name : " + telephoneMgr.getNetworkOperatorName());
        Log.d("TAG", "EventSpy SIM PhoneNumber : " + phoneNumber); // Code IMEI
        if(phoneNumber.length() != 0)
            mSharePreferenceHelper.setPhoneNumber(phoneNumber);
    }

    // When you have the request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE_PHONE_STATE: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (SEND_SMS).
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.i( LOG_TAG,"Permission granted!");
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    this.getPhoneNumbers();

                }
                // Cancelled or denied.
                else {
                    Log.i( LOG_TAG,"Permission denied!");
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    // When results returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_PERMISSION_REQUEST_CODE_PHONE_STATE) {
            if (resultCode == RESULT_OK) {
                // Do something with data (Result returned).
                Toast.makeText(this, "Action OK", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}