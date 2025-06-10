package com.sssl.seqrgraphdemo.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.view.PreviewView;
import androidx.camera.view.TransformExperimental;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.camera.CameraException;
import com.sssl.seqrgraphdemo.camera.CameraHelper;
import com.sssl.seqrgraphdemo.models.Result;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;
import com.sssl.seqrgraphdemo.tlvdecoder.TLVDecodeException;
import com.sssl.seqrgraphdemo.utils.ImageUtils;
import com.sssl.seqrgraphdemo.utils.SignatureVerifyException;
import com.sssl.seqrgraphdemo.utils.UiText;


import ai.tech5.sdk.abis.cryptograph.T5CryptoClient;


@TransformExperimental
@ExperimentalGetImage
public class CameraViewModel extends AndroidViewModel {

    private static final String TAG = CameraViewModel.class.getSimpleName();


    private final MutableLiveData<Result> detectionResult = new MutableLiveData<>();
    private final MutableLiveData<UiText> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isProcessing = new MutableLiveData<>();

    public MutableLiveData<UiText> getErrorMessage() {
        return errorMessage;
    }

    public boolean isDetected() {
        return isDetected;
    }


    public void setIsDetected(boolean isDetected) {
        this.isDetected = isDetected;
    }

    public ImageUtils getImageUtils() {
        return imageUtils;
    }

    public CryptographDecodeUtil getCryptographDecodeUtil() {
        return cryptographDecodeUtil;
    }

    public LiveData<Boolean> getIsProcessing() {
        return isProcessing;
    }

    public MutableLiveData<Bitmap> getCapturedBitmapLiveData() {
        return capturedBitmapLiveData;
    }

    private final MutableLiveData<Bitmap> capturedBitmapLiveData = new MutableLiveData<>();


    private final MutableLiveData<Integer> flashState = new MutableLiveData<>();


    private final Application application;
    private CameraHelper cameraHelper;


    private boolean isDetected = false;

    private final ImageUtils imageUtils;
    private CryptographDecodeUtil cryptographDecodeUtil;

    private final AppSharedPreference appSharedPreference;


    public String getJwsJson() {
        return appSharedPreference.getPublicKeyJson();
    }


    public CameraViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
        imageUtils = new ImageUtils();


        appSharedPreference = new AppSharedPreference(application.getApplicationContext());

    }


    public MutableLiveData<Result> getDetectionResult() {
        return detectionResult;
    }

    public LiveData<Integer> getFlashState() {
        return flashState;
    }


    public void init(T5CryptoClient t5CryptoClient) {

        cryptographDecodeUtil = new CryptographDecodeUtil(t5CryptoClient, false);

    }


    public void toggleFlash() {

        cameraHelper.toggleFlash();

        flashState.postValue(cameraHelper.flashState());


    }


    public void startCapture(LifecycleOwner lifecycleOwner, PreviewView previewView, boolean isAutoCapture) throws CameraException {


        startCamera(lifecycleOwner, previewView, isAutoCapture);

    }


    private void startCamera(LifecycleOwner lifecycleOwner, PreviewView previewView, boolean isAutoCapture) throws CameraException {


        Log.d(TAG, "start camera " + previewView);

        isDetected = false;


        cameraHelper = new CameraHelper.Builder()
                .context(application)
                .cameraListener(new CameraListenerImpl(this))
                .lifeCycleOwner(lifecycleOwner)
                .setAutoCapture(isAutoCapture)
                .previewOn(previewView)
                .build();


        cameraHelper.start();


    }


    public void stopCamera() {

        if (cameraHelper != null) {

            cameraHelper.stop();
            cameraHelper = null;

        }

    }

    public void takePicture() {
        cameraHelper.takePicture();
    }

    public void decodeCryptographImage(Bitmap bitmap) {
        isProcessing.postValue(true);

        new Thread(() -> {

            try {
                Result result = cryptographDecodeUtil.decodeBarcodeImage(ImageUtils.getBytesFromImage(bitmap), getJwsJson());
                isProcessing.postValue(false);
                detectionResult.postValue(result);
            } catch (TLVDecodeException e) {
                isProcessing.postValue(false);
                errorMessage.postValue(UiText.stringResource(R.string.unable_to_decode,  e.getLocalizedMessage()));
            } catch (SignatureVerifyException e) {
                isProcessing.postValue(false);
                errorMessage.postValue(UiText.stringResource(R.string.unable_to_verify_signature,  e.getLocalizedMessage()));
            }

        }).start();


    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopCamera();
    }


    public void playBeep() {


        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_S, 100);
        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(toneGenerator::release, 150);


    }
}