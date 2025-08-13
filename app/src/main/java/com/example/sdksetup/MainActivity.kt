package com.example.sdksetup

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.sensecrypt.sdk.core.ActiveFaceCaptureStateName
import com.sensecrypt.sdk.core.DecryptedSensePrintData
import com.sensecrypt.sdk.core.HeadPose
import com.sensecrypt.sdk.core.LivenessErrorReason
import com.sensecrypt.sdk.core.SenseCryptSdkException
import com.sensecrypt.sdk.core.initOfflineSdk
import com.sensecrypt.sdk.core.SensePrintInfo
import com.sensecrypt.sdk.core.SensePrintRawVerificationMobileRequest
import com.sensecrypt.sdk.core.parseSenseprintBytes
import com.example.sdksetup.SessionHolder
import com.sensecrypt.sdk.core.LivenessToleranceSchema
import com.sensecrypt.sdk.core.OsSchema
import com.sensecrypt.sdk.core.checkLiveness

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

        val showCurrentHeadPose =
            setOf(
                ActiveFaceCaptureStateName.WAITING_FOR_FIRST_CENTERED_FACE,
            )

        /**
         * Get the resource id for the string for a head pose
         */
        val currentHeadPoseInstructionsMap =
            mapOf(
                HeadPose.NORMAL to R.string.empty_string,
                HeadPose.TOO_FAR to R.string.move_closer,
                HeadPose.LOOKING_LEFT to R.string.look_left,
                HeadPose.LOOKING_RIGHT to R.string.look_right,
                HeadPose.LOOKING_UP to R.string.look_down,
                HeadPose.LOOKING_DOWN to R.string.look_up,
                HeadPose.TILTED_LEFT to R.string.look_straight,
                HeadPose.TILTED_RIGHT to R.string.look_straight,
                HeadPose.TOO_CLOSE to R.string.move_further,
                HeadPose.NOT_CENTERED to R.string.center_your_face,
                HeadPose.LOOKING_TOP_LEFT to R.string.look_straight,
                HeadPose.LOOKING_TOP_RIGHT to R.string.look_straight,
                HeadPose.LOOKING_BOTTOM_LEFT to R.string.look_straight,
                HeadPose.LOOKING_BOTTOM_RIGHT to R.string.look_straight,
            )

        /**
         * Get the resource id for the string for an active capture state name
         */
        val activeFaceCaptureTextMap =
            mapOf(
                ActiveFaceCaptureStateName.USER_SHOULD_STAY_STILL to R.string.stay_still,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_TO_THE_LEFT to R.string.look_left,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_TO_THE_RIGHT to R.string.look_right,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_UP to R.string.look_up,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_DOWN to R.string.look_down,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_TOP_LEFT to R.string.look_top_left,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_TOP_RIGHT to R.string.look_top_right,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_BOTTOM_LEFT to R.string.look_bottom_left,
                ActiveFaceCaptureStateName.USER_SHOULD_LOOK_BOTTOM_RIGHT to R.string.look_bottom_right,
                ActiveFaceCaptureStateName.USER_SHOULD_MOVE_CLOSER to R.string.move_closer,
                ActiveFaceCaptureStateName.USER_SHOULD_MOVE_FARTHER to R.string.move_further,
                ActiveFaceCaptureStateName.USER_SHOULD_CENTER_THEIR_FACE to R.string.center_your_face,
                ActiveFaceCaptureStateName.ACTIVE_FACE_CAPTURE_COMPLETE to R.string.we_got_all,
            )

        /**
         * Set of states that should show center face message based on the current head pose
         * (if the pose is not normal)
         */
        val checkHeadPose =
            setOf(
                ActiveFaceCaptureStateName.USER_SHOULD_STAY_STILL,
            )

        fun <K, V> Map<K, V>.toSerializable(): HashMap<String, String> {
            val map = HashMap<String, String>()
            for ((key, value) in this) {
                map[key as String] = value as String
            }
            return map
        }

        /**
         * Check if the face is live or not
         * @param imageBytes image byte data
         * @return true if face is live or if SDKMode is Online
         */
        @Throws(SenseCryptSdkException::class)
        fun isLiveFace(imageBytes: ByteArray,context: Context):Boolean {
            return try {
                checkLiveness(imageBytes,
                    OsSchema.ANDROID,
                    LivenessToleranceSchema.REGULAR,Constants.LIVENESS_THRESHOLD)

            } catch (e: Exception) {
                throw e
            }

        }

        /**
         * Decrypt the SensePrint data using the capture session, and the QR code bytes
         * (SensePrint bytes)
         *
         * @param sensePrintBytes The SensePrint bytes (read from the QR code)
         * @param password The password
         * @param sessionHolder The session holder containing the active or passive face capture session
         *
         * @return The parsed (and decrypted) SensePrint data
         * @throws SenseCryptSdkException
         */
        @Throws(SenseCryptSdkException::class)
        fun verifySensePrint(
            context: Context,
            sensePrintBytes: ByteArray,
            password: String?,
            sessionHolder: SessionHolder,
        ): DecryptedSensePrintData {
            // Get the best face from the session
            val face = sessionHolder.getBestFrame()

            val spInfo = parseSensePrintBytes(sensePrintBytes)
            if (spInfo != null && spInfo.isLivenessEnabled) {
                /**
                 * If sdk is running in offline mode then the liveness check is done locally
                 */

                    try {
                        val isLiveFace = isLiveFace(face, context)
                        print("Liveness check result: $isLiveFace")

                        if (!isLiveFace) {
                            throw SenseCryptSdkException.LivenessFailed(LivenessErrorReason.UNKNOWN_ERROR)
                        }
                    } catch (e: SenseCryptSdkException) {
                        Log.d("Liveness Failed", "Liveness check result had an exception: ${e.message}")
                        throw e
                    }

            }

            val requestSchema =
                SensePrintRawVerificationMobileRequest(
                    password = password,
                    senseprint = sensePrintBytes,
                    verifiersAuthKey = Constants.SENSEPRINT_VERIFIER_AUTH_KEY,
                    livenessTolerance = null,
                )

            return try {
                sessionHolder.verifySensePrint(requestSchema)
            } catch (e: SenseCryptSdkException) {
                throw e
            }
        }
    }

    /**
     * Set of states that should show center face message based on the current head pose
     * (regardless of the pose)
     */


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
