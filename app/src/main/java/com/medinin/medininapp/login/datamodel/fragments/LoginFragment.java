package com.medinin.medininapp.login.datamodel.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.CountDownTimer;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.hbb20.CountryCodePicker;
import com.medinin.medininapp.BaseFragment;
import com.medinin.medininapp.Myapp;
import com.medinin.medininapp.R;
import com.medinin.medininapp.interfaces.TaskCompleteListener;
import com.medinin.medininapp.login.datamodel.CheckOtpRequest;
import com.medinin.medininapp.login.datamodel.CheckOtpResponse;
import com.medinin.medininapp.login.datamodel.GetOtpRequest;
import com.medinin.medininapp.login.datamodel.GetOtpResponse;
import com.medinin.medininapp.login.datamodel.UserSignupResponse;
import com.medinin.medininapp.services.NetworkOperationService;
import com.medinin.medininapp.utils.DLog;
import com.medinin.medininapp.utils.DataParser;
import com.medinin.medininapp.utils.FingerprintHandler;
import com.medinin.medininapp.utils.MedininProgressDialog;
import com.medinin.medininapp.utils.NetworkConfig;
import com.medinin.medininapp.utils.PinEntryEditText;
import com.medinin.medininapp.utils.PrefManager;
import com.medinin.medininapp.utils.UIUtility;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;


public class LoginFragment extends BaseFragment implements TaskCompleteListener {


    private static final String TAG = "LoginFragment";
    @BindView(R.id.mobileNumber)
    EditText etMobileNumber;

    @BindView(R.id.btnGetOtp)
    TextView btnGetOTP;

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.countryCodePicker)
    CountryCodePicker countryCode;

    @BindView(R.id.verifyOTP)
    LinearLayout llverifyotp;

    @BindView(R.id.cancel_txt)
    TextView tvCancel;

    @BindView(R.id.customOTP)
    PinEntryEditText customOTP;

    @BindView(R.id.loginCountDownTxt)
    TextView tvTimer;

    @BindView(R.id.resendOTP)
    TextView tvresend;

    @BindView(R.id.tvVoiceOTP)
    TextView tvVoiceOTP;

    @BindView(R.id.fingerPrintIcon)
    ImageView ivFingerPrint;

    @BindView(R.id.llFingerPrint)
    LinearLayout llFingerPrint;

    @BindView(R.id.fingerPrintIconText)
    TextView tvfingerPrintIconText;

    @BindView(R.id.btnVerifyOtp)
    TextView btnVerifyOtp;

    private Context mcontext;
    private NetworkOperationService mService;
    private CancellationSignal mCancellationSignal;
    private KeyStore keyStore;
    private Cipher cipher;
    private static final String KEY_NAME = "androidHive";
    private PrefManager prefManager = new PrefManager();

    private UserLoginRole userLoginRole;
    private UserDetails userDetails;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        mService = new NetworkOperationService();


        return inflater.inflate(R.layout.fragment_login, container, false);





    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setTaskCompleteListener(mContext, this);
        ivFingerPrint.setImageResource(R.drawable.ic_fingerprint_black_24dp);

        userLoginRole = new UserLoginRole();
        userDetails = new UserDetails();


        if(!prefManager.isFirstTimeLogin()){
            ivFingerPrint.setVisibility(View.GONE);
            tvfingerPrintIconText.setVisibility(View.GONE);
        }else{
            fingerPrintExecution();
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fingerPrintExecution() {
        ivFingerPrint.setVisibility(View.VISIBLE);
        tvfingerPrintIconText.setVisibility(View.VISIBLE);
        mCancellationSignal = new CancellationSignal();

        ivFingerPrint.setImageResource(R.drawable.ic_fingerprint_black_24dp);
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);

        // Check whether the device has a Fingerprint sensor.
        if (!fingerprintManager.isHardwareDetected()) {
            /**
             * An error message will be displayed if the device does not contain the fingerprint hardware.
             * However if you plan to implement a default authentication method,
             * you can redirect the user to a default authentication activity from here.
             * Example:
             * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
             * startActivity(intent);
             */
            // Toast.makeText(mContext, "Your Device does not have a Fingerprint Sensor", Toast.LENGTH_LONG).show();
            ivFingerPrint.setVisibility(View.GONE);
            tvfingerPrintIconText.setVisibility(View.GONE);

            return;
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(mContext, "Register at least one fingerprint in Settings", Toast.LENGTH_LONG).show();
                } else {
                    // Checks whether lock screen security is  enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(mContext, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();

                    } else {
                        generateKey();
                        if (cipherInit()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerprintHandler helper = new FingerprintHandler(mContext);

                            helper.doFingerPrintAuthentication(new FingerprintHandler.AuthenticationStatus() {
                                @Override
                                public void onStatusChange(boolean isSuccess) {

                                    if (isSuccess) {
                                        ivFingerPrint.setImageResource(R.drawable.ic_fingerprint_selected_24dp);
                                        if (Myapp.isNetworkAvailable()) {

                                            UIUtility.showToastMsg_short(getActivity(),"Finger success");

                                            FragmentTransaction trx = getActivity().getSupportFragmentManager().beginTransaction();
                                            trx.hide(LoginFragment.this);
                                            trx.add(R.id.container,userLoginRole);
                                            trx.show(userLoginRole).commit();

//                                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                                            startActivity(intent);
//                                            getActivity().finish();
                                        } else {
                                            //for offline login
                                            UIUtility.showToast_noInternet(mContext);
                                        }

                                    } else {
                                        ivFingerPrint.setImageResource(R.drawable.ic_fingerprint_black_24dp);
                                        Toast.makeText(mContext, "Authentication unsuccessful,Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onStatusmessages(int statusId, String msg) {
                                    DLog.d(TAG, "1111" + statusId);
                                    ivFingerPrint.setImageResource(R.drawable.ic_fingerprint_locked_24dp);
                                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                }
                            }, fingerprintManager, cryptoObject, mCancellationSignal);
                        }
                    }
                }
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    @OnClick({R.id.btnGetOtp, R.id.cancel_txt, R.id.resendOTP, R.id.tvVoiceOTP, R.id.fingerPrintIcon, R.id.btnVerifyOtp})
    void OnClickListener(View view) {
        switch (view.getId()){

            case R.id.btnGetOtp:
                final String CountryCode= countryCode.getSelectedCountryCodeWithPlus().trim();
                final String phoneNumber = etMobileNumber.getText().toString().trim();
                if(CountryCode.length() > 0 && phoneNumber.length() > 0) {
                    if (CountryCode.startsWith("+91") && phoneNumber.length() == 10) {
                        int maxLength = 10;
                        etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    } else if (CountryCode.startsWith("+91") && phoneNumber.length() < 10) {
                        UIUtility.showToastMsg_short(mContext, "Phone number should be 10 digits");
                        return;
                    }
                    MedininProgressDialog.show(getContext(),false);
                    GetOtpRequest getotpRequest = new GetOtpRequest();
                    getotpRequest.setMobile(etMobileNumber.getText().toString());


                    HashMap<String, String> headerMap = new HashMap<>();
                    headerMap.put("Content-Type", "application/json");

                    Intent intent = new Intent(mContext, NetworkOperationService.class);


                    intent.putExtra(NetworkConfig.API_URL, NetworkConfig.SendOTP);
                    intent.putExtra(NetworkConfig.HEADER_MAP, headerMap);
                    if(!prefManager.getMySignUpRole().equalsIgnoreCase("LOGIN")){
                        getotpRequest.setModule(prefManager.getMySignUpRole());
                        intent.putExtra(NetworkConfig.API_URL, NetworkConfig.userSignup);
                    }
                    intent.putExtra(NetworkConfig.INPUT_BODY, getotpRequest);

                    mContext.startService(intent);
                    etMobileNumber.setEnabled(false);
                    btnGetOTP.setEnabled(false);
                }else{
                    UIUtility.showToastMsg_short(mContext, "Invalid phone number");
                }
                break;

            case R.id.cancel_txt:
                etMobileNumber.setText("");
                etMobileNumber.setEnabled(true);
                btnGetOTP.setEnabled(false);
                llverifyotp.setVisibility(View.INVISIBLE);
                break;

            case R.id.resendOTP:
                final String CountryCode_r= countryCode.getSelectedCountryCodeWithPlus().trim();
                final String phoneNumber_r = etMobileNumber.getText().toString().trim();
                if(CountryCode_r.length() > 0 && phoneNumber_r.length() > 0) {
                    if (CountryCode_r.startsWith("+91") && phoneNumber_r.length() == 10) {
                        int maxLength = 10;
                        etMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
                    } else if (CountryCode_r.startsWith("+91") && phoneNumber_r.length() < 10) {
                        UIUtility.showToastMsg_short(mContext, "Phone number should be 10 digits");
                        return;
                    }
                    MedininProgressDialog.show(getContext(),false);
                    GetOtpRequest getotpRequest = new GetOtpRequest();
                    getotpRequest.setMobile(etMobileNumber.getText().toString());
                    if(!prefManager.getMySignUpRole().equalsIgnoreCase("LOGIN")){
                        getotpRequest.setModule(prefManager.getMySignUpRole());
                    }

                    HashMap<String, String> headerMap = new HashMap<>();
                    headerMap.put("Content-Type", "application/json");

                    Intent intent = new Intent(mContext, NetworkOperationService.class);
                    intent.putExtra(NetworkConfig.API_URL, NetworkConfig.SendOTP);
                    intent.putExtra(NetworkConfig.HEADER_MAP, headerMap);

                    if(!prefManager.getMySignUpRole().equalsIgnoreCase("LOGIN")){
                        getotpRequest.setModule(prefManager.getMySignUpRole());
                        intent.putExtra(NetworkConfig.API_URL, NetworkConfig.userSignup);
                    }

                    intent.putExtra(NetworkConfig.INPUT_BODY, getotpRequest);
                    mContext.startService(intent);
                }else{
                    UIUtility.showToastMsg_short(mContext, "Invalid phone number");
                }
                break;

            case R.id.tvVoiceOTP:
                UIUtility.showToastMsg_short(mContext, "Coming Soon !..");
                break;

            case R.id.fingerPrintIcon:
                Toast.makeText(mContext, "Place your finger on fingerprint sensor of the device", Toast.LENGTH_SHORT).show();
                break;


            case R.id.btnVerifyOtp:
                String otp = customOTP.getText().toString();
                if(otp.length()<4){
                    UIUtility.showToastMsg_short(mContext, "Enter the 4 digit OTP !..");
                    return;
                } else if(otp.length() == 4){
                    MedininProgressDialog.show(getContext(),false);
                    CheckOtpRequest checkOtpRequest = new CheckOtpRequest();
                    checkOtpRequest.setMobile(etMobileNumber.getText().toString());
                    checkOtpRequest.setOtp(otp);
                    HashMap<String, String> headerMap = new HashMap<>();
                    headerMap.put("Content-Type", "application/json");

                    Intent intent = new Intent(mContext, NetworkOperationService.class);
                    intent.putExtra(NetworkConfig.API_URL, NetworkConfig.CheckOTP);
                    intent.putExtra(NetworkConfig.HEADER_MAP, headerMap);
                    intent.putExtra(NetworkConfig.INPUT_BODY, checkOtpRequest);
                    mContext.startService(intent);
                }
            default:
                break;



        }

    }

    @Override
    public void onTaskCompleted(Context context, Intent intent) {

        String requestType = intent.getStringExtra(NetworkConfig.REQUEST_TYPE);
        String apiUrl = intent.getStringExtra(NetworkConfig.API_URL);
        String responseString = intent.getStringExtra(NetworkConfig.RESPONSE_BODY);




        if(responseString != null && apiUrl.equals(NetworkConfig.SendOTP)) {
            MedininProgressDialog.dismissDialog();
            GetOtpResponse data = DataParser.parseJson(responseString, GetOtpResponse.class);
            if(data.getStatuscode()==0){
                UIUtility.showToastMsg_short(getActivity(), data.getStatusMessage());
                btnGetOTP.setEnabled(true);
                return;
            }
            llverifyotp.setVisibility(View.VISIBLE);
//            String username=data.getData().getName()==null?"New user":"back "+data.getData().getName().toUpperCase();
//            tvUserName.setText("Welcome "+username+" .. Plz enter the OTP you received");
            setTimer(300);
            return;
        }

        if(responseString != null && apiUrl.equals(NetworkConfig.CheckOTP)) {
            MedininProgressDialog.dismissDialog();

            CheckOtpResponse data = DataParser.parseJson(responseString, CheckOtpResponse.class);
            if(data.getStatuscode()==0) {
                UIUtility.showToastMsg_short(mContext, data.getStatusMessage());
                return;
            }else {
                if(data.getStatuscode() == 1){
                    UIUtility.showToastMsg_short(getActivity(), "OTP Verified !..");
                    prefManager.saveMetaData(data);
                    prefManager.setIsFirstTimeLogin(true);

                    prefManager.setMobilenumber(data.getData().getMobile());
                    prefManager.setApikey(data.getData().getApikey());

                    prefManager.setDocModule(data.getData().getDocStatus().equalsIgnoreCase("1"));
                    prefManager.setClinicModule(data.getData().getClinicStatus().equalsIgnoreCase("1"));
                    prefManager.setPatientModule(data.getData().getPatientStatus().equalsIgnoreCase("1"));
                    prefManager.setLabModule(data.getData().getLabStatus().equalsIgnoreCase("1"));
                    prefManager.setHospitalModule(data.getData().getHospitalStatus().equalsIgnoreCase("1"));




                    if(prefManager.getMySignUpRole().equalsIgnoreCase("LOGIN")){
                        FragmentTransaction trx = getActivity().getSupportFragmentManager().beginTransaction();
                        trx.hide(LoginFragment.this);
                        trx.add(R.id.container,userLoginRole);
                        trx.show(userLoginRole).commit();
                    } else {
                        FragmentTransaction trx = getActivity().getSupportFragmentManager().beginTransaction();
                        trx.hide(LoginFragment.this);
                        trx.add(R.id.container,userDetails);
                        trx.show(userDetails).commit();
                    }


//                    Intent mainActivity = new Intent(getActivity(), MainActivity.class);
//                    startActivity(mainActivity);
//                    getActivity().finish();

                }

            }
            return;
        }

        if(responseString != null && apiUrl.equalsIgnoreCase(NetworkConfig.userSignup)) {
            MedininProgressDialog.dismissDialog();
            UserSignupResponse data = DataParser.parseJson(responseString, UserSignupResponse.class);
            if(data.getStatuscode() == 0){
                UIUtility.showToastMsg_short(getActivity(), data.getStatusMessage());
                return;
            }else if(data.getStatuscode() ==1 ){
                llverifyotp.setVisibility(View.VISIBLE);
//            String username=data.getData().getName()==null?"New user":"back "+data.getData().getName().toUpperCase();
//            tvUserName.setText("Welcome "+username+" .. Plz enter the OTP you received");
                setTimer(300);
                return;

            }

        }
    }

    @Override
    public void onDestroyView() {
        if (ivFingerPrint.getVisibility() == View.VISIBLE) {
            stopListening();
        }
        super.onDestroyView();
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    private void setTimer(int timeinSeconds) {
        tvTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(timeinSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                millisUntilFinished = millisUntilFinished / 1000;
                long min = millisUntilFinished / 60;
                long sec = millisUntilFinished % 60;
                String arg1 = String.valueOf(min);
                String arg2 = String.valueOf(sec);
                if (String.valueOf(arg1).length() == 1)
                    arg1 = "0" + arg1;
                if (arg2.toString().length() == 1)
                    arg2 = "0" + arg2;
                tvTimer.setText(arg1 + ":" + arg2);
            }

            public void onFinish() {
                btnGetOTP.setEnabled(true);
                etMobileNumber.setEnabled(true);
            }
        }.start();
    }
}
