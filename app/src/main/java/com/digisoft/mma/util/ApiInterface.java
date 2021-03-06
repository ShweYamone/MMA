package com.digisoft.mma.util;

import com.digisoft.mma.model.CheckListModel;
import com.digisoft.mma.model.FilterModelBody;
import com.digisoft.mma.model.PMServiceInfoDetailModel;
import com.digisoft.mma.model.PMServiceListModel;

import com.digisoft.mma.model.LoginModel;
import com.digisoft.mma.model.PhotoAttachementModel;
import com.digisoft.mma.model.QRReturnBody;
import com.digisoft.mma.model.ThirdPartyModel;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.model.ReturnStatus;
import com.digisoft.mma.model.UserModel;
import com.digisoft.mma.model.UserProfile;
import com.digisoft.mma.model.VerificationReturnBody;

import java.util.List;

import okhttp3.ResponseBody;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


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
    @POST("http://alb-java-apps-776075049.ap-southeast-1.elb.amazonaws.com:9000/upload/{bucketName}")
    Call<ReturnStatus> uploadPhoto(@Header("Authorization") String auth, @Path("bucketName") String bucketName, @Body RequestBody file);

    //GET Data with QRCode
    @GET("https://hz35b2raaj.execute-api.ap-southeast-1.amazonaws.com/dev/component/{qrCode}")
    Call<QRReturnBody> getAssetInformation(@Header("Authorization") String auth, @Path("qrCode") String qrCode);

    //Get check list
    @GET("service-orders/{serviceOrderId}/pm-check-list")
    Call<List<CheckListModel>> getCheckList(@Header("Authorization") String auth, @Path("serviceOrderId") String serviceOrderId);

    //Fault Clearance Verification
    @GET("http://control-center-nightly-alb-906188569.ap-southeast-1.elb.amazonaws.com/control/fault-status")
    Call<VerificationReturnBody> verifyWorks(@Header("Authorization") String auth, @Query("id") String serviceOrderId);

    //Get photo attachment from serviceOrderId
    @GET("https://oiyryh245h.execute-api.ap-southeast-1.amazonaws.com/dev/service-orders/{service_order_id}/file-attachments")
    Call<PhotoAttachementModel> getPhotoAttachment(@Header("Authorization") String auth, @Path("service_order_id") String serviceOrderId);

    //Get download photo
    @GET("http://alb-java-apps-776075049.ap-southeast-1.elb.amazonaws.com:9000{photo_filename}")
    Call<ResponseBody> downloadPhoto(@Header("Authorization") String auth, @Path("photo_filename") String fileName );

    //Get ThirdParty Info
    @GET("service-orders/{service_order_id}/third-parties")
    Call<List<ThirdPartyModel>> getThirdParties(@Header("Authorization") String auth, @Path("service_order_id") String serviceOrderId);
}

