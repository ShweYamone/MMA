package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.Data;
import com.freelance.solutionhub.mma.model.UserProfile;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.arjsna.passcodeview.PassCodeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasscodeActivity extends AppCompatActivity{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tv_pin_wrong)
    TextView pinWrongInfo;

    @BindView(R.id.pass_code_view)
    PassCodeView passCodeView;


    private SharePreferenceHelper mSharePreferenceHelper;
    private int count = 0;
    private ApiInterface apiInterface;
    private Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mSharePreferenceHelper = new SharePreferenceHelper(this);
        apiInterface = ApiClient.getClient(this);
        network = new Network(this);

        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                Log.i("Passcode", "text");
                if(text.length() == 6){
                    count++;
                    if(text.equals(mSharePreferenceHelper.getPinCode())){
                        mSharePreferenceHelper.setLock(false);
                        if (mSharePreferenceHelper.isLogin()) {

                                if (getIntent().hasExtra("workInMiddle") &&
                                        getIntent().getStringExtra("workInMiddle").equals("work")) {

                                } else
                                    startActivity(new Intent(PasscodeActivity.this, MainActivity.class));

                                finish();
                        } else {
                            startActivity(new Intent(PasscodeActivity.this, LoginActivity.class));
                            finish();
                        }
                    }else {
                        passCodeView.setError(true);
                    }
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