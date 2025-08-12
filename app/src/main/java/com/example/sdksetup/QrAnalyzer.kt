package com.example.sdksetup

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import zxingcpp.BarcodeReader

/**
 * An image analyzer to analyze the QR code
 */
class QrAnalyzer(
    /**
     * The barcode reader
     */
    private val barCodeReader: BarcodeReader,
    /**
     * The listener for the QR code reader
     */
    private val qrScanListener: QrScannerActivity,
) : ImageAnalysis.Analyzer {
    /**
     * Analyzes the image and processes it for scanning QR codes
     *
     * @param image The image to be analyzed
     */
    override fun analyze(image: ImageProxy) {
        image.use {
            // check qr code is located within scanner view
            // val rect = Rect(0, 0, image.width, image.height)
            val result = barCodeReader.read(image)
            if (result.isNotEmpty()) {
                val qrResult = result[0]
                qrResult.bytes?.let {
                    qrScanListener.onQRCodeBytesAvailable(it)
                }
            }
        }
    }

    /**
     * Analyzes the image and processes to know whether the QR code is Senseprint or not
     * @param image The selected image to be analyzed
     */
    fun analyzeQRCodeFromGallery(image: Bitmap) {
        val result = barCodeReader.read(image)
        if (result.isNotEmpty()) {
            val qrResult = result[0]
            qrResult.bytes?.let {
                qrScanListener.onQRCodeBytesAvailable(it)
            } ?: run {
//                qrScanListener.onProcessingError()
            }
        } else {
//            qrScanListener.onProcessingError()
        }
    }
}