package com.medinin.medininapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.utils.SpinnerDropDown;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class Login {
    private String TAG, med_user_id, med_user_token;
    private boolean isLoggedIn;

    public boolean isLoggedIn(Context _ctx) {
        TAG = _ctx.getClass().getSimpleName();

        SharedPreferences sp = _ctx.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        isLoggedIn = false;

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("id", med_user_id);
            _obj.put("token", med_user_token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("isLoggedIn_obj", _obj.toString());
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.LoginCheck, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("isLoggedIn", response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        isLoggedIn = true;
                    } else {
                        isLoggedIn = false;
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
        Volley.newRequestQueue(_ctx).add(jsonReq);

        return false;
    }
}
