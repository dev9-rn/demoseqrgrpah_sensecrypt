package com.sssl.seqrgraphdemo.camera;

import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;

public interface CameraListener {
   void onFrameAvailableForAnalysis(ImageProxy imageProxy);

   void onPictureTaken(ImageProxy imageProxy);

   void onFailedToTakePicture(ImageCaptureException exception);
}
