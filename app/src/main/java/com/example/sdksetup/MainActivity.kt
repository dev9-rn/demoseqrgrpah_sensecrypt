package com.example.sdksetup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sdksetup.ui.theme.SdksetupTheme
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.sensecrypt.sdk.core.ActiveFaceCaptureSession
import com.sensecrypt.sdk.core.SenseCryptSdkException
import com.sensecrypt.sdk.core.initOfflineSdk
import com.sensecrypt.sdk.core.SensePrintInfo
import com.sensecrypt.sdk.core.parseSenseprintBytes

class MainActivity : ComponentActivity() {

    companion object {


        /**
         * Gets a new active face capture session
         *
         * @return The active face capture session
         * @throws SenseCryptSdkException
         */
        @kotlin.jvm.Throws(SenseCryptSdkException::class)
        fun getActiveFaceCaptureSession(): ActiveFaceCaptureSession {
            try {
                return ActiveFaceCaptureSession()
            } catch (e: SenseCryptSdkException) {
                throw e
            }
        }


        /**
         * Get the QR code info from the decoded QR code bytes.
         * This doesn't decrypt the SensePrint.
         *
         * @param qrCodeDecodedBytes The QR code image bytes
         *
         * @return The QR code info
         * @throws SenseCryptSdkException
         */
        @Throws(SenseCryptSdkException::class)
        fun parseSensePrintBytes(qrCodeDecodedBytes: ByteArray): SensePrintInfo? =
            parseSenseprintBytes(
                qrCodeDecodedBytes,
                Constants.SENSEPRINT_VERIFIER_AUTH_KEY,
            )
    }

    private var permissionGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initSDK(this)

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    permissionGranted = true
                    showContent()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        this@MainActivity,
                        "Camera permission is required to scan QR codes",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun showContent() {
        setContent {
            SdksetupTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        onScanClick = {
                            if (permissionGranted) {
                                val intent = Intent(this@MainActivity, QrScannerActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun initSDK(context: Context) {
    try {
        initOfflineSdk(modelPath = "")
    } catch (e: SenseCryptSdkException) {
        println("Error while initing the SDK $e")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onScanClick: () -> Unit) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello $name!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onScanClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Scan QR Code")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SdksetupTheme {
        Greeting(name = "Android", onScanClick = {})
    }
}
