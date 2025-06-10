package com.sssl.seqrgraphdemo.camera;

import static androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.camera.view.TransformExperimental;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class CameraHelper {


    private static final String TAG = CameraHelper.class.getSimpleName();
    private final Context context;
    private final LifecycleOwner lifecycleOwner;
    private final ExecutorService cameraExecutor;
    private PreviewView mViewFinder;

    private boolean isAutoCapture;
    private static final int LENS_FACING = CameraSelector.LENS_FACING_BACK;
    private ProcessCameraProvider mCameraProvider = null;

    private Camera camera;

    private ImageCapture imageCapture;


    private final CameraListener cameraListener;


    public CameraHelper(Builder builder) {


        this.context = builder.context;

        this.mViewFinder = builder.previewView;

        this.cameraListener = builder.cameraListener;

        this.lifecycleOwner = builder.lifecycleOwner;

        this.isAutoCapture = builder.isAutoCapture;

        // Initialize our background executor
        this.cameraExecutor = Executors.newSingleThreadExecutor();


    }


    public void start() {

        startCamera();
    }

    public void stop() {

        if (mCameraProvider != null) {
            mCameraProvider.unbindAll();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }

        mViewFinder = null;
    }


    /**
     * Initialize CameraX, and prepare to bind the camera use cases
     */
    private void startCamera() {


        Log.d(TAG, "view finder : " + mViewFinder);
        Log.d(TAG, "context : " + context);
        Log.d(TAG, "lifecycle owner : " + lifecycleOwner);


        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {

            // CameraProvider
            try {
                mCameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {

                Log.e("TAG", "InterruptedException: " + e.getLocalizedMessage());
                Thread.currentThread().interrupt();

            }


            // Build and bind the camera use cases
            bindCameraUseCases();


        }, ContextCompat.getMainExecutor(context));


    }


    /**
     * Declare and bind preview, capture and analysis use cases
     */
    @SuppressLint({"RestrictedApi", "UnsafeExperimentalUsageError", "UnsafeOptInUsageError"})
    private void bindCameraUseCases() {

        Log.d(TAG, "bindCameraUseCases called");


        int rotation = mViewFinder.getDisplay().getRotation();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(LENS_FACING).build();


        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation).build();


        // Set initial target rotation, we will have to call
        // this again if rotation changes
        // during the lifecycle of this use case
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)


                // Set initial target rotation, we will have to call
                // this again if rotation changes
                // during the lifecycle of this use case

                .setTargetRotation(rotation).build();

        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalyzer());

        if (!isAutoCapture) {

            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    // We request aspect ratio but no resolution to match preview config, but letting
                    // CameraX optimize for whatever specific resolution best fits our use cases
                    // .setTargetAspectRatio(screenAspectRatio)
                    .setBufferFormat(ImageFormat.YUV_420_888)

                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setMaxResolution(new Size(2000, 2000))

                    // Set initial target rotation, we will have to call this again if rotation changes
                    // during the lifecycle of this use case
                    .setTargetRotation(rotation)
                    .build();
        }


        // Must unbind the use-cases before rebinding them
        mCameraProvider.unbindAll();

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            if (isAutoCapture) {
                Log.d(TAG, "binding image analysis");
                camera = mCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis);
            } else {
                Log.d(TAG, "binding image capture");
                camera = mCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);
            }

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mViewFinder.getSurfaceProvider());

            Log.d(TAG, "output transform : " + mViewFinder.getOutputTransform());


            autoFocus();


        } catch (Exception exc) {
            Log.d(TAG, "bindCameraUseCases() failure :" + exc.getLocalizedMessage());
        }

        Log.d(TAG, "bindCameraUseCases() done");
    }


    public void toggleFlash() {
        if (camera != null) {

            CameraInfo cameraInfo = camera.getCameraInfo();


            if (cameraInfo.getTorchState().getValue() != null) {
                int torchState = cameraInfo.getTorchState().getValue();
                Log.d(TAG, "torchState....." + torchState);
                camera.getCameraControl().enableTorch(torchState == TorchState.OFF);

            }
        }
    }

    public int flashState() {

        int torchState;
        if (camera != null) {

            CameraInfo cameraInfo = camera.getCameraInfo();

            if (cameraInfo.getTorchState().getValue() != null) {
                torchState = cameraInfo.getTorchState().getValue();
                Log.d(TAG, "torchState....." + torchState);
                return torchState;
            }
        }
        return 5;
    }

    public void takePicture() {

        if (mCameraProvider != null && !isAutoCapture) {

            imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    cameraListener.onPictureTaken(image);
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    cameraListener.onFailedToTakePicture(exception);
                }
            });

        }
    }


    public void restartCamera() {
        if (mCameraProvider != null) {

            bindCameraUseCases();

        }
    }


    private void autoFocus() {
        try {
            MeteringPointFactory factory =
                    new SurfaceOrientedMeteringPointFactory(mViewFinder.getWidth(),
                            mViewFinder.getHeight());

            int centerWidth = mViewFinder.getWidth() / 2;
            int centreHeight = mViewFinder.getHeight() / 2;

            MeteringPoint autoFocusPoint = factory.createPoint(centerWidth, centreHeight);

            FocusMeteringAction.Builder builder =
                    new FocusMeteringAction.Builder(autoFocusPoint,
                            FocusMeteringAction.FLAG_AF |
                                    FocusMeteringAction.FLAG_AE);

            builder.setAutoCancelDuration(1, TimeUnit.SECONDS);

            camera.getCameraControl().startFocusAndMetering(builder.build());

        } catch (Exception e) {
            Log.d(TAG, "auto focus failed " + e.getLocalizedMessage());
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public static final class Builder {

        private Context context;

        private PreviewView previewView;

        private CameraListener cameraListener;

        private LifecycleOwner lifecycleOwner;

        private boolean isAutoCapture = true;


        public Builder context(Context context) {

            this.context = context;
            return this;
        }

        public Builder lifeCycleOwner(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
            return this;
        }


        public Builder previewOn(PreviewView previewView) {

            this.previewView = previewView;
            return this;
        }


        public Builder setAutoCapture(boolean isAutoCapture) {

            this.isAutoCapture = isAutoCapture;
            return this;

        }

        public Builder cameraListener(CameraListener listener) {
            this.cameraListener = listener;
            return this;
        }


        public CameraHelper build() throws CameraException {
            if (context == null) {
                throw new CameraException("context is null ");
            }

            if (lifecycleOwner == null) {
                throw new CameraException("lifecycleOwner is null ");
            }


            if (cameraListener == null) {
                throw new CameraException("cameraListener is null");
            }
            if (previewView == null) {
                throw new CameraException("you must preview on a preview");
            }
            return new CameraHelper(this);
        }


    }

    @TransformExperimental
    class ImageAnalyzer implements ImageAnalysis.Analyzer {


        @Override
        public void analyze(@NonNull ImageProxy image) {
            cameraListener.onFrameAvailableForAnalysis(image);

        }


        @Nullable
        @Override
        public Size getDefaultTargetResolution() {
            return new Size(1000, 1000);

        }

    }

}




