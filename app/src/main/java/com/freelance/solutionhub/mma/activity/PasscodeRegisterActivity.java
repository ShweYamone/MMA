package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasscodeRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_pin_code)
    EditText pinCode;

    @BindView(R.id.et_re_pin_code)
    EditText rePinCode;

    @BindView(R.id.btn_save)
    EditText save;

    private SharePreferenceHelper mSharePreferenceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_register);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mSharePreferenceHelper = new SharePreferenceHelper(this);
        if(mSharePreferenceHelper.isPinCode()){
            startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
            finish();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                setPinCode();
                break;
        }
    }
    public void setPinCode(){

        if(!isEmpty(pinCode) && !isEmpty(rePinCode)){
            String pin1 = pinCode.getText().toString().trim();
            String pin2 = rePinCode.getText().toString().trim();

            if(pin1.equals(pin2)){
                mSharePreferenceHelper.setPinCode(pin1);
                startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
                finish();
            }else {
                new AlertDialog.Builder(this)
                        .setTitle("Pin Code")
                        .setMessage("Pin codes must be same.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })
                        .show();
            }
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Pin Code")
                    .setMessage("Pin code must be at least 4-digits and most 10-digits.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }

                    })
                    .show();
        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}