package com.medinin.medininapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.medinin.medininapp.Myapp;
import com.medinin.medininapp.login.datamodel.CheckOtpResponse;


/**
 * Created by Bharath on 06/22/19.
 */
public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;


    public static final String ACCESS_TOKEN = "accessToken";
    public static final String KEY_SERVER_VERSION = "serverVersion";


    // shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = PrefManager.class.getSimpleName();


    public static final String IS_TC_ACCEPTED = "isAcceptedTC";
    public static final String IS_FIRST_TIME_LOGIN = "isFirstTimeLogin";
    public static final String METADATA = "metaData";
    public static final String MY_ROLE = "myRole";
    public static final String MY_PASSCODE = "myPasscode";
    public static final String SIGNUP_ROLE = "signupRole";

    public static final String MOBILENUMBER = "mobileNumber";
    public static final String NAME = "name";
    public static final String DOB = "dob";
    public static final String GENDER = "gender";
    public static final String IMAGEURL = "profileUrl";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String QUALIFICATION = "qualitifcation";
    public static final String REGNO = "regno";
    public static final String EXPERINCE = "exp";
    public static final String SPECIALITYID = "spID";
    public static final String COUNTRYCODE = "countryCode";
    public static final String APIKEY = "apikey";

    public static final String DOC_MODULE = "docModule";
    public static final String CLINIC_MODULE = "clinicModule";
    public static final String HOSPITAL_MODULE = "hospitalModule";
    public static final String LAB_MODULE = "labModule";
    public static final String PATIENT_MODULE = "patModule";
    public static final String PHARM_MODULE = "pharmModule";

    public static final String USER_LOGGEDIN_ROLE = "userRole";



    public PrefManager() {
        this._context = Myapp.getInstance();
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveAccessToken(String accessToken) {
        editor.putString(ACCESS_TOKEN, accessToken).commit();
    }

    public String getAccessToken() {
        return pref.getString(ACCESS_TOKEN, null);
    }

    public Float getServerVersions() {
        return pref.getFloat(KEY_SERVER_VERSION, 0);
    }

    public void saveServerVersion(Float tenantIds) {
        editor.putFloat(KEY_SERVER_VERSION, tenantIds).commit();
    }

    public void clearSession() {
        editor.clear().apply();
    }

    public void logout(){
        clearSession();
    }


    public void setTCstatus(boolean status){
        editor.putBoolean(IS_TC_ACCEPTED,status).commit();
    }

    public boolean getTCstatus(){
        return pref.getBoolean(IS_TC_ACCEPTED,false);
    }


    public boolean isFirstTimeLogin() {
        return pref.getBoolean(IS_FIRST_TIME_LOGIN,false);
    }

    public void setIsFirstTimeLogin(boolean status){
        editor.putBoolean(IS_FIRST_TIME_LOGIN,status).commit();
    }

    public CheckOtpResponse getMetaData(){
        Gson gson = new Gson();
        String json = pref.getString(METADATA, "");
        CheckOtpResponse obj = gson.fromJson(json, CheckOtpResponse.class);
        return obj ;
    }

    public void saveMetaData(CheckOtpResponse response){
        Gson gson = new Gson();
        String json = gson.toJson(response);
        editor.putString(METADATA, json);
        editor.commit();
    }

    public void setMyRole(String role){
        editor.putString(MY_ROLE,role).commit();
    }

    public String getMyRole(){
        return pref.getString(MY_ROLE,"NewUser");
    }

    public void setPasscode(String passcode) {
        editor.putString(MY_PASSCODE, passcode).commit();
    }

    public String getMyPasscode(){
        return pref.getString(MY_PASSCODE,"");
    }

    public void setMobilenumber (String number){
        editor.putString(MOBILENUMBER, number).commit();
    }

    public String getMobilenumber(){
        return pref.getString(MOBILENUMBER,"");
    }

    public void setCountrycodewithPlus (String countrycode){
        editor.putString(COUNTRYCODE, countrycode).commit();
    }

    public String getCountrycodewithPlus(){
        return pref.getString(COUNTRYCODE,"+91");
    }


    public void setApikey (String apikey){
        editor.putString(APIKEY, apikey).commit();
    }

    public String getApikey(){
        return pref.getString(APIKEY," ");
    }

    public void setDocModule (boolean value){
        editor.putBoolean(DOC_MODULE, value).commit();
    }

    public boolean getDocModule(){
        return pref.getBoolean(DOC_MODULE,false);
    }


    public void setClinicModule (boolean value){
        editor.putBoolean(CLINIC_MODULE, value).commit();
    }

    public boolean getClinicModule(){
        return pref.getBoolean(CLINIC_MODULE,false);
    }


    public void setHospitalModule (boolean value){
        editor.putBoolean(HOSPITAL_MODULE, value).commit();
    }

    public boolean getHospitalModule(){
        return pref.getBoolean(HOSPITAL_MODULE,false);
    }

    public void setLabModule (boolean value){
        editor.putBoolean(LAB_MODULE, value).commit();
    }

    public boolean getLabMdule(){
        return pref.getBoolean(LAB_MODULE,false);
    }

    public void setPatientModule (boolean value){
        editor.putBoolean(PATIENT_MODULE, value).commit();
    }

    public boolean getPatientModule(){
        return pref.getBoolean(PATIENT_MODULE,false);
    }

    public void setMySignUpRole (String role){
        editor.putString(SIGNUP_ROLE, role).commit();
    }

    public String getMySignUpRole(){
        return pref.getString(SIGNUP_ROLE,"LOGIN");
    }

    public void setPharmModule(boolean value){
        editor.putBoolean(PHARM_MODULE,value);
    }

    public boolean getPharmModule(){
        return pref.getBoolean(PHARM_MODULE,false);
    }


    public void setUserLoggedinRole(int roleID){
        editor.putInt(USER_LOGGEDIN_ROLE,roleID).commit();
    }

    public int getUserLoggedinRole(){
        return pref.getInt(USER_LOGGEDIN_ROLE,0);
    }


    public void setName (String name){
        editor.putString(NAME, name).commit();
    }

    public String getName(){
        return pref.getString(NAME,"");
    }

    public void setGender (String gender){
        editor.putString(GENDER, gender).commit();
    }

    public String getGender(){
        return pref.getString(GENDER,"");
    }

    public void setDob (String dob){
        editor.putString(DOB, dob).commit();
    }

    public String getDob(){
        return pref.getString(DOB,"");
    }

    public void setImageurl (String url){
        editor.putString(IMAGEURL, url).commit();
    }

    public String getImageurl(){
        return pref.getString(IMAGEURL,"");
    }

    public void setLat (String lat){
        editor.putString(LAT, lat).commit();
    }

    public String getLat(){
        return pref.getString(LAT,"");
    }

    public void setLon (String lon){
        editor.putString(LON, lon).commit();
    }

    public String getLon(){
        return pref.getString(LON,"");
    }


    public void setQualification (String details){
        editor.putString(QUALIFICATION, details).commit();
    }

    public String getQualification(){
        return pref.getString(QUALIFICATION,"");
    }


    public void setRegno (String number){
        editor.putString(REGNO, number).commit();
    }

    public String getRegno(){
        return pref.getString(REGNO,"");
    }


    public void setExperince (String years){
        editor.putString(EXPERINCE, years).commit();
    }

    public String getExperince(){
        return pref.getString(EXPERINCE,"");
    }


    public void setSpecialityid (String id){
        editor.putString(SPECIALITYID, id).commit();
    }

    public String getSpecialityid(){
        return pref.getString(SPECIALITYID,"");
    }





}
