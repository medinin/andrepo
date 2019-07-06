package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.print.PrintHelper;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.medinin.medininapp.Interface.MyCallBack;
import com.medinin.medininapp.R;
import com.medinin.medininapp.adapters.MedicineListAdapter;
import com.medinin.medininapp.app.AppController;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.data.MedicineItem;
import com.medinin.medininapp.data.StringWithTag;
import com.medinin.medininapp.helpers.DownLoadImageTask;
import com.medinin.medininapp.helpers.CustomSlider;
import com.medinin.medininapp.helpers.PatientPrescription;
import com.medinin.medininapp.helpers.PatientReport;
import com.medinin.medininapp.models.ImageSlide;
import com.medinin.medininapp.utils.ClearFocusOnKBClose;
import com.medinin.medininapp.utils.CustomDateToString;
import com.medinin.medininapp.utils.SlideshowDialogFragment;
import com.medinin.medininapp.utils.SpinnerDropDown;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ir.apend.slider.model.Slide;


public class PatientHistory extends AppCompatActivity {
    private static final String TAG = PatientHistory.class.getSimpleName();

    //Common variables
    private String patient_id, med_user_id, med_user_token;
    private ProgressDialog progress;
    private RequestQueue mRequestQueue;
    private LayoutInflater layoutInflater;
    private TextView userNameTxt, appointmentDate, appointmentTime;
    int realWidth;
    int realHeight;
    private EditText customEdit;
    private String mobile_str = "", docmobile_str = "", docNameTxt_str = "", appointmentDate_str = "", appointmentTime_str = "", status = "", customFeedback = "";
    private FrameLayout topSettingPopup;
    private TextView viewChartTxtBtn;
    private ImageView topSettingImgBtn;
    private Dialog getOTPDialog, otpInputDialog;
    private RelativeLayout activity_patient_history;
    private String message;
    ArrayList<ImageSlide> galleryImages;
    private Boolean isprogressBarStatus;

    //TabLinks section variables
    private ImageView drugTabLink, reportTabLink, vitalTabLink;
    private CoordinatorLayout prescriptionContWrap, reportContWrap, bodyVitalsContWrap;

    //Prescription section variables
    private ScrollView presScrollView;
    private ImageView addNewPresImg;

    private Boolean _presLoadMoreStatus = true;
    private int pres_list_page_num = 1;
    private LinearLayout presListWrap;
    private BottomSheetDialog addPresdialog;
    private String selectedPrescription_id;

    //Add prescription dialog variables
    private LinearLayout medicineListWrap, addMedicineCardBtn, search_wrap, plus_wrap, medicineCameraBtnWrap;
    private ArrayList<StringWithTag> dosageList, daysList;
    private Uri _file_path;
    private Boolean firstMedicineCard = true;
    private EditText followUpDateInput, presNoteInput;
    private NestedScrollView medicineScrollView;
    private Boolean follow_up_status = false, isPresUpdate = false, isPresEditDetails = false;
    private String prescription_id;
    private int count_pres_post_api_call = 0, total_pres_apis_call = 0;
    private int pres_card_row_index = 0;

    private TextView savePresDialogBtn, updatePresDialogTxt, updatePresDialogBtn, default_followup_txt, print_txt;

    private LinearLayout selectedMedPhotoSliderWrap;
    List<Slide> addNewPresPhotoSlideList;

    //Search medicine
    private EditText search_medicine_box;
    private FrameLayout medicine_autocomplete_popup;
    private ListView searchListView;
    private MedicineListAdapter searchListAdapter;
    private List<MedicineItem> searchModelItems;
    private Boolean loadMoreMedecines = false;
    private int searchPage = 1;
    private ImageView closeSearchMedImg;
    private View closeSearchPopupMedView;

    Calendar calDate = Calendar.getInstance();
    int year = calDate.get(Calendar.YEAR);
    int month = calDate.get(Calendar.MONTH);
    int day = calDate.get(Calendar.DAY_OF_MONTH);
    String currentDate = String.format("%02d-%02d-%d", (month + 1), day, year);

    //Camera request codes
    private static final int ADD_REPORT_REQUEST_CODE = 111,
            ADD_MORE_REPORT_REQUEST_CODE = 222,
            ADD_PRESC_REQUEST_CODE = 333,
            ADD_MORE_PRESC_REQUEST_CODE = 444,
            ADD_MORE_PRESC_LIST_ROW_REQUEST_CODE = 555;

    //Reports variables
    private ImageView imageHolder;
    private Boolean _reportLoadMoreStatus = true;
    private int report_page_load_num = 1;
    private LinearLayout selectedPhotoSliderWrap;
    private ScrollView reportScrollView;
    private LinearLayout reportListWrap, addReportPhoto, medicineLinearListWrap, selectedLayoutDots;
    private ViewPager selectedViewPager;
    private String report_id;

    //Body vitals variables
    ArrayList<StringWithTag> bloodGroupList;
    JSONArray _blood_arr;
    private String blood_group_id = "";
    private ImageView bloodGroupLockIcon, bloodGroupArrow;

    private Boolean bloodGroupLockStatus = false;

    String _otpID;
    Dialog otpDialog;
    TextView countDownTxt, dialogSendOtp;

    private EditText otp_input_one, otp_input_two, otp_input_three, otp_input_four;

    private Boolean _vitalsLoadMoreStatus = true;
    private int vital_page_load_num = 1;
    private LinearLayout vitalsListWrap;
    private TextView heightTxt;
    private boolean onFirstClickOfVitalSpinnerHeight = true;

    private Spinner bloodGroupSpinner, heightSpinner;

    private int bloodSelectCount = 0;
    private ScrollView vitalsTableScrollView;
    private BottomSheetDialog addBodyVitalsdialog;
    private ImageView addNewVitalsImg;

    //add vitals variables
    private Spinner weightInputSpinner, pulseInputSpinner,
            tempInputSpinner, bpHighSpinner, bpLowSpinner;

    String heightInput = "";
    private LinearLayout CGSLinkWrap, SILinkWrap;
    private boolean isPageLoadBloodGroupChange = false;
    private boolean isPageLoadHieghtChange = false, isSIUnit = true;

    //Appointment variables
    private String appointment_id, _status = "", default_txt = "";
    private FrameLayout topEditProfileSettingPopup;
    private TextView editProfileTxtBtn;
    private ConstraintLayout userProfilePicWrap;
    private ImageView userProfileImg;

    //Edit patient profile dialog varables
    private Spinner genderSpinner;
    EditText name, name_edit, dob_edit, num_edit, otp_edit;
    TextView updateBtn, doneBtn, getOtpBtn, resendOtpBtn, otpDisabledBtn;
    TextWatcher _textWatcher, _num_textWatcher, _otp_textWatcher;
    LinearLayout otpInputWrap;
    ConstraintLayout otpVerifyTxtWrap;
    private FrameLayout next_btn_wrap, update_next_btn_wrap, no_data_perscription, no_data_report;
    private LinearLayout getOtpBtnWrap;
    private BottomSheetDialog editProfileDialog;


    //follow-up sms
    private RadioGroup selectRadioBtn;
    private ImageView sendBtn;
    private RadioButton defaultRadioBtn, customRadioBtn;
    private ConstraintLayout default_sec, custom_sec;
    private TextView feedback_txt;

    private BottomSheetDialog registerNewPatDialog;
    private ImageView regTabLink, addPhotoTabLink;
    private TextView regTabLinkBr, addPhotoTabLinkBar;
    private TextView nextBtntxt;
    private LinearLayout patGetOtpBtnWrap, patientBasicDetailWrap, addPhotoWrap;

    WebView myWebView;
    private PermissionRequest mPermissionRequest;
    ImageView camPreview1, camPreview2, camPreview3, reloadSec, uploadSec, changeCam;
    private Boolean camPreview1_status = false, camPreview2_status = false, camPreview3_status = false, reloading = false;
    String camPreview1_status_str = "off";
    String profilePicData;
    Button uploadBtn;
    LinearLayout previewImgSec;
    private Boolean isNewPatMobileVerified = false, isEditPatientDialog = false;
    private String ageStr = "", tempDateStr;
    private Boolean is_doc_can_edit_pres = true;
    private boolean isPresPhotoRow = false;
    private boolean appointStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        initMenu();

        Intent intent = getIntent();
        isprogressBarStatus = intent.getBooleanExtra("ProgressBar", false);
        patient_id = intent.getStringExtra("patient_id");
        appointment_id = intent.getStringExtra("appointment_id");

        SharedPreferences sp = this.getSharedPreferences("Login", MODE_PRIVATE);
        med_user_id = sp.getString("med_user_id", null);
        med_user_token = sp.getString("med_user_token", null);

        mRequestQueue = Volley.newRequestQueue(this);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        userNameTxt = (TextView) findViewById(R.id.userNameTxt);
        viewChartTxtBtn = (TextView) findViewById(R.id.viewChartTxtBtn);
        topSettingPopup = (FrameLayout) findViewById(R.id.topSettingPopup);
        topSettingImgBtn = (ImageView) findViewById(R.id.topSettingImgBtn);
        userProfileImg = (ImageView) findViewById(R.id.userProfileImg);

        activity_patient_history = (RelativeLayout) findViewById(R.id.activity_patient_history);
        galleryImages = new ArrayList<>();

        //dialog
        Display display = this.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }
        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }

        topSettingImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appointment_id != null && !appointment_id.isEmpty()) {
                    openEditProfileSettingPopup();
                } else {
                    if (topSettingPopup.getTag().toString().equals("0")) {
                        topSettingPopup.setVisibility(View.VISIBLE);
                        topSettingPopup.setTag(1);
                    } else {
                        topSettingPopup.setVisibility(View.GONE);
                        topSettingPopup.setTag(0);
                    }
                }
            }
        });

        viewChartTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topSettingPopup.setVisibility(View.GONE);
                topSettingPopup.setTag(0);
                openVitalChartDialog();
            }
        });

        //TabLinks variable initialization
        initTabLinks();

        //Prescription section
        initPrescriptionEvents();

        //Reports section
        initReportsEvents();

        //Body vitals section
        initBodyVitals();

        //Body vitals section
        initAppointmentEvents();


        fetchDoctorDetails();
        fetchAppointmentDetails();

        //On close of soft keyboard clear edittext focus
        // it's works only if activity windowSoftInputMode=adjustResize
        new ClearFocusOnKBClose(activity_patient_history);
        hideKeyboard(PatientHistory.this);
    }

    @Override
    public void onBackPressed() {

        if (appointment_id != null) {
            Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
            intent.putExtra("ProgressBar", true);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), AllPatients.class);
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
            intent.putExtra("ProgressBar", true);
            startActivity(intent);
        }
    }

    //Common functions section start -------------------------------------------------------------->
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

        llyt_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
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
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        llyt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AccountPremium.class);
                finish();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private void initTabLinks() {
        drugTabLink = (ImageView) findViewById(R.id.drugTabLink);
        reportTabLink = (ImageView) findViewById(R.id.reportTabLink);
        vitalTabLink = (ImageView) findViewById(R.id.vitalTabLink);

        prescriptionContWrap = (CoordinatorLayout) findViewById(R.id.prescriptionContWrap);
        reportContWrap = (CoordinatorLayout) findViewById(R.id.reportContWrap);
        bodyVitalsContWrap = (CoordinatorLayout) findViewById(R.id.bodyVitalsContWrap);

        drugTabLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drugTabLink.setBackgroundResource(R.drawable.btn_white_trans_circle);
                reportTabLink.setBackgroundResource(0);
                vitalTabLink.setBackgroundResource(0);

                prescriptionContWrap.setVisibility(View.VISIBLE);
                reportContWrap.setVisibility(View.GONE);
                bodyVitalsContWrap.setVisibility(View.GONE);
            }
        });
        drugTabLink.performClick();

        reportTabLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drugTabLink.setBackgroundResource(0);
                reportTabLink.setBackgroundResource(R.drawable.btn_white_trans_circle);
                vitalTabLink.setBackgroundResource(0);

                prescriptionContWrap.setVisibility(View.GONE);
                reportContWrap.setVisibility(View.VISIBLE);
                bodyVitalsContWrap.setVisibility(View.GONE);
            }
        });

        vitalTabLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                drugTabLink.setBackgroundResource(0);
                reportTabLink.setBackgroundResource(0);
                vitalTabLink.setBackgroundResource(R.drawable.btn_white_trans_circle);

                prescriptionContWrap.setVisibility(View.GONE);
                reportContWrap.setVisibility(View.GONE);
                bodyVitalsContWrap.setVisibility(View.VISIBLE);
            }
        });

    }

    public void goToAllPatientsPage(View v) {
        Intent intent = new Intent(getApplicationContext(), AllPatients.class);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    public void goToAllAppointmentsPage(View v) {
        Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
        finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
        intent.putExtra("ProgressBar", true);
        startActivity(intent);
    }

    public String removeNull(String _string) {
        return _string.equals("null") ? "" : _string;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixelSize) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = roundPixelSize;
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private String notesDate(Date _date) {
        String _validate = _date.toString();
        DateFormat dateFormat;
        if (!_validate.equals("")) {
            dateFormat = new SimpleDateFormat("d");
            String date = dateFormat.format(_date);
            if (date.endsWith("1") && !date.endsWith("11"))
                dateFormat = new SimpleDateFormat("d'st' MMM");
            else if (date.endsWith("2") && !date.endsWith("12"))
                dateFormat = new SimpleDateFormat("d'nd' MMM");
            else if (date.endsWith("3") && !date.endsWith("13"))
                dateFormat = new SimpleDateFormat("d'rd' MMM");
            else
                dateFormat = new SimpleDateFormat("d'th' MMM");
            return dateFormat.format(_date);

        } else {
            return "";
        }
    }

    private String localDateFormat(String _date) {
        if (!_date.equals("")) {
            DateFormat localFormat = new SimpleDateFormat("MM-dd-yyyy");
            try {
                Date date = localFormat.parse(_date);
                return notesDate(date);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    private String dateFormatFun(String _date) {
        if (!_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("MM-dd-yyyy");
            DateFormat dateFormat = new SimpleDateFormat("DD-MMM-YYYY");
            try {
                Date date = localFormat.parse(_date);
                String result = dateFormat.format(date);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    private class DownLoadSliderImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadSliderImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            // imageView.setImageBitmap(result);
            imageView.setImageBitmap(getRoundedCornerBitmap(result, 60));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void bindSpinnerDropDown(Spinner spinner, ArrayList<StringWithTag> list) {
        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(PatientHistory.this, android.R.layout.simple_spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }


    //API CALL
    private void fetchPatientDetails() {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + patient_id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonPatient(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }


    private void fetchDoctorDetails() {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.Doc + "/" + med_user_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonDoctor(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }


    private void fetchAppointmentDetails() {
        progress.show();
        progress.setMessage("Loading...");
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientAppointment + "/" + appointment_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonAppointment(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }

    private void parseJsonDoctor(JSONObject response) {
        try {
            progress.dismiss();
            Boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONObject _data = (JSONObject) response.getJSONObject("data");
                docNameTxt_str = _data.getString("name");
                docmobile_str = _data.getString("mobile");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonAppointment(JSONObject response) {
        try {
            progress.dismiss();
            Boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONObject _data = (JSONObject) response.getJSONObject("data");
                appointmentDate_str = _data.getString("date");
                appointmentTime_str = _data.getString("time");
                status = _data.getString("status");
            }
//            undocancle(status);
            if (status.equals("cancelled")) {
                appointStatus = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonPatient(JSONObject response) {
        try {
            progress.dismiss();
            Boolean _error = Boolean.parseBoolean(response.getString("error"));
            if (!_error && !response.isNull("data")) {
                JSONObject _data = (JSONObject) response.getJSONObject("data");
                userNameTxt.setText(_data.getString("name"));
                mobile_str = _data.getString("mobile");
                if (!_data.isNull("mobile_verification") && _data.getString("mobile_verification").equals("YES")) {
                    follow_up_status = true;
                    is_doc_can_edit_pres = false;
                    bloodGroupLockStatus = true;
                }


                if (!_data.isNull("blood_group_id") && !_data.getString("blood_group_id").equals("")) {
                    isPageLoadBloodGroupChange = true;
                    SpinnerDropDown.setSpinnerItem(bloodGroupSpinner, _data.getString("blood_group_id"));

                    if (!bloodGroupLockStatus) {
                        bloodGroupSpinner.setEnabled(true);
                        bloodGroupLockIcon.setVisibility(View.GONE);
                        bloodGroupArrow.setVisibility(View.VISIBLE);
                    } else {
                        bloodGroupSpinner.setEnabled(false);
                        bloodGroupLockIcon.setVisibility(View.VISIBLE);
                        bloodGroupArrow.setVisibility(View.GONE);
                    }
                }

                if (!_data.isNull("height") && !_data.getString("height").equals("") && !_data.getString("height").equals("null")) {
                    isPageLoadHieghtChange = true;
                    SpinnerDropDown.setSpinnerItem(heightSpinner, _data.getString("height"));
                    heightTxt.setText(_data.getString("height") + " cm");
                }

                if (!_data.isNull("photo")) {
                    String newurl = API_URL._domain + _data.getString("photo");
                    new DownLoadImageTask(userProfileImg).execute(newurl);
                } else {
                    if (_data.getString("gender").equals("male")) {
                        userProfileImg.setImageResource(R.drawable.male_user);
                    } else {
                        userProfileImg.setImageResource(R.drawable.female_user);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadBloodGroups() {
        bloodGroupList = new ArrayList<StringWithTag>();
        bloodGroupList.add(new StringWithTag("Select", ""));
        JsonObjectRequest jsonReqBloodGroups = new JsonObjectRequest(Request.Method.GET,
                API_URL.BloodGroup, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        _blood_arr = response.getJSONArray("data");
                        for (int i = 0; i < _blood_arr.length(); i++) {
                            JSONObject feedObj = (JSONObject) _blood_arr.get(i);
                            bloodGroupList.add(new StringWithTag(feedObj.getString("name"), feedObj.getString("id")));
                        }
                        bindBloodGroupEvents();
                        fetchPatientDetails();
                    } else {
                        Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
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
        mRequestQueue.add(jsonReqBloodGroups);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case ADD_PRESC_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), _file_path);

                        //MediaStore.getDocumentUri()
                        thumbnail = rotateImage(thumbnail);
                        createPresPhotoRow(thumbnail);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_MORE_PRESC_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), _file_path);

                        thumbnail = rotateImage(thumbnail);
                        addMorePresSlideImage(thumbnail);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_MORE_PRESC_LIST_ROW_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), _file_path);

                        thumbnail = rotateImage(thumbnail);
                        imageHolder.setImageBitmap(thumbnail);
                        uploadMorePresListPhotoRow(thumbnail);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_REPORT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), _file_path);


                        thumbnail = rotateImage(thumbnail);
                        imageHolder.setImageBitmap(thumbnail);
                        postLabReport(thumbnail);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case ADD_MORE_REPORT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bitmap thumbnail = null;
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), _file_path);

                        thumbnail = rotateImage(thumbnail);
                        imageHolder.setImageBitmap(thumbnail);
                        uploadMoreReportPhoto(thumbnail);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    private Bitmap rotateImage(Bitmap _imageBitmap) {
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90);
        _imageBitmap = Bitmap.createBitmap(_imageBitmap, 0, 0,
                _imageBitmap.getWidth(), _imageBitmap.getHeight(),
                rotateMatrix, false);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        _imageBitmap.compress(Bitmap.CompressFormat.PNG, 60, ostream);

        return _imageBitmap;
    }

    private void openVitalChartDialog() {
        final Dialog dialog = new Dialog(PatientHistory.this);
        dialog.setContentView(R.layout.vital_chart_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView vitalChartImg_viewer = (ImageView) dialog.findViewById(R.id.vitalChartImg_viewer);

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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
    //Common functions section end <----------------------------------------------------------------


    //Prescription functions start ---------------------------------------------------------------->
    private void initPrescriptionEvents() {
        presListWrap = (LinearLayout) findViewById(R.id.presListWrap);
        presScrollView = (ScrollView) findViewById(R.id.presScrollView);
        addNewPresImg = (ImageView) findViewById(R.id.addNewPresImg);
        no_data_perscription = findViewById(R.id.no_data_perscription);

        addNewPresImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPresDialog(false);
            }
        });

        presScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                LinearLayout view = (LinearLayout) presScrollView.getChildAt(0);
                if (view.getChildCount() > 1) {
                    int diff = (view.getBottom() - (presScrollView.getHeight() + presScrollView.getScrollY()));
                    if (diff < 40 && _presLoadMoreStatus) {
                        _presLoadMoreStatus = false;
                        loadPrescriptionList(true);
                    }
                }
            }
        });

        loadPrescriptionList(false);

        //Add prescription dialog events
        addNewPresPhotoSlideList = new ArrayList<>();

        dosageList = new ArrayList<StringWithTag>();
        for (int i = 0; i < 10; i++) {
            dosageList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }

        daysList = new ArrayList<StringWithTag>();
        for (int i = 0; i < 100; i++) {
            daysList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }
    }

    private void loadPrescriptionList(final Boolean loadMore) {
        progress.setTitle("Loading...");
        progress.show();
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            if (loadMore) {
                pres_list_page_num = pres_list_page_num + 1;
                _obj.put("page", pres_list_page_num);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonPatient = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPrescriptionSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();

                if (!response.isNull("data")) {

                    TextView presc_instruction_txt = findViewById(R.id.presc_instruction_txt);

                    presc_instruction_txt.setText("Click the + button on the lower right corner to create a new prescription");
                    try {
                        JSONObject _dataObj = response.getJSONObject("data");
                        JSONArray _resultArr = _dataObj.getJSONArray("result");

                        if (_dataObj.getString("pages").equals(String.valueOf(pres_list_page_num))) {
                            _presLoadMoreStatus = false;
                        } else {
                            _presLoadMoreStatus = true;
                        }

                        for (int t = 0; t < _resultArr.length(); t++) {
                            JSONObject _feedObj = (JSONObject) _resultArr.get(t);

                            if (!_feedObj.isNull("pre")) {
                                JSONObject presObj = (JSONObject) _feedObj.getJSONObject("pre");
                                JSONObject docObj = (JSONObject) _feedObj.getJSONObject("doc");

                                JSONArray presMedArr = presObj.getJSONArray("pres_meds");
                                JSONArray presMedFilesArr = presObj.getJSONArray("pres_med_files");

                                if (presMedArr.length() > 0) {
                                    createPresListRow(presMedArr, presObj, docObj, false, false);
                                } else if (presMedFilesArr.length() > 0) {
                                    createPresListPhotoRow(presMedFilesArr, presObj, false);
                                }
                            }
                        }

                        if (!loadMore && _resultArr.length() == 0) {
                            no_data_perscription.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
        mRequestQueue.add(jsonPatient);
    }

    private void createPresListRow(JSONArray presMedArr, JSONObject presObj, JSONObject docObj, Boolean addOnTop, Boolean updateCard) {
        try {

            final View preview = layoutInflater.inflate(R.layout.pat_drug_row, null);

            final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
            final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);
            TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);

            final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
            final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
            final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);
            final LinearLayout filesWrap = (LinearLayout) preview.findViewById(R.id.filesWrap);
            final ConstraintLayout editAndFollowUpWrap = (ConstraintLayout) preview.findViewById(R.id.editAndFollowUpWrap);
            final ConstraintLayout DocNameAndPrintWrap = (ConstraintLayout) preview.findViewById(R.id.DocNameAndPrintWrap);

            final View pat_drug_details_view = layoutInflater.inflate(R.layout.pat_drug_details, null);
            final View pat_drug_notes_view = layoutInflater.inflate(R.layout.pat_drug_notes, null);

            final ViewPager view_pager = (ViewPager) preview.findViewById(R.id.view_pager);
            final LinearLayout layoutDots = (LinearLayout) preview.findViewById(R.id.layoutDots);

            final ImageView more_med_list_arrow = (ImageView) pat_drug_details_view.findViewById(R.id.more_med_list_arrow);
            final NestedScrollView pat_drug_list_scrollview = (NestedScrollView) pat_drug_details_view.findViewById(R.id.pat_drug_list_scrollview);
            ImageView editPresImg = (ImageView) pat_drug_details_view.findViewById(R.id.editPresImg);
            TextView follow_up_txt = (TextView) pat_drug_details_view.findViewById(R.id.follow_up_txt);
            TextView doc_name = (TextView) pat_drug_details_view.findViewById(R.id.doc_name);
            final LinearLayout medicineLinearListWrap = (LinearLayout) pat_drug_details_view.findViewById(R.id.medicineLinearListWrap);

            ImageView printPresImg = (ImageView) pat_drug_notes_view.findViewById(R.id.printPresImg);
            TextView follow_up_notes_txt = (TextView) pat_drug_notes_view.findViewById(R.id.follow_up_notes_txt);
            TextView notes_txt = (TextView) pat_drug_notes_view.findViewById(R.id.notes_txt);

            CustomSlider customSlider = new CustomSlider();

            ArrayList<ViewGroup> viewGroup = new ArrayList<ViewGroup>();
            viewGroup.add((ViewGroup) pat_drug_details_view);
            viewGroup.add((ViewGroup) pat_drug_notes_view);

            customSlider.set(this, view_pager, layoutDots, viewGroup);

            final String pre_id = presObj.getString("id");


            printPresImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    doPhotoPrint();

                    Intent intent = new Intent(getApplicationContext(), PrintPrescription.class);
                    intent.putExtra("pre_id", pre_id);
                    startActivity(intent);

                }
            });


            add_title_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleInput.setVisibility(View.VISIBLE);
                    add_title_btn.setVisibility(View.GONE);

                    new CountDownTimer(200, 1000) { // adjust the milli seconds here
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            titleInput.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                        }
                    }.start();
                }
            });


            editPresImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prescription_id = pre_id;
                    pres_card_row_index = presListWrap.indexOfChild(preview);
                    if (is_doc_can_edit_pres) {
                        showAddPresDialog(true);
                    } else {
                        openGetOtpDialog();
                    }

                }
            });

            if (!presObj.isNull("doc_id") && !presObj.getString("doc_id").equals("null")) {
                if (!presObj.getString("doc_id").equals(med_user_id)) {
                    editAndFollowUpWrap.setVisibility(View.GONE);
                    DocNameAndPrintWrap.setVisibility(View.VISIBLE);
                }
            }

            //title
            if (!presObj.isNull("title")) {
                if (!presObj.getString("title").equals("null") && !presObj.getString("title").isEmpty()) {
                    titleInput.setText(presObj.getString("title"));
                    // add_title_btn.performClick();
                    add_title_btn.setVisibility(View.GONE);
                    titleInput.setVisibility(View.VISIBLE);
                }
            }
            titleInput.setTag(pre_id);

            titleInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        hideKeyboard(PatientHistory.this);
                        if (titleInput.getText().toString().isEmpty()) {
                            titleInput.setVisibility(View.GONE);
                            add_title_btn.setVisibility(View.VISIBLE);
                        }
                        updatePresTitle(titleInput);
                    }
                    return true;
                }
            });

            titleInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (titleInput.getText().toString().isEmpty()) {
                            titleInput.setVisibility(View.GONE);
                            add_title_btn.setVisibility(View.VISIBLE);
                        }
                        updatePresTitle(titleInput);
                    }
                }
            });

            if (docObj != null) {
                doc_name.setText(docObj.getString("name"));
            }

            String _follow_up_str = "Follow Up" + " " + CustomDateToString.month(presObj.getString("date"));
            follow_up_txt.setText(_follow_up_str);
            follow_up_notes_txt.setText(_follow_up_str);

            date_txt.setText(localDateFormat(presObj.getString("date")));
            String notes_txt_str = presObj.getString("other_details");

            if (notes_txt_str == null || notes_txt_str.isEmpty() || notes_txt_str.equals("null")) {
                notes_txt_str = " --";
            }

            notes_txt.setText(notes_txt_str);


            final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);

            final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            showHideFilesBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (showHideFilesBtn.getTag().equals("0")) {
                        showHideFilesBtn.setTag("1");
                        upwardArrowImg.setVisibility(View.GONE);
                        closeImg.setVisibility(View.VISIBLE);
                        filesWrap.setVisibility(View.VISIBLE);

                        filesWrap.setVisibility(View.VISIBLE);
                        filesWrap.startAnimation(slide_up);

                    } else {
                        showHideFilesBtn.setTag("0");
                        upwardArrowImg.setVisibility(View.VISIBLE);
                        closeImg.setVisibility(View.GONE);
                        filesWrap.setVisibility(View.GONE);

                        //filesWrap.startAnimation(slide_down);
                        filesWrap.setVisibility(View.GONE);
                    }
                }
            });

            if (updateCard) {
                presListWrap.removeViewAt(pres_card_row_index);
                presListWrap.addView(preview, pres_card_row_index);
            } else {
                if (addOnTop) {
                    presListWrap.addView(preview, 0);
                    showHideFilesBtn.performClick();

                } else {
                    presListWrap.addView(preview);
                }
            }

            for (int medicine_index = 0; medicine_index < presMedArr.length(); medicine_index++) {
                JSONObject _medObj = (JSONObject) presMedArr.get(medicine_index);

                View med_dosage_details_row = layoutInflater.inflate(R.layout.med_dosage_details_row, null);

                TextView medicineNameTxt = (TextView) med_dosage_details_row.findViewById(R.id.medicineNameTxt);
                TextView medTotalDaysTxt = (TextView) med_dosage_details_row.findViewById(R.id.medTotalDaysTxt);
                TextView med_dosage = (TextView) med_dosage_details_row.findViewById(R.id.med_dosage);

                String morning_dosage = "0", afternoon_dosage = "0", night_dosage = "0";

                if (checkDosage(_medObj.getString("morning_dosage"))) {
                    morning_dosage = _medObj.getString("morning_dosage");
                }
                if (checkDosage(_medObj.getString("afternoon_dosage"))) {
                    afternoon_dosage = _medObj.getString("afternoon_dosage");
                }
                if (checkDosage(_medObj.getString("night_dosage"))) {
                    night_dosage = _medObj.getString("night_dosage");
                }

                if (!_medObj.isNull("total_days") && !_medObj.getString("total_days").isEmpty()) {
                    medTotalDaysTxt.setText("(" + _medObj.getString("total_days") + "d)");
                }

                if (!_medObj.isNull("medicine")) {
                    JSONObject medicineObj = _medObj.getJSONObject("medicine");

                    add_title_btn.setVisibility(View.GONE);
                    titleInput.setVisibility(View.VISIBLE);
                    try {
                        titleInput.setText(medicineObj.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!_medObj.isNull("name")) {
                        if (!_medObj.getString("name").equals("null")) {
                            medicineNameTxt.setText(_medObj.getString("name"));
                        }
                    }
                }

                String _dosageStr = morning_dosage + "-" + afternoon_dosage + "-" + night_dosage;
                med_dosage.setText(_dosageStr);
                medicineLinearListWrap.addView(med_dosage_details_row);
            }
            if (presMedArr.length() > 2) {
                more_med_list_arrow.setVisibility(View.VISIBLE);
            }
            pat_drug_list_scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    LinearLayout view = (LinearLayout) pat_drug_list_scrollview.getChildAt(0);
                    if (view.getChildCount() > 1) {
                        int diff = (view.getBottom() - (pat_drug_list_scrollview.getHeight() + pat_drug_list_scrollview.getScrollY()));
                        if (diff < (-150)) {
                            more_med_list_arrow.setVisibility(View.GONE);
                        } else {
                            more_med_list_arrow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPhotoPrint() {
//        PrintHelper photoPrinter = new PrintHelper(PatientHistory.this);
//        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.vital_chart);
//        photoPrinter.printBitmap("droids.jpg - test print", bitmap);

        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.vital_chart);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }


    private Boolean checkDosage(String _str) {
        Boolean _dosage = false;

        if (_str != null && !_str.isEmpty() && !_str.equals("null")) {
            _dosage = true;
        }

        return _dosage;
    }

    private void createPresListPhotoRow(JSONArray presMedFilesArr, JSONObject presObj, Boolean addOnTop) {
        try {

            final View preview = layoutInflater.inflate(R.layout.pat_drug_photo_row, null);

            LinearLayout medicineLinearListWrap = (LinearLayout) preview.findViewById(R.id.medicineLinearListWrap);

            final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
            final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);
            TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);


            date_txt.setText(localDateFormat(presObj.getString("date")));

            final String pre_id = presObj.getString("id");
            titleInput.setTag(pre_id);

            if (!presObj.isNull("title")) {
                if (!presObj.getString("title").equals("null") && !presObj.getString("title").isEmpty()) {
                    titleInput.setText(presObj.getString("title"));
                    //add_title_btn.performClick();
                    add_title_btn.setVisibility(View.GONE);
                    titleInput.setVisibility(View.VISIBLE);
                }
            }
            titleInput.setTag(pre_id);
            titleInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        hideKeyboard(PatientHistory.this);
                        updatePresTitle(titleInput);
                    }
                    return true;
                }
            });

            titleInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (titleInput.getText().toString().isEmpty()) {
                            titleInput.setVisibility(View.GONE);
                            add_title_btn.setVisibility(View.VISIBLE);
                        }
                        updatePresTitle(titleInput);

                    }
                }
            });

            add_title_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleInput.setVisibility(View.VISIBLE);
                    add_title_btn.setVisibility(View.GONE);
                    new CountDownTimer(200, 1000) { // adjust the milli seconds here
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            titleInput.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                        }
                    }.start();
                }
            });

            final ImageView addMoreFile = (ImageView) preview.findViewById(R.id.addMoreFile);
            final ImageView deleteFile = (ImageView) preview.findViewById(R.id.deleteFile);

            final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
            final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
            final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);
            final ConstraintLayout filesWrap = (ConstraintLayout) preview.findViewById(R.id.filesWrap);

            final ViewPager view_pager = (ViewPager) preview.findViewById(R.id.view_pager);
            final LinearLayout layoutDots = (LinearLayout) preview.findViewById(R.id.layoutDots);

            View pat_drug_notes = layoutInflater.inflate(R.layout.pat_drug_notes, null);
            TextView follow_up_notes_txt = (TextView) pat_drug_notes.findViewById(R.id.follow_up_notes_txt);
            TextView notes_txt = (TextView) pat_drug_notes.findViewById(R.id.notes_txt);

            CustomSlider customSlider = new CustomSlider();
            final ArrayList<ViewGroup> viewGroup = new ArrayList<ViewGroup>();
            final ArrayList<ImageSlide> _galleryImages = new ArrayList<>();
            for (int medicine_index = 0; medicine_index < presMedFilesArr.length(); medicine_index++) {
                JSONObject _medObj = (JSONObject) presMedFilesArr.get(medicine_index);
                String img_url = API_URL._domain + "/pres-file/photo/" + _medObj.getString("file");
                View slider_image_view = layoutInflater.inflate(R.layout.slider_preview_img, null);
                final ImageView slideImg = (ImageView) slider_image_view.findViewById(R.id.slideImg);
                final View fileObjectView = (View) slider_image_view.findViewById(R.id.fileObjectView);

                fileObjectView.setTag(_medObj);
                viewGroup.add(((ViewGroup) slider_image_view));

                ImageSlide image = new ImageSlide();
                //image.setName("New image");
                image.setSmall(img_url);
                image.setMedium(img_url);
                image.setLarge(img_url);
                _galleryImages.add(image);

                slider_image_view.setTag(img_url);
                slideImg.setTag(medicine_index);
                slideImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        galleryImages = _galleryImages;
                        showBigImgPopup(slideImg);
                    }
                });
            }

            deleteFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(PatientHistory.this)
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    selectedViewPager = view_pager;
                                    selectedLayoutDots = layoutDots;
                                    galleryImages = _galleryImages;
                                    prescription_id = pre_id;
                                    deletePrescriptionFile(preview);
                                }

                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            });

            addMoreFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedViewPager = view_pager;
                    selectedLayoutDots = layoutDots;
                    galleryImages = _galleryImages;
                    prescription_id = pre_id;
                    addMorePresListPhotoRowByCamera();
                }
            });

            viewGroup.add(((ViewGroup) pat_drug_notes));
            view_pager.setTag(viewGroup);
            customSlider.set(this, view_pager, layoutDots, viewGroup);


            String _follow_up_str = "Follow Up" + " " + CustomDateToString.month(presObj.getString("date"));
            follow_up_notes_txt.setText(_follow_up_str);

            date_txt.setText(localDateFormat(presObj.getString("date")));
            String notes_txt_str = presObj.getString("other_details");

            if (notes_txt_str == null || notes_txt_str.isEmpty() || notes_txt_str.equals("null")) {
                notes_txt_str = " --";
            }

            notes_txt.setText(notes_txt_str);

            view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrollStateChanged(int state) {
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    View _view = view_pager.getChildAt(view_pager.getCurrentItem());
                    if (view_pager.getAdapter().getCount() - 1 == position) {
                        addMoreFile.setVisibility(View.GONE);
                        deleteFile.setVisibility(View.GONE);
                    } else {
                        addMoreFile.setVisibility(View.VISIBLE);
                        deleteFile.setVisibility(View.VISIBLE);
                    }
                }
            });

            final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);

            final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            final boolean[] loadFirstTime = {true};
            showHideFilesBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (showHideFilesBtn.getTag().equals("0")) {
                        showHideFilesBtn.setTag("1");
                        upwardArrowImg.setVisibility(View.GONE);
                        closeImg.setVisibility(View.VISIBLE);
                        filesWrap.setVisibility(View.VISIBLE);

                        filesWrap.setVisibility(View.VISIBLE);
                        filesWrap.startAnimation(slide_up);

                        if (loadFirstTime[0]) {
                            loadFirstTime[0] = false;
                            for (int imgIndex = 0; imgIndex < viewGroup.size() - 1; imgIndex++) {
                                ViewGroup slider_image_view = viewGroup.get(imgIndex);
                                final ImageView slideImg = (ImageView) slider_image_view.findViewById(R.id.slideImg);
                                new DownLoadSliderImageTask(slideImg).execute(slider_image_view.getTag().toString());
                            }
                        }

                    } else {
                        showHideFilesBtn.setTag("0");
                        upwardArrowImg.setVisibility(View.VISIBLE);
                        closeImg.setVisibility(View.GONE);
                        filesWrap.setVisibility(View.GONE);

                        //filesWrap.startAnimation(slide_down);
                        filesWrap.setVisibility(View.GONE);
                    }
                }
            });


            if (addOnTop) {
                presListWrap.addView(preview, 0);
                showHideFilesBtn.performClick();
            } else {
                presListWrap.addView(preview);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showBigImgPopup(View v) {
        View view = (View) findViewById(v.getId());
        // int _position = parseInt(view.getTag().toString());

        Bundle bundle = new Bundle();
        bundle.putSerializable("images", galleryImages);
        //bundle.putInt("position", _position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    private void updatePresTitle(EditText titleInput) {
        progress.setTitle("Updating title...");
        progress.show();

        JSONObject _obj = new JSONObject();
        try {
            JSONObject _em_s = new JSONObject();
            _obj.put("title", titleInput.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.Prescription + "/" + titleInput.getTag().toString(), _obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        Toast.makeText(PatientHistory.this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
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

    public void addMorePresListPhotoRowByCamera() {
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
            startActivityForResult(intent, ADD_MORE_PRESC_LIST_ROW_REQUEST_CODE);
        }
    }

    private void uploadMorePresListPhotoRow(final Bitmap bitmapImage) {
        if (bitmapImage != null) {
            progress.setTitle("uploading...");
            progress.show();
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.PrescriptionMedicineFileUpload, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    progress.dismiss();
                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject result = new JSONObject(json);
                        boolean _error = Boolean.parseBoolean(result.getString("error"));
                        if (!_error && !result.isNull("data")) {
                            postMorePresListPhotoRow(result.getJSONObject("data"));
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
                    params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageHolder.getDrawable()), "image/jpeg"));

                    return params;
                }
            };

            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }
    }

    private void postMorePresListPhotoRow(JSONObject _data) {
        final JSONObject _obj = new JSONObject();
        progress.setTitle("updating photo list...");
        progress.show();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            _obj.put("file", _data.getString("file"));
            _obj.put("file_type", _data.getString("file_type"));
            _obj.put("pre_id", prescription_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PrescriptionMedicineFile, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _fileData = (JSONObject) response.getJSONObject("data");
                        addNewPresListRowSlideImage(_fileData);
                        Toast.makeText(PatientHistory.this, "Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("Error ", "Something went wrong!" + response.toString());
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void addNewPresListRowSlideImage(JSONObject fileData) {
        try {
            LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

            View imageWrap = layoutInflater.inflate(R.layout.slider_preview_img, null);
            ImageView slideImg = (ImageView) imageWrap.findViewById(R.id.slideImg);
            if (fileData != null) {
                if (!fileData.isNull("file")) {
                    String img_url = API_URL._domain + "/pres-file/photo/" + fileData.getString("file");
                    View slider_image_view = layoutInflater.inflate(R.layout.slider_preview_img, null);
                    ImageView slideImg1 = (ImageView) slider_image_view.findViewById(R.id.slideImg);
                    new DownLoadSliderImageTask(slideImg1).execute(img_url);

                    ArrayList<ViewGroup> viewGroup = (ArrayList<ViewGroup>) selectedViewPager.getTag();
                    int _lenCount = viewGroup.size();
                    viewGroup.add(_lenCount - 1, (ViewGroup) slider_image_view);
                    selectedViewPager.removeAllViews();
                    selectedLayoutDots.removeAllViews();
                    CustomSlider customSlider = new CustomSlider();

                    ImageSlide image = new ImageSlide();
                    image.setSmall(img_url);
                    image.setMedium(img_url);
                    image.setLarge(img_url);
                    galleryImages.add(image);

                    customSlider.set(this, selectedViewPager, selectedLayoutDots, viewGroup);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private void deletePrescriptionFile(final View _rowView) {
        final ArrayList<ViewGroup> viewGroup = (ArrayList<ViewGroup>) selectedViewPager.getTag();
        final int position = selectedViewPager.getCurrentItem();
        final ViewGroup preview = (ViewGroup) viewGroup.get(position);
        View fileObjectView = (View) preview.findViewById(R.id.fileObjectView);
        JSONObject jsonObject = (JSONObject) fileObjectView.getTag();

        progress.setMessage("Deleting...");
        progress.show();
        new PatientPrescription().delPhoto(PatientHistory.this, jsonObject, new MyCallBack() {
            @Override
            public void call(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        if (_data.getString("code").toString().equals("200")) {
                            viewGroup.remove(position);
                            if (viewGroup.size() > 1) {
                                progress.dismiss();
                                selectedViewPager.removeAllViews();
                                selectedLayoutDots.removeAllViews();
                                galleryImages.remove(position);
                                new CustomSlider().set(PatientHistory.this, selectedViewPager, selectedLayoutDots, viewGroup);
                                Toast.makeText(PatientHistory.this, "Photo deleted successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                new PatientPrescription().delPrescription(PatientHistory.this, prescription_id, new MyCallBack() {
                                    @Override
                                    public void call(JSONObject response) {
                                        progress.dismiss();
                                        Log.i("delReport", response.toString());
                                        try {
                                            boolean _error = Boolean.parseBoolean(response.getString("error"));
                                            if (!_error) {
                                                JSONObject _data = (JSONObject) response.getJSONObject("data");
                                                if (_data.getString("code").toString().equals("200")) {
                                                    presListWrap.removeView(_rowView);
                                                    Toast.makeText(PatientHistory.this, "Photo deleted successfully.", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    } else {
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                    Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Add prescription dialog --------------------------------------------------------------------------------------------
    public void showAddPresDialog(Boolean editPres) {
        isPresUpdate = false;
        isPresPhotoRow = false;
        isPresEditDetails = editPres;
        View loginView = getLayoutInflater().inflate(R.layout.add_prescription_dialog, null);
        addPresdialog = new BottomSheetDialog(this);
        addPresdialog.setContentView(loginView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) loginView.getParent());
        // mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    addPresdialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        new ClearFocusOnKBClose(loginView);

        if (addNewPresPhotoSlideList != null) {
            addNewPresPhotoSlideList.clear();
        }

        medicineListWrap = (LinearLayout) addPresdialog.findViewById(R.id.medicineListWrap);
        addMedicineCardBtn = (LinearLayout) addPresdialog.findViewById(R.id.addMedicineCardBtn);

        search_wrap = (LinearLayout) addPresdialog.findViewById(R.id.search_wrap);
        plus_wrap = (LinearLayout) addPresdialog.findViewById(R.id.plus_wrap);

        medicineCameraBtnWrap = (LinearLayout) addPresdialog.findViewById(R.id.medicineCameraBtnWrap);
        followUpDateInput = (EditText) addPresdialog.findViewById(R.id.followUpDateInput);
        presNoteInput = (EditText) addPresdialog.findViewById(R.id.presNoteInput);

        savePresDialogBtn = (TextView) addPresdialog.findViewById(R.id.savePresDialogBtn);
        updatePresDialogTxt = (TextView) addPresdialog.findViewById(R.id.updatePresDialogTxt);
        updatePresDialogBtn = (TextView) addPresdialog.findViewById(R.id.updatePresDialogBtn);
        print_txt = (TextView) addPresdialog.findViewById(R.id.print_txt);

        addMedicineCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMedicineRow(null);
            }
        });

        print_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPhotoPrint();
            }
        });
        medicineCameraBtnWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPresPhotoByCamera();
            }
        });

        medicineScrollView = (NestedScrollView) addPresdialog.findViewById(R.id.medicineScrollView);
        medicineScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) medicineScrollView.getChildAt(0);
            }
        });

        followUpDateInput.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get Current Date
                if (follow_up_status) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(PatientHistory.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(
                                        DatePicker view,
                                        int year,
                                        int monthOfYear,
                                        int dayOfMonth
                                ) {
                                    followUpDateInput.setText(String.format("%02d-%02d-%d", dayOfMonth, (monthOfYear + 1), year));
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                } else {
                    openGetOtpDialog();
                }
            }
        });

        bindSearchBox(editPres);

        if (editPres) {
            fetchSinglePrescription();
            savePresDialogBtn.setVisibility(View.GONE);
            updatePresDialogTxt.setVisibility(View.VISIBLE);
            updatePresDialogBtn.setVisibility(View.GONE);
            medicineCameraBtnWrap.setVisibility(View.GONE);
        } else {
            createMedicineRow(null);
        }

        addPresdialog.show();
    }


    private void createMedicineRow(JSONObject _med_obj) {
        View preview = layoutInflater.inflate(R.layout.pat_add_pres_row, null);

        TextView plus_txt = (TextView) preview.findViewById(R.id.plus_txt);
        TextView title_txt = (TextView) preview.findViewById(R.id.title_txt);
        TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);

        final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
        final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
        final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);
        final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
        final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);
        final ConstraintLayout filesWrap = (ConstraintLayout) preview.findViewById(R.id.filesWrap);

        Spinner morningSpinner = (Spinner) preview.findViewById(R.id.morningSpinner);
        Spinner afternoonSpinner = (Spinner) preview.findViewById(R.id.afternoonSpinner);
        Spinner eveningSpinner = (Spinner) preview.findViewById(R.id.eveningSpinner);
        Spinner daysSpinner = (Spinner) preview.findViewById(R.id.daysSpinner);

        bindSpinnerDropDown(morningSpinner, dosageList);
        bindSpinnerDropDown(afternoonSpinner, dosageList);
        bindSpinnerDropDown(eveningSpinner, dosageList);
        bindSpinnerDropDown(daysSpinner, daysList);

        add_title_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_title_btn.setVisibility(View.GONE);
                titleInput.setVisibility(View.VISIBLE);
                titleInput.requestFocus();

                new CountDownTimer(200, 1000) { // adjust the milli seconds here
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        titleInput.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                    }
                }.start();
            }
        });


        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        showHideFilesBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("", showHideFilesBtn.getTag().toString());

                if (showHideFilesBtn.getTag().equals("0")) {
                    showHideFilesBtn.setTag("1");
                    upwardArrowImg.setVisibility(View.GONE);
                    closeImg.setVisibility(View.VISIBLE);
                    filesWrap.setVisibility(View.VISIBLE);

                    filesWrap.setVisibility(View.VISIBLE);
                    filesWrap.startAnimation(slide_up);

                } else {
                    showHideFilesBtn.setTag("0");
                    upwardArrowImg.setVisibility(View.VISIBLE);
                    closeImg.setVisibility(View.GONE);
                    filesWrap.setVisibility(View.GONE);

                    //filesWrap.startAnimation(slide_down);
                    filesWrap.setVisibility(View.GONE);
                }
            }
        });
        showHideFilesBtn.performClick();

        medicineListWrap.addView(preview, 0);

        if (_med_obj != null) {
            add_title_btn.setVisibility(View.GONE);
            titleInput.setVisibility(View.VISIBLE);
            try {
                titleInput.setText(_med_obj.getString("name"));
                titleInput.setTag(_med_obj.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (isPresEditDetails) {
            bindSpinnerPresDosageChangeEvent(morningSpinner);
            bindSpinnerPresDosageChangeEvent(afternoonSpinner);
            bindSpinnerPresDosageChangeEvent(eveningSpinner);
            bindPresTitleTextChangeEvents(titleInput);
        }
    }

    private void hideSearch() {
        search_wrap.setVisibility(View.GONE);
        plus_wrap.setVisibility(View.GONE);

    }

    private void createPresPhotoRow(Bitmap _uri) {
        medicineListWrap.removeAllViews();
        hideSearch();
        isPresPhotoRow = true;

        View preview = layoutInflater.inflate(R.layout.pat_add_pres_photo_row, null);

        TextView plus_txt = (TextView) preview.findViewById(R.id.plus_txt);
        TextView title_txt = (TextView) preview.findViewById(R.id.title_txt);
        TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);

        final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
        final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
        final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);
        final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
        final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);


        final ConstraintLayout filesWrap = (ConstraintLayout) preview.findViewById(R.id.filesWrap);
        final LinearLayout tempPresImgsHolderWrap = (LinearLayout) preview.findViewById(R.id.tempPresImgsHolderWrap);
        selectedMedPhotoSliderWrap = tempPresImgsHolderWrap;

        add_title_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_title_btn.setVisibility(View.GONE);
                titleInput.setVisibility(View.VISIBLE);
                titleInput.requestFocus();

                new CountDownTimer(200, 1000) { // adjust the milli seconds here
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        titleInput.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                    }
                }.start();
            }
        });


        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        showHideFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showHideFilesBtn.getTag().equals("0")) {
                    showHideFilesBtn.setTag("1");
                    upwardArrowImg.setVisibility(View.GONE);
                    closeImg.setVisibility(View.VISIBLE);
                    filesWrap.setVisibility(View.VISIBLE);

                    filesWrap.setVisibility(View.VISIBLE);
                    filesWrap.startAnimation(slide_up);

                } else {
                    showHideFilesBtn.setTag("0");
                    upwardArrowImg.setVisibility(View.VISIBLE);
                    closeImg.setVisibility(View.GONE);
                    filesWrap.setVisibility(View.GONE);

                    //filesWrap.startAnimation(slide_down);
                    filesWrap.setVisibility(View.GONE);
                }
            }
        });

        //setImageBitmap
        View imageWrap = layoutInflater.inflate(R.layout.slider_preview_img, null);
        ImageView slideImg = (ImageView) imageWrap.findViewById(R.id.slideImg);
        slideImg.setImageBitmap(_uri);
        //slideImg.setImageBitmap(getRoundedCornerBitmap(_uri, 100));
        slideImg.setAlpha(0.70f);
        tempPresImgsHolderWrap.addView(imageWrap);
        showHideFilesBtn.performClick();

        final ir.apend.slider.ui.Slider slider = (ir.apend.slider.ui.Slider) preview.findViewById(R.id.slider);

        addNewPresPhotoSlideList.add(new Slide(0, _file_path.toString(), getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        //handle slider click listener
        slider.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //do what you want
            }
        });
        //add slides to slider
        slider.addSlides(addNewPresPhotoSlideList);

        medicineListWrap.addView(preview);


        //add more photos events

        final ImageView addMoreFile = (ImageView) preview.findViewById(R.id.addMoreFile);
        final ImageView deleteFile = (ImageView) preview.findViewById(R.id.deleteFile);


        addMoreFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMorePresPhotoByCamera();
                selectedMedPhotoSliderWrap = tempPresImgsHolderWrap;
            }
        });

    }

    public void addPresPhotoByCamera() {
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
            startActivityForResult(intent, ADD_PRESC_REQUEST_CODE);
        }
    }

    public void addMorePresPhotoByCamera() {
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
            startActivityForResult(intent, ADD_MORE_PRESC_REQUEST_CODE);
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

    private void addMorePresSlideImage(Bitmap _uri) {
        if (selectedMedPhotoSliderWrap != null) {
            //setImageBitmap
            View imageWrap = layoutInflater.inflate(R.layout.slider_preview_img, null);
            ImageView slideImg = (ImageView) imageWrap.findViewById(R.id.slideImg);
            slideImg.setImageBitmap(getRoundedCornerBitmap(_uri, 100));
            slideImg.setAlpha(0.70f);

            selectedMedPhotoSliderWrap.addView(imageWrap, 0);

            View _parent = (View) selectedMedPhotoSliderWrap.getParent();
            final ir.apend.slider.ui.Slider slider = (ir.apend.slider.ui.Slider) _parent.findViewById(R.id.slider);
            addNewPresPhotoSlideList.add(new Slide(1, _file_path.toString(), getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
            slider.removeAllViews();
            slider.addSlides(addNewPresPhotoSlideList);

//            CustomSlider customSlider = new CustomSlider();
//            ArrayList<ViewGroup> viewGroup = (ArrayList<ViewGroup>) selectedViewPager.getTag();
//            int _lenCount = viewGroup.size();
//            viewGroup.add(((ViewGroup) imageWrap));
//            selectedViewPager.removeAllViews();
//            selectedLayoutDots.removeAllViews();
//            customSlider.set(this, selectedViewPager, selectedLayoutDots, viewGroup);
        }
    }

    public void openSpinnerDropDown(View v) {
        Spinner _spinner = (Spinner) v.findViewWithTag("spinner");
        _spinner.performClick();
    }

    private void openGetOtpDialog() {
        getOTPDialog = new Dialog(PatientHistory.this);
        getOTPDialog.setContentView(R.layout.get_otp_popup);
        TextView getOtpBtn = (TextView) getOTPDialog.findViewById(R.id.getOtp);
        TextView number_txt = (TextView) getOTPDialog.findViewById(R.id.number_txt);


        if (mobile_str != null && !mobile_str.isEmpty() && !mobile_str.equals("null")) {
            number_txt.setText(mobile_str);
            getOTPDialog.show();
        } else {
            Toast.makeText(PatientHistory.this, "No contact details found!", Toast.LENGTH_LONG).show();
        }

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
            }
        });
    }

    private void openOTPInputDialog() {
        otpInputDialog = new Dialog(PatientHistory.this);
        otpInputDialog.setContentView(R.layout.otp_input_popup);
        otpInputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        otpInputDialog.setCancelable(false);
        otpInputDialog.show();

        final TextView countDownTxt = (TextView) otpInputDialog.findViewById(R.id.countDownTxt);
        final TextView dialogSendOtp = (TextView) otpInputDialog.findViewById(R.id.dialogSendOtp);
        final TextView dialogClose = (TextView) otpInputDialog.findViewById(R.id.dialogClose);
        TextView dialogMobileNo = (TextView) otpInputDialog.findViewById(R.id.dialogMobileNo);

        otp_input_one = (EditText) otpInputDialog.findViewById(R.id.otp_input_one);
        otp_input_two = (EditText) otpInputDialog.findViewById(R.id.otp_input_two);
        otp_input_three = (EditText) otpInputDialog.findViewById(R.id.otp_input_three);
        otp_input_four = (EditText) otpInputDialog.findViewById(R.id.otp_input_four);

        bindOTPFormClickEvents(otp_input_one, otp_input_two, null);
        bindOTPFormClickEvents(otp_input_two, otp_input_three, otp_input_one);
        bindOTPFormClickEvents(otp_input_three, otp_input_four, otp_input_two);
        bindOTPFormClickEvents(otp_input_four, null, otp_input_three);

        dialogMobileNo.setText(mobile_str);

        otp_input_one.setFocusable(true);

        new CountDownTimer(180000, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                countDownTxt.setText("" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                countDownTxt.setVisibility(View.GONE);
                dialogSendOtp.setVisibility(View.VISIBLE);
                otpInputDialog.dismiss();
            }
        }.start();


        dialogSendOtp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                countDownTxt.setVisibility(View.VISIBLE);
                dialogSendOtp.setVisibility(View.GONE);
                otpInputDialog.dismiss();
                getOTP();

            }
        });

        dialogClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                otpInputDialog.dismiss();
            }
        });
    }

    //Search medicine
    private void bindSearchBox(final Boolean editPres) {
        medicine_autocomplete_popup = (FrameLayout) addPresdialog.findViewById(R.id.medicine_autocomplete_popup);
        searchListView = (ListView) addPresdialog.findViewById(R.id.medicine_list);
        searchModelItems = new ArrayList<MedicineItem>();
        searchListAdapter = new MedicineListAdapter(this, searchModelItems);
        searchListView.setAdapter(searchListAdapter);

        searchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    if (loadMoreMedecines) {
                        loadMoreMedicineFun();
                    }
                }
            }
        });

        closeSearchMedImg = (ImageView) addPresdialog.findViewById(R.id.closeSearchMedImg);
        closeSearchPopupMedView = (View) addPresdialog.findViewById(R.id.closeSearchPopupMedView);
        search_medicine_box = (EditText) addPresdialog.findViewById(R.id.search_medicine_box);
        final TextWatcher _df = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (search_medicine_box.getText().toString().length() >= 2) {

                    //Make the API request
                    JSONObject _em_obj = new JSONObject();
                    try {
                        _em_obj.put("query", search_medicine_box.getText().toString());
                        _em_obj.put("limit", 10);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                            API_URL.MedicineSearch, _em_obj, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            parseJsonMedicineList(response, search_medicine_box.getText().toString(), false);
                            loadMoreMedecines = true;
                            searchPage = 1;
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Response: ", error.getMessage());
                        }
                    });
                    // Adding request to volley request queue
                    mRequestQueue.add(jsonReq);
                } else {
                    searchModelItems.clear();
                    searchListAdapter.notifyDataSetChanged();
                    TextView total_medicine = (TextView) addPresdialog.findViewById(R.id.total_medicine);
                    total_medicine.setText("Found " + 0 + " results");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_medicine_box.getText().toString().length() > 0) {
                    closeSearchMedImg.setVisibility(View.VISIBLE);
                    medicineCameraBtnWrap.setVisibility(View.GONE);
                } else {
                    closeSearchMedImg.setVisibility(View.GONE);
                    if (!editPres) {
                        medicineCameraBtnWrap.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        search_medicine_box.addTextChangedListener(_df);
        medicine_autocomplete_popup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                medicine_autocomplete_popup.setVisibility(View.GONE);
                getCurrentFocus().clearFocus();
                View view = PatientHistory.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        closeSearchMedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearchMedImg.setVisibility(View.GONE);
                medicine_autocomplete_popup.setVisibility(View.GONE);
                if (!editPres) {
                    medicineCameraBtnWrap.setVisibility(View.VISIBLE);
                }

                search_medicine_box.setText("");
                search_medicine_box.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_medicine_box.getWindowToken(), 0);
            }
        });

        closeSearchPopupMedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearchMedImg.setVisibility(View.GONE);
                medicine_autocomplete_popup.setVisibility(View.GONE);
                if (!editPres) {
                    medicineCameraBtnWrap.setVisibility(View.VISIBLE);
                }

                search_medicine_box.setText("");
                search_medicine_box.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_medicine_box.getWindowToken(), 0);
            }
        });
    }

    private void parseJsonMedicineList(JSONObject response, String _searchedStr, Boolean load_more) {
        try {
            if (!load_more) {
                searchModelItems.clear();
            }
            if (!response.isNull("data")) {
                JSONObject dataObj = response.getJSONObject("data");
                JSONArray feedArray = dataObj.getJSONArray("result");
                for (int i = 0; i < feedArray.length(); i++) {
                    JSONObject feedObj = (JSONObject) feedArray.get(i);

                    MedicineItem item = new MedicineItem();
                    item.setSearch_str(_searchedStr);
                    item.setId(feedObj.getString("id"));
                    item.setName(feedObj.getString("name"));
                    item.setForm(feedObj.getString("form"));
                    item.setStatus(feedObj.getString("status"));
                    item.setMedicine_id(feedObj.getString("id"));
                    item.setTradeName(feedObj.getString("trade_name"));
                    item.setStrengthValue(feedObj.getString("strength_value"));
                    item.setUnitOfStrength(feedObj.getString("unit_of_strength"));

                    searchModelItems.add(item);
                }

                medicine_autocomplete_popup.setVisibility(View.VISIBLE);

                TextView total_medicine = (TextView) addPresdialog.findViewById(R.id.total_medicine);
                total_medicine.setText("Found " + dataObj.getString("total") + " results");

                if (feedArray.length() == 0 || dataObj.getString("pages").equals(searchPage)) {
                    loadMoreMedecines = false;
                }

                // notify data changes to list adapter
                searchListAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMoreMedicineFun() {
        searchPage = searchPage + 1;
        JSONObject _em_obj = new JSONObject();
        try {
            _em_obj.put("query", search_medicine_box.getText().toString());
            _em_obj.put("limit", 10);
            _em_obj.put("page", searchPage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.MedicineSearch, _em_obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                parseJsonMedicineList(response, search_medicine_box.getText().toString(), true);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Response: ", error.getMessage());
            }
        });
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private Boolean search_pres_status = false;

    public void addNewPresRow(View v) {
        medicine_autocomplete_popup.setVisibility(View.VISIBLE);
        TextView medicine_name_txt = (TextView) v.findViewById(R.id.medicine_name_txt);
        TextView medicine_name_and_unit_txt = (TextView) v.findViewById(R.id.medicine_name_and_unit_txt);
        //TextView medicine_trade_name = (TextView) v.findViewById(R.id.medicine_trade_name);
        //String _name = medicine_name_and_unit_txt.getText().toString() + medicine_trade_name.getText().toString();

        String _id = medicine_name_txt.getTag().toString();

        JSONObject _med_obj = new JSONObject();
        try {
            JSONObject _em_s = new JSONObject();
            _med_obj.put("name", medicine_name_and_unit_txt.getText());
            _med_obj.put("med_id", _id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        createMedicineRow(_med_obj);
        medicine_autocomplete_popup.setVisibility(View.GONE);
    }

    public void postPrescription(View v) {
        progress.show();
        progress.setMessage("Saving...");
        int count = medicineListWrap.getChildCount();
        Boolean _error = false;

        if (count <= 0) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please add medicines!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            if (validateMedicines() || isPresPhotoRow) {
                JSONObject _obj = new JSONObject();
                try {
                    JSONObject _em_s = new JSONObject();
                    _obj.put("patient_id", patient_id);
                    _obj.put("doc_id", med_user_id);
                    _obj.put("date", currentDate);
                    _obj.put("other_details", presNoteInput.getText());

                    final View med_row = medicineListWrap.getChildAt(0);
                    ConstraintLayout filesWrap = (ConstraintLayout) med_row.findViewById(R.id.filesWrap);
                    if (filesWrap.getTag().toString().equals("photos")) {
                        EditText titleInput = (EditText) med_row.findViewById(R.id.titleInput);
                        _obj.put("title", titleInput.getText());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                        API_URL.Prescription, _obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());
                        Log.i("postPrescription", response.toString());
                        try {
                            if (!response.isNull("data")) {
                                JSONObject dataObj = response.getJSONObject("data");
                                postPresMedicine(dataObj.getString("id"));

                                no_data_perscription.setVisibility(View.GONE);
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
            } else {
                progress.dismiss();
                Toast.makeText(PatientHistory.this, "Medicine name and dosage cannot be empty!", Toast.LENGTH_LONG).show();
            }
        } else {
            progress.dismiss();
        }
    }

    public Boolean validateMedicines() {
        int count = medicineListWrap.getChildCount();
        View med_row = null;
        Boolean isSingleMedFilled = false;
        for (int i = 0; i < count; i++) {
            med_row = medicineListWrap.getChildAt(i);
            Boolean dosageTimeStatus = false;
            ConstraintLayout filesWrap = (ConstraintLayout) med_row.findViewById(R.id.filesWrap);
            if (!filesWrap.getTag().toString().equals("photos")) {
                EditText titleInput;
                Spinner morningSpinner, afternoonSpinner, eveningSpinner, daysSpinner;

                titleInput = (EditText) med_row.findViewById(R.id.titleInput);
                morningSpinner = (Spinner) med_row.findViewById(R.id.morningSpinner);
                afternoonSpinner = (Spinner) med_row.findViewById(R.id.afternoonSpinner);
                eveningSpinner = (Spinner) med_row.findViewById(R.id.eveningSpinner);
                daysSpinner = (Spinner) med_row.findViewById(R.id.daysSpinner);

                String tempDosage = SpinnerDropDown.getSpinnerItem(morningSpinner);

                if (!titleInput.getText().toString().isEmpty()) {
                    if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                        dosageTimeStatus = true;
                    }

                    tempDosage = SpinnerDropDown.getSpinnerItem(afternoonSpinner);
                    if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                        dosageTimeStatus = true;
                    }

                    tempDosage = SpinnerDropDown.getSpinnerItem(eveningSpinner);
                    if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                        dosageTimeStatus = true;
                    }

                    if (dosageTimeStatus) {
                        isSingleMedFilled = true;
                        total_pres_apis_call = total_pres_apis_call + 1;
                    } else {
                        isSingleMedFilled = false;
                        break;
                    }
                }
            }
        }
        return isSingleMedFilled;
    }

    public Boolean validateMedicineRow(View med_row) {
        int count = medicineListWrap.getChildCount();
        Boolean isSingleMedFilled = false;

        Boolean dosageTimeStatus = false;
        ConstraintLayout filesWrap = (ConstraintLayout) med_row.findViewById(R.id.filesWrap);
        if (!filesWrap.getTag().toString().equals("photos")) {
            EditText titleInput;
            Spinner morningSpinner, afternoonSpinner, eveningSpinner, daysSpinner;

            titleInput = (EditText) med_row.findViewById(R.id.titleInput);
            morningSpinner = (Spinner) med_row.findViewById(R.id.morningSpinner);
            afternoonSpinner = (Spinner) med_row.findViewById(R.id.afternoonSpinner);
            eveningSpinner = (Spinner) med_row.findViewById(R.id.eveningSpinner);
            daysSpinner = (Spinner) med_row.findViewById(R.id.daysSpinner);


            String tempDosage = SpinnerDropDown.getSpinnerItem(morningSpinner);
            if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                dosageTimeStatus = true;
            }

            tempDosage = SpinnerDropDown.getSpinnerItem(afternoonSpinner);
            if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                dosageTimeStatus = true;
            }

            tempDosage = SpinnerDropDown.getSpinnerItem(eveningSpinner);
            if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                dosageTimeStatus = true;
            }

            if (dosageTimeStatus && !titleInput.getText().toString().isEmpty()) {
                isSingleMedFilled = true;
            }
        }
        return isSingleMedFilled;
    }

    public void postPresMedicine(String _pre_id) {
        int count = medicineListWrap.getChildCount();
        View med_row = null;
        for (int i = 0; i < count; i++) {
            med_row = medicineListWrap.getChildAt(i);

            ConstraintLayout filesWrap = (ConstraintLayout) med_row.findViewById(R.id.filesWrap);
            if (filesWrap.getTag().toString().equals("photos")) {
                Log.i("uploadPresFiles", "post");
                uploadPresFiles(_pre_id);
            } else {
                Log.i("postPresText", "post");
                if (validateMedicineRow(med_row)) {
                    postPresText(med_row, _pre_id);
                }
            }
        }
        postFollowUpSMS(_pre_id);
    }

    private void postFollowUpSMS(final String _pre_id) {
        if (followUpDateInput != null && !followUpDateInput.getText().toString().isEmpty()) {
            JSONObject _obj = new JSONObject();
            try {
                JSONObject _em_s = new JSONObject();
                _obj.put("pre_id", _pre_id);
                _obj.put("doc_id", med_user_id);
                _obj.put("patient_id", patient_id);
                _obj.put("date", followUpDateInput.getText());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientFollowUp, _obj, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to volley request queue
            mRequestQueue.add(jsonReq);
        }
    }

    private void postPresText(View med_row, final String _pre_id) {
        EditText titleInput;
        Spinner morningSpinner, afternoonSpinner, eveningSpinner, daysSpinner;

        titleInput = (EditText) med_row.findViewById(R.id.titleInput);
        morningSpinner = (Spinner) med_row.findViewById(R.id.morningSpinner);
        afternoonSpinner = (Spinner) med_row.findViewById(R.id.afternoonSpinner);
        eveningSpinner = (Spinner) med_row.findViewById(R.id.eveningSpinner);
        daysSpinner = (Spinner) med_row.findViewById(R.id.daysSpinner);
        Boolean dosageTimeStatus = false;

        JSONObject _obj = new JSONObject();
        try {
            JSONObject _em_s = new JSONObject();
            _obj.put("pre_id", _pre_id);
            _obj.put("name", titleInput.getText());

            if (titleInput.getTag() != null && !titleInput.getTag().toString().equals("null")) {
                _obj.put("medicine_id", titleInput.getTag());
            }

            _obj.put("morning_dosage", SpinnerDropDown.getSpinnerItem(morningSpinner));
            _obj.put("night_dosage", SpinnerDropDown.getSpinnerItem(eveningSpinner));
            _obj.put("afternoon_dosage", SpinnerDropDown.getSpinnerItem(afternoonSpinner));

            _obj.put("total_days", SpinnerDropDown.getSpinnerItem(daysSpinner));

            String tempDosage = SpinnerDropDown.getSpinnerItem(morningSpinner);
            if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                _obj.put("morning", true);
                dosageTimeStatus = true;
            } else {
                _obj.put("morning", false);
            }

            tempDosage = SpinnerDropDown.getSpinnerItem(afternoonSpinner);
            if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                _obj.put("afternoon", true);
                dosageTimeStatus = true;
            } else {
                _obj.put("afternoon", false);
            }

            tempDosage = SpinnerDropDown.getSpinnerItem(eveningSpinner);
            if (!(tempDosage == null || tempDosage.isEmpty() || tempDosage.equals("0"))) {
                _obj.put("night", true);
                dosageTimeStatus = true;
            } else {
                _obj.put("night", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (dosageTimeStatus) {
            if (med_row.getTag() != null && !med_row.getTag().toString().isEmpty()) {
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                        API_URL.PrescriptionMedicine + "/" + med_row.getTag().toString(), _obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());

                        fetchNewPrescription(_pre_id, isPresUpdate);

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
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                        API_URL.PrescriptionMedicine, _obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());

                        fetchNewPrescription(_pre_id, isPresUpdate);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
                // Adding request to volley request queue
                mRequestQueue.add(jsonReq);
            }
        }
    }

    private void uploadPresFiles(final String _id) {
        final View _imageViewSec = medicineListWrap.getChildAt(0);
        LinearLayout tempPresImgsHolderWrap = (LinearLayout) _imageViewSec.findViewById(R.id.tempPresImgsHolderWrap);
        int _file_count = tempPresImgsHolderWrap.getChildCount();
        total_pres_apis_call = _file_count;
        Log.i("_file_count", String.valueOf(_file_count));
        for (int i = 0; i < _file_count; i++) {
            final View _child = tempPresImgsHolderWrap.getChildAt(i);
            final ImageView slideImg = (ImageView) _child.findViewById(R.id.slideImg);
            Log.i("psot", API_URL.PrescriptionMedicineFileUpload);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.PrescriptionMedicineFileUpload, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);

                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject result = new JSONObject(json);
                        boolean _error = Boolean.parseBoolean(result.getString("error"));
                        if (!_error && !result.isNull("data")) {
                            postPresFiles(result.getJSONObject("data"), _id);

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
                        try {
                            JSONObject response = new JSONObject(result);
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Log.e("Error Status", status);
                            Log.e("Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message + " Please login again";
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message + " Check your inputs";
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message + " Something is getting wrong";
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), slideImg.getDrawable()), "image/jpeg"));

                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }
    }

    private void postPresFiles(JSONObject _data, final String pre_id) {
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            _obj.put("pre_id", pre_id);
            _obj.put("file", _data.getString("file"));
            _obj.put("file_type", _data.getString("file_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PrescriptionMedicineFile, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("postPresFiles", response.toString());
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        Toast.makeText(PatientHistory.this, "Success!", Toast.LENGTH_LONG).show();
                        fetchNewPrescription(pre_id, false);
                    } else {
                        Log.i("Error ", "Something went wrong!" + response.toString());
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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


    //Prescription edit dialog section
    public void updatePresMedicine(View v) {
        progress.setTitle("Updating...");
        progress.show();
        final int count = medicineListWrap.getChildCount();
        Boolean _error = false;

        if (count <= 0) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please add medicines!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            if (validateMedicines()) {
                JSONObject _obj = new JSONObject();
                try {
                    JSONObject _em_s = new JSONObject();
                    _obj.put("other_details", presNoteInput.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                        API_URL.Prescription + "/" + prescription_id, _obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.d(TAG, "Response: " + response.toString());
                        Log.i("updatePresMedicine", response.toString());
                        progress.dismiss();
                        try {
                            Boolean _error = Boolean.parseBoolean(response.getString("error"));
                            if (!_error && !response.isNull("data")) {

                                isPresUpdate = true;
                                View med_row = null;
                                for (int i = 0; i < count; i++) {
                                    med_row = medicineListWrap.getChildAt(i);
                                    ConstraintLayout filesWrap = (ConstraintLayout) med_row.findViewById(R.id.filesWrap);
                                    if (validateMedicineRow(med_row)) {
                                        postPresText(med_row, prescription_id);
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
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        progress.dismiss();
                    }
                });
                // Adding request to volley request queue
                mRequestQueue.add(jsonReq);
            } else {
                progress.dismiss();
                Toast.makeText(PatientHistory.this, "Medicine name and dosage cannot be empty!", Toast.LENGTH_LONG).show();
            }
        } else {
            progress.dismiss();
        }

    }

    private void fetchSinglePrescription() {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.Prescription + "/" + prescription_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonPrescription(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void parseJsonPrescription(JSONObject response) {
        try {
            Boolean error = Boolean.parseBoolean(response.getString("error"));
            if (!error && !response.isNull("data")) {
                JSONObject dataObj = response.getJSONObject("data");
                // JSONObject docObj = dataObj.getJSONObject("doc");

                presNoteInput.setText(removeNull(dataObj.getString("other_details")));

                if (!dataObj.isNull("pres_meds")) {
                    JSONArray presMedArray = dataObj.getJSONArray("pres_meds");
                    for (int i = 0; i < presMedArray.length(); i++) {
                        JSONObject presObj = (JSONObject) presMedArray.get(i);
                        createExistingPresRow(presObj);
                    }
                }
                bindPresTitleTextChangeEvents(presNoteInput);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createExistingPresRow(final JSONObject presObj) {
        //medicineListWrap.removeAllViews();
        try {
            final View preview = layoutInflater.inflate(R.layout.pat_add_pres_row, null);

            TextView plus_txt = (TextView) preview.findViewById(R.id.plus_txt);
            TextView title_txt = (TextView) preview.findViewById(R.id.title_txt);
            TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);

            final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
            final LinearLayout removeMedRow = (LinearLayout) preview.findViewById(R.id.removeMedRow);
            final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
            final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);
            final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
            final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);
            final ConstraintLayout filesWrap = (ConstraintLayout) preview.findViewById(R.id.filesWrap);
            final String pres_med_id = presObj.getString("id");

            Spinner morningSpinner = (Spinner) preview.findViewById(R.id.morningSpinner);
            Spinner afternoonSpinner = (Spinner) preview.findViewById(R.id.afternoonSpinner);
            Spinner eveningSpinner = (Spinner) preview.findViewById(R.id.eveningSpinner);
            Spinner daysSpinner = (Spinner) preview.findViewById(R.id.daysSpinner);

            bindSpinnerDropDown(morningSpinner, dosageList);
            bindSpinnerDropDown(afternoonSpinner, dosageList);
            bindSpinnerDropDown(eveningSpinner, dosageList);
            bindSpinnerDropDown(daysSpinner, daysList);

            add_title_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add_title_btn.setVisibility(View.GONE);
                    titleInput.setVisibility(View.VISIBLE);
                    titleInput.requestFocus();

                    new CountDownTimer(200, 1000) { // adjust the milli seconds here
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            titleInput.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                        }
                    }.start();
                }
            });

            if (!presObj.isNull("medicine")) {
                JSONObject medicineObj = presObj.getJSONObject("medicine");

                add_title_btn.setVisibility(View.GONE);
                titleInput.setVisibility(View.VISIBLE);
                try {
                    titleInput.setText(medicineObj.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (!presObj.isNull("name")) {
                    if (!presObj.getString("name").equals("null")) {
                        titleInput.setText(presObj.getString("name"));
                    }
                }
            }
            // add_title_btn.performClick();
            add_title_btn.setVisibility(View.GONE);
            titleInput.setVisibility(View.VISIBLE);

            SpinnerDropDown.setSpinnerItem(morningSpinner, presObj.getString("morning_dosage"));
            SpinnerDropDown.setSpinnerItem(afternoonSpinner, presObj.getString("afternoon_dosage"));
            SpinnerDropDown.setSpinnerItem(eveningSpinner, presObj.getString("night_dosage"));
            SpinnerDropDown.setSpinnerItem(daysSpinner, presObj.getString("total_days"));

            removeMedRow.setTag(pres_med_id);
            removeMedRow.setVisibility(View.VISIBLE);

            final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);

            final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            showHideFilesBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i("", showHideFilesBtn.getTag().toString());

                    if (showHideFilesBtn.getTag().equals("0")) {
                        showHideFilesBtn.setTag("1");
                        upwardArrowImg.setVisibility(View.GONE);
                        closeImg.setVisibility(View.VISIBLE);
                        filesWrap.setVisibility(View.VISIBLE);

                        filesWrap.setVisibility(View.VISIBLE);
                        filesWrap.startAnimation(slide_up);

                    } else {
                        showHideFilesBtn.setTag("0");
                        upwardArrowImg.setVisibility(View.VISIBLE);
                        closeImg.setVisibility(View.GONE);
                        filesWrap.setVisibility(View.GONE);

                        //filesWrap.startAnimation(slide_down);
                        filesWrap.setVisibility(View.GONE);

                    }
                }
            });

            removeMedRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(PatientHistory.this)
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deletePresMedicine(pres_med_id, preview);
                                }

                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            });

            medicineListWrap.addView(preview);


            if (!presObj.isNull("id")) {
                preview.setTag(pres_med_id);
            }

            bindSpinnerPresDosageChangeEvent(morningSpinner);
            bindSpinnerPresDosageChangeEvent(afternoonSpinner);
            bindSpinnerPresDosageChangeEvent(eveningSpinner);
            bindPresTitleTextChangeEvents(titleInput);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deletePresMedicine(String pres_med_id, final View _rowView) {
        progress.setMessage("Deleting...");
        progress.show();
        new PatientPrescription().delMedicine(PatientHistory.this, pres_med_id, new MyCallBack() {
            @Override
            public void call(JSONObject response) {
                Log.i("deletePresMedicine", response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        if (_data.getString("code").toString().equals("200")) {
                            medicineListWrap.removeView(_rowView);
                            if (medicineListWrap.getChildCount() > 1) {
                                progress.dismiss();
                            } else {
                                new PatientPrescription().delPrescription(PatientHistory.this, prescription_id, new MyCallBack() {
                                    @Override
                                    public void call(JSONObject response) {
                                        progress.dismiss();
                                        Log.i("deletePrescription", response.toString());
                                        try {
                                            boolean _error = Boolean.parseBoolean(response.getString("error"));
                                            if (!_error) {
                                                JSONObject _data = (JSONObject) response.getJSONObject("data");
                                                if (_data.getString("code").toString().equals("200")) {
                                                    presListWrap.removeView(_rowView);
                                                    Toast.makeText(PatientHistory.this, "Medicine deleted successfully.", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    } else {
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                    Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void bindSpinnerPresDosageChangeEvent(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                updatePresDialogBtn.setVisibility(View.VISIBLE);
                updatePresDialogTxt.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void bindPresTitleTextChangeEvents(EditText editText) {
        TextWatcher _textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePresDialogBtn.setVisibility(View.VISIBLE);
                updatePresDialogTxt.setVisibility(View.GONE);
            }
        };
        editText.addTextChangedListener(_textWatcher);
    }

    private void fetchNewPrescription(String _pre_id, final Boolean updateRow) {
        count_pres_post_api_call = count_pres_post_api_call + 1;
        Log.i("total_pres_apis_call", String.valueOf(total_pres_apis_call));
        Log.i("count_pres_post_api_call", String.valueOf(count_pres_post_api_call));
        if (count_pres_post_api_call == total_pres_apis_call) {
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    API_URL.Prescription + "/" + _pre_id, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    try {
                        Boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error && !response.isNull("data")) {
                            JSONObject presObj = response.getJSONObject("data");
                            JSONObject docObj = presObj.getJSONObject("doc");

                            JSONArray presMedArr = presObj.getJSONArray("pres_meds");
                            JSONArray presMedFilesArr = presObj.getJSONArray("pres_med_files");

                            if (presMedArr.length() > 0) {
                                createPresListRow(presMedArr, presObj, docObj, true, updateRow);
                            } else if (presMedFilesArr.length() > 0) {
                                createPresListPhotoRow(presMedFilesArr, presObj, true);
                            }
                            addPresdialog.hide();
                            progress.dismiss();
                            count_pres_post_api_call = 0;
                            total_pres_apis_call = 0;
                            pres_card_row_index = 0;
                            isPresUpdate = false;
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
            mRequestQueue.add(jsonReq);
        }
    }
    //Prescription functions end <------------------------------------------------------------------


    //Reports functions Start ---------------------------------------------------------------------->
    private void initReportsEvents() {
        reportListWrap = (LinearLayout) findViewById(R.id.reportListWrap);
        addReportPhoto = (LinearLayout) findViewById(R.id.addReportPhoto);
        imageHolder = (ImageView) findViewById(R.id.imageHolder);
        no_data_report = findViewById(R.id.no_data_report);

        addReportPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureReportPhoto();
            }
        });

        reportScrollView = (ScrollView) findViewById(R.id.reportScrollView);
        reportScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                LinearLayout view = (LinearLayout) reportScrollView.getChildAt(0);
                if (view.getChildCount() > 1) {
                    int diff = (view.getBottom() - (reportScrollView.getHeight() + reportScrollView.getScrollY()));
                    if (diff < 40 && _reportLoadMoreStatus) {
                        _reportLoadMoreStatus = false;
                        loadReports(true);
                    }
                }
            }
        });
        loadReports(false);
    }

    public void captureReportPhoto() {
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
            startActivityForResult(intent, ADD_REPORT_REQUEST_CODE);
        }
    }

    public void captureMoreReportPhoto() {
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
            startActivityForResult(intent, ADD_MORE_REPORT_REQUEST_CODE);
        }
    }

    private void postLabReport(final Bitmap bitmapImage) {
        progress.show();
        progress.setTitle("Uploading report....");
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            _obj.put("date", currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientReport, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));

                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");

                        no_data_report.setVisibility(View.GONE);
                        uploadLabReportFiles(bitmapImage, _data);
                    } else {
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void uploadLabReportFiles(final Bitmap bitmapImage, final JSONObject reportData) {
        if (bitmapImage != null) {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.PatientReportFilesUpload, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject result = new JSONObject(json);
                        boolean _error = Boolean.parseBoolean(result.getString("error"));
                        if (!_error && !result.isNull("data")) {
                            postLabReportFiles(result.getJSONObject("data"), reportData);
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
                    params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageHolder.getDrawable()), "image/jpeg"));

                    return params;
                }
            };

            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }

    }

    private void postLabReportFiles(JSONObject _data, final JSONObject reportData) {
        final JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            _obj.put("file", _data.getString("file"));
            _obj.put("file_type", _data.getString("file_type"));
            _obj.put("patient_report_id", reportData.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientReportFiles, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _fileData = (JSONObject) response.getJSONObject("data");
                        createNewReportRow(_fileData, reportData);
                        Toast.makeText(PatientHistory.this, "Success!", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("Error ", "Something went wrong!" + response.toString());
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void createNewReportRow(JSONObject fileData, final JSONObject reportData) {
        Log.i("fileData", fileData.toString());
        Log.i("reportData", reportData.toString());
        try {
            LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View preview = layoutInflater.inflate(R.layout.pat_report_row, null);

            final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
            final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);
            TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);

            final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
            final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
            final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);

            final ImageView addMoreFile = (ImageView) preview.findViewById(R.id.addMoreFile);
            final ImageView deleteFile = (ImageView) preview.findViewById(R.id.deleteFile);
            final ConstraintLayout reportFilesWrap = (ConstraintLayout) preview.findViewById(R.id.reportFilesWrap);

            String _report_id = "";

            if (reportData != null) {
                titleInput.setTag(reportData.getString("id"));
                _report_id = reportData.getString("id");
            }

            titleInput.setTag(_report_id);
            add_title_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleInput.setVisibility(View.VISIBLE);
                    add_title_btn.setVisibility(View.GONE);
                    new CountDownTimer(200, 1000) { // adjust the milli seconds here
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            titleInput.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                        }
                    }.start();
                }
            });

            titleInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        hideKeyboard(PatientHistory.this);
                        updateReportTitle(titleInput);
                    }
                    return true;
                }
            });

            titleInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (titleInput.getText().toString().isEmpty()) {
                            titleInput.setVisibility(View.GONE);
                            add_title_btn.setVisibility(View.VISIBLE);
                        }
                        updateReportTitle(titleInput);
                    }
                }
            });


            final ViewPager view_pager = (ViewPager) preview.findViewById(R.id.view_pager);
            final LinearLayout layoutDots = (LinearLayout) preview.findViewById(R.id.layoutDots);

            CustomSlider customSlider = new CustomSlider();
            ArrayList<ViewGroup> viewGroup = new ArrayList<ViewGroup>();
            final ArrayList<ImageSlide> _galleryImages = new ArrayList<>();

            if (fileData != null) {

                String img_url = API_URL._domain + "/patient-report-files/photo/" + fileData.getString("file");

                View slider_image_view = layoutInflater.inflate(R.layout.slider_preview_img, null);
                final ImageView slideImg = (ImageView) slider_image_view.findViewById(R.id.slideImg);

                new DownLoadSliderImageTask(slideImg).execute(img_url);
                viewGroup.add(((ViewGroup) slider_image_view));

                ImageSlide image = new ImageSlide();
                //image.setName("New image");
                image.setSmall(img_url);
                image.setMedium(img_url);
                image.setLarge(img_url);
                _galleryImages.add(image);

                slider_image_view.setTag(img_url);
                slideImg.setTag(0);
                slideImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        galleryImages = _galleryImages;
                        showBigImgPopup(slideImg);
                    }
                });

            }

            view_pager.setTag(viewGroup);
            customSlider.set(this, view_pager, layoutDots, viewGroup);


            final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down_pres_list);

            final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up_pres_list);
            showHideFilesBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (showHideFilesBtn.getTag().equals("0")) {
                        showHideFilesBtn.setTag("1");
                        upwardArrowImg.setVisibility(View.GONE);
                        closeImg.setVisibility(View.VISIBLE);
                        reportFilesWrap.setVisibility(View.VISIBLE);

                        reportFilesWrap.setVisibility(View.VISIBLE);
                        reportFilesWrap.startAnimation(slide_up);
                    } else {
                        showHideFilesBtn.setTag("0");
                        upwardArrowImg.setVisibility(View.VISIBLE);
                        closeImg.setVisibility(View.GONE);
                        reportFilesWrap.setVisibility(View.GONE);

                        //reportFilesWrap.startAnimation(slide_down);
                        reportFilesWrap.setVisibility(View.GONE);
                    }
                }
            });

            final String final_report_id = _report_id;
            addMoreFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedViewPager = view_pager;
                    selectedLayoutDots = layoutDots;
                    report_id = final_report_id;
                    captureMoreReportPhoto();
                }
            });

            deleteFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(PatientHistory.this)
                            .setTitle("Delete")
                            .setMessage("Do you want to Delete")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();

                                }

                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                }
                            })
                            .create();
                    selectedViewPager = view_pager;
                    report_id = final_report_id;
                    dialog.show();
                }
            });

            showHideFilesBtn.performClick();

            bindReportTitleUpdateEvent(titleInput);
            reportListWrap.addView(preview, 0);
            progress.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private class DownLoadPresRoundCornersImg extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadPresRoundCornersImg(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
            /*
                decodeStream(InputStream is)
                    Decode an input stream into a bitmap.
             */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            // imageView.setImageBitmap(result);
            imageView.setImageBitmap(getRoundedCornerBitmap(result, 60));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void bindReportTitleUpdateEvent(final EditText editText) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    JSONObject _obj = new JSONObject();
                    try {
                        _obj.put("title", editText.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateReportReportTitle(_obj, editText.getTag().toString());
                }
                return false;
            }
        });
    }

    private void updateReportReportTitle(JSONObject _obj, String _rep_id) {
        progress.show();
        progress.setTitle("Updating title...");

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.PatientReport + "/" + _rep_id, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                Log.i("updateReportReportTitle", response.toString());
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        Toast.makeText(PatientHistory.this, "Title updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void loadReports(final Boolean loadMore) {
        progress.setTitle("Loading...");
        progress.show();
        JSONObject _obj = new JSONObject();
        try {
            _obj.put("limit", 10);
            _obj.put("doc_id", med_user_id);
            _obj.put("patient_id", patient_id);
            if (loadMore) {
                report_page_load_num = report_page_load_num + 1;
                _obj.put("page", report_page_load_num);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocReportSearch, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();


                TextView report_instruction_txt = findViewById(R.id.report_instruction_txt);
                report_instruction_txt.setText("Click the + button on the lower right corner to create a new report");
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        if (!response.isNull("data")) {
                            JSONObject dataObj = (JSONObject) response.getJSONObject("data");
                            JSONArray feedArray = dataObj.getJSONArray("result");

                            if (dataObj.getString("pages").equals(String.valueOf(report_page_load_num))) {
                                _reportLoadMoreStatus = false;
                            } else {
                                _reportLoadMoreStatus = true;
                            }

                            if (!loadMore) {
                                String _report_count_str = "Reports " + "(" + dataObj.getString("total") + ")";
                            }

                            for (int i = 0; i < feedArray.length(); i++) {
                                JSONObject feedObj = (JSONObject) feedArray.get(i);

                                JSONObject _patientObj = (JSONObject) feedObj.getJSONObject("patient");
                                JSONObject docObj = (JSONObject) feedObj.getJSONObject("doc");

                                LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                                View preview;
                                if (!_patientObj.isNull("patient_reports")) {
                                    JSONArray _reportsArray = _patientObj.getJSONArray("patient_reports");

                                    for (int j = 0; j < _reportsArray.length(); j++) {
                                        JSONObject _reportObj = (JSONObject) _reportsArray.get(j);
                                        JSONArray _fileData = (JSONArray) _reportObj.getJSONArray("patient_report_files");

                                        createReportRow(_fileData, _reportObj);
                                    }

                                    if (!loadMore && _reportsArray.length() == 0) {
//                                        reportListWrap.addView(no_data_row);
                                        no_data_report.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                        }
                    } else {

                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void createReportRow(JSONArray fileData, final JSONObject reportData) {
        try {
            LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            final View preview = layoutInflater.inflate(R.layout.pat_report_row, null);

            final LinearLayout add_title_btn = (LinearLayout) preview.findViewById(R.id.add_title_btn);
            final EditText titleInput = (EditText) preview.findViewById(R.id.titleInput);
            TextView date_txt = (TextView) preview.findViewById(R.id.date_txt);

            final LinearLayout showHideFilesBtn = (LinearLayout) preview.findViewById(R.id.showHideFilesBtn);
            final ImageView upwardArrowImg = (ImageView) preview.findViewById(R.id.upwardArrowImg);
            final ImageView closeImg = (ImageView) preview.findViewById(R.id.closeImg);

            final ImageView addMoreFile = (ImageView) preview.findViewById(R.id.addMoreFile);
            final ImageView deleteFile = (ImageView) preview.findViewById(R.id.deleteFile);

            final ConstraintLayout reportFilesWrap = (ConstraintLayout) preview.findViewById(R.id.reportFilesWrap);
            final LinearLayout photoSliderWrap = (LinearLayout) preview.findViewById(R.id.photoSliderWrap);

            add_title_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleInput.setVisibility(View.VISIBLE);
                    add_title_btn.setVisibility(View.GONE);

                    new CountDownTimer(200, 1000) { // adjust the milli seconds here
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            titleInput.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(titleInput, InputMethodManager.SHOW_FORCED);
                        }
                    }.start();
                }
            });


            titleInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        hideKeyboard(PatientHistory.this);
                        updateReportTitle(titleInput);
                    }
                    return true;
                }
            });

            titleInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (titleInput.getText().toString().isEmpty()) {
                            titleInput.setVisibility(View.GONE);
                            add_title_btn.setVisibility(View.VISIBLE);
                        }
                        updateReportTitle(titleInput);
                    }
                }
            });


            String _report_id = "";
            if (reportData != null) {
                titleInput.setTag(reportData.getString("id"));
                String tempTitleStr = reportData.getString("title");
                if (tempTitleStr != null && !tempTitleStr.isEmpty() && !tempTitleStr.equals("null")) {
                    titleInput.setText(removeNull(reportData.getString("title")));
                    //add_title_btn.performClick();
                    add_title_btn.setVisibility(View.GONE);
                    titleInput.setVisibility(View.VISIBLE);
                }
                _report_id = reportData.getString("id");
                date_txt.setText(localDateFormat(reportData.getString("date")));
            }
            final String final_report_id = _report_id;

            final ViewPager view_pager = (ViewPager) preview.findViewById(R.id.view_pager);
            final LinearLayout layoutDots = (LinearLayout) preview.findViewById(R.id.layoutDots);

            CustomSlider customSlider = new CustomSlider();
            final ArrayList<ViewGroup> viewGroup = new ArrayList<ViewGroup>();
            final ArrayList<ImageSlide> _galleryImages = new ArrayList<>();

            if (fileData != null && fileData.length() > 0) {
                for (int k = 0; k < fileData.length(); k++) {
                    JSONObject _fileObj = (JSONObject) fileData.get(k);
                    String img_url = API_URL._domain + "/patient-report-files/photo/" + _fileObj.getString("file");

                    View slider_image_view = layoutInflater.inflate(R.layout.slider_preview_img, null);
                    final ImageView slideImg = (ImageView) slider_image_view.findViewById(R.id.slideImg);
                    final View fileObjectView = (View) slider_image_view.findViewById(R.id.fileObjectView);

                    fileObjectView.setTag(_fileObj);
                    viewGroup.add(((ViewGroup) slider_image_view));

                    ImageSlide image = new ImageSlide();
                    //image.setName("New image");
                    image.setSmall(img_url);
                    image.setMedium(img_url);
                    image.setLarge(img_url);
                    _galleryImages.add(image);

                    slider_image_view.setTag(img_url);
                    slideImg.setTag(k);
                    slideImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            galleryImages = _galleryImages;
                            showBigImgPopup(slideImg);
                        }
                    });
                }

                deleteFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog dialog = new AlertDialog.Builder(PatientHistory.this)
                                .setTitle("Delete")
                                .setMessage("Do you want to Delete")
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        selectedViewPager = view_pager;
                                        selectedLayoutDots = layoutDots;
                                        report_id = final_report_id;
                                        galleryImages = _galleryImages;
                                        deleteReportFile(preview);
                                    }

                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                    }
                                })
                                .create();
                        dialog.show();
                    }
                });

                view_pager.setTag(viewGroup);
                customSlider.set(this, view_pager, layoutDots, viewGroup);

                addMoreFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedViewPager = view_pager;
                        selectedLayoutDots = layoutDots;
                        report_id = final_report_id;
                        galleryImages = _galleryImages;
                        captureMoreReportPhoto();
                    }
                });
            }

            final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);

            final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            final boolean[] loadFirstTime = {true};
            showHideFilesBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (showHideFilesBtn.getTag().equals("0")) {
                        showHideFilesBtn.setTag("1");
                        upwardArrowImg.setVisibility(View.GONE);
                        closeImg.setVisibility(View.VISIBLE);
                        reportFilesWrap.setVisibility(View.VISIBLE);

                        reportFilesWrap.setVisibility(View.VISIBLE);
                        reportFilesWrap.startAnimation(slide_up);

                        if (loadFirstTime[0]) {
                            loadFirstTime[0] = false;
                            for (int imgIndex = 0; imgIndex < viewGroup.size(); imgIndex++) {
                                ViewGroup slider_image_view = viewGroup.get(imgIndex);
                                final ImageView slideImg = (ImageView) slider_image_view.findViewById(R.id.slideImg);
                                new DownLoadSliderImageTask(slideImg).execute(slider_image_view.getTag().toString());
                            }
                        }
                    } else {
                        showHideFilesBtn.setTag("0");
                        upwardArrowImg.setVisibility(View.VISIBLE);
                        closeImg.setVisibility(View.GONE);
                        reportFilesWrap.setVisibility(View.GONE);

                        //reportFilesWrap.startAnimation(slide_down);
                        reportFilesWrap.setVisibility(View.GONE);
                    }
                }
            });


            bindReportTitleUpdateEvent(titleInput);
            reportListWrap.addView(preview);
            progress.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private void updateReportTitle(EditText titleInput) {
        progress.show();

        JSONObject _obj = new JSONObject();
        try {
            JSONObject _em_s = new JSONObject();
            _obj.put("title", titleInput.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.PatientReport + "/" + titleInput.getTag().toString(), _obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        Toast.makeText(PatientHistory.this, "Data updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
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

    private void uploadMoreReportPhoto(final Bitmap bitmapImage) {
        if (bitmapImage != null) {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, API_URL.PatientReportFilesUpload, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    try {
                        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject result = new JSONObject(json);
                        boolean _error = Boolean.parseBoolean(result.getString("error"));
                        if (!_error && !result.isNull("data")) {
                            postMoreReport(result.getJSONObject("data"));
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
                    params.put("photo", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), imageHolder.getDrawable()), "image/jpeg"));

                    return params;
                }
            };

            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }
    }

    private void postMoreReport(JSONObject _data) {
        final JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            _obj.put("file", _data.getString("file"));
            _obj.put("file_type", _data.getString("file_type"));
            _obj.put("patient_report_id", report_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientReportFiles, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _fileData = (JSONObject) response.getJSONObject("data");
                        addNewReportSliderItem(_fileData);
                        Toast.makeText(PatientHistory.this, "Photo uploaded successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("Error ", "Something went wrong!" + response.toString());
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void addNewReportSliderItem(JSONObject fileData) {
        Log.i("fileData", fileData.toString());
        try {
            LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

            if (fileData != null) {
                if (!fileData.isNull("file")) {
                    String img_url = API_URL._domain + "/patient-report-files/photo/" + fileData.getString("file");
                    View slider_image_view = layoutInflater.inflate(R.layout.slider_preview_img, null);
                    final ImageView slideImg = (ImageView) slider_image_view.findViewById(R.id.slideImg);
                    new DownLoadSliderImageTask(slideImg).execute(img_url);

                    ArrayList<ViewGroup> viewGroup = (ArrayList<ViewGroup>) selectedViewPager.getTag();
                    int _lenCount = viewGroup.size();
                    viewGroup.add(_lenCount, (ViewGroup) slider_image_view);
                    selectedViewPager.removeAllViews();
                    selectedLayoutDots.removeAllViews();
                    CustomSlider customSlider = new CustomSlider();

                    ImageSlide image = new ImageSlide();
                    image.setSmall(img_url);
                    image.setMedium(img_url);
                    image.setLarge(img_url);
                    galleryImages.add(image);

                    final ArrayList<ImageSlide> tempGalleryImages;
                    tempGalleryImages = galleryImages;

                    slider_image_view.setTag(img_url);
                    slideImg.setTag(_lenCount);
                    slideImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            galleryImages = tempGalleryImages;
                            showBigImgPopup(slideImg);
                        }
                    });

                    customSlider.set(this, selectedViewPager, selectedLayoutDots, viewGroup);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private void deleteReportFile(final View _rowView) {
        final ArrayList<ViewGroup> viewGroup = (ArrayList<ViewGroup>) selectedViewPager.getTag();
        final int position = selectedViewPager.getCurrentItem();
        final ViewGroup preview = (ViewGroup) viewGroup.get(position);
        View fileObjectView = (View) preview.findViewById(R.id.fileObjectView);
        JSONObject jsonObject = (JSONObject) fileObjectView.getTag();

        progress.setMessage("Deleting...");
        progress.show();
        new PatientReport().delPhoto(PatientHistory.this, jsonObject, new MyCallBack() {
            @Override
            public void call(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        if (_data.getString("code").toString().equals("200")) {
                            viewGroup.remove(position);
                            if (viewGroup.size() != 0) {
                                progress.dismiss();
                                selectedViewPager.removeAllViews();
                                selectedLayoutDots.removeAllViews();
                                galleryImages.remove(position);
                                new CustomSlider().set(PatientHistory.this, selectedViewPager, selectedLayoutDots, viewGroup);
                                Toast.makeText(PatientHistory.this, "Photo deleted successfully.", Toast.LENGTH_LONG).show();
                            } else {
                                new PatientReport().delReport(PatientHistory.this, report_id, new MyCallBack() {
                                    @Override
                                    public void call(JSONObject response) {
                                        progress.dismiss();
                                        Log.i("delReport", response.toString());
                                        try {
                                            boolean _error = Boolean.parseBoolean(response.getString("error"));
                                            if (!_error) {
                                                JSONObject _data = (JSONObject) response.getJSONObject("data");
                                                if (_data.getString("code").toString().equals("200")) {
                                                    reportListWrap.removeView(_rowView);
                                                    Toast.makeText(PatientHistory.this, "Photo deleted successfully.", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    } else {
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                    Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //Reports functions end <-----------------------------------------------------------------------


    //Body vitals functions start ----------------------------------------------------------------------->
    private void initBodyVitals() {
        bloodGroupSpinner = (Spinner) findViewById(R.id.bloodGroupSpinner);
        heightSpinner = (Spinner) findViewById(R.id.heightSpinner);
        userNameTxt = (TextView) findViewById(R.id.userNameTxt);
        heightTxt = (TextView) findViewById(R.id.heightTxt);

        vitalsListWrap = (LinearLayout) findViewById(R.id.vitalsListWrap);
        addNewVitalsImg = (ImageView) findViewById(R.id.addNewVitalsImg);


        ArrayList<StringWithTag> heightNumberList = new ArrayList<StringWithTag>();
        heightNumberList.add(new StringWithTag("Select", ""));
        for (int i = 30; i < 250; i++) {

            // double inch = 0.3937 * i;
            double feet = 0.0328 * i;
            double roundOff = Math.round(feet * 100.0) / 100.0;
            String tempStr = String.format("%.1f", roundOff);
            String[] splitStr = tempStr.split("\\.");

            String resultStr = i + " cm" + " (" + splitStr[0] + "\'" + splitStr[1] + "\")";
            heightNumberList.add(new StringWithTag(String.valueOf(resultStr), String.valueOf(i)));
        }
        bindSpinnerDropDown(heightSpinner, heightNumberList);

        ArrayList<StringWithTag> bloodGroupList = new ArrayList<StringWithTag>();
        bloodGroupList.add(new StringWithTag("Select", ""));

        bindSpinnerDropDown(bloodGroupSpinner, bloodGroupList);

        bloodGroupArrow = (ImageView) findViewById(R.id.bloodGroupArrow);
        bloodGroupLockIcon = (ImageView) findViewById(R.id.bloodGroupLockIcon);
        bloodGroupLockIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bloodGroupLockStatus = false;
                openBloodChangeGetOTPDialog();
            }
        });

        vitalsTableScrollView = (ScrollView) findViewById(R.id.vitalsTableScrollView);
        vitalsTableScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                LinearLayout view = (LinearLayout) vitalsTableScrollView.getChildAt(0);
                if (view.getChildCount() > 1) {
                    int diff = (view.getBottom() - (vitalsTableScrollView.getHeight() + vitalsTableScrollView.getScrollY()));
                    if (diff < 0 && _vitalsLoadMoreStatus) {
                        _vitalsLoadMoreStatus = false;
                        fetchPatientBodyVitals(true);
                    }
                }

            }
        });
        loadBloodGroups();
        fetchPatientBodyVitals(false);

//        heightTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                heightSpinner.setVisibility(View.VISIBLE);
//                heightTxt.setVisibility(View.GONE);
//                heightSpinner.performClick();
//            }
//        });

        addNewVitalsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddBodyVitalsDialog();
            }
        });


        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!onFirstClickOfVitalSpinnerHeight) {
//                    heightSpinner.setVisibility(View.GONE);
//                    heightTxt.setVisibility(View.VISIBLE);
                }
                onFirstClickOfVitalSpinnerHeight = false;
                heightTxt.setText(SpinnerDropDown.getSpinnerItem(heightSpinner) + " cm");

                if (!isPageLoadHieghtChange) {
                    updatePatientHeight();
                }
                isPageLoadHieghtChange = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
//                heightSpinner.setVisibility(View.GONE);
//                heightTxt.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindBloodGroupEvents() {
        bindSpinnerDropDown(bloodGroupSpinner, bloodGroupList);
        bloodGroupSpinner.setEnabled(false);
        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parant, View v, int pos, long id) {
                StringWithTag s = (StringWithTag) parant.getItemAtPosition(pos);
                Object tag = s.tag;
                String string = s.string;
                blood_group_id = tag.toString();
                if (!isPageLoadBloodGroupChange) {
                    updatePatientBloodGroup();
                }
                isPageLoadBloodGroupChange = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        bloodGroupSpinner.setEnabled(true);
    }

    private void bindOTPFormClickEvents(EditText _input_listener, final EditText
            _next_focusable, final EditText _prev_focusable) {
        _input_listener.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_next_focusable != null) {
                    _next_focusable.requestFocus();
                }
                checkOTP();
            }
        });
        _input_listener.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (_prev_focusable != null) {
                        _prev_focusable.requestFocus();
                    }
                }
                return false;
            }
        });
    }

    private void openBloodChangeGetOTPDialog() {
        getOTPDialog = new Dialog(PatientHistory.this);
        getOTPDialog.setContentView(R.layout.get_otp_popup);
        getOTPDialog.show();

        TextView getOtp = (TextView) getOTPDialog.findViewById(R.id.getOtp);
        TextView number_txt = (TextView) getOTPDialog.findViewById(R.id.number_txt);

        number_txt.setText(mobile_str);

        getOtp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getOTPDialog.dismiss();
                getOTP();
            }
        });
    }

    private void getOTP() {
        //Loader
        progress.setMessage("Sending OTP...");

        Boolean _error = false;


        if (mobile_str.isEmpty()) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please enter contact number!", Toast.LENGTH_LONG).show();
        }

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("doc_id", med_user_id);
                _obj.put("mobile", mobile_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientGetOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {

                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            _otpID = _data.getString("id");
                            Toast.makeText(PatientHistory.this, "OTP has been sent to your mobile.", Toast.LENGTH_LONG).show();
                            if (getOTPDialog != null) {
                                getOTPDialog.dismiss();
                            }
                            openOTPInputDialog();
                        } else {
                            Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void checkOTP() {
        //Loader
        progress.setMessage("Verifying OTP...");

        Boolean _error = false;

        String _otp_str = otp_input_one.getText().toString() + otp_input_two.getText().toString() + otp_input_three.getText().toString() + otp_input_four.getText().toString();

        if (validateEditTextInput(otp_input_one)) {
            _error = true;
        }
        if (validateEditTextInput(otp_input_two)) {
            _error = true;
        }
        if (validateEditTextInput(otp_input_three)) {
            _error = true;
        }
        if (validateEditTextInput(otp_input_four)) {
            _error = true;
        }

        if (!_error) {
            progress.show();
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("id", _otpID);
                _obj.put("otp", _otp_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientCheckOTP, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    Log.i("checkOTP", response.toString());
                    progress.hide();
                    otpInputDialog.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            JSONObject _data = (JSONObject) response.getJSONObject("data");
                            String otp_code = _data.getString("code");
                            if (otp_code.equals("200")) {
                                if (!bloodGroupLockStatus) {
                                    bloodGroupSpinner.setEnabled(true);
                                    bloodGroupLockIcon.setVisibility(View.GONE);
                                    bloodGroupArrow.setVisibility(View.VISIBLE);
                                }

                                is_doc_can_edit_pres = true;
                                follow_up_status = true;
                                updateVerifiedPatient();

                                Toast.makeText(PatientHistory.this, "Mobile number verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PatientHistory.this, "Please enter valid OTP number!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private Boolean validateEditTextInput(EditText _view) {
        String _value = _view.getText().toString();
        if (_value != null && !_value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    private void fetchPatientBodyVitals(Boolean loadMore) {
        progress.setTitle("Loading...");
        progress.show();
        JSONObject _vitals_obj = new JSONObject();
        try {
            JSONObject _em_s = new JSONObject();
            _vitals_obj.put("limit", 10);
            _vitals_obj.put("doc_id", med_user_id);
            _vitals_obj.put("patient_id", patient_id);

            if (loadMore) {
                vital_page_load_num = vital_page_load_num + 1;
                _vitals_obj.put("page", vital_page_load_num);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReqPatBodyVitals = new JsonObjectRequest(Request.Method.POST,
                API_URL.DocPatientVitalsSearch, _vitals_obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                try {
                    if (!response.isNull("data")) {
                        JSONObject dataObj = response.getJSONObject("data");
                        JSONArray feedArray = dataObj.getJSONArray("result");
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);
                            createVitalsRow(feedObj, false);
                        }

                        if (dataObj.getString("pages").equals(String.valueOf(vital_page_load_num))) {
                            _vitalsLoadMoreStatus = false;
                        } else {
                            _vitalsLoadMoreStatus = true;
                        }

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
        mRequestQueue.add(jsonReqPatBodyVitals);
    }

    private void createVitalsRow(JSONObject vitalObj, Boolean prepend) {
        if (vitalObj != null) {
            try {
                LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
                View preview = layoutInflater.inflate(R.layout.body_vitals_row, null);

                TextView weightTxt = (TextView) preview.findViewById(R.id.weightTxt);
                TextView vitalDateTxt = (TextView) preview.findViewById(R.id.vitalDateTxt);
                TextView bpTxt = (TextView) preview.findViewById(R.id.bpTxt);
                TextView temperatureTxt = (TextView) preview.findViewById(R.id.temperatureTxt);
                TextView pulseTxt = (TextView) preview.findViewById(R.id.pulseTxt);

                TextView bpStageTxt = (TextView) preview.findViewById(R.id.bpStageTxt);
                TextView bpStageDot = (TextView) preview.findViewById(R.id.bpStageDot);
                TextView feverTxt = (TextView) preview.findViewById(R.id.feverTxt);
                TextView feverDot = (TextView) preview.findViewById(R.id.feverDot);
                TextView weightUnitTxt = (TextView) preview.findViewById(R.id.weightUnitTxt);
                TextView degree_unit_txt = (TextView) preview.findViewById(R.id.degree_unit_txt);

                Boolean bp_normal_status = false,
                        temp_normal_status = false;

                String _bpStr = "";
                pulseTxt.setText(removeNull(vitalObj.getString("pulse")));
                preview.setTag(vitalObj.getString("id"));


                if (!vitalObj.isNull("weight") && !vitalObj.getString("weight").equals("0")) {
                    weightTxt.setText(removeNull(vitalObj.getString("weight")));
                } else {
                    weightTxt.setText("--");
                }

                if (!vitalObj.isNull("pulse") && !vitalObj.getString("pulse").equals("0")) {
                    pulseTxt.setText(removeNull(vitalObj.getString("pulse")));
                } else {
                    pulseTxt.setText("--");
                }

                if (vitalObj.isNull("bp_high") || vitalObj.getString("bp_high").equals("0")) {
                    _bpStr = "--/";
                    bpStageTxt.setVisibility(View.GONE);
                    bpStageDot.setVisibility(View.GONE);
                } else {
                    _bpStr = vitalObj.getString("bp_high") + "/";
                }

                if (vitalObj.isNull("bp_low") || vitalObj.getString("bp_low").equals("0")) {
                    _bpStr = _bpStr + "--";
                    bpStageTxt.setVisibility(View.GONE);
                    bpStageDot.setVisibility(View.GONE);
                } else {
                    _bpStr = _bpStr + vitalObj.getString("bp_low");
                }
                bpTxt.setText(_bpStr);

                if (!vitalObj.isNull("date")) {
                    vitalDateTxt.setText(CustomDateToString.month(vitalObj.getString("date")));
                }

                if (!vitalObj.isNull("bp_high") && !vitalObj.isNull("bp_low")) {
                    int high_bp = Integer.parseInt(vitalObj.getString("bp_high"));
                    int low_bp = Integer.parseInt(vitalObj.getString("bp_low"));

                    if ((high_bp < 120 && high_bp > 0) && (low_bp < 80 && low_bp > 0)) {
                        bpStageTxt.setText("Normal");
                        bpStageDot.setBackground(getDrawable(R.drawable.vital_green_dot));
                        bp_normal_status = true;
                    } else if ((high_bp >= 120 && high_bp <= 129) && low_bp < 80) {
                        bpStageTxt.setText("Elevated BP");
                        bpStageDot.setBackground(getDrawable(R.drawable.vital_red_dot));
                    } else if ((high_bp >= 130 && high_bp <= 139) || (low_bp >= 80 && low_bp <= 89)) {
                        bpStageTxt.setText("High BP Stage 1");
                        bpStageDot.setBackground(getDrawable(R.drawable.vital_red_dot));
                    } else if ((high_bp >= 140 && high_bp < 180) || (low_bp >= 90 && low_bp < 120)) {
                        bpStageTxt.setText("High BP Stage 2");
                        bpStageDot.setBackground(getDrawable(R.drawable.vital_red_dot));
                    } else if ((high_bp >= 180) || (low_bp >= 120)) {
                        bpStageTxt.setText("Seek emergency care");
                        bpStageDot.setBackground(getDrawable(R.drawable.vital_red_dot));
                    }
                }

                if (!vitalObj.isNull("weight_unit")) {
                    String weightUnitStr = vitalObj.getString("weight_unit");
                    if (!weightUnitStr.isEmpty() && !weightUnitStr.equals("0")) {
                        weightUnitTxt.setText(weightUnitStr);
                    }
                }

                String degreeUnitStr = vitalObj.getString("temperature_unit");
                if (!degreeUnitStr.isEmpty() && !degreeUnitStr.equals("0")) {
                    degree_unit_txt.setText(degreeUnitStr + " º");

                    if (degreeUnitStr.equals("F")) {
                        if (!vitalObj.isNull("temperature")) {
                            int temperature = Integer.parseInt(vitalObj.getString("temperature"));

                            if (temperature > 0 && temperature <= 95) {
                                feverTxt.setText("Low fever");
                                feverTxt.setVisibility(View.VISIBLE);
                                feverDot.setVisibility(View.VISIBLE);
                            } else if ((temperature >= 96.8) && (temperature <= 99.5)) {
                                if (!bp_normal_status) {
                                    feverTxt.setText("Normal");
                                    feverTxt.setVisibility(View.VISIBLE);
                                    feverDot.setVisibility(View.VISIBLE);
                                    feverDot.setBackground(getDrawable(R.drawable.vital_green_dot));
                                } else {
                                    feverTxt.setText("");
                                    feverTxt.setVisibility(View.GONE);
                                    feverDot.setVisibility(View.GONE);
                                }
                                temp_normal_status = true;
                            } else if ((temperature >= 99.5) && (temperature < 104)) {
                                feverTxt.setText("High fever");
                                feverTxt.setVisibility(View.VISIBLE);
                                feverDot.setVisibility(View.VISIBLE);
                            } else if ((temperature >= 104)) {
                                feverTxt.setText("Very high fever");
                                feverTxt.setVisibility(View.VISIBLE);
                                feverDot.setVisibility(View.VISIBLE);
                            }

                            if (temperature != 0) {
                                temperatureTxt.setText(String.valueOf(temperature));
                            } else {
                                temperatureTxt.setText("--");
                                temp_normal_status = true;
                            }
                        }
                    } else {
                        if (!vitalObj.isNull("temperature")) {
                            int temperature = Integer.parseInt(vitalObj.getString("temperature"));

                            if (temperature > 0 && temperature <= 35) {
                                feverTxt.setText("Low fever");
                                feverTxt.setVisibility(View.VISIBLE);
                                feverDot.setVisibility(View.VISIBLE);
                            } else if ((temperature >= 36) && (temperature <= 37.5)) {
                                if (!bp_normal_status) {
                                    feverTxt.setText("Normal");
                                    feverTxt.setVisibility(View.VISIBLE);
                                    feverDot.setVisibility(View.VISIBLE);
                                    feverDot.setBackground(getDrawable(R.drawable.vital_green_dot));
                                } else {
                                    feverTxt.setText("");
                                    feverTxt.setVisibility(View.GONE);
                                    feverDot.setVisibility(View.GONE);
                                }
                                temp_normal_status = true;
                            } else if ((temperature >= 37.5) && (temperature < 40)) {
                                feverTxt.setText("High fever");
                                feverTxt.setVisibility(View.VISIBLE);
                                feverDot.setVisibility(View.VISIBLE);
                            } else if ((temperature >= 40)) {
                                feverTxt.setText("Very high fever");
                                feverTxt.setVisibility(View.VISIBLE);
                                feverDot.setVisibility(View.VISIBLE);
                            }

                            if (temperature != 0) {
                                temperatureTxt.setText(String.valueOf(temperature));
                            } else {
                                temperatureTxt.setText("--");
                                temp_normal_status = true;
                            }
                        }
                    }
                }

                if (bp_normal_status && !temp_normal_status) {
                    bpStageTxt.setVisibility(View.GONE);
                    bpStageDot.setVisibility(View.GONE);
                }


                if (prepend != null && prepend) {
                    vitalsListWrap.addView(preview, 0);
                } else {
                    vitalsListWrap.addView(preview);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePatientBloodGroup() {
        if (blood_group_id != null && !blood_group_id.isEmpty()) {
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("blood_group_id", blood_group_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.Patient + "/" + patient_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {

                            Toast.makeText(PatientHistory.this, "Blood group updated successfully!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("updatePatientBloodGroup", response.toString());
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            mRequestQueue.add(jsonReq);
        }
    }

    private void updatePatientHeight() {
        if (heightSpinner != null && !SpinnerDropDown.getSpinnerItem(heightSpinner).toString().isEmpty()) {
            JSONObject _obj = new JSONObject();
            try {
                _obj.put("height", SpinnerDropDown.getSpinnerItem(heightSpinner).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.Patient + "/" + patient_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    Log.i("updatePatientHeight", response.toString());
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {

                            Toast.makeText(PatientHistory.this, "Patient height updated successfully!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("updatePatientHeight", response.toString());
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            mRequestQueue.add(jsonReq);
        }
    }

    //add body vitals popup section
    public void showAddBodyVitalsDialog() {
        isSIUnit = true;
        View loginView = getLayoutInflater().inflate(R.layout.add_body_vitals_dialog, null);
        addBodyVitalsdialog = new BottomSheetDialog(this);
        addBodyVitalsdialog.setContentView(loginView);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) loginView.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    addBodyVitalsdialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        CGSLinkWrap = (LinearLayout) addBodyVitalsdialog.findViewById(R.id.CGSLinkWrap);
        SILinkWrap = (LinearLayout) addBodyVitalsdialog.findViewById(R.id.SILinkWrap);

        final TextView SITxt = (TextView) addBodyVitalsdialog.findViewById(R.id.SITxt);
        final View SIBarView = (View) addBodyVitalsdialog.findViewById(R.id.SIBarView);

        final TextView CGStxt = (TextView) addBodyVitalsdialog.findViewById(R.id.CGStxt);
        final View CGSBarView = (View) addBodyVitalsdialog.findViewById(R.id.CGSBarView);

        final TextView kg_txt = (TextView) addBodyVitalsdialog.findViewById(R.id.kg_txt);
        final TextView degree_txt = (TextView) addBodyVitalsdialog.findViewById(R.id.degree_txt);

        SILinkWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SITxt.setTextColor(Color.parseColor("#8e63e6"));
                SIBarView.setVisibility(View.VISIBLE);

                CGStxt.setTextColor(Color.parseColor("#afbfc6"));
                CGSBarView.setVisibility(View.GONE);

                kg_txt.setText("kg");
                degree_txt.setText("C º");
                isSIUnit = true;
            }
        });

        CGSLinkWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SITxt.setTextColor(Color.parseColor("#afbfc6"));
                SIBarView.setVisibility(View.GONE);

                CGStxt.setTextColor(Color.parseColor("#8e63e6"));
                CGSBarView.setVisibility(View.VISIBLE);

                kg_txt.setText("Lbs");
                degree_txt.setText("F º");
                isSIUnit = false;
            }
        });

        weightInputSpinner = (Spinner) addBodyVitalsdialog.findViewById(R.id.weightInputSpinner);
        pulseInputSpinner = (Spinner) addBodyVitalsdialog.findViewById(R.id.pulseInputSpinner);
        tempInputSpinner = (Spinner) addBodyVitalsdialog.findViewById(R.id.tempInputSpinner);

        bpHighSpinner = (Spinner) addBodyVitalsdialog.findViewById(R.id.bpHighSpinner);
        bpLowSpinner = (Spinner) addBodyVitalsdialog.findViewById(R.id.bpLowSpinner);


        ArrayList<StringWithTag> weightNumberList = new ArrayList<StringWithTag>();
        weightNumberList.add(new StringWithTag("NA", ""));
        for (int i = 1; i < 200; i++) {
            weightNumberList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }
        bindSpinnerDropDown(weightInputSpinner, weightNumberList);

        ArrayList<StringWithTag> pulseNumberList = new ArrayList<StringWithTag>();
        pulseNumberList.add(new StringWithTag("NA", ""));
        for (int i = 30; i < 200; i++) {
            pulseNumberList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }
        bindSpinnerDropDown(pulseInputSpinner, pulseNumberList);

        ArrayList<StringWithTag> temperatureNumberList = new ArrayList<StringWithTag>();
        temperatureNumberList.add(new StringWithTag("NA", ""));
        for (int i = 10; i <= 150; i++) {
            temperatureNumberList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }
        bindSpinnerDropDown(tempInputSpinner, temperatureNumberList);

        ArrayList<StringWithTag> bpHighNumberList = new ArrayList<StringWithTag>();
        bpHighNumberList.add(new StringWithTag("NA", ""));
        for (int i = 50; i < 250; i++) {
            bpHighNumberList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }
        bindSpinnerDropDown(bpHighSpinner, bpHighNumberList);

        ArrayList<StringWithTag> bpLowNumberList = new ArrayList<StringWithTag>();
        bpLowNumberList.add(new StringWithTag("NA", ""));
        for (int i = 40; i < 100; i++) {
            bpLowNumberList.add(new StringWithTag(String.valueOf(i), String.valueOf(i)));
        }
        bindSpinnerDropDown(bpLowSpinner, bpHighNumberList);

        addBodyVitalsdialog.show();
    }


    public void postBodyVitals(View v) {
        progress.setTitle("Saving...");
        progress.show();

        String weightInput, temperatureInput, pulseInput, bpStr, bpHighStr, bpLowStr;
        weightInput = SpinnerDropDown.getSpinnerItem(weightInputSpinner);
        temperatureInput = SpinnerDropDown.getSpinnerItem(tempInputSpinner);
        pulseInput = SpinnerDropDown.getSpinnerItem(pulseInputSpinner);

        bpHighStr = SpinnerDropDown.getSpinnerItem(bpHighSpinner);
        bpLowStr = SpinnerDropDown.getSpinnerItem(bpLowSpinner);

        Calendar vitalsCal = Calendar.getInstance();
        int year = vitalsCal.get(Calendar.YEAR);
        int month = vitalsCal.get(Calendar.MONTH);
        int day = vitalsCal.get(Calendar.DAY_OF_MONTH);
        String currDate = String.format("%02d-%02d-%d", day, (month + 1), year);
        String currTime = "";

        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date currTempTime = Calendar.getInstance().getTime();
        currTime = dateFormat.format(currTempTime);

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_id", patient_id);
            _obj.put("doc_id", med_user_id);
            _obj.put("weight", weightInput);
            _obj.put("temperature", temperatureInput);
            _obj.put("pulse", pulseInput);
            _obj.put("date", currDate);
            _obj.put("time", currTime);
            _obj.put("bp_high", bpHighStr);
            _obj.put("bp_low", bpLowStr);
            _obj.put("height", SpinnerDropDown.getSpinnerItem(heightSpinner));

            if (isSIUnit) {
                _obj.put("weight_unit", "Kg");
                _obj.put("temperature_unit", "C");
            } else {
                _obj.put("weight_unit", "Lbs");
                _obj.put("temperature_unit", "F");
            }

            if (!weightInput.isEmpty() && heightInput != null && !heightInput.isEmpty()) {
                float _weight = Integer.parseInt(weightInput);
                float _height = Integer.parseInt(heightInput);
                float _bmi = 0;

                if (_height != 0 && _weight != 0) {
                    _bmi = (_weight / _height / _height) * 10000;
                }
                _obj.put("bmi", String.format("%.2f", _bmi));

                //BMI formula
                //bmi=[weight (kg) / height (cm) / height (cm)] x 10,000
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("_obj", _obj.toString());
        if (!weightInput.isEmpty() || !bpHighStr.isEmpty() || !bpLowStr.isEmpty() || !temperatureInput.isEmpty()) {
            JsonObjectRequest jsonReqPatBodyVitals = new JsonObjectRequest(Request.Method.POST,
                    API_URL.PatientVitals, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();
                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            if (!response.isNull("data")) {
                                Toast.makeText(PatientHistory.this, "Data saved successfully.", Toast.LENGTH_LONG).show();
                                addBodyVitalsdialog.hide();
                                createVitalsRow(response.getJSONObject("data"), true);
                            }
                        } else {
                            Toast.makeText(PatientHistory.this, "Something went wrong!.", Toast.LENGTH_LONG).show();
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
            mRequestQueue.add(jsonReqPatBodyVitals);
        } else {
            Toast.makeText(PatientHistory.this, "Please select body vitals!", Toast.LENGTH_LONG).show();
            progress.dismiss();
        }
    }
    //Body vitals functions end <-----------------------------------------------------------------------


    //Appointment functions star ----------------------------------------------------------------------->
    private void initAppointmentEvents() {
        topEditProfileSettingPopup = (FrameLayout) findViewById(R.id.topEditProfileSettingPopup);
        editProfileTxtBtn = (TextView) findViewById(R.id.editProfileTxtBtn);
        userProfilePicWrap = (ConstraintLayout) findViewById(R.id.userProfilePicWrap);

        editProfileTxtBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                showEditPatientProfileDialog();

                topEditProfileSettingPopup.setVisibility(View.GONE);
            }
        });

        if (appointment_id != null && !appointment_id.isEmpty()) {
            userNameTxt.setVisibility(View.GONE);
            userProfilePicWrap.setVisibility(View.VISIBLE);
        }
    }

    private void openEditProfileSettingPopup() {
        if (topEditProfileSettingPopup.getVisibility() == View.GONE) {
            topEditProfileSettingPopup.setVisibility(View.VISIBLE);
        } else {
            topEditProfileSettingPopup.setVisibility(View.GONE);
        }
    }

    public void openAppointmentDetailDialog(View v) {
        Dialog dialog = new Dialog(PatientHistory.this);
        dialog.setContentView(R.layout.appointment_view_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView undocancle_txt = (TextView) dialog.findViewById(R.id.undocancle_txt);
        TextView cancel_txt = (TextView) dialog.findViewById(R.id.cancel_txt);
        undocancle_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                AlertDialog dialog = new AlertDialog.Builder(PatientHistory.this)
                        .setTitle("Cancel")
                        .setMessage("Undo Cancel the appointment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //calcle appoinement
                                JSONObject _jsonObj = new JSONObject();
                                try {
                                    _jsonObj.put("status", "pending");

                                    Log.i("_jsonObj", _jsonObj.toString());
                                    updateAppointment(_jsonObj, appointment_id);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });


        if (appointStatus == true) {
            undocancle_txt.setVisibility(View.VISIBLE);
            cancel_txt.setVisibility(View.GONE);
        }

        TextView reschedule_txt = (TextView) dialog.findViewById(R.id.reschedule_txt);
        reschedule_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = new Intent(getApplicationContext(), AppointmentReschedule.class);
                intent.putExtra("appointment_id", appointment_id);
                intent.putExtra("patient_id", patient_id);
                startActivity(intent);

            }
        });

        TextView cancle_appoint_txt = (TextView) dialog.findViewById(R.id.cancel_txt);
        cancle_appoint_txt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                AlertDialog dialog = new AlertDialog.Builder(PatientHistory.this)
                        .setTitle("Cancel")
                        .setMessage("Are you sure you want to Cancel the appointment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //calcle appoinement
                                JSONObject _jsonObj = new JSONObject();
                                try {
                                    _jsonObj.put("status", "cancelled");

                                    Log.i("_jsonObj", _jsonObj.toString());
                                    updateAppointment(_jsonObj, appointment_id);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        TextView invoice_txt = (TextView) dialog.findViewById(R.id.invoice_txt);
        invoice_txt.setPaintFlags(invoice_txt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        invoice_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), Invoice_One.class);
                startActivity(intent);
            }
        });


        TextView follow_up_txt = (TextView) dialog.findViewById(R.id.follow_up_txt);
        follow_up_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (follow_up_status) {
                    openFollowUpDialog(null);
                } else {
                    openGetOtpDialog();
                }
            }
        });

        dialog.show();
    }

//    public void undocancle(String status) {
//        TextView undocancle_txt = (TextView) findViewById(R.id.undocancle_txt);
//        if (this.status == "cancelled") {
//            undocancle_txt.setVisibility(View.VISIBLE);
//        }
//    }

    public void openFollowUpDialog(View v) {
        final BottomSheetDialog dialog = new BottomSheetDialog(PatientHistory.this);
        dialog.setContentView(R.layout.followup_popup);
        selectRadioBtn = (RadioGroup) dialog.findViewById(R.id.radioSelect);
        defaultRadioBtn = (RadioButton) dialog.findViewById(R.id.defaultRadioBtn);
        customRadioBtn = (RadioButton) dialog.findViewById(R.id.customRadioBtn);
        default_sec = (ConstraintLayout) dialog.findViewById(R.id.default_sec);
        custom_sec = (ConstraintLayout) dialog.findViewById(R.id.custom_sec);
        sendBtn = (ImageView) dialog.findViewById(R.id.sendBtn);
        default_followup_txt = (TextView) dialog.findViewById(R.id.default_followup_txt);
        final ImageView sendBtnGray = (ImageView) dialog.findViewById(R.id.sendGray);
        final ImageView sendBtnpurple = (ImageView) dialog.findViewById(R.id.sendBtn2);
        final EditText customEdit = (EditText) dialog.findViewById(R.id.custom_feedback_txt);

        default_txt = "Friendly reminder. You have an appointment with Dr. " + docNameTxt_str + " at " + appointmentTime_str + " of " + appointmentDate_str + " .Call him at +91 " + docmobile_str + " if you have any questions.";
        default_followup_txt.setText(default_txt);
        selectRadioBtn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (defaultRadioBtn.getId() == checkedId) {
                    // do something
                    default_sec.setVisibility(View.VISIBLE);
                    custom_sec.setVisibility(View.GONE);
                } else if (customRadioBtn.getId() == checkedId) {
                    // do something
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
                if (customEdit.getText().toString().length() >= 1) {
                    sendBtnpurple.setVisibility(View.VISIBLE);
                    sendBtnGray.setVisibility(View.GONE);
                } else {
                    sendBtnpurple.setVisibility(View.GONE);
                    sendBtnGray.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        customEdit.addTextChangedListener(_df);


        sendBtnpurple.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                //post SMS
                JSONObject _jsonObj = new JSONObject();


                try {
                    _jsonObj.put("message", customEdit.getText().toString());
                    _jsonObj.put("doc_id", med_user_id);
                    _jsonObj.put("patient_id", patient_id);


                    Log.i("_jsonObj", _jsonObj.toString());
                    postSms(_jsonObj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {


                //post SMS
                JSONObject _jsonObj = new JSONObject();

                String message = "Friendly reminder. You have an appointment with Dr. " + docNameTxt_str + " at " + appointmentTime_str + " of " + appointmentDate_str + " .Call him at +91 " + docmobile_str + " if you have any questions.";
                try {
                    _jsonObj.put("message", message);
                    _jsonObj.put("doc_id", med_user_id);
                    _jsonObj.put("patient_id", patient_id);


                    Log.i("_jsonObj", _jsonObj.toString());
                    postSms(_jsonObj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
    }


    private void postSms(JSONObject _jsonObj) {

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientFollowUpSmsSend, _jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("response.toString()", response.toString());

                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    private void updateAppointment(JSONObject _jsonObj, final String appointment_id) {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.PatientAppointment + "/" + appointment_id, _jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                Log.i("response.toString()", response.toString());

                Intent intent = new Intent(getApplicationContext(), AppointmentList.class);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    //Appointment functions end <-----------------------------------------------------------------------

    //Patient edit profile start ---------------------------------------------------------------------->
    public void showEditPatientProfileDialog() {
        if (otpInputDialog != null) {
            otpInputDialog.dismiss();
        }
        isEditPatientDialog = true;
        View preview = getLayoutInflater().inflate(R.layout.patient_register_dialog, null);
        editProfileDialog = new BottomSheetDialog(this);
        editProfileDialog.setContentView(preview);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) preview.getParent());
        mBehavior.setPeekHeight(realHeight);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    editProfileDialog.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        editProfileDialog.show();

        regTabLink = preview.findViewById(R.id.regTabLink);
        regTabLinkBr = preview.findViewById(R.id.regTabLinkBr);

        addPhotoTabLink = preview.findViewById(R.id.addPhotoTabLink);
        addPhotoTabLinkBar = preview.findViewById(R.id.addPhotoTabLinkBar);

        next_btn_wrap = preview.findViewById(R.id.next_btn_wrap);
        update_next_btn_wrap = preview.findViewById(R.id.update_next_btn_wrap);
        patientBasicDetailWrap = preview.findViewById(R.id.patientBasicDetailWrap);
        addPhotoWrap = preview.findViewById(R.id.addPhotoWrap);


        next_btn_wrap.setVisibility(View.GONE);
        update_next_btn_wrap.setVisibility(View.VISIBLE);

        //tab links
        regTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTabLinkBr.setVisibility(View.VISIBLE);
                regTabLink.setImageDrawable(getDrawable(R.drawable.ic_edit_purple_icon));

                addPhotoTabLink.setImageDrawable(getDrawable(R.drawable.facial_recognition_tab_link));
                addPhotoTabLinkBar.setVisibility(View.GONE);

                patientBasicDetailWrap.setVisibility(View.VISIBLE);
                addPhotoWrap.setVisibility(View.GONE);
                update_next_btn_wrap.setVisibility(View.VISIBLE);
            }
        });


        addPhotoTabLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regTabLinkBr.setVisibility(View.GONE);
                regTabLink.setImageDrawable(getDrawable(R.drawable.ic_edit_gray_icon));

                addPhotoTabLink.setImageDrawable(getDrawable(R.drawable.facial_recognition_purple_tab_link));
                addPhotoTabLinkBar.setVisibility(View.VISIBLE);

                patientBasicDetailWrap.setVisibility(View.GONE);
                addPhotoWrap.setVisibility(View.VISIBLE);

                update_next_btn_wrap.setVisibility(View.GONE);
            }
        });


        //edit-profile --------------------------------------------------------------------------------
        genderSpinner = preview.findViewById(R.id.genderSpinner);
        name_edit = preview.findViewById(R.id.name_edit);
        dob_edit = preview.findViewById(R.id.dob_edit);
        num_edit = preview.findViewById(R.id.num_edit);

        otp_input_one = (EditText) preview.findViewById(R.id.otpInputOne);
        otp_input_two = (EditText) preview.findViewById(R.id.otpInputTwo);
        otp_input_three = (EditText) preview.findViewById(R.id.otpInputThree);
        otp_input_four = (EditText) preview.findViewById(R.id.otpInputFour);

        bindOTPFormClickEvents(otp_input_one, otp_input_two, null);
        bindOTPFormClickEvents(otp_input_two, otp_input_three, otp_input_one);
        bindOTPFormClickEvents(otp_input_three, otp_input_four, otp_input_two);
        bindOTPFormClickEvents(otp_input_four, null, otp_input_three);

        updateBtn = preview.findViewById(R.id.updateBtn);
        doneBtn = preview.findViewById(R.id.doneBtn);
        getOtpBtn = preview.findViewById(R.id.getOtpBtn);
        otpDisabledBtn = preview.findViewById(R.id.otpDisabledBtn);
        resendOtpBtn = preview.findViewById(R.id.resendOtpBtn);

        countDownTxt = preview.findViewById(R.id.countDownTxt);

        otpInputWrap = preview.findViewById(R.id.otpInputWrap);
        getOtpBtnWrap = preview.findViewById(R.id.getOtpBtnWrap);
        otpVerifyTxtWrap = preview.findViewById(R.id.otpVerifyTxtWrap);

        doneBtn = preview.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileDialog.hide();
            }
        });

        bindGenderDropDown(genderSpinner);

        fetchEditPatientDetails();

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTP();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatientDetails();
            }
        });

        dob_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePickerDialog();
            }
        });
        initAddPhotoEvents(preview);
    }


    private void bindGenderDropDown(Spinner spinner) {
        ArrayList<StringWithTag> monthList = new ArrayList<StringWithTag>();
        monthList.add(new StringWithTag("Select", ""));
        monthList.add(new StringWithTag("Male", "male"));
        monthList.add(new StringWithTag("Female", "female"));

        ArrayAdapter<StringWithTag> spinnerArrayAdapter = new ArrayAdapter<StringWithTag>(PatientHistory.this, android.R.layout.simple_spinner_item, monthList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private Boolean validatePatientDetails() {
        Boolean _error = false;
        if (validateEditTextInput(name_edit)) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please enter name!", Toast.LENGTH_LONG).show();
        } else if (validateEditTextInput(dob_edit)) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please enter your date of birth!", Toast.LENGTH_LONG).show();
        } else if (SpinnerDropDown.getSpinnerItem(genderSpinner).isEmpty()) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please select gender!", Toast.LENGTH_LONG).show();
        } else if (num_edit.getText().length() < 10) {
            _error = true;
            Toast.makeText(PatientHistory.this, "Please enter valid contact number!", Toast.LENGTH_LONG).show();
        }
        return _error;
    }

    private void updatePatientDetails() {
        if (!validatePatientDetails()) {
            progress.show();
            progress.setMessage("Verifying...");

            JSONObject _obj = new JSONObject();
            try {
                String name_str = name_edit.getText().toString();
                _obj.put("name", name_str);
                _obj.put("dob", tempDateStr);
                _obj.put("age", ageStr);
                _obj.put("gender", SpinnerDropDown.getSpinnerItem(genderSpinner));
                _obj.put("mobile", num_edit.getText());
                _obj.put("doc_id", med_user_id);

                if (_otpID != null) {
                    _obj.put("mobile_otp_id", _otpID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                    API_URL.Patient + "/" + patient_id, _obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    progress.dismiss();

                    try {
                        boolean _error = Boolean.parseBoolean(response.getString("error"));
                        if (!_error) {
                            editProfileDialog.hide();
                            _otpID = null;

                        } else {
                            Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    private void openDatePickerDialog() {
        final Dialog dialog = new Dialog(PatientHistory.this);
        dialog.setContentView(R.layout.dob_select_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        TextView doneBtn = (TextView) dialog.findViewById(R.id.doneBtn);


        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String _dateStr = datePicker.getDayOfMonth() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getYear();
                dob_edit.setText(trimDateYear(_dateStr));
                tempDateStr = _dateStr;
                getAge(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();


        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            age = 0;
        }

        String ageS = String.valueOf(age);
        ageStr = ageS;
    }

    private void initAddPhotoEvents(View preview) {
        previewImgSec = (LinearLayout) preview.findViewById(R.id.previewImgSec);
        camPreview1 = (ImageView) preview.findViewById(R.id.patientImg1);
        camPreview2 = (ImageView) preview.findViewById(R.id.patientImg2);
        camPreview3 = (ImageView) preview.findViewById(R.id.patientImg3);
        reloadSec = (ImageView) preview.findViewById(R.id.cameraIcon);
        uploadSec = (ImageView) preview.findViewById(R.id.uploadSec);
        changeCam = (ImageView) preview.findViewById(R.id.changeCam);

        //Menu links sec end
        myWebView = (WebView) preview.findViewById(R.id.cameraSec);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        myWebView.addJavascriptInterface(new PatientHistory.WebAppInterface(this), "Android");
        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setWebChromeClient(new WebChromeClient() {
            // Grant permissions for cam
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                mPermissionRequest = request;
                final String[] requestedResources = request.getResources();
                for (String r : requestedResources) {
                    if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        mPermissionRequest.grant(new String[]{PermissionRequest.RESOURCE_VIDEO_CAPTURE, PermissionRequest.RESOURCE_AUDIO_CAPTURE});
                    }
                }
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
                Toast.makeText(PatientHistory.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        });
        myWebView.loadUrl(API_URL.AddFaceScanMobile);

        changeCam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                myWebView.loadUrl("javascript:(function(){changeCam();})()");
            }
        });

        reloadSec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!reloading) {
                    reloading = true;
                    reloadSec.setAlpha(0.5f);
                    uploadSec.setAlpha(0.5f);
                    camPreview1_status = false;
                    camPreview2_status = false;
                    camPreview3_status = false;
                    myWebView.loadUrl("javascript:(function(){startScanAgain();})()");
                }
            }
        });

        uploadSec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (camPreview1_status && camPreview2_status && camPreview3_status) {
                    uploadFace(profilePicData);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    // Shows the system bars by removing all the flags
    //except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public class WebAppInterface {
        Context mContext;
        String data;
        Bitmap bitmap;
        InputStream stream;
        String imageDataBytes;

        WebAppInterface(Context ctx) {
            this.mContext = ctx;
        }

        @JavascriptInterface
        public void sendData(String data) {
            //Get the string value to process
            this.data = data;
            if (!camPreview1_status || !camPreview2_status || !camPreview3_status) {
                profilePicData = data;
                imageDataBytes = data.substring(data.indexOf(",") + 1);
                stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));
                bitmap = BitmapFactory.decodeStream(stream);
            }

            if (!camPreview1_status) {
                camPreview1_status = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camPreview1.setImageBitmap(bitmap);
                        camPreview1.setVisibility(View.VISIBLE);
                    }
                });
            }

            if (!camPreview2_status && !camPreview3_status && camPreview1_status) {
                camPreview2_status = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camPreview2.setImageBitmap(bitmap);
                        camPreview2.setVisibility(View.VISIBLE);
                    }
                });
            }

            if (!camPreview3_status && camPreview2_status && camPreview1_status) {
                camPreview3_status = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camPreview3.setImageBitmap(bitmap);
                        camPreview3.setVisibility(View.VISIBLE);
                        previewImgSec.setVisibility(View.VISIBLE);
                        reloadSec.setAlpha(1f);
                        uploadSec.setAlpha(1f);
                        reloading = false;
                    }
                });
            }
        }
    }

    private void uploadFace(final String encodedImageString) {
        progress = new ProgressDialog(this);
        progress.setTitle("Uploading");
        progress.setMessage("Wait while we upload your photo");
        progress.show();
        JSONObject _em_s = new JSONObject();
        try {
            _em_s.put("src", encodedImageString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.PatientPhotoFileUpload + "/" + patient_id, _em_s, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        Log.i("Suresh", _data.toString());
                        //Update the patient profile photo using the path value got from server
                        updatePatientPhotoPath(_data.getString("path"), encodedImageString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("Photo error : ", error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void updatePatientPhotoPath(String _path, final String encodedImageString) {
        progress.dismiss();
        progress = new ProgressDialog(this);
        progress.setTitle("Uploading");
        progress.setMessage("Wait while we upload your photo");
        progress.show();
        JSONObject _em_s = new JSONObject();
        try {
            _em_s.put("photo", _path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.Patient + "/" + patient_id, _em_s, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progress.dismiss();
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");

                        if (registerNewPatDialog != null) {
                            registerNewPatDialog.hide();
                        }
                        if (editProfileDialog != null) {
                            editProfileDialog.hide();
                        }

                        //Upload Image for training
                        TrainFace(encodedImageString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("Photo error : ", error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void TrainFace(String encodedImageString) {
        JSONObject _em_s = new JSONObject();
        try {
            _em_s.put("image", encodedImageString);
            _em_s.put("name", patient_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                API_URL.FaceTrain, _em_s, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error && !response.isNull("data")) {
                        JSONObject _data = (JSONObject) response.getJSONObject("data");
                        Log.i("TrainFace", _data.toString());
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                Log.i("TrainFace error : ", error.toString());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        mRequestQueue.add(jsonReq);
    }

    private void fetchEditPatientDetails() {
        progress.show();
        progress.setMessage("Loading...");
        Log.i("", API_URL.PatientFetchFull + patient_id);

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                API_URL.PatientFetchFull + patient_id, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                parseJsonEditPatient(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to volley request queue
        jsonReq.setShouldCache(false);
        mRequestQueue.add(jsonReq);
    }

    private void parseJsonEditPatient(JSONObject response) {
        try {
            progress.dismiss();
            if (!response.isNull("data")) {
                JSONObject _data = (JSONObject) response.getJSONObject("data");
                name_edit.setText(removeNull(_data.getString("name")));
                dob_edit.setText(trimDateYear(_data.getString("dob")));
                tempDateStr = removeNull(_data.getString("dob"));
                num_edit.setText(removeNull(_data.getString("mobile")));
                SpinnerDropDown.setSpinnerItem(genderSpinner, _data.getString("gender"));

                bindTextChangeEvents();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String trimDateYear(String _date) {
        if (_date != null && !_date.equals("null") && !_date.isEmpty()) {
            String[] splitDate = _date.split("\\-");
            String subStringYr = splitDate[2].substring(Math.max(splitDate[2].length() - 2, 0));

            return CustomDateToString.month(splitDate[0] + "-" + splitDate[1] + "-" + subStringYr);
        } else {
            return "";
        }
    }

    private void bindTextChangeEvents() {
        _textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateBtn.setVisibility(View.VISIBLE);
                doneBtn.setVisibility(View.GONE);
            }
        };


        name_edit.addTextChangedListener(_textWatcher);
        dob_edit.addTextChangedListener(_textWatcher);


        _num_textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getOtpBtnWrap.setVisibility(View.VISIBLE);
                if (num_edit.getText().toString().length() == 10) {
                    updateBtn.setVisibility(View.VISIBLE);
                    doneBtn.setVisibility(View.GONE);
                    otpVerifyTxtWrap.setVisibility(View.VISIBLE);
                    otpDisabledBtn.setVisibility(View.GONE);
                    getOtpBtn.setVisibility(View.VISIBLE);
                } else {
                    otpDisabledBtn.setVisibility(View.VISIBLE);
                    getOtpBtn.setVisibility(View.GONE);
                }
            }
        };
        num_edit.addTextChangedListener(_num_textWatcher);
    }

    private void updateVerifiedPatient() {
        //Loader

        JSONObject _obj = new JSONObject();
        try {
            _obj.put("patient_mobile_id", _otpID);
            _obj.put("mobile_verification", "YES");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.PUT,
                API_URL.Patient + "/" + patient_id, _obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                progress.dismiss();
                Log.i("checkOTP", response.toString());
                try {
                    boolean _error = Boolean.parseBoolean(response.getString("error"));
                    if (!_error) {

                    } else {
                        Toast.makeText(PatientHistory.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PatientHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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

    //Patient edit profile end <----------------------------------------------------------------------
}