package com.medinin.medininapp.interfaces;


import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Bharath on 06/24/2019.
 */

public interface NetworkOperation {

    @POST
    Call<Object> getNetworkCallPlainText(
            @Url String apiUrl,
            @HeaderMap Map<String, String> headerMap,
            @Body RequestBody requestBody
    );

    @POST
    Call<Object> getNetworkCall(
            @Url String apiUrl,
            @HeaderMap Map<String, String> headerMap,
            @Body Object requestBody
    );

    @POST
    Call<Object> getOKTANetworkCall(
            @Url String apiUrl
    );

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(
            @Url String fileUrl
    );

    @GET
    Call<Object> makeGetCall(
            @Url String url
    );



    @Streaming
    @POST
    Call<ResponseBody> downloadFile(
            @Url String fileUrl,
            @HeaderMap Map<String, String> headerMap,
            @Body Object requestBody
    );

    @Streaming
    @POST
    Call<ResponseBody> downloadFile(
            @Url String fileUrl,
            @HeaderMap Map<String, String> headerMap
    );

    @Streaming
    @GET
    Call<ResponseBody> downloadSubtitleFile(
            @Url String fileUrl,
            @HeaderMap Map<String, String> headerMap
    );

}
