package com.medinin.medininapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Bharath on 06/23/2019.
 */

public class DataParser {
    private static final String TAG = "DataParser";

    /**
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     */
    public static <T> T parseJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        try {
            return new GsonBuilder().create().fromJson(json, classOfT);

        } catch (Exception e) {
            DLog.d("exception123", e.toString());
            return null;
        }

    }

    public static String toJsonString(Object object) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        return gson.toJson(object);
    }


}