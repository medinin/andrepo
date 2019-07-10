package com.medinin.medininapp.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Bharath on 06/24/2019.
 */

public class OYEApiClient {
    public static OkHttpClient okHttpClient;
    public static Retrofit retrofit;


    public static OkHttpClient getClient() {
        if (okHttpClient==null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(5);
            httpClient.connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .dispatcher(dispatcher)
                    .socketFactory(new MedininClient.CustomSocketFactory(new OkHttpClient().socketFactory()));
            okHttpClient = httpClient.build();
        }
        return okHttpClient;
    }

    public static Retrofit getClient2() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}