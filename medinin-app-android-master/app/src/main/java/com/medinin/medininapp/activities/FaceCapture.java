package com.medinin.medininapp.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.medinin.medininapp.FaceGraphic;
import com.medinin.medininapp.R;
import com.medinin.medininapp.camera.CameraSourcePreview;
import com.medinin.medininapp.camera.GraphicOverlay;
import com.medinin.medininapp.config.API_URL;
import com.medinin.medininapp.volley.AppHelper;
import com.medinin.medininapp.volley.VolleyMultipartRequest;
import com.medinin.medininapp.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FaceCapture extends AppCompatActivity {
    public static final String TAG = AllPatients.class.getSimpleName();
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private FrameLayout no_data;
    private LinearLayout previewImgSec;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private int rotation;
    private int cameraId;
    boolean startCam = true;
    boolean faceScanUploading = false, safeToTakePicture = true, _scanPatientTabVisible = false;
    int camCount = 0;
    int faceScanApiCount = 0;
    AudioManager mgr;
    Bitmap loadedImage = null;
    Bitmap rotatedBitmap = null;
    FaceDetector detector;
    private ImageView regTabLink, addPhotoTabLink, faceTmpImageHolder, camPreview1, camPreview2, camPreview3;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_capture);
        mContext = getApplicationContext();
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        faceTmpImageHolder = findViewById(R.id.faceTmpImageHolder);

//        findViewById(R.id.closeCamIcon).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View arg0) {
//                patGlobalFaceScanDialog.dismiss();
//                glFaceScanStatus = false;
//                if (scannedPatListDialog != null) {
//                    scannedPatListDialog.dismiss();
//                }
//                mCameraSource = null;
//                closeCameraAndPreview();
//            }
//        });
        camPreview1 = findViewById(R.id.patientImg1);
        camPreview2 = findViewById(R.id.patientImg2);
        camPreview3 = findViewById(R.id.patientImg3);
        previewImgSec = findViewById(R.id.previewImgSec);
        createCameraSource(CameraSource.CAMERA_FACING_BACK);
        startCameraSource();
    }

    private void createCameraSource(int cameraFacing) {
        detector = new FaceDetector.Builder(mContext)
                .setClassificationType(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .setTrackingEnabled(false)
                .build();


        detector.setProcessor(new MultiProcessor.Builder<>(new FaceCapture.GraphicFaceTrackerFactory()).build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = null;
        mCameraSource = new CameraSource.Builder(mContext, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(cameraFacing)
                .setAutoFocusEnabled(true)
                .setRequestedFps(30.0f)
                .build();
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new FaceCapture.GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            Log.d("face found", "2");
            camCount++;
            mFaceGraphic.setId(faceId);
            if (safeToTakePicture && camCount > 30) {
                safeToTakePicture = false;
                takePicture();
                Log.d("face update", "3");
            }
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            camCount++;
            if (safeToTakePicture && camCount > 30) {
                safeToTakePicture = false;
                takePicture();
                Log.d("face update", "3");
            }
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void takePicture() {
        MuteAudio();
        mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
            private File imageFile;

            @Override
            public void onPictureTaken(byte[] bytes) {
                try {
                    // convert byte array into bitmap
                    loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        // frontFacing
                        rotation = -90;
                    } else {
                        // Back-facing
                        rotation = 90;
                    }
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                            loadedImage.getWidth(), loadedImage.getHeight(),
                            rotateMatrix, false);
                    ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                    rotatedBitmap = resize(rotatedBitmap, 480, 600);
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    faceTmpImageHolder.setImageBitmap(rotatedBitmap);
                    previewImgSec.setVisibility(View.VISIBLE);

                    Frame frame = new Frame.Builder().setBitmap(rotatedBitmap).build();
                    SparseArray<Face> mFaces = detector.detect(frame);
                    int size = 600;
                    float left = 0;
                    float top = 0;
                    float right = 0;
                    float bottom = 0;
                    for (int i = 0; i < mFaces.size(); i++) {
                        Face face = mFaces.valueAt(i);
                        left = (float) (face.getPosition().x);
                        top = (float) (face.getPosition().y);
                        right = (float) (face.getPosition().x + face.getWidth());
                        bottom = (float) (face.getPosition().y + face.getHeight());
                    }
                    Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
                    Rect dst = new Rect(0, 0, size, size);
                    Bitmap tempBitmap = Bitmap.createBitmap(rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(tempBitmap);
                    canvas.drawBitmap(rotatedBitmap, src, dst, null);
                    camPreview1.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                    camPreview2.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                    camPreview3.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

                } catch (Exception e) {
                    safeToTakePicture = true;
                    e.printStackTrace();
                }
            }
        });
    }

    public void MuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) FaceCapture.this.getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(mAlramMAnager).adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }

    public void UnMuteAudio() {
        AudioManager mAlramMAnager = (AudioManager) FaceCapture.this.getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(mAlramMAnager).adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
    }

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private void stopCamera() {
        camCount = 20;
        faceScanApiCount = 5;
        safeToTakePicture = false;
        if (mPreview != null) {
            mPreview.stop();
        }
        UnMuteAudio();
    }

    private void killCamera() {
        if (mCameraSource != null) {
            try {
                mCameraSource.release();
            } catch (NullPointerException ignored) {

            }
        }
    }

    private void startCameraAgain() {
        camCount = 0;
        faceScanApiCount = 0;
        safeToTakePicture = true;
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    protected void closeCameraAndPreview() {
        Log.d("mcam", String.valueOf(mCameraSource));
        if (mPreview != null) {
            mPreview.release();
            mPreview = null;
        }
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }
}
