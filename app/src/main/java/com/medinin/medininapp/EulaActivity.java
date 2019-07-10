package com.medinin.medininapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.medinin.medininapp.utils.MedininProgressDialog;
import com.medinin.medininapp.utils.PrefManager;
import com.medinin.medininapp.utils.UIUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EulaActivity  extends BaseActivity{



    @BindView(R.id.eulaWebView)
    WebView eulaWebView;

    @BindView(R.id.closeBtn)
    Button btnClose;

    private Context context;
    private PrefManager prefManager = new PrefManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eula);
        ButterKnife.bind(this);
        context = getApplicationContext();
    }

    @OnClick({R.id.agree_txt, R.id.terms_txt, R.id.privacy_txt, R.id.closeBtn})
    void OnClickListener(View v) {
        switch (v.getId()){

            case R.id.agree_txt:
                startActivity(new Intent(this, LoginActivity.class));
                prefManager.setTCstatus(true);
                finish();
                break;

            case R.id.terms_txt:
                if(!Myapp.isNetworkAvailable()){
                    UIUtility.showToast_noInternet(context);
                    return;
                }
                MedininProgressDialog.show(this,true);
                eulaWebView.loadUrl("https://www.medinin.com/terms-condition");
                eulaWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress >= 80) {
                            eulaWebView.setVisibility(View.VISIBLE);
                            btnClose.setVisibility(View.VISIBLE);
                        }
                        if(progress >=99) {
                            MedininProgressDialog.dismissDialog();
                        }
                    }
                });
                break;

            case R.id.privacy_txt:
                if(!Myapp.isNetworkAvailable()){
                    UIUtility.showToast_noInternet(context);
                    return;
                }
                MedininProgressDialog.show(this,true);
                eulaWebView.loadUrl("https://www.medinin.com/privacy-policy");
                eulaWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        if (progress >= 80) {
                            eulaWebView.setVisibility(View.VISIBLE);
                            btnClose.setVisibility(View.VISIBLE);
                        }
                        if(progress >=99) {
                            MedininProgressDialog.dismissDialog();
                        }
                    }
                });
                break;

            case R.id.closeBtn:
                eulaWebView.setVisibility(View.GONE);
                btnClose.setVisibility(View.GONE);

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(eulaWebView.getVisibility()==View.VISIBLE){
            eulaWebView.setVisibility(View.GONE);
            btnClose.setVisibility(View.GONE);
        }else
            super.onBackPressed();
    }
}
