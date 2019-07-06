package com.medinin.medininapp.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.activities.AllPatients;
import com.medinin.medininapp.config.API_URL;

import org.json.JSONException;
import org.json.JSONObject;

public class DeletePatient extends AppCompatActivity {
    private ProgressDialog progress;
    private String med_user_id, TAG;
    private Context context;
    private LinearLayout linearLayoutList;
    private RequestQueue mRequestQueue;
    private SharedPreferences sp;

    public void deletePatientAccount(final Context context, final View preview, String pat_id, final LinearLayout linearLayoutList) {
        this.context = context;
        sp = this.context.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        TAG = AllPatients.class.getSimpleName();
        mRequestQueue = Volley.newRequestQueue(this.context);
        progress = new ProgressDialog(context);
        progress.setTitle("Deleting patient account...");
        progress.show();
        SharedPreferences sp = context.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        TAG = AllPatients.class.getSimpleName();


        JSONObject _obj = new JSONObject();
        try {
            _obj.put("doc_id", med_user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientDeleteAccount + pat_id, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        linearLayoutList.removeView(preview);
                        Toast.makeText(context, "Patient account deleted successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }
}
