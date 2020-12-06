package com.freelance.solutionhub.mma.util;

import com.freelance.solutionhub.mma.model.FilterModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface ApiInterface {

    @Headers({"Content-Type: application/json"})
    @POST("dev/service-orders")
    Call<PMServiceListModel> getPMServiceOrders(@Header("Authorization") String auth, @Body FilterModel param);

}

