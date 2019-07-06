package com.medinin.medininapp.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

public class AccountPremium extends AppCompatActivity {
    private static final String TAG = Settings.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ProgressDialog progress;

    private Button BetaVersionBtn;
    //follow-up sms
    private RadioGroup selectRadioBtn;
    private ImageView sendBtn;
    private RadioButton defaultRadioBtn, customRadioBtn;
    private ConstraintLayout default_sec, custom_sec;
    private String med_user_id, med_user_token, _subject = "Medinin App Bug";
    private TextView feedback_txt, bug_txt;
    private JSONObject docDetails;
    boolean isBugVisible = true, isSendingFeedback = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_premium);

        initMenu();
        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);
        mRequestQueue = Volley.newRequestQueue(this);

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("id", med_user_id);
            _obj.put("token", med_user_token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.LoginCheck, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("response", response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        docDetails = response.getJSONObject("data");
                    } else {
                        Log.i("Login detail", "Not logged in");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("Login detail", "Not logged in");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.i("Login detail", "Not logged in");
            }
        });
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
        progress = new ProgressDialog(this);
        progress.setTitle("Saving...");

        BetaVersionBtn = findViewById(R.id.BetaVersionBtn);

        BetaVersionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                betaVersionInf();
            }
        });

        LinearLayout profile_card = findViewById(R.id.profile_card);
        profile_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), DocBasicProfile.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout education_card = findViewById(R.id.education_card);
        education_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), DocEducation.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout clinic_card = findViewById(R.id.clinic_card);
        clinic_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), MyClinic.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout conditions_card = findViewById(R.id.conditions_card);
        conditions_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Conditions1.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout scan_card = findViewById(R.id.scan_card);
        scan_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), AiScan1.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout chat_card = findViewById(R.id.chat_card);
        chat_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout models_card = findViewById(R.id.models_card);
        models_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Anatomy.class);
                startActivity(intent);
            }
        });

        LinearLayout campaign_card = findViewById(R.id.campaign_card);
        campaign_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Campaign.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout analytics_card = findViewById(R.id.analytics_card);
        analytics_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Analytics.class);
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout settings_card = findViewById(R.id.settings_card);
        settings_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        LinearLayout forms_card = findViewById(R.id.forms_card);
        forms_card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Forms.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }



    private void betaVersionInf() {
        final Dialog dialog = new Dialog(AccountPremium.this);
        dialog.setContentView(R.layout.beta_version_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView closeDialogImg = dialog.findViewById(R.id.closeDialogImg);

        closeDialogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), AllPatients.class);
        finish();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    public void openFeedbackDialog(View v) {

        View dialogView = getLayoutInflater().inflate(R.layout.feedback_ticket_popup, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(AccountPremium.this);
        dialog.setContentView(dialogView);

        final EditText feedback_txt = dialogView.findViewById(R.id.feedback_txt);
        final EditText bug_txt = dialogView.findViewById(R.id.bug_txt);
        selectRadioBtn = dialogView.findViewById(R.id.radioSelect);
        defaultRadioBtn = dialogView.findViewById(R.id.defaultRadioBtn);
        customRadioBtn = dialogView.findViewById(R.id.customRadioBtn);
        default_sec = dialogView.findViewById(R.id.default_sec);
        custom_sec = dialogView.findViewById(R.id.custom_sec);
        final ImageView sendBtnGray = dialogView.findViewById(R.id.sendGray1);
        sendBtn = dialogView.findViewById(R.id.sendBtn1);
        selectRadioBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (defaultRadioBtn.getId() == checkedId) {
                    isBugVisible = true;
                    _subject = "Medinin App Bug";
                    default_sec.setVisibility(View.VISIBLE);
                    custom_sec.setVisibility(View.GONE);
                } else if (customRadioBtn.getId() == checkedId) {
                    isBugVisible = false;
                    _subject = "Medinin App Feedback";
                    default_sec.setVisibility(View.GONE);
                    custom_sec.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextWatcher _df = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (feedback_txt.getText().toString().length() >= 1) {
                    sendBtn.setVisibility(View.VISIBLE);
                    sendBtnGray.setVisibility(View.GONE);
                } else {
                    sendBtn.setVisibility(View.GONE);
                    sendBtnGray.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        final TextWatcher _ddf = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (bug_txt.getText().toString().length() >= 1) {
                    sendBtn.setVisibility(View.VISIBLE);
                    sendBtnGray.setVisibility(View.GONE);
                } else {
                    sendBtn.setVisibility(View.GONE);
                    sendBtnGray.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        feedback_txt.addTextChangedListener(_df);
        bug_txt.addTextChangedListener(_ddf);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //post SMS
                progress.setTitle("Sending...");
                progress.show();
                if (!isSendingFeedback) {
                    isSendingFeedback = true;
                    JSONObject _jsonObj = new JSONObject();
                    try {
                        _jsonObj.put("subject", _subject);
                        if (!isBugVisible) {
                            _jsonObj.put("message", feedback_txt.getText().toString());
                        } else {
                            _jsonObj.put("message", bug_txt.getText().toString());
                        }
                        _jsonObj.put("mobile", docDetails.getString("mobile"));
                        _jsonObj.put("name", docDetails.getString("name"));
                        Log.i("_jsonObj", _jsonObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.SendFeedback, _jsonObj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            VolleyLog.d(TAG, "Response: " + response.toString());
                            Log.i("response", response.toString());
                            isSendingFeedback = false;
                            dialog.hide();
                            progress.hide();
                            try {
                                boolean _error = Boolean.parseBoolean(response.getString("error"));
                                if (!_error) {
                                    Toast.makeText(getBaseContext(), "Your details has been submitted.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i("Login detail", "Not logged in");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.i("Login detail", "Not logged in");
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            isSendingFeedback = false;
                            progress.hide();
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                }
            }
        });
        default_sec.setVisibility(View.VISIBLE);
        custom_sec.setVisibility(View.GONE);

        dialog.show();
    }

    private void initMenu() {
        RelativeLayout llyt_home = findViewById(R.id.llyt_home);
        RelativeLayout llyt_appointment = findViewById(R.id.llyt_appointment);
        RelativeLayout llyt_setting = findViewById(R.id.llyt_setting);

        View home_view = findViewById(R.id.home_view);
        View home_appoitment = findViewById(R.id.home_appoitment);
        View home_setting = findViewById(R.id.home_setting);

        ImageView img_home = findViewById(R.id.img_home);
        ImageView img_appoint = findViewById(R.id.img_appoint);
        ImageView img_setting = findViewById(R.id.img_setting);

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
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });

        llyt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllPatients.class);
//                finish();
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }
}
