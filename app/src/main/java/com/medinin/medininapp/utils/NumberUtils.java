package com.medinin.medininapp.utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kalyan pvs on 07-Nov-16.
 */

public class NumberUtils {

    static NumberFormat numberFormat = NumberFormat.getInstance();

    public static float toFloat(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                float parseFloat = Float.parseFloat(string);
                return parseFloat;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return 0f;
    }



// change the date formate here . only date
    public static String dateFormatFun(String _date) {
        if (_date != null && !_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("yyyy/MM/dd");
            DateFormat dateFormat = new SimpleDateFormat("dd");
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

    //custome datetime ;


    public static String month(String _date) {
        if (_date != null && !_date.equals("null") && !_date.isEmpty()) {

            String[] splitDate = _date.split("\\-");
            String monthStr = "";

            switch (Integer.parseInt(splitDate[1])) {
                case 01:
                    monthStr = "Jan";
                    break;
                case 02:
                    monthStr = "Feb";
                    break;
                case 3:
                    monthStr = "Mar";
                    break;
                case 4:
                    monthStr = "Apr";
                    break;
                case 5:
                    monthStr = "May";
                    break;
                case 6:
                    monthStr = "Jun";
                    break;
                case 7:
                    monthStr = "Jul";
                    break;
                case 8:
                    monthStr = "Aug";
                    break;
                case 9:
                    monthStr = "Sep";
                    break;
                case 10:
                    monthStr = "Oct";
                    break;
                case 11:
                    monthStr = "Nov";
                    break;
                case 12:
                    monthStr = "Dec";
                    break;
            }

            return splitDate[0] + "-" + monthStr + "-" + splitDate[2];

        } else {
            return "";
        }
    }


//change month formate ;

    public static  String dateFormatMonth(String _date) {
        if (_date != null && !_date.equals("")) {
            //Convert date into local format
            DateFormat localFormat = new SimpleDateFormat("yyyy/MM/dd");
            DateFormat dateFormat = new SimpleDateFormat("MMM");
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

    public static long toLong(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                long parseFloat = Long.parseLong(string);
                return parseFloat;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }

    public static double toDouble(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                long parseFloat = Long.parseLong(string);
                return parseFloat;
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }
        return 0f;
    }

    public static int toInteger(String string) {
        try {
            Number number = numberFormat.parse(string);
            return number.intValue();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Integer parseInt = Integer.parseInt(string);
                return parseInt;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return 0;
    }
}
