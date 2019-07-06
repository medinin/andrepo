package com.medinin.medininapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.medinin.medininapp.R;
import com.medinin.medininapp.app.AppController;
import com.medinin.medininapp.data.MedicineItem;

import java.util.List;
import java.util.Locale;

public class MedicineListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MedicineItem> modelItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Context baseContext;
    private String temp;

    public Context getBaseContext() {
        return baseContext;
    }

    public MedicineListAdapter(Activity activity, List<MedicineItem> modelItems) {
        this.activity = activity;
        this.modelItems = modelItems;
    }

    @Override
    public int getCount() {
        return modelItems.size();
    }

    @Override
    public Object getItem(int location) {
        return modelItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.prescription_search_list, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        MedicineItem item = modelItems.get(position);
        TextView medicineName = (TextView) convertView.findViewById(R.id.medicine_name_txt);
        TextView medicine_trade_name = (TextView) convertView.findViewById(R.id.medicine_trade_name);
        TextView strength_value_and_unit = (TextView) convertView.findViewById(R.id.strength_value_and_unit);
        TextView medicine_name_and_unit_txt = (TextView) convertView.findViewById(R.id.medicine_name_and_unit_txt);

        medicineName.setText(item.getName());
        medicineName.setTag(item.getId());

        temp = item.getStrengthValue();
        if (!temp.equals("null")) {
            strength_value_and_unit.setText(item.getStrengthValue() + "" + item.getUnitOfStrength());
        }

        // highlight search text start -->
        String fullText = item.getName() + " " + item.getStrengthValue() + "" + item.getUnitOfStrength();
        String mSearchText = item.getSearch_str();

        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = fullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
            int endPos = startPos + mSearchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(fullText);
                ColorStateList _color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#282f3f")});//Color.parseColor("#e6282f3f")
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan("@font/mulibold", Typeface.NORMAL, -1, _color, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                medicine_name_and_unit_txt.setText(spannable);
            } else {
                medicine_name_and_unit_txt.setText(fullText);
            }
        } else {
            medicine_name_and_unit_txt.setText(fullText);
        }

        String tradefullText = "";
        temp = item.getTradeName();
        if (!temp.equals("null")) {
            tradefullText = "( " + item.getTradeName() + " )";
        }
        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = tradefullText.toLowerCase(Locale.US).indexOf(mSearchText.toLowerCase(Locale.US));
            int endPos = startPos + mSearchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(tradefullText);
                ColorStateList _color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#282f3f")});//Color.parseColor("#e6282f3f")
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan("@font/mulibold", Typeface.NORMAL, -1, _color, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                medicine_trade_name.setText(spannable);
            } else {
                medicine_trade_name.setText(tradefullText);
            }
        } else {
            medicine_trade_name.setText(tradefullText);
        }
        //<-- end

        return convertView;
    }

    public String removeNull(String _string) {
        return _string == "null" ? "" : _string;
    }

}
