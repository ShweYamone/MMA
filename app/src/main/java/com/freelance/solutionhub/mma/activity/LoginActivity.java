package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.LoginModel;
import com.freelance.solutionhub.mma.model.UserModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etUserName)
    EditText etUserName;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.toolbar)

    Toolbar toolbar;
    private ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupToolbar();


        apiInterface = ApiClient.getClient(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                UserModel userModel = new UserModel(username, password);
                Call<LoginModel> call = apiInterface.getToken(userModel);
                call.enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                        LoginModel loginModel = response.body();
                        if(response.isSuccessful()){
                            Toast.makeText(getApplicationContext(), loginModel.getUsername() +", " ,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "response didn't go well", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void setupToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


}
