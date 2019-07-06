package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.helpers.DownLoadCircleImageTask;
import com.medinin.medininapp.utils.CustomDateToString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class Forms extends AppCompatActivity {
    public static final String TAG = Forms.class.getSimpleName();
    private static final String AUTHORITY = "com.medinin.medininapp";
    RelativeLayout pdfViewerWrap;

    private LinearLayout FormsList, popularFormslist, allFormslist;
    public ProgressDialog progress;
    private RelativeLayout all_text, popular_text;

    TextWatcher _textWatcher1;

    public RequestQueue mRequestQueue;
    private long back_pressed = 0;
    boolean doubleBackToExitPressedOnce = false;

    LayoutInflater layoutInflater;
    Boolean _formsLoadStatus = true;
    ScrollView scrollView;
    private EditText mainSearchBox;
    private ImageView closeSearchImg;
    private boolean isSearchForm = false;
    int _page_number = 0;
    Context mContext;
    private ConstraintLayout basicDetailSec;

    private String patient_id, med_user_id, med_user_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mRequestQueue = Volley.newRequestQueue(this);
        FormsList = findViewById(R.id.FormsList);
        popularFormslist = findViewById(R.id.popularFormslist);
        allFormslist = findViewById(R.id.allFormslist);
        all_text = findViewById(R.id.all_text);
        popular_text = findViewById(R.id.popular_text);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        mContext = this;


        initMenu();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        pdfViewerWrap = (RelativeLayout) findViewById(R.id.pdfViewerWrap);
        basicDetailSec = (ConstraintLayout) findViewById(R.id.basicDetailSec);

        LinearLayout back_sec = (LinearLayout) findViewById(R.id.back_sec);
        back_sec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                launchAccountScreen();
            }
        });


        scrollView = findViewById(R.id.feedScroll);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                //example
                View view = scrollView.getChildAt(0);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff <= 0 && _formsLoadStatus) {
                    _formsLoadStatus = false;
                    loadMoreForms();
                }
            }
        });

        mainSearchBox = findViewById(R.id.searchBox);
        closeSearchImg = findViewById(R.id.closeSearchImg);
        closeSearchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSearchBox.setText("");
                closeSearchImg.setVisibility(View.GONE);
                mainSearchBox.clearFocus();
                hideKeyboard(Forms.this);
            }
        });
        fetchPopularForms();
        fetchAllForms();
        bindFormSearchBox();

    }

    private void launchAccountScreen() {
        Intent intent = new Intent(getApplicationContext(), AccountPremium.class);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            launchAccountScreen();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void fetchAllForms() {
        allFormslist.removeAllViews();
        //api call
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", 10);
            _obj.put("page", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress.show();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.FormsSearch,_obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    parseJsonFeed(response, false);
                } catch (Exception e) {
                    Log.d("API callback error", e.getMessage());
                }
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject dataObj = response.getJSONObject("data");
                        JSONArray feedArray = dataObj.getJSONArray("result");
                        if (feedArray.length() != 0) {
                            _formsLoadStatus = true;
                        }
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                        }
                        progress.dismiss();
                    } else {
                        progress.dismiss();
                        Toast.makeText(Forms.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
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

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }


    private void parseJsonFeed(JSONObject response, Boolean search) {
        if (search) {
            allFormslist.removeAllViews();
        }
        try {
            boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONObject dataObj = response.getJSONObject("data");
                JSONArray feedArray = dataObj.getJSONArray("result");
                if (feedArray.length() != 0) {
                    _formsLoadStatus = true;
                }
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    createAllFormsRow(feedObj, search, -1);
                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(Forms.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }

    public void createAllFormsRow(JSONObject feedObj, Boolean search, int index) {

        try {
            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            final View preview = layoutInflater.inflate(R.layout.all_forms_row, null);

            TextView form_name = preview.findViewById(R.id.form_name);
            ConstraintLayout basicDetailSec = preview.findViewById(R.id.basicDetailSec);

            form_name.setText(feedObj.getString("name"));
            final String Url_path = (feedObj.getString("file_path"));
            final String file_name = (feedObj.getString("name"));



            if (search) {

                String nameFullText = feedObj.getString("name");
                String mSearchText = mainSearchBox.getText().toString();


                if (!mSearchText.isEmpty()) {
                    int startPos = nameFullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
                    int endPos = startPos + mSearchText.length();

                    Log.i("mSearchText", mSearchText);
                    Log.i("nameFullText", nameFullText);

                    if (startPos != -1) {
                        Spannable spannable = new SpannableString(nameFullText);
                        ColorStateList _color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#282f3f")});//Color.parseColor("#e6282f3f")
                        TextAppearanceSpan highlightSpan = new TextAppearanceSpan("@font/mulibold", Typeface.NORMAL, -1, _color, null);
                        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        form_name.setText(spannable);

                    } else {
                        form_name.setText(nameFullText);
                    }
                } else {
                    form_name.setText(nameFullText);
                }
            }
            basicDetailSec.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    downloadForm(Url_path, file_name);
                }
            });

            if (index < 0){
                allFormslist.addView(preview);
            }

//            allFormslist.addView(preview);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fetchPopularForms() {
        popularFormslist.removeAllViews();
        //api call
        JSONObject _obj = new JSONObject();
        progress.show();
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.FormsFetchPopular, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    parseJsonFeedPopular(response);
                } catch (Exception e) {
                    Log.d("API callback error", e.getMessage());
                }
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {

                        JSONArray feedArray = response.getJSONArray("data");
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                        }
                        progress.dismiss();
                    } else {
                        progress.dismiss();
                        Toast.makeText(Forms.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
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

    private void parseJsonFeedPopular(JSONObject response) {
        try {
            boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONArray feedArray = response.getJSONArray("data");
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);
                    createPopularFormsRow(feedObj, -1);
                }
                progress.dismiss();
            } else {
                progress.dismiss();
                Toast.makeText(Forms.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            progress.dismiss();
            e.printStackTrace();
        }
    }
    public void createPopularFormsRow(JSONObject feedObj, int index) {
        try {
            layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            final View preview = layoutInflater.inflate(R.layout.popular_forms_row, null);

            final TextView form_name = preview.findViewById(R.id.form_name);
            ConstraintLayout basicDetailSec = preview.findViewById(R.id.basicDetailSec);

            form_name.setText(feedObj.getString("name"));
            final String Url_path = (feedObj.getString("file_path"));
            final String file_name = (feedObj.getString("name"));
            basicDetailSec.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    downloadForm(Url_path, file_name);
                }
            });
            popularFormslist.addView(preview);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMoreForms() {
        progress.show();
        progress.setTitle("Loading...");

        _page_number = _page_number + 1;
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("page", _page_number);
            _obj.put("limit", 10);

            if (!mainSearchBox.getText().toString().isEmpty()) {
                _obj.put("query", mainSearchBox.getText().toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.FormsSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    parseJsonFeed(response, isSearchForm);
                } catch (Exception e) {
                    Log.d("API callback error", e.getMessage());
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

    public void bindFormSearchBox() {
        final TextWatcher _df = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mainSearchBox.getText().toString().length() >= 1) {
                    //Make the API request
                    _page_number = 0;
                    isSearchForm = true;
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("query", mainSearchBox.getText().toString());
                        _obj.put("limit", 10);
                        _obj.put("page", _page_number);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.FormsSearch, _obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                popularFormslist.setVisibility(View.GONE);
                                all_text.setVisibility(View.GONE);
                                popular_text.setVisibility(View.GONE);
                                allFormslist.removeAllViews();
                                parseJsonFeed(response, true);
                            } catch (Exception e) {
                                Log.d("API callback error", e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    all_text.setVisibility(View.VISIBLE);
                    popular_text.setVisibility(View.VISIBLE);
                    isSearchForm = false;
                    fetchAllForms();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mainSearchBox.getText().toString().length() > 0) {
                    closeSearchImg.setVisibility(View.VISIBLE);
                } else {
                    closeSearchImg.setVisibility(View.GONE);
                    popularFormslist.setVisibility(View.VISIBLE);
                }
            }
        };

        mainSearchBox.addTextChangedListener(_df);
    }

    private void downloadForm(String Url_path, String file_name) {
        new DownloadFile().execute(Url_path, file_name);
    }


    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "medinin");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File_downloader.downloadFile(fileUrl, pdfFile);

            viewPdfFile(fileName);
            return null;
        }
    }

    private void viewPdfFile(String pdfFile) {
        try {
            Uri path = Uri.fromFile(new File("/storage/self/primary/medinin/" + pdfFile));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(path, "application/pdf");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(Forms.this, "There is no any PDF Viewer", Toast.LENGTH_LONG).show();
        }

    }

    private void initMenu() {
        RelativeLayout llyt_home = (RelativeLayout) findViewById(R.id.llyt_home);
        RelativeLayout llyt_appointment = (RelativeLayout) findViewById(R.id.llyt_appointment);
        RelativeLayout llyt_setting = (RelativeLayout) findViewById(R.id.llyt_setting);

        View home_view = (View) findViewById(R.id.home_view);
        View home_appoitment = (View) findViewById(R.id.home_appoitment);
        View home_setting = (View) findViewById(R.id.home_setting);

        ImageView img_home = (ImageView) findViewById(R.id.img_home);
        ImageView img_appoint = (ImageView) findViewById(R.id.img_appoint);
        ImageView img_setting = (ImageView) findViewById(R.id.img_setting);

        home_view.setBackgroundColor(getResources().getColor(R.color.gcolor));
        home_appoitment.setBackgroundColor(getResources().getColor(R.color.gcolor));
        home_setting.setBackgroundColor(getResources().getColor(R.color.color_blue));


        img_home.setBackgroundResource(R.drawable.ic_patient);
        img_appoint.setBackgroundResource(R.drawable.ic_cal);
        img_setting.setBackgroundResource(R.drawable.ic_user_circle_blue);

        llyt_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                startActivity(intent);

            }
        });

        llyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
                startActivity(intent);
            }
        });
    }

}
