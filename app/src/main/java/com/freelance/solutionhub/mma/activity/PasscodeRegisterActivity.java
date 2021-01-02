package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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

//            TelephonyManager telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//
//            }
//            String phoneNumber = telephoneMgr.getLine1Number();
//            Log.d("NETWORK OPERATOR", "EventSpy SIM Network Operator Name : " + telephoneMgr.getNetworkOperatorName());
//            Log.d("TAG", "EventSpy SIM PhoneNumber : " + phoneNumber); // Code IMEI
//            mSharePreferenceHelper.setPhoneNumber(phoneNumber);

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
}