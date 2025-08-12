package com.example.sdksetup

/**
 * Interface for listening to QR code scanning results.
 */
interface QRCodeReaderListener {
    /**
     * Called after a QR code has been scanned and the bytes
     * of the scanned code are available.
     *
     * @param qrBytes The QR bytes (after reading a QR code)
     */
    fun onQRCodeBytesAvailable(qrBytes: ByteArray)

    /**
     * Called when an error occurs while processing the QR code.
     */
    fun onProcessingError()
}