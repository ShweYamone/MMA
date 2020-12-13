package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
        if(mSharePreferenceHelper.isPinCode()){
            startActivity(new Intent(PasscodeRegisterActivity.this, LauncherActivity.class));
            finish();
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
                if(text.length() == 4){
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
                Log.i("Passcode", "text");
                if(text.length() == 4){
                    if(text.equals(pinCodeStr)){
                        mSharePreferenceHelper.setPinCode(text);
                     //   mSharePreferenceHelper.setIsPinCodeActive(true);
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