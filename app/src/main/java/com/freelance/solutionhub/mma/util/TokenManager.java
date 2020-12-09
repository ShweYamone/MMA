package com.freelance.solutionhub.mma.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.freelance.solutionhub.mma.activity.MainActivity;
import com.freelance.solutionhub.mma.model.LoginModel;
import com.freelance.solutionhub.mma.model.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenManager {

    Context mContext;
    ApiInterface apiInterface;
    SharePreferenceHelper mSharedPreference;

    public TokenManager(Context mContext, ApiInterface apiInterface, SharePreferenceHelper mSharedPreference) {
        this.mContext = mContext;
        this.apiInterface = apiInterface;
        this.mSharedPreference = mSharedPreference;
    }

    /*
    public void setTokenAgain() {
        Call<LoginModel> call = apiInterface.getToken(new UserModel(mSharedPreference.getUserName(), mSharedPreference.getUserPwd()));
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                LoginModel loginModel = response.body();
                if(response.isSuccessful()){
                    mSharedPreference.setToken(loginModel.getRefreshToken());
                } else {
                    Toast.makeText(mContext, response.code() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Toast.makeText(mContext, "response didn't go well", Toast.LENGTH_SHORT).show();
            }
        });
    }
*/


}
