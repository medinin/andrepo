package com.medinin.medininapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.medinin.medininapp.interfaces.NetworkOperation;
import com.medinin.medininapp.utils.DLog;
import com.medinin.medininapp.utils.DataParser;
import com.medinin.medininapp.utils.GAConstants;
import com.medinin.medininapp.utils.NetworkConfig;
import com.medinin.medininapp.utils.OYEApiClient;
import com.medinin.medininapp.utils.PrefManager;

import java.io.File;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.medinin.medininapp.utils.NetworkConfig.API_URL;


/**
 * Created by murali.matampalli on 11/15/2016.
 */
public class NetworkOperationService extends Service {
    public static final String TAG = "NetworkOperationService";
    public static final String GET = "GET_CALL";
    private static final String TEMP = "Temp";

    private int mInActivityInterval;
    private int mLockInterval;
    private int mSessionInterval;

    private NetworkOperation mNetworkOperation;
    private PrefManager mLocalSession = new PrefManager();

    public static final Integer DEF_IDLE_TIME = (5 * 1000); // ideally 5 seconds.
    public static final Integer DEF_LOCKTIME = (20*60 * 1000);// ideally 180 seconds.
    public static final Integer DEF_SESSIONTIME = (25*60 * 1000); // ideally 180 seconds.
    private static final long MIN_CLICK_INTERVAL = 10000000; //in millis    private static long lastClickTime = 0;
    private static long lastViewClickTime = 0;
    private static int lastViewId = 0;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
//            if (NetworkConfig.BASE_URL.equalsIgnoreCase("")) {
//                NetworkConfig.BASE_URL = new PrefManager().getBaseUrl();
//            }
            Retrofit mRetrofit = new Retrofit.Builder()
                    .baseUrl(NetworkConfig._domain)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OYEApiClient.getClient())
                    .build();
            mNetworkOperation = mRetrofit.create(NetworkOperation.class);

            if (intent == null) return super.onStartCommand(intent, flags, startId);

            String apiUrl = intent.getStringExtra(API_URL);
//            int apiUrlType = intent.getIntExtra(NetworkConfig.SESSION_TYPE, 0);
                new BackgroundTask(intent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private Intent mIntent;

        BackgroundTask(Intent intent) {
            mIntent = intent;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (mIntent == null) {
                DLog.e(TAG, "doInBackground() intent is null !!");
                return null;
            }
//            doGetCall();
            doNetworkOperation();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                String apiUrl = mIntent.getStringExtra(API_URL);
                sendBroadcastToUI(apiUrl, null, mIntent);
        }

        private void doGetCall() {
            final String apiUrl = mIntent.getStringExtra(API_URL);
            Call<Object> networkCall = mNetworkOperation.makeGetCall(apiUrl);
            networkCall.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response != null && response.body() != null) {
                        String jsonResponse = response.body().toString();
                        sendBroadcastToUI(apiUrl, jsonResponse, mIntent);
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    sendBroadcastToUI(apiUrl, null, mIntent);
                }
            });
        }


        protected Void doNetworkOperation() {
            final String apiUrl = mIntent.getStringExtra(API_URL);
            HashMap<String, String> headerMap = (HashMap) mIntent.getSerializableExtra(NetworkConfig.HEADER_MAP);
            final Object inputBody = mIntent.getSerializableExtra(NetworkConfig.INPUT_BODY);

            if (null == headerMap) {
                headerMap = new HashMap<>();
            }
            if (apiUrl == null ||
                    apiUrl.length() == 0 ||
                    headerMap == null ||
                    inputBody == null) {
                DLog.e(TAG, "doNetworkOperation():: some values is null !!");
            }
            DLog.d(TAG, "doNetworkOperation():: Request Url : " + apiUrl + " \nBody : " + DataParser.toJsonString(inputBody));

            //TODO :Only for testing
            //replaceGetObjectInfoAPI(apiUrl);

            //TODO: add conditions for plain response and object response
            Call<Object> networkCall;
            networkCall = mNetworkOperation.getNetworkCall(apiUrl, headerMap, inputBody);
//            if (!true) {
//                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), inputBody.toString());
//                networkCall = mNetworkOperation.getNetworkCallPlainText(apiUrl, headerMap, requestBody);
//            } else {
//                networkCall = mNetworkOperation.getNetworkCall(apiUrl, headerMap, inputBody);
//            }

            //Initiate the network call
            final long timeStamp = System.currentTimeMillis();
            networkCall.enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    DLog.d(TAG, "================> " + NetworkConfig.BASE_URL + apiUrl + "    " + (System.currentTimeMillis() - timeStamp) + " ms");
                    if (response.body() != null) {
                        try {
                            logForGoogleAnalytics(timeStamp, apiUrl, inputBody, null);
                            String jsonResponse = response.body().toString();
                            DLog.d(TAG, "doNetworkOperation():: onResponse() setting Body" + jsonResponse);
                            Gson gson = new Gson();
                            if (jsonResponse.startsWith("[")) {
                                JsonArray jsonArray = gson.toJsonTree(response.body()).getAsJsonArray();
                                sendBroadcastToUI(apiUrl, jsonArray.toString(), mIntent);
                            } else {
                                JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                                sendBroadcastToUI(apiUrl, jsonObject.toString(), mIntent);
                            }
                        } catch (Exception e) {
                            sendBroadcastToUI(apiUrl, response.body().toString(), mIntent);
                            DLog.e(TAG, e.getMessage());
                        }
                    } else {
                        sendBroadcastToUI(apiUrl, null, mIntent);
                        String resBody = DataParser.toJsonString(response.errorBody());
                        if (resBody == null || resBody.isEmpty()) {
                            logForGoogleAnalytics(timeStamp, apiUrl, inputBody, response.message());
                        } else {
                            logForGoogleAnalytics(timeStamp, apiUrl, inputBody, DataParser.toJsonString(response.errorBody()));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {

                    t.printStackTrace();
                    //Send broadcast
                    sendBroadcastToUI(apiUrl, null, mIntent);
                    logForGoogleAnalytics(timeStamp, apiUrl, inputBody, t.getMessage());
                }
            });
            return null;
        }




    }

    public static void makeGetCall(Context context, String url) {
        Intent intent = new Intent(context, NetworkOperationService.class);
        intent.putExtra(TAG, NetworkOperationService.GET);
        intent.putExtra(API_URL, url);
        context.startService(intent);
    }



    public static boolean isSingleClick() {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastViewClickTime > MIN_CLICK_INTERVAL) {
            lastViewClickTime = currentTime;
            lastViewClickTime = currentTime;
            return true;
        } else {
            lastViewClickTime = currentTime;
            return false;
        }

    }




    private void sendBroadcastToUI(String apiUrl, String responseString, Intent requestIntent) {
        Intent intent = new Intent(NetworkConfig.TASK_COMPLETE);
        intent.putExtra(API_URL, apiUrl);
        if (requestIntent != null) {
            //intent.putExtra(NetworkConfig.REQUEST_TYPE, requestIntent.getIntExtra(NetworkConfig.REQUEST_TYPE, 0));
            intent.putExtra(NetworkConfig.REQUEST_TYPE, requestIntent.getStringExtra(NetworkConfig.REQUEST_TYPE));
            intent.putExtra(NetworkConfig.INPUT_BODY, requestIntent.getSerializableExtra(NetworkConfig.INPUT_BODY));
        }

        intent.putExtra(NetworkConfig.RESPONSE_BODY, responseString);
        //Send broadcast
        DLog.d(TAG, "doNetworkOperation():: sendBroadcastToUI()");
        LocalBroadcastManager.getInstance(NetworkOperationService.this).sendBroadcast(intent);
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void logForGoogleAnalytics(long timeStamp, String apiUrl, Object request, String errorresponse) {
        String label = "";

        String category = null;
        if (apiUrl != null) {
            if ((System.currentTimeMillis() - timeStamp) > 1500) {
                label = "Time Taken: " + ((System.currentTimeMillis() - timeStamp) / 1000);
                category = GAConstants.API_RESPONSE_TIME;
            }
            label += " API: " + apiUrl;
            if (request != null) {
                label = label + " Request: " + DataParser.toJsonString(request);
            } else {
                label = label + " Request: " + "null";

            }
            if (errorresponse != null) {
                label = label + " Error: " + errorresponse;
                category = GAConstants.API_ERROR;
            }

            if (category != null) {
                DLog.trackEvent(category, new File(apiUrl).getName(), label);
            }
        }
    }
}