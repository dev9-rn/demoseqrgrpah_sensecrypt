package com.sssl.seqrgraphdemo.viewmodels;

import android.graphics.Bitmap;
import android.graphics.YuvImage;
import android.util.Log;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.TransformExperimental;


import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.camera.CameraListener;
import com.sssl.seqrgraphdemo.models.Result;
import com.sssl.seqrgraphdemo.utils.CryptographResultCode;
import com.sssl.seqrgraphdemo.utils.ImageUtils;
import com.sssl.seqrgraphdemo.utils.SignatureVerifyException;
import com.sssl.seqrgraphdemo.utils.UiText;

import java.lang.ref.WeakReference;

import ai.tech5.sdk.abis.cryptograph.ResultCode;

@ExperimentalGetImage
@TransformExperimental
public class CameraListenerImpl implements CameraListener {

    private static final String TAG = CameraListenerImpl.class.getSimpleName();


    CameraViewModel cameraViewModel;

    private final WeakReference<CameraViewModel> weakReference;


    public CameraListenerImpl(CameraViewModel cameraViewModel) {
        weakReference = new WeakReference<>(cameraViewModel);

        this.cameraViewModel = weakReference.get();
    }


    @Override
    public void onFrameAvailableForAnalysis(ImageProxy imageProxy) {


        Log.d(TAG, "onFrameAvailableForAnalysis " + imageProxy.getWidth() + "X" + imageProxy.getHeight());


        try {


            if (cameraViewModel.isDetected()) {
                return;
            }


            YuvImage yuv = cameraViewModel.getImageUtils().toYuvImage(imageProxy.getImage());

            Bitmap bitmap = cameraViewModel.getImageUtils().yuvImageToBitmap(yuv);


            if (imageProxy.getImageInfo().getRotationDegrees() != 0) {

                bitmap = cameraViewModel.getImageUtils().rotateBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());

            }
            Log.d("Tag" , "cameraViewModel.getJwsJson()" + cameraViewModel.getJwsJson());
            Result result = cameraViewModel.getCryptographDecodeUtil().decodeBarcodeImage(ImageUtils.getBytesFromImage(bitmap), cameraViewModel.getJwsJson());


            if (result.getResultCode() == ResultCode.successful) {

                cameraViewModel.setIsDetected(true);

                cameraViewModel.playBeep();

                cameraViewModel.getDetectionResult().postValue(result);

            } else if (result.getResultCode() != ResultCode.errorDecode) {


                CryptographResultCode cryptographResultCode = new CryptographResultCode(result.getResultCode());

                cameraViewModel.setIsDetected(true);

                cameraViewModel.getErrorMessage().postValue(cryptographResultCode.getErrorDescription());


            }


        } catch (SignatureVerifyException e) {
            cameraViewModel.setIsDetected(true);
            cameraViewModel.getErrorMessage().postValue(UiText.stringResource(R.string.unable_to_verify_signature, e.getLocalizedMessage()));
        } catch (Exception e) {
            cameraViewModel.setIsDetected(true);
            cameraViewModel.getErrorMessage().postValue(UiText.stringResource(R.string.unable_to_decode, e.getLocalizedMessage()));
        } finally {


            Log.e(TAG, "-------------------------------------------- ");
            imageProxy.close();
        }

    }

    @Override
    public void onPictureTaken(ImageProxy imageProxy) {


        try {
            Log.d(TAG, "onPictureTaken " + imageProxy.getWidth() + "X" + imageProxy.getHeight());

            YuvImage yuv = cameraViewModel.getImageUtils().toYuvImage(imageProxy.getImage());

            Bitmap bitmap = cameraViewModel.getImageUtils().yuvImageToBitmap(yuv);


            if (imageProxy.getImageInfo().getRotationDegrees() != 0) {

                bitmap = cameraViewModel.getImageUtils().rotateBitmap(bitmap, imageProxy.getImageInfo().getRotationDegrees());

            }


            cameraViewModel.getCapturedBitmapLiveData().postValue(bitmap);


        } catch (Exception e) {

            Log.e(TAG, "exception in take picture " + e.getLocalizedMessage());

            cameraViewModel.getErrorMessage().postValue(UiText.nonTranslatableString("Unable to take picture " + e.getLocalizedMessage()));

        } finally {


            Log.e(TAG, "-------------------------------------------- ");
            imageProxy.close();
        }


    }

    @Override
    public void onFailedToTakePicture(ImageCaptureException exception) {

        cameraViewModel.getErrorMessage().postValue(UiText.nonTranslatableString("Unable to take picture " + exception.getLocalizedMessage()));
    }


}

