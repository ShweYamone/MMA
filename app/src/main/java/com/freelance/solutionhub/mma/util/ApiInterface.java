package com.freelance.solutionhub.mma.util;

import com.freelance.solutionhub.mma.model.FilterModelBody;
import com.freelance.solutionhub.mma.model.NotificationReadModel;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;

import com.freelance.solutionhub.mma.model.LoginModel;
import com.freelance.solutionhub.mma.model.QRReturnBody;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UserModel;
import com.freelance.solutionhub.mma.model.UserProfile;

import okhttp3.ResponseBody;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    //TAG IN, TAG OUT, Problem Update,
    @Headers({"Content-Type: application/json"})
    @PUT("service-order-details")
    Call<ReturnStatus> updateEvent(@Header("Authorization") String auth, @Body UpdateEventBody updateEventBody);

    //APPR to ACK
    @PUT("service-order-status")
    Call<ReturnStatus> updateStatusEvent(@Header("Authorization") String auth, @Body UpdateEventBody updateEventBody);

    @GET("https://ufqzjtxo67.execute-api.ap-southeast-1.amazonaws.com/dev/fault-mappings")
    Call<ResponseBody> getFaultMappings(@Header("Authorization") String auth);

    //Upload Photo
    @POST("https://ufqzjtxo67.execute-api.ap-southeast-1.amazonaws.com/dev/upload/{bucketName}")
    Call<ReturnStatus> uploadPhoto(@Path("bucketName") String bucketName, @Body RequestBody file);

    //GET Data with QRCode
    @GET("https://hz35b2raaj.execute-api.ap-southeast-1.amazonaws.com/dev/component/{qrCode}")
    Call<QRReturnBody> getAssetInformation(@Header("Authorization") String auth, @Path("qrCode") String qrCode);

    //GET Notification Data
    @GET("http://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/api/notifications?page_number={page_number}&page_size={page_size}")
    Call<NotificationReadModel> getNotificationReadList(@Header("Authorization") String auth, @Path("page_number") int pageNumber, @Path("page_size") int pageSize);

}

