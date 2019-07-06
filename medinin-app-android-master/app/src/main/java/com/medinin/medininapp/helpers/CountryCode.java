package com.medinin.medininapp.helpers;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.activities.AllPatients;
import com.medinin.medininapp.activities.Welcome2;
import com.medinin.medininapp.activities.welcome;
import com.medinin.medininapp.config.API_URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class CountryCode {
    private Context context;
    private Dialog dialog;
    private ProgressDialog progress;
    private String TAG;
    private RequestQueue mRequestQueue;

    public void set(final Context context, final ImageView flagImg, final TextView country_code, final EditText mobile_number) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(this.context);
        TAG = context.getClass().getSimpleName();

        progress = new ProgressDialog(context);
        progress.setTitle("Loading...");
        progress.show();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.country_flag_and_code_dialog);

        final LinearLayout country_list = (LinearLayout) dialog.findViewById(R.id.country_list);


        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.CountryFetchAll, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        JSONArray _data = (JSONArray) response.getJSONArray("data");
                        for (int i = 0; i < _data.length(); i++) {
                            JSONObject feedObj = (JSONObject) _data.get(i);

                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View preview = inflater.inflate(R.layout.country_flag_and_code_row, null);

                            final ImageView dialogFlagImage = (ImageView) preview.findViewById(R.id.flagImage);
                            final TextView dialogCountryShortName = (TextView) preview.findViewById(R.id.countryShortName);
                            final TextView dialogCountryCode = (TextView) preview.findViewById(R.id.countryCode);

                            try {

                                dialogCountryShortName.setText(feedObj.getString("name"));
                                dialogCountryCode.setText("( " + feedObj.getString("code") + " )");
                                dialogCountryCode.setTag(feedObj.getString("code"));

                                String _img_url = API_URL.CountryFlagImage + feedObj.getString("name") + ".png";
                                dialogFlagImage.setTag(_img_url);

                                new DownLoadImageTask(dialogFlagImage).execute(_img_url);

                            } catch (Exception e) { // Catch the download exception
                                e.printStackTrace();
                            }

                            preview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String tempImgUrl = dialogFlagImage.getTag().toString();
                                    new DownLoadImageTask(flagImg).execute(tempImgUrl);
                                    country_code.setText(dialogCountryCode.getTag().toString());
                                    mobile_number.setText("");
                                    dialog.dismiss();
                                }
                            });

                            country_list.addView(preview);

                        }
                        dialog.show();
                        progress.dismiss();
                    } else {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progress.dismiss();
            }
        });
        mRequestQueue.add(jsonReq);
    }

    public void get(final Context context, final ImageView flagImg, final TextView country_code, final String _code) {
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(this.context);
        TAG = context.getClass().getSimpleName();

        if (_code != null && !_code.isEmpty() && !_code.equals("null")) {
            country_code.setText(_code);
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    API_URL.CountryFetchAll, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONArray _data = (JSONArray) response.getJSONArray("data");
                            for (int i = 0; i < _data.length(); i++) {
                                JSONObject feedObj = (JSONObject) _data.get(i);
                                try {
                                    String _codeStr = feedObj.getString("code");
                                    if (_code.equals(_codeStr)) {
                                        String _img_url = API_URL.CountryFlagImage + feedObj.getString("name") + ".png";
                                        flagImg.setTag(_img_url);
                                        new DownLoadImageTask(flagImg).execute(_img_url);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
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
            mRequestQueue.add(jsonReq);
        }
    }

}
