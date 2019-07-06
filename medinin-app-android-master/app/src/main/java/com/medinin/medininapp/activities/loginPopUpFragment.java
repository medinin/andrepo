package com.medinin.medininapp.activities;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medinin.medininapp.R;

public class loginPopUpFragment extends BottomSheetDialogFragment {
    public loginPopUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.login_dialog, container, false);
        ImageView hidePopUp = (ImageView) v.findViewById(R.id.hide_popup);
        hidePopUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

            }
        });
        return v;
    }
}
