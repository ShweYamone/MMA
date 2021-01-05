package com.digisoft.mma.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.digisoft.mma.util.AppConstant.BASE_URL;

public class ApiClient {

    private static ApiInterface apiService;
    private static Cache cache;

    public static ApiInterface getClient(final Context context) {
        if (apiService == null) {

            Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    int maxAge = 300; // read from cache for 5 minute
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                }
            };

            //setup cache
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            cache = new Cache(httpCacheDirectory, cacheSize);

            final OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
            okClientBuilder.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            // Add other Interceptors
            okClientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
//                            .header("Authorization", token_type + " " + access_token)
                            .method(original.method(), original.body())
                            .build();


                    Response response =  chain.proceed(request);

                    if (response.code() == 401){

                        // Magic is here ( Handle the error as your way )
                        return response;
                    }
                    return response;
                }
            });
            okClientBuilder.cache(cache);

            OkHttpClient okClient = okClientBuilder.build();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = client.create(ApiInterface.class);
        }
        return apiService;
    }

    public static void removeFromCache(String url) {

        try {
            Iterator<String> it = cache.urls();

            while (it.hasNext()) {
                String next = it.next();
                Log.i("!!!url", next);

                if (next.contains(BASE_URL + url)) {
                    Log.i("!!!true" , BASE_URL + url);
                    it.remove();
                }
            }
        } catch (IOException e) {
           Log.i("REMOVEFROMCACHE", e.getMessage());
        }
    }
}
