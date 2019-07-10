package com.medinin.medininapp.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;


/**
 * Created by Bharath on 11/16/16.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    public AuthenticationStatus getAuthenticationStatus() {
        return mAuthenticationStatus;
    }

    public void doFingerPrintAuthentication(AuthenticationStatus authenticationStatus, FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject, CancellationSignal mCancellationSignal) {
        this.mAuthenticationStatus = authenticationStatus;
        startAuth(manager, cryptoObject,mCancellationSignal);

    }

    public interface AuthenticationStatus {
        void onStatusChange(boolean isSuccess);

        void onStatusmessages(int statusID, String msg);
    }

    private AuthenticationStatus mAuthenticationStatus;

    private Context context;

    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject, CancellationSignal mCancellationSignal) {
        CancellationSignal cancellationSignal =mCancellationSignal;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication error\n" + errString);
        if (errMsgId != FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
            mAuthenticationStatus.onStatusmessages(errMsgId, "Fingerprint Authentication error\n" + errString);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication help\n" + helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.");

        if (mAuthenticationStatus != null)
            mAuthenticationStatus.onStatusChange(false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        if (mAuthenticationStatus != null)
            mAuthenticationStatus.onStatusChange(true);

    }

    private void update(String e) {

    }

}
