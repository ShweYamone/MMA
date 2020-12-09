package com.freelance.solutionhub.mma.util;


import com.freelance.solutionhub.mma.model.FaultMapping;
import com.freelance.solutionhub.mma.model.FilterModelBody;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;

import com.freelance.solutionhub.mma.model.LoginModel;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UserModel;
import com.freelance.solutionhub.mma.model.UserProfile;
import com.google.gson.JsonObject;


import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiInterface {

    @Headers({"Content-Type: application/json"})
    @POST("service-orders")
    Call<PMServiceListModel> getPMServiceOrders(@Header("Authorization") String auth, @Body FilterModelBody param);

    @GET("service-orders/{pm_id}")
    Call<PMServiceInfoDetailModel> getPMServiceOrderByID(@Header("Authorization") String auth, @Path("pm_id") String pmID);

    @POST("https://7gs3iv1pt0.execute-api.ap-southeast-1.amazonaws.com/auth/login")
    Call<LoginModel> getToken(@Body UserModel userModel);

    @GET("https://nj3qiw3gn5.execute-api.ap-southeast-1.amazonaws.com/dev/profile")
    Call<UserProfile> getUserProfile(@Header("Authorization") String auth);

    @PUT("service-order-details")
    Call<ReturnStatus> updateEvent(@Header("Authorization") String auth, @Body UpdateEventBody updateEventBody);

    //APPR to ACK
    @PUT("service-order-status")
    Call<ReturnStatus> updateStatusEvent(@Header("Authorization") String auth, @Body UpdateEventBody updateEventBody);

    @GET("https://ufqzjtxo67.execute-api.ap-southeast-1.amazonaws.com/dev/fault-mappings")
    Call<ResponseBody> getFaultMappings(@Header("Authorization") String auth);

}

