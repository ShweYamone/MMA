package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasscodeActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_pin_code)
    EditText pinCode;

    @BindView(R.id.tv_pin_wrong)
    TextView pinWrongInfo;

    @BindView(R.id.btn_login)
    Button login;

    private SharePreferenceHelper mSharePreferenceHelper;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mSharePreferenceHelper = new SharePreferenceHelper(this);
        if(count == 0)
            pinWrongInfo.setVisibility(View.GONE);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                checkPinCode();
                break;
        }
    }

    private void checkPinCode(){
        count++;
        if(!isEmpty(pinCode)){
            String pin = pinCode.getText().toString().trim();
            if(pin.equals(mSharePreferenceHelper.getPinCode())){
                finish();
            }else if(count == 1){
                pinWrongInfo.setVisibility(View.VISIBLE);
                pinWrongInfo.setText("1 time, wrong pin code!");
            }else if(count > 5){
                //Delete app data
            }else {
                pinWrongInfo.setText(count+" times, wrong pin code!");
            }
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Pin Code")
                    .setMessage("You must enter your pin code.")
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