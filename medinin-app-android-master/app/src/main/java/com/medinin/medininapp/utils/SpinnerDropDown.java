package com.medinin.medininapp.utils;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.medinin.medininapp.data.StringWithTag;

public class SpinnerDropDown {

    public static String getSpinnerItem(Spinner spinner) {
        StringWithTag temp_str = (StringWithTag) spinner.getSelectedItem();
        Object temp_tag = temp_str.tag;
        if (temp_tag.toString().equals("0")) {
            return "";
        } else {
            return temp_tag.toString();
        }
    }

    public static void setSpinnerItem(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();

        for (int pos = 0; pos < adapter.getCount(); pos++) {
            StringWithTag s = (StringWithTag) spinner.getItemAtPosition(pos);
            Object tag = s.tag;
            if (tag.toString().equals(value)) {
                spinner.setSelection(pos);
                return;
            }
        }
    }
}
