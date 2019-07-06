package com.medinin.medininapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.medinin.medininapp.R;

public class PrintPrescription extends AppCompatActivity {


    private String presc_id, med_user_id, med_user_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print__prescription);

        Intent intent = getIntent();
        presc_id = intent.getStringExtra("presc_id");



        doPhotoPrint();
    }

    private void doPhotoPrint() {
//        PrintHelper photoPrinter = new PrintHelper(PatientHistory.this);
//        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.vital_chart);
//        photoPrinter.printBitmap("droids.jpg - test print", bitmap);

        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.vital_chart);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
}
