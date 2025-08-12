package com.example.sdksetup

import androidx.appcompat.app.AppCompatActivity

class ActiveFaceCaptureActivity : AppCompatActivity() {
    companion object {


        /**
         * Intent extra for QR code data
         */
        const val EXTRA_QR_CODE_BYTES = "com.sensecrypt.sdk.EXTRA_QR_CODE_BYTES"

        /**
         * Intent extra for password
         */
        const val EXTRA_PASSWORD = "com.sensecrypt.sdk.EXTRA_PASSWORD"


    }
}