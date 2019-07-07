package com.medinin.medininapp.network;

/**
 * Created by Hemanth A on 10/5/2016.
 */

public enum URLData {
    URL_PATIENTS_NO_PAGE("https://api.medinin.in/v1/get-doc-patient-list", false, true, "", 1),


    URL_HOTELS_MENU_LIST("https://goohungrry.com/stack/v1/menu", false, true, "", 2),
    URL_HOTELS_MENU_DETAILS("https://goohungrry.com/stack/v1/menuDetails", false, true, "", 3),
    URL_HOTELS_MENU_LIST_DETAILS("https://goohungrry.com/stack/v1/menuItems", false, true, "", 4),
    URL_LOGIN("https://goohungrry.com/stack/v1/clogin", true, true, "", 5),
    URL_SIGNUP("https://goohungrry.com/stack/v1/cregister", true, true, "", 6),
    URL_HOTEL_LIST_NEARBY("https://goohungrry.com/stack/v1/nearby ", false, true, "", 7),
    URL_ADDCART("https://goohungrry.com/stack/v1/additemtocart", true, true, "", 8),
    URL_GET_ADDRESS("https://goohungrry.com/stack/v1/getaddress", true, true, "", 9),
    URL_CHECKOUT("https://goohungrry.com/stack/v1/checkout", true, true, "", 10),
    URL_ADD_ADDRESS("https://goohungrry.com/stack/v1/addaddress", true, true, "", 11),
    URL_PAST_ORDER("https://goohungrry.com/stack/v1/getuserorders", true, true, "", 12),
    URL_ADD_DEVICEID("https://goohungrry.com/stack/v1/adddeviceid", false, true, "", 13);


    private String mUrl;
    private boolean showProgress;
    private boolean showNoNetworkAlert;
    private String progressText;
    private int urlId;

    URLData(String url, boolean showProgress, boolean showNoNetworkAlert, String progressText, int urlId) {
        this.mUrl = url;
        this.showProgress = showProgress;
        this.showNoNetworkAlert = showNoNetworkAlert;
        this.progressText = progressText;
        this.urlId = urlId;
    }


    public String getmUrl() {
        return mUrl;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public boolean isShowNoNetworkAlert() {
        return showNoNetworkAlert;
    }

    public String getProgressText() {
        return progressText;
    }

    public int getUrlId() {
        return urlId;
    }

    @Override
    public String toString() {
        return mUrl;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
}
