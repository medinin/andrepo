package com.medinin.medininapp.utils;

import android.util.Log;

import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Kalyan on 4/29/2017.
 */

public class LocalDb {


//    public static ArrayList<MenuItem> getCartData() {
//        String cartData = Prefs.getString(PrefKeys.HOTEL_DATA, null);
//        if (cartData == null) {
//            return null;
//        } else {
//            //   Log.v("GetCartData",cartData);
//            Type type = new TypeToken<ArrayList<MenuItem>>() {
//            }.getType();
//            ArrayList<MenuItem> menuList = ParseUtils.fromJson(cartData, type, "LocalDb");
//            return menuList;
//        }
//    }
//
//
//    public static void saveCartData(ArrayList<MenuItem> menuItems) {
//        String tojson;
//        if (menuItems == null || menuItems.size() == 0) {
//            saveHotelId("");
//            tojson = "";
//        } else {
//            tojson = ParseUtils.tojson(menuItems, "LocalDb");
//        }
//        Prefs.putString(PrefKeys.HOTEL_DATA, tojson);
//        Log.v("Data", Prefs.getString(PrefKeys.HOTEL_DATA, tojson));
//    }
//
//
//    public static String getHotelId() {
//        return Prefs.getString(PrefKeys.HOTEL_ID, "");
//    }
//
//    public static void saveHotelId(String id) {
//        Prefs.putString(PrefKeys.HOTEL_ID, id);
//    }
//
//    public static void saveUserData(LoginDetails loginDetails) {
//        Prefs.putString(PrefKeys.LOGIN_DATA, ParseUtils.tojson(loginDetails, ""));
//    }
//
//    public static LoginDetails getLoginDetails() {
//        String string = Prefs.getString(PrefKeys.LOGIN_DATA, "");
//        LoginDetails loginDetails = ParseUtils.fromJson(string, LoginDetails.class, "");
//        return loginDetails;
//    }
//
//    public static void saveAddressList(ArrayList<AddressResponse> addressResponses) {
//        Prefs.putString(PrefKeys.ADDRESS_LIST, ParseUtils.tojson(addressResponses, ""));
//    }
//
//    public static ArrayList<AddressResponse> getAddressList() {
//        String string = Prefs.getString(PrefKeys.ADDRESS_LIST, "");
//        Type type = new TypeToken<ArrayList<AddressResponse>>() {
//        }.getType();
//        ArrayList<AddressResponse> addressResponses = ParseUtils.fromHtmlJson(string, type, "");
//        return addressResponses;
//    }
//
//
//    public static void saveRestaurantDetails(RestaurantDetails restuarentData) {
//        Prefs.putString(PrefKeys.RESTAURANT_DATA, ParseUtils.tojson(restuarentData, ""));
//    }
//
//    public static RestaurantDetails getRestaurantDetails() {
//        String string = Prefs.getString(PrefKeys.RESTAURANT_DATA, "");
//        RestaurantDetails restaurantDetails = ParseUtils.fromHtmlJson(string, RestaurantDetails.class, "");
//        return restaurantDetails;
//
//    }
//
//
//    public static void saveAddressData(AddressResponse addressResponses) {
//        Prefs.putString(PrefKeys.ADDRESS_DATA, ParseUtils.tojson(addressResponses, ""));
//    }
//
//    public static AddressResponse getAddressData() {
//        String string = Prefs.getString(PrefKeys.ADDRESS_DATA, "");
//        AddressResponse addressResponses = ParseUtils.fromHtmlJson(string, AddressResponse.class, "");
//        return addressResponses;
//    }


}
