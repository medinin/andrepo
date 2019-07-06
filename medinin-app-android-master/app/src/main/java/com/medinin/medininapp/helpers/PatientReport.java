package com.medinin.medininapp.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.Interface.MyCallBack;
import com.medinin.medininapp.config.API_URL;

import org.json.JSONException;
import org.json.JSONObject;

public class PatientReport {
    private String TAG, med_user_id, med_user_token;
    private Context context;

    public void delPhoto(Context _ctx, final JSONObject _fileData, final MyCallBack callback) {
        context = _ctx;
        TAG = context.getClass().getSimpleName();
        try {
            final JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE,
                    API_URL.PatientReportFileDelete + _fileData.getString("file"), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("delPhoto", response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            if (_data.getString("code").toString().equals("200")) {
                                delPhotoDetails(_fileData.getString("id"), callback);
                            }
                        } else {
                            callback.call(response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            Volley.newRequestQueue(context).add(jsonReq);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void delPhotoDetails(String report_file_id, final MyCallBack callback) {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE,
                API_URL.PatientReportFiles + "/" + report_file_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                callback.call(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        Volley.newRequestQueue(context).add(jsonReq);
    }

    public void delReport(Context _ctx, final String report_id, final MyCallBack callback) {
        context = _ctx;
        TAG = context.getClass().getSimpleName();
        final JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.DELETE,
                API_URL.PatientReport + "/" + report_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                callback.call(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to volley request queue
        Volley.newRequestQueue(context).add(jsonReq);

    }
}
