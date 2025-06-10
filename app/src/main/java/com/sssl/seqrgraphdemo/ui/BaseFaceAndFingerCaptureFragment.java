package com.sssl.seqrgraphdemo.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.phoenixcapture.camerakit.FaceBox;
import com.sssl.seqrgraphdemo.R;
import com.sssl.seqrgraphdemo.dialog.GifProgressDialog;
import com.sssl.seqrgraphdemo.preferences.AppSharedPreference;

import ai.tech5.finger.utils.CaptureMode;
import ai.tech5.finger.utils.ImageConfiguration;
import ai.tech5.finger.utils.ImageType;
import ai.tech5.finger.utils.SegmentationMode;
import ai.tech5.finger.utils.T5FingerCaptureController;
import ai.tech5.finger.utils.T5FingerCapturedListener;
import ai.tech5.pheonix.capture.controller.AirsnapFaceThresholds;
import ai.tech5.pheonix.capture.controller.FaceCaptureController;
import ai.tech5.pheonix.capture.controller.FaceCaptureListener;


public abstract class BaseFaceAndFingerCaptureFragment extends Fragment implements FaceCaptureListener , T5FingerCapturedListener {

    private AppSharedPreference appSharedPreference;
    private GifProgressDialog gifProgressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        appSharedPreference = new AppSharedPreference(requireContext());

        gifProgressDialog = new GifProgressDialog(requireActivity());
    }


    @Override
    public void onFailure(String s) {

        Toast.makeText(requireContext(), "Failed to capture fingers :" + s, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onFaceCaptured(byte[] bytes, FaceBox faceBox) {

    }

    @Override
    public void OnFaceCaptureFailed(String s) {
        Toast.makeText(requireContext(), "Failed to capture face image " + s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancelled() {
        Toast.makeText(requireContext(), "capture cancelled ", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTimedout() {
        Toast.makeText(requireContext(), "capture timeout ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimedout(byte[] faceImage) {
        Toast.makeText(requireContext(), "capture timeout ", Toast.LENGTH_LONG).show();

    }


    public void startFaceCapture() {


        FaceCaptureController controller = FaceCaptureController.getInstance();
        controller.setUseBackCamera(appSharedPreference.isUseBackCamera());

        controller.setAutoCapture(true);
        controller.setEyeClosedEnabled(true);
        controller.setOcclusionEnabled(true);
        controller.setCompression(false);

        controller.setLivenessEnabled(appSharedPreference.isFaceLivenessEnabled());
        /*comment set
        liveness
        from sharedprefs*/

        controller.setCaptureTimeoutInSecs(60);


        controller.setIsGetFullFrontalCrop(false);
        controller.setIsISOEnabled(false);



        AirsnapFaceThresholds thresholds = new AirsnapFaceThresholds();
        thresholds.setPITCH_THRESHOLD(15);
        thresholds.setYAW_THRESHOLD(15);
        thresholds.setRollThreshold(10);
        thresholds.setBRISQUE_THRESHOLD(60);
        thresholds.setMASK_THRESHOLD(0.5f);
        thresholds.setSUNGLASS_THRESHOLD(0.5f);
        thresholds.setEYE_CLOSE_THRESHOLD(0.4f);
        thresholds.setLIVENESS_THRESHOLD(0.5f);

        thresholds.setFaceCentreToImageCentreTolerance(10F);

        thresholds.setFaceWidthToImageWidthRatioTolerance(10F);


        controller.setAirsnapFaceThresholds(thresholds);

        controller.startFaceCapture("", requireContext(), this);


    }

    public void captureFingers(SegmentationMode segmentationMode) {
        T5FingerCaptureController t5FingerCaptureController = T5FingerCaptureController.getInstance();
        t5FingerCaptureController.setLicense("");

        t5FingerCaptureController.showElipses(true);
        t5FingerCaptureController.setLivenessCheck(true);
        t5FingerCaptureController.setIsGetQuality(true);
        t5FingerCaptureController.setDetectorThreshold(0.9f);
        t5FingerCaptureController.setSegmentationMode(segmentationMode);
        t5FingerCaptureController.setCaptureMode(CaptureMode.CAPTURE_MODE_SELF);

        ImageConfiguration segmentedFingersConfiguration = new ImageConfiguration();
        segmentedFingersConfiguration.setImageType(ImageType.IMAGE_TYPE_WSQ);
        //compresion ratio is only applicable for IMAGE_TYPE_WSQ
        segmentedFingersConfiguration.setCompressionRatio(10);
        segmentedFingersConfiguration.setIsCropImage(true);
        segmentedFingersConfiguration.setCroppedImageWidth(512);
        segmentedFingersConfiguration.setCroppedImageHeight(512);
        //0->Black color padding; 255->white color padding
        segmentedFingersConfiguration.setPaddingColor(255);
        t5FingerCaptureController.setSegmentedFingerImagesConfig(segmentedFingersConfiguration);


        ImageConfiguration slapConfig = new ImageConfiguration();
        slapConfig.setImageType(ImageType.IMAGE_TYPE_BMP);
        slapConfig.setCompressionRatio(10);
        slapConfig.setIsCropImage(false);

        t5FingerCaptureController.setSlapImagesConfig(slapConfig);
        t5FingerCaptureController.setTimeoutInSecs(60);
        t5FingerCaptureController.captureFingers(requireContext(), this);
    }


    public void showProgressDialog(boolean isSShow) {

        Log.d("TAG", "show progress dialog here " + isSShow);
        if (isSShow) {
            gifProgressDialog.showDialog(R.raw.load, null);

        } else {
            gifProgressDialog.hideDialog();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (gifProgressDialog != null) {
            gifProgressDialog.hideDialog();
        }
    }

}