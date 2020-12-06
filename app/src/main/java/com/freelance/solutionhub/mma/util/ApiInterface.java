package com.freelance.solutionhub.mma.util;


import com.freelance.solutionhub.mma.model.FilterModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;

import com.freelance.solutionhub.mma.model.LoginModel;
import com.freelance.solutionhub.mma.model.UserModel;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiInterface {

    @POST("service-orders")
    Call<PMServiceListModel> getPMServiceOrders(@Header("Authorization") String auth, @Body FilterModel param);

    @POST("https://7gs3iv1pt0.execute-api.ap-southeast-1.amazonaws.com/auth/login")
    Call<LoginModel> getToken(@Body UserModel userModel);

}

