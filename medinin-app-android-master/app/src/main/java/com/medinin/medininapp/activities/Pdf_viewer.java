package com.medinin.medininapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.medinin.medininapp.BuildConfig;
import com.medinin.medininapp.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.concurrent.Callable;

import static java.security.AccessController.getContext;

public class Pdf_viewer extends AppCompatActivity {

    private static final String TAG = Pdf_viewer.class.getSimpleName();
    private PermissionRequest mPermissionRequest;
    RelativeLayout open_form, pdfViewerWrap;

    private FrameLayout topSettingPopup;
    private ImageView topSettingImgBtn;
    private TextView printTxtBtn, shareTxtBtn, downloadTxtBtn;
    private WebView mywebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfViewerWrap = (RelativeLayout) findViewById(R.id.pdfViewerWrap);
        topSettingPopup = (FrameLayout) findViewById(R.id.topSettingPopup);
        topSettingImgBtn = (ImageView) findViewById(R.id.topSettingImgBtn);
        printTxtBtn = (TextView) findViewById(R.id.printTxtBtn);
        shareTxtBtn = (TextView) findViewById(R.id.shareTxtBtn);
        downloadTxtBtn = (TextView) findViewById(R.id.downloadTxtBtn);
        WebView myWebView = (WebView) findViewById(R.id.webView);

        String myPdfUrl = "http://www.incemic.org/docs/Incemic-2018-Workshop-Brochure-1.pdf";
        Log.i(TAG, "Opening PDF: " + myPdfUrl);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        myWebView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + myPdfUrl);

//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        startActivityForResult(intent, 1);


        topSettingImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (topSettingPopup.getTag().toString().equals("0")) {
                    topSettingPopup.setVisibility(View.VISIBLE);
                    topSettingPopup.setTag(1);
                } else {
                    topSettingPopup.setVisibility(View.GONE);
                    topSettingPopup.setTag(0);
                }
            }
        });

        printTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topSettingPopup.setVisibility(View.GONE);
                topSettingPopup.setTag(0);
//                openPrintDialog();
            }
        });

        shareTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topSettingPopup.setVisibility(View.GONE);
                topSettingPopup.setTag(0);
//                openshareDialog();
            }
        });

        downloadTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topSettingPopup.setVisibility(View.GONE);
                topSettingPopup.setTag(0);

//                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/testthreepdf/" + "Incemic-2018-Workshop-Brochure-1.pdf");  // -> filename = maven.pdf
//                Uri path = Uri.fromFile(pdfFile);
//                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//                pdfIntent.setDataAndType(path, "application/pdf");
//                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                try{
//                    startActivity(pdfIntent);
//                }catch(ActivityNotFoundException e){
//                    Toast.makeText(Pdf_viewer.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//                }


            }
        });


//    private void openshareDialog() {
//        final Dialog dialog = new Dialog(Pdf_viewer.this);
//
//        ShareCompat.IntentBuilder.from(this)
//                .setType("text/plain")
//                .setText("I'm sharing!")
//                .startChooser();
//
//
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case 1:
//                if (resultCode == RESULT_OK) {
//                    // Get the Uri of the selected file
//                    Uri uri = data.getData();
//                    String uriString = uri.toString();
//                    File myFile = new File(uriString);
//                    String path = myFile.getAbsolutePath();
//                    String displayName = null;
//
//
//                    pdfViewerWrap.setVisibility(View.VISIBLE);
//
//                    pdfView.fromUri(uri)
//                            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//                            .enableSwipe(true) // allows to block changing pages using swipe
//                            .swipeHorizontal(false)
//                            .enableDoubletap(true)
//                            .defaultPage(0)
//                            // allows to draw something on the current page, usually visible in the middle of the screen
////                            .onDraw(onDrawListener)
////                            // allows to draw something on all pages, separately for every page. Called only for visible pages
////                            .onDrawAll(onDrawListener)
////                            .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
////                            .onPageChange(onPageChangeListener)
////                            .onPageScroll(onPageScrollListener)
////                            .onError(onErrorListener)
////                            .onPageError(onPageErrorListener)
////                            .onRender(onRenderListener) // called after document is rendered for the first time
////                            // called on single tap, return true if handled, false to toggle scroll handle visibility
////                            .onTap(onTapListener)
////                            .onLongPress(onLongPressListener)
//                            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
//                            .password(null)
//                            .scrollHandle(null)
////                            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
////                            // spacing between pages in dp. To define spacing color, set view background
////                            .spacing(0)
////                            .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
////                            .linkHandler(DefaultLinkHandler)
////                            .pageFitPolicy(FitPolicy.WIDTH)
////                            .pageSnap(true) // snap pages to screen boundaries
////                            .pageFling(false) // make a fling change only a single page like ViewPager
////                            .nightMode(false) // toggle night mode
//                            .load();
//
//
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }


    }
}
