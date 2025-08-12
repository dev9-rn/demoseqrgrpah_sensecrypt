package com.example.sdksetup

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.sdksetup.FaceCaptureMode
import java.util.Objects

/**
 * KeyValueStore is a class that stores key value pairs in encrypted shared preferences
 */
class KeyValueStore {
    /**
     * Key for gallery bucket id
     */
    private val keyGalleryBucketId = "KEY_GALLERY_BUCKET_ID"

    /**
     * Key for generation capture mode
     */
    private val keyGenerationCaptureMode = "KEY_GENERATION_CAPTURE_MODE"

    /**
     * Key for verification capture mode
     */
    private val keyVerificationCaptureMode = "KEY_VERIFICATION_CAPTURE_MODE"

    /**
     * Key for liveness capture mode for passive capture
     */
    private val keyLivenessCaptureModeForGeneration = "KEY_LIVENESS_CAPTURE_MODE_FOR_PASSIVE_CAPTURE"

    /**
     * key for showing the privacy policy dialog box on splash screen
     */
    private val keyPrivacyShown = "KEY_PRIVACY_SHOWN"

    /**
     * key for showing the SDK mode selection dialog box on splash screen
     */
    private val keySelectOnlineOrOfflineShown = "KEY_SDK_MODE_SELECTION_SHOWN"

    /**
     * key for showing the carousels activity
     */
    private val keyCarouseleShown = "KEY_CAROUSEL_SHOWN"

    /**
     * key for setting up the current sdk mode
     */
    private val keyCurrentSDKMode = "KEY_CURRENT_SDK_MODE"

    /**
     * Gets the encrypted shared preferences
     *
     * @param context the context
     * @return the encrypted shared preferences
     */
    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKey = getMasterKey(context)
        return EncryptedSharedPreferences.create(
            Objects.requireNonNull<Context>(context),
            "com.sensecrypt.sdk.online.utils.KeyValueStore",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /**
     * Gets the master key
     *
     * @param context the context
     */
    private fun getMasterKey(context: Context): MasterKey {
        val spec =
            KeyGenParameterSpec
                .Builder(
                    "_androidx_security_master_key_",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        return MasterKey.Builder(context).setKeyGenParameterSpec(spec).build()
    }



    /**
     * Gets the verification capture mode
     * @param context the context
     */
    fun getVerificationCaptureMode(context: Context): FaceCaptureMode {
        val mode: String? =
            getEncryptedSharedPreferences(context).getString(keyVerificationCaptureMode, FaceCaptureMode.ActiveCaptureFrontCamera.name)
        return FaceCaptureMode.valueOf(mode!!)
    }



}