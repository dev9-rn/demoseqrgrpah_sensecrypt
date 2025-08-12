package com.example.sdksetup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.sensecrypt.sdk.core.ActiveFaceCaptureSession
import com.sensecrypt.sdk.core.SenseCryptSdkException
import java.util.concurrent.Executors
import com.sensecrypt.sdk.core.ActiveFaceCaptureProcessingResult
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import android.util.Log

class ActiveFaceCaptureActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    lateinit var session: ActiveFaceCaptureSession

    /**
     * The capture analyzer, which processes frames from the camera
     */
    private lateinit var captureAnalyzer: ActiveFaceCaptureAnalyzer

    companion object {
        const val EXTRA_QR_CODE_BYTES = "com.sensecrypt.sdk.EXTRA_QR_CODE_BYTES"
        const val EXTRA_PASSWORD = "com.sensecrypt.sdk.EXTRA_PASSWORD"

        fun newIntentForVerification(
            context: Context,
            spBytes: ByteArray,
            password: String?,
        ): Intent {
            // Always start with the front camera
            val intent = Intent(context, ActiveFaceCaptureActivity::class.java)
            intent.putExtra(EXTRA_QR_CODE_BYTES, spBytes)
            intent.putExtra(EXTRA_PASSWORD, password)
            return intent
        }



    }





    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
                finish()
            }
        }

     fun onProcessingResultAvailable(result: ActiveFaceCaptureProcessingResult) {
        Log.d(
            "onProcessingResultAvailable",
            "new process res available Directional: ${result.directionalScores}"
        )

        if (session.isCompleted()) {
            // Haptic feedback
            vibrate()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        onFaceCaptureCompleted()
                    } catch (e: SenseCryptSdkException) {
                        withContext(Dispatchers.Main) {
                            Log.d("FaceCapture", "Face capture completed")
//                            showErrorInUIThread(e)
                        }
                    }
                }
            }
        }

        runOnUiThread {
            updateUI(result)
        }
    }

    private fun vibrate() {
        // vibration logic here (you can adapt from your previous code)
    }

    fun onProcessingError(exception: SenseCryptSdkException) {
        showErrorInUIThread(exception)
    }

    private fun onFaceCaptureCompleted() {
        Log.d("FaceCapture", "Face capture completed")
        // Handle completion logic here
    }

    private fun updateUI(result: ActiveFaceCaptureProcessingResult) {
        // Update your UI with the result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create PreviewView dynamically
        previewView = PreviewView(this)
        previewView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        setContentView(previewView)

        // Request camera permission or start camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        println("Created intent for PreScanningActiveCaptureActivity: $intent")
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                session = MainActivity.getActiveFaceCaptureSession()
                resetUI()
            } catch (e: SenseCryptSdkException) {
//                    showErrorInUIThread(e)
                return@addListener
            }

            val executor = Executors.newSingleThreadExecutor()

            val imageAnalysis =
                ImageAnalysis
                    .Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setOutputImageRotationEnabled(true)
                    .build()
                    .also {
                        captureAnalyzer =
                            ActiveFaceCaptureAnalyzer(
                                this,
                                session = session,
                            )
                        it.setAnalyzer(
                            executor,
                            captureAnalyzer,
                        )
                    }





            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA, // use front camera
                    preview,
                    imageAnalysis
                )


            } catch (exc: Exception) {
                Toast.makeText(this, "Error starting camera: ${exc.message}", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }



    private fun resetUI() {
        println("UI Reset")
    }


}
