package com.example.sdksetup

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.nio.ByteBuffer


/**
 * Utility class for image operations
 */
class ImageUtil {
    companion object {
        /**
         * Convert a ByteBuffer to a ByteArray
         *
         * @return The byte array
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind() // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data) // Copy the buffer into a byte array
            return data.clone() // Return the byte array
        }

        /**
         * Get a square center crop bitmap from an ImageProxy
         * @param image The ImageProxy
         * @param isFrontCamera Whether the camera is facing front (will mirror the image)
         *
         * @return The center crop bitmap
         */
        fun getCenterCropBitmap(
            image: ImageProxy,
            isFrontCamera: Boolean,
        ): Bitmap {
            // Convert to bitmap
            val imageBitmap = image.toBitmap()
            return getCenterCropBitmap(imageBitmap, isFrontCamera)
        }

        /**
         * Get a square center crop bitmap from a bitmap
         *
         * @param imageBitmap The bitmap
         * @param isFrontCamera Whether the camera is facing front (this will mirror the image)
         *
         * @return The center crop bitmap
         */
        fun getCenterCropBitmap(
            bitmap: Bitmap,
            isFrontCamera: Boolean,
        ): Bitmap {
            val matrix = Matrix()

            // Since we are showing a mirrored preview, we need to flip the image in the x-axis
            if (isFrontCamera) matrix.preScale(-1f, 1f)

            val width = bitmap.width
            val height = bitmap.height
            val size = minOf(width, height)
            val x = (width - size) / 2
            val y = (height - size) / 2
            return Bitmap.createBitmap(bitmap, x, y, size, size, matrix, true)
        }

        /**
         * Convert a bitmap to a byte array
         *
         * @param bitmap The bitmap
         * @return The byte array
         */
        @Synchronized
        fun bitMap2ByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            return stream.toByteArray()
        }

        /**
         * Create a bitmap from an RGBA byte array
         *
         * @param bytes The byte array
         * @param width The width of the image
         * @param height The height of the image
         */
        private fun createBitmapFromRGBA(
            bytes: ByteArray,
            width: Int,
            height: Int,
        ): Bitmap {
            // Create a ByteBuffer from the byte array
            val buffer = ByteBuffer.wrap(bytes)

            // Create a Bitmap using BitmapFactory
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(buffer)
            return bitmap
        }

        /**
         * Convert a byte array to a bitmap
         *
         * @param byteArray The byte array
         * @return The bitmap
         */
        @Synchronized
        fun byteArray2Bitmap(byteArray: ByteArray): Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        fun getBitmapFromUri(
            contentResolver: ContentResolver,
            uri: Uri,
        ): Bitmap? =
            try {
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }

        fun createQRCodeJpeg(rawBytes: ByteArray): ByteArray {

            // Set QR code parameters
            val hints: MutableMap<EncodeHintType, Any> = HashMap()
            hints[EncodeHintType.CHARACTER_SET] = "ISO-8859-1"
            hints[EncodeHintType.MARGIN] = 2

            val data = String(rawBytes, charset("ISO-8859-1"))

            // Generate BitMatrix from the data
            val bitMatrix = MultiFormatWriter().encode(
                data,
                BarcodeFormat.QR_CODE,
                400,
                400,
                hints
            )

            // Create a bitmap from the BitMatrix
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height

            val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
            for (x in 0 until matrixWidth) {
                for (y in 0 until matrixHeight) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            return bitMap2ByteArray(bitmap)
        }
    }
}