package com.medinin.medininapp.utils;

import java.util.Random;

/**
 * Created by Kalyan on 5/28/2017.
 */

public class RandomUtils {

    public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();

    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
