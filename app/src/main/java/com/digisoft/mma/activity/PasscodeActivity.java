package com.digisoft.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.arjsna.passcodeview.PassCodeView;

public class PasscodeActivity extends AppCompatActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_pin_wrong)
    TextView pinWrongInfo;

    @BindView(R.id.pass_code_view)
    PassCodeView passCodeView;

    @BindView(R.id.tv_count)
    TextView countText;


    private SharePreferenceHelper mSharePreferenceHelper;
    private int count;
    private ApiInterface apiInterface;
    private Network network;

    InitializeDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mSharePreferenceHelper = new SharePreferenceHelper(this);
        apiInterface = ApiClient.getClient(this);

        dbHelper = InitializeDatabase.getInstance(this);
        network = new Network(this);
        count = 1;
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                Log.i("Passcode", "text");
                if(text.length() == 6){
                    if(text.equals(mSharePreferenceHelper.getPinCode())){
                        mSharePreferenceHelper.setLock(false);
                        if (getIntent().hasExtra("workInMiddle") &&
                                getIntent().getStringExtra("workInMiddle").equals("work")) {
                            //do nothing
                        } else {//this is the start of the app, thus navigate to MainActivity
                            if(mSharePreferenceHelper.isLogin())
                                startActivity(new Intent(PasscodeActivity.this, MainActivity.class));
                            else
                                startActivity(new Intent(PasscodeActivity.this, LoginActivity.class));
                        }
                        finish();
                    }else {
                        countText.setVisibility(View.VISIBLE);
                        passCodeView.setError(true);
                        if(count == 1){
                            countText.setText("1 time wrong!");
                        }else {
                            countText.setText(count +" times wrong!");
                        }
                        if(count > 5){
                            //TODO Delete all shared preference and databases
                            mSharePreferenceHelper.logoutSharePreference();
                            mSharePreferenceHelper.deletePinCode();

                            dbHelper.checkListDescDAO().deleteAll();
                            dbHelper.eventDAO().deleteAll();
                            dbHelper.updateEventBodyDAO().deleteAll();
                            dbHelper.uploadPhotoDAO().deleteAll();

                            startActivity(new Intent(PasscodeActivity.this, PasscodeRegisterActivity.class));
                            finishAffinity();


                        }
                    }
                    count++;
                }
            }
        });
    }



    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {

    }
}