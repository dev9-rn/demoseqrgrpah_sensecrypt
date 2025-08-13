package com.example.sdksetup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import android.widget.TextView
import android.widget.LinearLayout
import android.graphics.Color
import android.view.Gravity

import androidx.annotation.RequiresPermission
import com.example.sdksetup.MainActivity.Companion.toSerializable
import com.sensecrypt.sdk.core.DecryptedSensePrintData
import com.sensecrypt.sdk.core.ActiveFaceCaptureStateName
import com.sensecrypt.sdk.core.HeadPose
import com.example.sdksetup.SessionHolder

class ActiveFaceCaptureActivity : AppCompatActivity() {

    private lateinit var tvInstructions: TextView
    private lateinit var previewView: PreviewView
    lateinit var session: ActiveFaceCaptureSession

    /**
     * The vibrator service
     */
    private lateinit var vibrator: Vibrator

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
                            showErrorInUIThread(e)
                        }
                    }
                }
            }
        }

        runOnUiThread {
            updateUI(result)
        }
    }

    private fun showErrorInUIThread(exception: Exception) {
        runOnUiThread {
            val msg = exception.message ?: "Unknown error occurred"
            Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
            Log.d("showErrorInUIThread", "Showing error toast: $msg")
        }
    }


    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // backward compatibility for Android API < 26
            // noinspection deprecation
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    fun onProcessingError(exception: SenseCryptSdkException) {
        showErrorInUIThread(exception)
    }



    /**
     * Move to the next activity when the face capture session is complete
     */
    fun onFaceCaptureCompleted() {
        // It is possible that the session has not been initialized when
        // this function is called back from an error dialog, therefore
        // we check and restart the session if needed


        // We have a suitable image. We can launch the next activity
        // If QR code bytes are set, we need to decrypt the SensePrint
        if (intent.hasExtra(EXTRA_QR_CODE_BYTES)) {
            val qrCodeBytes =
                intent.getByteArrayExtra(EXTRA_QR_CODE_BYTES)
            val password =
                intent.getStringExtra(EXTRA_PASSWORD)
            var parsedData: DecryptedSensePrintData? = null

            // This exception will be set by the in thread lambda
            var exception: SenseCryptSdkException? = null

            if (qrCodeBytes != null) {
                Log.d(
                    "IS_INIT", com.sensecrypt.sdk.core
                        .isInitialized().toString()
                )
                runOnUiThread {


                    try {
                        session.finalize()
                        parsedData =
                            MainActivity.verifySensePrint(
                                this,
                                qrCodeBytes,
                                password,
                                SessionHolder(session),
                            )
                        Log.d("QRCodeBytes","$qrCodeBytes")
                    } catch (e: SenseCryptSdkException) {
                        exception = e
                        Log.e("VerifyError", "Exception occurred", e)
                    }

                    if (exception != null) {
                        showErrorInUIThread(exception!!)
                    } else if (parsedData != null) {
                        val metadata = parsedData!!.metadata.toSerializable()
                        val faceCrop = parsedData!!.faceCrop
                        println("final done $metadata")

                        if (faceCrop != null && metadata != null) {
                            startActivity(
                                PersonDetailActivity.newIntent(
                                    this@ActiveFaceCaptureActivity,
                                    faceCrop,
                                    metadata
                                )
                            )
                        } else {
                            Log.e("LaunchDetail", "faceCrop or metadata is null")
                        }


                    } else {
                        Log.w("VerifyResult", "No error, but parsedData is null")
                    }

                    }

            }
        } else {
            println("error while completing")
        }
    }

    /**
     * Update UI based on a processing result
     *
     * @param result the processing result from the capture analyzer
     */
    private fun updateUI(result: ActiveFaceCaptureProcessingResult) {
        val actionLivenessName = result.expectedUserAction
        // Update the instruction based on the action liveness name
        updateInstructions(actionLivenessName, result)
        // Update the circle color based on the action liveness name
//        updateCircleColor(actionLivenessName, result)
//        updateProgressTicks(result)
//        updateAnimations(actionLivenessName, result)
    }

    /**
     * Update instructions based on the Active Face Capture State
     *
     * @param activeFaceCaptureState the state name of the Active Face Capture session
     * @param result the processing result from the capture analyzer
     */
    private fun updateInstructions(
        activeFaceCaptureState: ActiveFaceCaptureStateName,
        result: ActiveFaceCaptureProcessingResult,
    ) {
        val instructionText =
            when {
                // Show the face scan complete text
                activeFaceCaptureState == ActiveFaceCaptureStateName.ACTIVE_FACE_CAPTURE_COMPLETE ->
                    R.string.face_scan_complete

                // When state is WAITING_FOR_FIRST_CENTERED_FACE, tell the user to center their face
                activeFaceCaptureState in MainActivity.showCurrentHeadPose ->
                    MainActivity.currentHeadPoseInstructionsMap[result.currentHeadPose]
                        ?: R.string.center_your_face

                // When state is USER_SHOULD_STAY_STILL, and HeadPose is not NORMAL, show the head pose instruction
                activeFaceCaptureState in MainActivity.checkHeadPose && result.currentHeadPose != HeadPose.NORMAL ->
                    MainActivity.currentHeadPoseInstructionsMap[result.currentHeadPose]
                        ?: R.string.center_your_face

                // Tell the user to center their face
                else ->
                    MainActivity.activeFaceCaptureTextMap[activeFaceCaptureState]
                        ?: R.string.center_your_face
            }
        tvInstructions.text = getString(instructionText)


        println("aaaaaaaaaaa $instructionText")
        println("bbbbbbbb $ActiveFaceCaptureStateName")
        println("ccccccc $result.currentHeadPose")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create parent vertical layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Camera preview (half screen height)
        previewView = PreviewView(this)
        val screenHeight = resources.displayMetrics.heightPixels
        previewView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            screenHeight / 2
        )
        mainLayout.addView(previewView)

        // Instruction text
        tvInstructions = TextView(this).apply {
            text = "Center your face"
            textSize = 18f
            setTextColor(Color.BLACK) // visible on white background
            setPadding(0, 32, 0, 0) // padding top for spacing from camera
            gravity = Gravity.CENTER
        }
        mainLayout.addView(tvInstructions)

        // Set layout
        setContentView(mainLayout)

        // Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(VIBRATOR_SERVICE) as Vibrator
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
                    showErrorInUIThread(e)
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
