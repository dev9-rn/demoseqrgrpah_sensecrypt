package com.example.sdksetup

class Constants {
    companion object {
        val SERVER_URL = ""
        val AUTH_HEADER = ""
        /** A verifier auth key can be set while generating a SensePrint QR code.
         * If it has been set, then the SenseCrypt SDK will need to be initialized
         * with the same verifier auth key.
         *
         * Set this to the unique value that you will use while creating the
         * SenseCrypt QR code that can be scanned by the SenseCrypt SDK
         *
         * Leave this null if you do not want to use a verifier auth key
         * When this is null, any verifier with an unset verifier auth key
         * will be able to use the SenseCrypt SDK
         */
        val SENSEPRINT_VERIFIER_AUTH_KEY: String? = null
    }
}