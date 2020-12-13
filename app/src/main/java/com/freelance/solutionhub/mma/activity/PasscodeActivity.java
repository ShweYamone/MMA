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
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import org.w3c.dom.Text;

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


    private SharePreferenceHelper mSharePreferenceHelper;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        mSharePreferenceHelper = new SharePreferenceHelper(this);

//        Typeface typeFace = Typeface.createFromAsset(getAssets(), "font/crimson_text_bold.ttf");
//        passCodeView.setTypeFace(typeFace);
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {
                Log.i("Passcode", "text");
                if(text.length() == 4){
                    count++;
                    if(text.equals(mSharePreferenceHelper.getPinCode())){
                       finish();
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


}