package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.medinin.medininapp.R;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.helpers.DownLoadCircleImageTask;
import com.medinin.medininapp.helpers.ImageEvents;
import com.medinin.medininapp.models.ImageSlide;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.SlideshowDialogFragment;
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DocEducation extends AppCompatActivity {
    private static final String TAG = DocEducation.class.getSimpleName();
    private String med_user_id, med_user_token;
    private RequestQueue mRequestQueue;
    private ProgressDialog progress;

    private Uri _file_path;
    private EditText qualificationInput, registrationNumInput;
    private Spinner expSpinner, departmentSpinner;
    private String education_id;
    private Boolean EduUpdateStatus = false;
    private TextView editBtnTxt, saveBtnTxt, add_photo_txt;
    private ConstraintLayout qualificationInputWrap,
            expSpinnerWrap,
            registrationNumInputWrap,
            departmentSpinnerWrap;
    private RelativeLayout activity_doc_education;
    private FrameLayout blockInputViews, errorMsgWrap;
    private LinearLayout add_photo_sec;
    private int PICK_IMAGE_MULTIPLE = 1;
    private ArrayList<Drawable> newPhotosArrayList;
    private ArrayList<ImageSlide> galleryImages;
    private AlertDialog alertPageExitDialog;
    private boolean isCertificatePresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_education);

        initMenu();

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(DocEducation.this);
        progress.setMessage("Loading...");
        galleryImages = new ArrayList<>();
        newPhotosArrayList = new ArrayList<Drawable>();

        blockInputViews = (FrameLayout) findViewById(R.id.blockInputViews);
        errorMsgWrap = (FrameLayout) findViewById(R.id.errorMsgWrap);
        activity_doc_education = (RelativeLayout) findViewById(R.id.activity_doc_education);
        qualificationInput = (EditText) findViewById(R.id.qualificationInput);
        registrationNumInput = (EditText) findViewById(R.id.registrationNumInput);
        expSpinner = (Spinner) findViewById(R.id.expSpinner);
        departmentSpinner = (Spinner) findViewById(R.id.departmentSpinner);

        ArrayList<StringWithTag> expList = new ArrayList<StringWithTag>();
        expList.add(new StringWithTag("Select", ""));
        for (int i = 1; i < 50; i++) {
            expList.add(new StringWithTag(String.valueOf(i + "  yrs"), String.valueOf(i)));
        }
        bindSpinnerDropDown(expSpinner, expList);

        fetchSpecialities();
        fetchEducationDetails();

        editBtnTxt = (TextView) findViewById(R.id.editBtnTxt);
        saveBtnTxt = (TextView) findViewById(R.id.saveBtnTxt);
        qualificationInputWrap = (ConstraintLayout) findViewById(R.id.qualificationInputWrap);
        expSpinnerWrap = (ConstraintLayout) findViewById(R.id.expSpinnerWrap);
        registrationNumInputWrap = (ConstraintLayout) findViewById(R.id.registrationNumInputWrap);
        departmentSpinnerWrap = (ConstraintLayout) findViewById(R.id.departmentSpinnerWrap);
        add_photo_sec = (LinearLayout) findViewById(R.id.add_photo_sec);
        add_photo_txt = (TextView) findViewById(R.id.add_photo_txt);

        editBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisableInputs(true);
            }
        });

        saveBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(DocEducation.this);
                if (EduUpdateStatus) {
                    updateEducationDetails();
                } else {
                    saveEducationDetails();
                }
            }
        });

        blockInputViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DocEducation.this, "Please click on the edit button to update details!", Toast.LENGTH_LONG).show();
            }
        });

        enableDisableInputs(false);

        add_photo_sec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!isCertificatePresent) {
                    if (blockInputViews.getVisibility() == View.GONE) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                    } else {
                        blockInputViews.performClick();
                    }
                } else {
                    showBigImgPopup(null);
                }

            }
        });

        alertPageExitDialog = new AlertDialog.Builder(DocEducation.this)
                .setTitle("Close page")
                .setMessage("Do you want to exit without saving your details?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        launchAccountScreen();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_sec);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (saveBtnTxt.getVisibility() == View.VISIBLE) {
                    alertPageExitDialog.show();
                } else {
                    launchAccountScreen();
                }
            }
        });

        //On close of soft keyboard clear edittext focus
        // it's works only if activity windowSoftInputMode=adjustResize
        new ClearFocusOnKBClose(activity_doc_education);
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
            if (saveBtnTxt.getVisibility() == View.VISIBLE) {
                alertPageExitDialog.show();
            } else {
                launchAccountScreen();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void openFileUploadOptionsDialog(View v) {
        Dialog dialog = new Dialog(DocEducation.this);
        dialog.setContentView(R.layout.file_upload_options);

        TextView cameraOpenBtn = (TextView) dialog.findViewById(R.id.cameraOpenBtn);
        TextView galleryOpenBtn = (TextView) dialog.findViewById(R.id.galleryOpenBtn);

        cameraOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraLayout(0);

            }
        });
        galleryOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showFileChooser(1);


            }
        });

        dialog.show();
    }


    private void showFileChooser(int reqCode) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            startActivityForResult(pickPhoto, reqCode);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCameraLayout(int reqCode) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                //android.Manifest.permission.READ_CONTACTS,
                //android.Manifest.permission.WRITE_CONTACTS,
                //android.Manifest.permission.READ_SMS,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            _file_path = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, _file_path);
            startActivityForResult(intent, 0);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void bindSpinnerDropDown(Spinner spinner, ArrayList<StringWithTag> list) {
        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(DocEducation.this, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private void fetchSpecialities() {
        progress.setTitle("Loading...");
        progress.show();
        JsonObjectRequest jsonEduReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.DocSpecialties, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {

                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data") && response.getJSONArray("data").length() != 0) {
                        JSONArray _data = (JSONArray) response.getJSONArray("data");

                        ArrayList<StringWithTag> specialitiesList = new ArrayList<StringWithTag>();
                        specialitiesList.add(new StringWithTag("Select", ""));
                        for (int i = 0; i < _data.length(); i++) {
                            JSONObject _feedObj = (JSONObject) _data.get(i);
                            specialitiesList.add(new StringWithTag(_feedObj.getString("name"), _feedObj.getString("id")));
                        }
                        bindSpinnerDropDown(departmentSpinner, specialitiesList);
                        fetchDocDetails();
                    } else {
                        Toast.makeText(DocEducation.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
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

        // Adding request to volley request queue
        jsonEduReq.setShouldCache(false);
        mRequestQueue.add(jsonEduReq);
    }

    private void fetchDocDetails() {
        progress.show();
        progress.setMessage("Loading...");

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.Doc + "/" + med_user_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        if (!response.isNull("data")) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");

                            if (!_data.getString("specialty_id").isEmpty() && !_data.getString("specialty_id").equals("null")) {
                                SpinnerDropDown.setSpinnerItem(departmentSpinner, _data.getString("specialty_id"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }

    private void fetchEducationDetails() {
        progress.setTitle("Loading...");
        progress.show();
        JsonObjectRequest jsonEduReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.DocEducationMine + med_user_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {

                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data") && response.getJSONArray("data").length() != 0) {
                        JSONArray _data = (JSONArray) response.getJSONArray("data");
                        JSONObject _feedObj = (JSONObject) _data.get(0);

                        qualificationInput.setText(removeNull(_feedObj.getString("qualification")));
                        registrationNumInput.setText(removeNull(_feedObj.getString("reg_no")));
                        SpinnerDropDown.setSpinnerItem(expSpinner, _feedObj.getString("exp"));

                        education_id = _feedObj.getString("id");
                        EduUpdateStatus = true;

                        if (!_feedObj.isNull("doc_education_files")) {
                            JSONArray _filesArray = (JSONArray) _feedObj.getJSONArray("doc_education_files");
                            int _totalFiles = _filesArray.length();

                            if (_totalFiles > 0) {
                                add_photo_txt.setText("View Certificate");
                                isCertificatePresent = true;
                            }

                            for (int f = 0; f < _totalFiles; f++) {
                                JSONObject _jObj = (JSONObject) _filesArray.get(f);
                                String img_url = API_URL._domain + "/doc-education-file/photo/" + _jObj.getString("file");

                                ImageSlide image = new ImageSlide();
                                //image.setName("New image");
                                image.setSmall(img_url);
                                image.setMedium(img_url);
                                image.setLarge(img_url);
                                galleryImages.add(image);
                            }
                        }

                    } else {
                        Toast.makeText(DocEducation.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
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

        // Adding request to volley request queue
        jsonEduReq.setShouldCache(false);
        mRequestQueue.add(jsonEduReq);
    }

    private void saveEducationDetails() {
        progress.setMessage("Saving...");

        Boolean _error = false;

        if (validateEditTextInput(qualificationInput)) {
            _error = true;
            Toast.makeText(DocEducation.this, "Please enter qualification!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(expSpinner).toString().isEmpty()) {
            _error = true;
            Toast.makeText(DocEducation.this, "Please enter experience!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(departmentSpinner).toString().isEmpty()) {
            _error = true;
            //Toast.makeText(DocEducation.this, "Please select specialty", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            errorMsgWrap.setVisibility(View.GONE);
            enableDisableInputs(false);
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("qualification", qualificationInput.getText());
                _obj.put("exp", SpinnerDropDown.getSpinnerItem(expSpinner));
                _obj.put("reg_no", registrationNumInput.getText());
                _obj.put("doc_id", med_user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.DocEducation, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            education_id = _data.getString("id");
                            EduUpdateStatus = true;
                            uploadCertificatePhotos();
                            Toast.makeText(DocEducation.this, "Data saved successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DocEducation.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DocEducation.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
            Volley.newRequestQueue(this).add(jsonReq);
        } else {
            errorMsgWrap.setVisibility(View.VISIBLE);
        }
    }

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }

    private void updateEducationDetails() {
        progress.setMessage("Updating...");
        Boolean _error = false;

        if (validateEditTextInput(qualificationInput)) {
            _error = true;
            //Toast.makeText(DocEducation.this, "Please enter qualification!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(expSpinner).toString().isEmpty()) {
            _error = true;
            //Toast.makeText(DocEducation.this, "Please enter experience!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(departmentSpinner).toString().isEmpty()) {
            _error = true;
            //Toast.makeText(DocEducation.this, "Please select specialty", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            errorMsgWrap.setVisibility(View.GONE);
            enableDisableInputs(false);
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("qualification", qualificationInput.getText());
                _obj.put("exp", SpinnerDropDown.getSpinnerItem(expSpinner));
                _obj.put("reg_no", registrationNumInput.getText());
                _obj.put("doc_id", med_user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.DocEducation + "/" + education_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            updateDocDetails();
                            uploadCertificatePhotos();
                            Toast.makeText(DocEducation.this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(DocEducation.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DocEducation.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

            // Adding request to volley request queue
            mRequestQueue.add(jsonReq);
        } else {
            errorMsgWrap.setVisibility(View.VISIBLE);
        }
    }

    private void updateDocDetails() {
        progress.show();
        progress.setMessage("Verifying...");

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("specialty_id", SpinnerDropDown.getSpinnerItem(departmentSpinner));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.Doc + "/" + med_user_id, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        Toast.makeText(DocEducation.this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DocEducation.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DocEducation.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void enableDisableInputs(Boolean _enable) {
        if (_enable) {
            qualificationInputWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            expSpinnerWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            registrationNumInputWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            departmentSpinnerWrap.setBackgroundResource(R.drawable.input_border_bottom_focus);
            blockInputViews.setVisibility(View.GONE);
            editBtnTxt.setVisibility(View.GONE);
            saveBtnTxt.setVisibility(View.VISIBLE);
        } else {
            qualificationInputWrap.setBackgroundResource(R.drawable.input_border_bottom);
            expSpinnerWrap.setBackgroundResource(R.drawable.input_border_bottom);
            registrationNumInputWrap.setBackgroundResource(R.drawable.input_border_bottom);
            departmentSpinnerWrap.setBackgroundResource(R.drawable.input_border_bottom);
            blockInputViews.setVisibility(View.VISIBLE);
            editBtnTxt.setVisibility(View.VISIBLE);
            saveBtnTxt.setVisibility(View.GONE);
        }
    }

    //Add certificates
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progress.setMessage("Uploading photos...");
        progress.show();
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    newPhotosArrayList = new ArrayList<Drawable>();

                    Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    bitmapImage = ImageEvents.orientation(DocEducation.this, bitmapImage, mImageUri);

                    BitmapDrawable bitmapDrawable = new BitmapDrawable(DocEducation.this.getResources(), bitmapImage);
                    newPhotosArrayList.add(bitmapDrawable);

                    String img_url = ImageEvents.getAbsolutePath(DocEducation.this, mImageUri);
                    ImageSlide image = new ImageSlide();
                    //image.setName("New image");
                    image.setSmall(img_url);
                    image.setMedium(img_url);
                    image.setLarge(img_url);
                    galleryImages.add(image);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        newPhotosArrayList = new ArrayList<Drawable>();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();

                            Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            bitmapImage = ImageEvents.orientation(DocEducation.this, bitmapImage, uri);

                            BitmapDrawable bitmapDrawable = new BitmapDrawable(DocEducation.this.getResources(), bitmapImage);
                            newPhotosArrayList.add(bitmapDrawable);

                            String img_url = ImageEvents.getAbsolutePath(DocEducation.this, uri);
                            ImageSlide image = new ImageSlide();
                            //image.setName("New image");
                            image.setSmall(img_url);
                            image.setMedium(img_url);
                            image.setLarge(img_url);
                            galleryImages.add(image);
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        progress.dismiss();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadCertificatePhotos() {
        for (int i = 0; i < newPhotosArrayList.size(); i++) {
            final Drawable drawableImg = newPhotosArrayList.get(i);

            if (drawableImg != null) {
                progress.setTitle("uploading...");
                progress.show();
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.DocEducationFileUpload, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        progress.dismiss();
                        try {
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            JSONObject result = new JSONObject(json);
                            boolean _error = Boolean.parseBoolean(result.getString("error"));
                            if (!_error && !result.isNull("data")) {
                                postEduCertificateFiles(result.getJSONObject("data"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        NetworkResponse networkResponse = error.networkResponse;
                        String errorMessage = "Unknown error";
                        if (networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                errorMessage = "Request timeout";
                            } else if (error.getClass().equals(NoConnectionError.class)) {
                                errorMessage = "Failed to connect server";
                            }
                        } else {
                            String result = new String(networkResponse.data);
                            Log.i("upload file error", result.toString());
                        }
                        Log.i("Error", errorMessage);
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        return params;
                    }

                    @Override
                    protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), drawableImg), "image/jpeg"));

                        return params;
                    }
                };

                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
            }
        }
        newPhotosArrayList.clear();
    }

    private void postEduCertificateFiles(JSONObject _data) {
        final JSONObject _obj = new JSONObject();
        progress.setTitle("updating photo list...");
        progress.show();
        try {
            _obj.put("doc_education_id", education_id);
            _obj.put("file", _data.getString("file"));
            _obj.put("file_type", _data.getString("file_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("_obj", _obj.toString());
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocEducationFile, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        Log.i("postEduCertificateFiles", response.toString());
                        JSONObject _fileData = (JSONObject) response.getJSONObject("data");
                        Toast.makeText(DocEducation.this, "Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("Error ", "Something went wrong!" + response.toString());
                        Toast.makeText(DocEducation.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
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

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void showBigImgPopup(View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", galleryImages);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    //Add certificates end

}
