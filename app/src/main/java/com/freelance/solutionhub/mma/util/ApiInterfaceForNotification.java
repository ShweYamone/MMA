package com.freelance.solutionhub.mma.util;

import com.freelance.solutionhub.mma.model.NotificationReadModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterfaceForNotification {
    //GET Notification Data
    @GET("http://hub-nightly-public-alb-1826126491.ap-southeast-1.elb.amazonaws.com/api/notifications")
    Call<NotificationReadModel> getNotificationReadList(@Header("Authorization") String auth, @Query("page_number") int pageNumber, @Query("page_size") int pageSize);

}
