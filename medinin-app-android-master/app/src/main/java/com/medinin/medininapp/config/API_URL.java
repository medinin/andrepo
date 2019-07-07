package com.medinin.medininapp.config;

public class API_URL {

    //Prod
//    public static String _domain = "http://medapi.medinin.com/api";
//    private static String _faceDomain = "https://www.medinin.com";
   // private static String _faceTrainDomain = "http://medface.medinin.com";
//    private static String _anatomyDomain = "http://medanatomy.medinin.com";
//    private static String _conditionDomain = "http://conditions.medinin.com/api";
//    private static String _conditionDomainPlain = "http://conditions.medinin.com";
//    private static String _medininWebDomain = "https://www.medinin.com";

    //QA
    public static String _domain = "http://docapi.medinin.com/api";
    private static String _faceDomain = "https://www.medinin.com";
   private static String _faceTrainDomain = "http://face.medinin.com";
    private static String _anatomyDomain = "http://anatomymobile.medinin.com";
    private static String _conditionDomain = "http://conditions.medinin.com/api";
    private static String _conditionDomainPlain = "http://conditions.medinin.com";
    private static String _medininWebDomain = "https://www.medinin.com";


    //Api URLs
    public static String Login = _domain + "/auth/login";
    public static String LoginCheck = _domain + "/auth/login/check";
    public static String PassCodeLogin = _domain + "/auth/login/pass-code";
    public static String SendOTP = _domain + "/auth/login/get-otp";
    public static String CheckOTP = _domain + "/auth/login/check-otp";

    //Patient API
    public static String Patient = _domain + "/patient";
    public static String PatientFetchFull = _domain + "/patient/fetch-full/";
    public static String PatientSearch = _domain + "/patient/search";
    public static String PatientAppointment = _domain + "/patient-appointment";
    public static String PatientAppointmentSearch = _domain + "/patient-appointment/search";
    public static String PatientReport = _domain + "/patient-report";
    public static String PatientReportFetch = _domain + "/patient-report/fetch/";
    public static String PatientReportFiles = _domain + "/patient-report-files";
    public static String PatientReportFileDelete = _domain + "/patient-report-files/photo/";
    public static String PatientReportFilesUpload = _domain + "/patient-report-files/photo/upload";
    public static String PatientPhotoFileUpload = _domain + "/patient/avatar/upload";
    public static String PatientChangePhoto = _domain + "/patient/avatar-file/upload";
    public static String PatientGetOTP = _domain + "/patient-mobile";
    public static String PatientCheckOTP = _domain + "/patient-mobile/check-otp";
    public static String PatientVitals = _domain + "/patient-vitals";
    public static String PatientFollowUp = _domain + "/patient-followup";
    public static String PatientFollowUpSmsSend = _domain + "/patient-followup/send/follow-sms";
    public static String PatientMobileUnique = _domain + "/patient/check/mobile/unique";
    public static String PatientDeleteAccount = _domain + "/patient/delete-account/";

    //Patient relation
    public static String PatientRelation = _domain + "/pat/relation";

    //Doctor API
    public static String Doc = _domain + "/doc";
    public static String DocRegister = _domain + "/auth/register";
    public static String DocEducation = _domain + "/doc-education";
    public static String DocEducationFile = _domain + "/doc-education-file";
    public static String DocEducationFileUpload = _domain + "/doc-education-file/photo/upload";
    public static String DocEducationMine = _domain + "/doc-education/mine/";
    public static String DocAvatarUpload = _domain + "/doc/avatar/upload/";
    public static String DocClinic = _domain + "/doc-clinic";
    public static String DocClinicFile = _domain + "/doc-clinic-file";
    public static String DocClinicFileUpload = _domain + "/doc-clinic-file/photo/upload";
    public static String DocClinicMine = _domain + "/doc-clinic/mine/";
    public static String DocMobileGetOTP = _domain + "/doc-mobile/get-otp";
    public static String DocMobileCheckOTP = _domain + "/doc-mobile/check-otp";
    public static String DocResetPassCode = _domain + "/doc/reset-passcode/";
    public static String DocSpecialties = _domain + "/specialties";
    public static String DocMobileUnique = _domain + "/doc/check/mobile/unique";
    public static String DocDeleteAccount = _domain + "/doc/delete/my/account";

    public static String DocRemainders = _domain + "/doc-remainder";
    public static String DocRemaindersSearch = _domain + "/doc-remainder/search";
    public static String DocAppointmentSearch = _domain + "/doc-appointment/search";
    public static String DocAppointmentTotalWeekly = _domain + "/doc-appointment/fetch/total";
    public static String DocReportSearch = _domain + "/doc-report/search";
    public static String DocReportMainSearch = _domain + "/doc-report/main-search";
    public static String DocPatientSearch = _domain + "/doc-patient/search";
    public static String DocPatientTotalWeekly = _domain + "/doc-patient/fetch/total";
    public static String DocPatientVitalsSearch = _domain + "/doc-vitals/search";
    public static String DocPrescriptionSearch = _domain + "/doc-pres/search";
    public static String DocResetPassword = _domain + "/reset-password/";
    public static String DocPatient = _domain + "/doc-patient";
    public static String DocPatientLinkStatus = _domain + "/doc-patient/check/link/status";
    public static String DocFetchTimeSlot = _domain + "/doc-appointment/fetch/time-slots";

    //FaceScan API
    public static String FaceScan = _faceDomain + "/face-scan";
    public static String AddFaceScanMobile = _faceDomain + "/add-face-mobile";
    public static String FacePredict = _faceDomain + "/predict-face";
    public static String FacePredictMobile = _faceDomain + "/predict-face-mobile";
    public static String FaceTrain = _faceTrainDomain + "/train";
    public static String FaceTrainImage = _faceTrainDomain + "/train-image-file";
    public static String FaceScanImage = _faceTrainDomain + "/check-face-with-image";

    //Medicine API
    public static String MedicineSearch = _domain + "/medicine/search";

    //Prescription API
    public static String Prescription = _domain + "/pres";
    public static String PrescriptionMedicine = _domain + "/pres-medicine";
    public static String PrescriptionMedicineFile = _domain + "/pres-file";
    public static String PrescriptionMedicineFileDelete = _domain + "/pres-file/photo/";
    public static String PrescriptionMedicineFileUpload = _domain + "/pres-file/photo/upload";

    //BloodGroup API
    public static String BloodGroup = _domain + "/blood-group";

    //Anatomy
    public static String headModel = _anatomyDomain + "/model-mobile/head";
    public static String handModel = _anatomyDomain + "/model-mobile/hand";
    public static String legModel = _anatomyDomain + "/model-mobile/leg";
    public static String noseModel = _anatomyDomain + "/model-mobile/nose";
    public static String hairModel = _anatomyDomain + "/model-mobile/hair";
    public static String eyeModel = _anatomyDomain + "/model-mobile/eye";
    public static String teethModel = _anatomyDomain + "/model-mobile/teeth";
    public static String kidneyModel = _anatomyDomain + "/model-mobile/kidney";
    public static String spinalModel = _anatomyDomain + "/model-mobile/spinal";
    public static String pelvisModel = _anatomyDomain + "/model-mobile/pelvis";
    public static String lungsModel = _anatomyDomain + "/model-mobile/lungs";
    public static String urinaryModel = _anatomyDomain + "/model-mobile/urinary";
    public static String femaleUrinaryModel = _anatomyDomain + "/model-mobile/female-urinary";
    public static String skullModel = _anatomyDomain + "/model-mobile/skull";
    public static String mouthModel = _anatomyDomain + "/model-mobile/mouth";
    public static String brainModel = _anatomyDomain + "/model-mobile/brain";
    public static String heartModel = _anatomyDomain + "/model-mobile/heart";
    public static String digestiveModel = _anatomyDomain + "/model-mobile/digestive";
    public static String breastModel = _anatomyDomain + "/model-mobile/breast";
    public static String torsoModel = _anatomyDomain + "/model-mobile/male-torso";
    public static String torsoUpModel = _anatomyDomain + "/model-mobile/male-torso-up";

    //Conditions
    public static String Conditions = _conditionDomain + "/conditions";
    public static String ConditionsFetchAll = _conditionDomain + "/conditions/fetch-all/light";
    public static String ConditionsSearchAll = _conditionDomain + "/condition-search";
    public static String ConditionGetImage = _conditionDomainPlain + "/cdn/img/conditions/photo/";

    //Get countries
    public static String CountryFetchAll = _medininWebDomain + "/api/get/all/countries";
    public static String CountryFlagImage = _medininWebDomain + "/img/flags/";

    //Feedback
    public static String SendFeedback = _domain + "/feedback";

    //forms
    public static String FormsFetchAll = _domain + "/forms/";
    public static String FormsFetchPopular = _domain + "/forms/get/popular";
    public static String FormsSearch = _domain + "/forms/search";

}
