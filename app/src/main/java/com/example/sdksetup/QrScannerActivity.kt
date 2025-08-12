package com.example.sdksetup

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.sensecrypt.sdk.core.SensePrintInfo
import com.sensecrypt.sdk.core.SensePrintType
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import zxingcpp.BarcodeReader
import com.sensecrypt.sdk.core.SenseCryptSdkException
import android.content.Intent


class QrScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var isQRScanComplete: Boolean = false  // Control flag
    private val barCodeReader = BarcodeReader()
    lateinit var qrAnalyzer: QrAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    startCamera()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    runOnUiThread {
                        Toast.makeText(
                            this@QrScannerActivity,
                            "Camera permission is needed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    finish()
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

//            val analyzer = ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { text, rawBytes ->
//                        runOnUiThread {
//                            if (!isQRScanComplete) {
//                                Toast.makeText(this, "QR Code Detected: $text", Toast.LENGTH_LONG).show()
//                                Log.d("QrScanner", "QR text: $text")
//
//
//                                try {
//                                    Log.d("QrScanner", "Raw bytes: $rawBytes")
//                                    val spInfo = MainActivity.parseSensePrintBytes(rawBytes)
//                                    processQR(spInfo, rawBytes)
//
//                                } catch (e: Exception) {
//                                    Log.e("QrScanner", "Invalid SenseCrypt QR: ${e.message}")
//                                    showInvalidQr()
//                                }
//                            }
//                        }
//                    })
//                }

            val executor = Executors.newSingleThreadExecutor()

            val resolutionSelector =
                ResolutionSelector
                    .Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy.HIGHEST_AVAILABLE_STRATEGY,
                    ).build()

            val analyzer =
                ImageAnalysis
                    .Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    // .setBackgroundExecutor(executor)
                    .setResolutionSelector(resolutionSelector)
                    .build()
                    .also {
                        qrAnalyzer = QrAnalyzer(barCodeReader, this)
                        it.setAnalyzer(
                            executor,
                            qrAnalyzer,
                        )
                    }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch (exc: Exception) {
                Log.e("QrScannerActivity", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }



    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /**
     * This method is used to handle the QR code bytes once they are available.
     *
     * @param qrBytes - the SensePrint bytes (decoded from the QR code)
     */
    fun onQRCodeBytesAvailable(qrBytes: ByteArray) {
        if (isQRScanComplete) {
            return
        }
        try {
            val sensePrintInfo = MainActivity.parseSensePrintBytes(qrBytes)
            processQR(sensePrintInfo, qrBytes)
            println("Invalid SensePrint Byte")
            qrBytes.forEach {
                print(it)
            }
        } catch (e: SenseCryptSdkException) {
            runOnUiThread {
                // To avoid showing multiple dialogs
                isQRScanComplete = true
                showErrorDialog(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(true)
        builder.show()
    }



    // ✅ Add this method to handle QR logic
    private fun processQR(spInfo: SensePrintInfo?, spBytes: ByteArray) {
        if (spInfo == null) {
            showInvalidQr()
        } else {

            println("-------fianl $spBytes")
            isQRScanComplete = true
            var isPasswordRequired = spInfo.spType == SensePrintType.WITH_PASSWORD

            if (isClearTextRequest()) {
                isPasswordRequired = false
            }

            if (isPasswordRequired) {
                runOnUiThread {
                    Log.e("QR Scanner Activity", "Password Required")
                    showPasswordDialog(spInfo, spBytes)
                }
            } else {
                navigateToNextActivity(spInfo, spBytes, null)
            }
        }
    }

    // Stub methods – replace with real logic
    private fun showInvalidQr() {
        runOnUiThread {
            Toast.makeText(this, "Invalid SenseCrypt QR", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isClearTextRequest(): Boolean {
        // Replace with your real logic
        return false
    }

    private fun showPasswordDialog(info: SensePrintInfo, bytes: ByteArray) {
        runOnUiThread {
            // Replace with your UI dialog to get password input
            Toast.makeText(this, "Password dialog should appear here.", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun navigateToNextActivity(info: SensePrintInfo, bytes: ByteArray, password: String?) {
//        runOnUiThread {
//            // Replace with your navigation logic
//            Toast.makeText(this, "Navigating to next activity...", Toast.LENGTH_SHORT).show()
//        }
//    }

    /**
     * For navigating to next activity based on qr scan type
     *
     * @param spInfo - the SensePrintInfo object
     * @param spBytes - the SensePrint bytes
     * @param password - the password to decrypt the QR code
     */
    private fun navigateToNextActivity(
        spInfo: SensePrintInfo,
        spBytes: ByteArray,
        password: String?,
    ) {

            // We are done with the scan open the FaceScanActivity
            println("getVerificationCaptureMode -------------")
            if (KeyValueStore().getVerificationCaptureMode(this) == FaceCaptureMode.ActiveCaptureFrontCamera) {
                println("getVerificationCaptureMode -------------11111")
                // 🔹 Redirect directly to ActiveFaceCaptureActivity
                val intent = Intent(this, ActiveFaceCaptureActivity::class.java).apply {
                    putExtra(ActiveFaceCaptureActivity.EXTRA_QR_CODE_BYTES, spBytes)
                    putExtra(ActiveFaceCaptureActivity.EXTRA_PASSWORD, password)
                }
                startActivity(intent)
            } else {
                println("getVerificationCaptureMode -------------12222221")
//                startActivity(
//                    PreScanningPassiveCaptureActivity.newIntentForVerification(
//                        this,
//                        spBytes,
//                        password,
//                    ),
//                )
            }

    }
}

// ✅ Modified to return both text and raw bytes
private class QrCodeAnalyzer(
    private val onQrCodeDetected: (String, ByteArray) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
            DecodeHintType.TRY_HARDER to true
        )
        setHints(hints)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            try {
                val buffer: ByteBuffer = mediaImage.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)

                val width = mediaImage.width
                val height = mediaImage.height

                val source = PlanarYUVLuminanceSource(
                    bytes, width, height, 0, 0, width, height, false
                )
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                val result = reader.decodeWithState(binaryBitmap)
                onQrCodeDetected(result.text, result.rawBytes)

            } catch (e: NotFoundException) {
                // QR code not found
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                imageProxy.close()
            }
        } else {
            imageProxy.close()
        }
    }
}
