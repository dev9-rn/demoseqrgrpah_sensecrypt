package com.example.sdksetup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sdksetup.ActivityPreScanningFrontCameraBinding

class PreScanningActiveCaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreScanningFrontCameraBinding



    companion object {
        /**
         * Creates an intent for launching PreScanningActiveCaptureActivity
         *
         * @param context - activity from which the intent is created
         * @param spBytes - QR code bytes (SensePrint)
         * @param password - password to decrypt the QR code
         *
         * @return Intent - the intent to start the activity
         */
        fun newIntent(
            context: Context,
            spBytes: ByteArray,
            password: String?
        ): Intent {
            return Intent(context, PreScanningActiveCaptureActivity::class.java).apply {
                putExtra(ActiveFaceCaptureActivity.EXTRA_QR_CODE_BYTES, spBytes)
                putExtra(ActiveFaceCaptureActivity.EXTRA_PASSWORD, password)
            }
        }
    }
}
