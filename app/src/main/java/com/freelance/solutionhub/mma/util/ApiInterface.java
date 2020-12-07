package com.freelance.solutionhub.mma.util;


import com.freelance.solutionhub.mma.model.FilterModel;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;

import com.freelance.solutionhub.mma.model.LoginModel;
import com.freelance.solutionhub.mma.model.UserModel;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiInterface {

    @POST("service-orders")
    Call<PMServiceListModel> getPMServiceOrders(@Header("Authorization") String auth, @Body FilterModel param);

    @Headers({"Content-Type: application/json"})
    @GET("dev/service-orders/{pm_id}")
    Call<PMServiceInfoDetailModel> getPMServiceOrderByID(@Path ("pm_id") String pmID , @Header("Authorization") String auth);

    @POST("https://7gs3iv1pt0.execute-api.ap-southeast-1.amazonaws.com/auth/login")
    Call<LoginModel> getToken(@Body UserModel userModel);



}

