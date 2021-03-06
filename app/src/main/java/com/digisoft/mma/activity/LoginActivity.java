package com.digisoft.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.digisoft.mma.R;
import com.digisoft.mma.model.LoginModel;
import com.digisoft.mma.model.UserModel;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.SharePreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digisoft.mma.util.AppConstant.ACCOUNT_LOCK;
import static com.digisoft.mma.util.AppConstant.ACCOUNT_LOCK_MSG;
import static com.digisoft.mma.util.AppConstant.INVALID_GRANT;
import static com.digisoft.mma.util.AppConstant.INVALID_GRANT_MSG;
import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etUserName)
    EditText etUserName;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.progress_bar)
    RelativeLayout progress_bar;

    @BindView(R.id.layoutShowPwd)
    RelativeLayout layoutShowPwd;

    @BindView(R.id.ivShowPwd)
    ImageView ivShowPwd;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharedPreferance;


    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mSharedPreferance = new SharePreferenceHelper(this);
        mSharedPreferance.setLock(false);

        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        apiInterface = ApiClient.getClient(this);

        layoutShowPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().length());
                    ivShowPwd.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().length());
                    ivShowPwd.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorLightBlack));
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (!username.equals("") && !password.equals("")) {
                    progress_bar.setVisibility(View.VISIBLE);
                    UserModel userModel = new UserModel(username, password);
                    Call<LoginModel> call = apiInterface.getToken(userModel);
                    call.enqueue(new Callback<LoginModel>() {
                        @Override
                        public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                            LoginModel loginModel = response.body();
                            if(response.isSuccessful()){
                                mSharedPreferance.setLogin(loginModel.getUsername(), loginModel.getRefreshToken());
                                if (mSharedPreferance.isLogin()) {
                                  //  Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                String errorMessage = "Login Failed.";
                                if (response.code() == 400) {
                                    try {
                                        ResponseBody errorReturnBody = response.errorBody();
                                        JSONObject jsonObject = new JSONObject(errorReturnBody.string());

                                        if (jsonObject.has("error")) {
                                            if (jsonObject.getString("error").equals(INVALID_GRANT)) {
                                                errorMessage = INVALID_GRANT_MSG;

                                            }
                                        } else if (jsonObject.has("message")) {
                                            if (jsonObject.getString("message").equals(ACCOUNT_LOCK))
                                                errorMessage = ACCOUNT_LOCK_MSG;
                                        }

                                    } catch (IOException e) {

                                    } catch (JSONException e) {

                                    }
                                    finally {
                                        showDialog(errorMessage);
                                    //    Toast.makeText(getApplicationContext(), errorMessage + "", Toast.LENGTH_SHORT).show();
                                    }


                                }

                            }
                            progress_bar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onFailure(Call<LoginModel> call, Throwable t) {
                       //     Toast.makeText(getApplicationContext(), "response didn't go well", Toast.LENGTH_SHORT).show();
                            progress_bar.setVisibility(View.GONE);
                            showDialog("Login Failed.");
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Userid or Password is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(LoginActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(LoginActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
    }

    private void showDialog(String dialogBody) {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("Authentication Error")
                .setMessage(dialogBody)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ONClick", "onClick: ");
                    }

                })
                .show();
    }



    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        // Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
        stopHandler();//stop first and then start
        if (startHandler)
            startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, user_inactivity_time); //for 3 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSharedPreferance.getLock()) {
            Intent intent = new Intent(LoginActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    protected void onStop() {
        Log.i("Tracing......", "onStop: ");
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        mSharedPreferance.setLock(true);
    }


}
