package com.sssl.seqrgraphdemo.utils;

import android.content.Context;


import com.sssl.seqrgraphdemo.R;

import ai.tech5.sdk.abis.cryptograph.ResultCode;

public class CryptographResultCode {

    private int resultCode = -1;
    private Context context;

    public CryptographResultCode(int resultCode) {
        this.resultCode = resultCode;
    }


    public UiText getErrorDescription() {

        if (resultCode == ResultCode.successful) {
            return UiText.stringResource(R.string.decode_successful);
        } else if (resultCode == ResultCode.errorNoInit) {
            return UiText.stringResource(R.string.crypto_err_no_init, resultCode);
        } else if (resultCode == ResultCode.errorLowMemory) {
            return UiText.stringResource(R.string.crypto_err_low_memory, resultCode);
        } else if (resultCode == ResultCode.errorBadLicense) {
            return UiText.stringResource(R.string.crypto_err_bad_license, resultCode);
        } else if (resultCode == ResultCode.errorLicenseExpired) {
            return UiText.stringResource(R.string.crypto_err_license_expired, resultCode);
        } else if (resultCode == ResultCode.errorWrongParameters) {
            return UiText.stringResource(R.string.crypto_err_wrong_params, resultCode);
        } else if (resultCode == ResultCode.errorLowMemoryResult) {
            return UiText.stringResource(R.string.crypto_err_low_memory_result, resultCode);
        } else if (resultCode == ResultCode.errorAmallData) {
            return UiText.stringResource(R.string.crypto_err_small_data, resultCode);
        } else if (resultCode == ResultCode.errorBigData) {
            return UiText.stringResource(R.string.crypto_err_big_data, resultCode);
        } else if (resultCode == ResultCode.errorEncode) {
            return UiText.stringResource(R.string.crypto_err_encode, resultCode);
        } else if (resultCode == ResultCode.errorDecode) {
            return UiText.stringResource(R.string.crypto_err_decode, resultCode);
        } else if (resultCode == ResultCode.errorSymmetricKey) {
            return UiText.stringResource(R.string.crypto_err_sym_key, resultCode);
        } else if (resultCode == ResultCode.errorKey) {
            return UiText.stringResource(R.string.crypto_err_key, resultCode);
        } else if (resultCode == ResultCode.errorBarcodeEmpty) {
            return UiText.stringResource(R.string.crypto_err_empty_barcode, resultCode);
        } else if (resultCode == ResultCode.errorRowsCount) {
            return UiText.stringResource(R.string.crypto_err_row_count, resultCode);
        } else if (resultCode == ResultCode.errorColsCount) {
            return UiText.stringResource(R.string.crypto_err_column_count, resultCode);
        } else if (resultCode == ResultCode.errorBlocksCount) {
            return UiText.stringResource(R.string.crypto_err_block_count, resultCode);
        } else if (resultCode == ResultCode.errorLoadEncryptingKey) {
            return UiText.stringResource(R.string.crypto_err_load_enc_key, resultCode);
        } else if (resultCode == ResultCode.errorCreatedEncryptingKey) {
            return UiText.stringResource(R.string.crypto_err_created_enc_key, resultCode);
        } else if (resultCode == ResultCode.errorNoEncryptingKey) {
            return UiText.stringResource(R.string.crypto_err_no_enc_key, resultCode);
        } else if (resultCode == ResultCode.errorBadCompactData) {
            return UiText.stringResource(R.string.crypto_err_bad_compact_data, resultCode);
        } else if (resultCode == ResultCode.errorJNI) {
            return UiText.stringResource(R.string.crypto_err_jni_error, resultCode);
        } else if (resultCode == ResultCode.errorBadKey) {
            return UiText.stringResource(R.string.crypto_err_bad_key, resultCode);
        } else if (resultCode == ResultCode.errorBigBarcode) {
            return UiText.stringResource(R.string.crypto_err_big_barcode, resultCode);
        } else if (resultCode == ResultCode.errorWrongExpirytime) {
            return UiText.stringResource(R.string.crypto_err_wrong_expiry_time, resultCode);
        } else if (resultCode == ResultCode.errorDataExpired) {
            return UiText.stringResource(R.string.crypto_err_data_expired);
        }


        return UiText.stringResource(R.string.crypto_err_unknown, resultCode);
    }


}
