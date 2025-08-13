package com.example.sdksetup

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.sensecrypt.sdk.core.ActiveFaceCaptureProcessingResult
import com.sensecrypt.sdk.core.ActiveFaceCaptureSession
import com.sensecrypt.sdk.core.FrameFormat
import com.sensecrypt.sdk.core.FrameParams
import com.sensecrypt.sdk.core.SenseCryptSdkException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/**
 * This class is used to analyze the pose of the user's face.
 */
class ActiveFaceCaptureAnalyzer(
    /**
     * The listener for the active face capture session
     */
    private val listener: ActiveFaceCaptureActivity,
    /**
     * The active face capture session
     */
    private var session: ActiveFaceCaptureSession,
    /**
     * Flag to check if the first frame has been notified
     */
    private var isFirstFrameNotified: Boolean = false,
    private val mutex: Mutex = Mutex(),
) : ImageAnalysis.Analyzer {
    /**
     * A cached result for change detection
     */
    private var cachedResult: ActiveFaceCaptureProcessingResult? = null

    private fun isResultChanged(result: ActiveFaceCaptureProcessingResult): Boolean {
        if (cachedResult == null) {
            cachedResult = result
            return true
        }

        val prevResult = cachedResult!!
        cachedResult = result

        return prevResult != result
    }

    /**
     * Analyzes the image and processes it using the SenseCrypt SDK
     *
     * @param image The image to be analyzed
     */
    override fun analyze(image: ImageProxy) {
        // Applies a one time UI adjustment to the camera preview
        // to align the face circle overlay with the camera preview
        if (!isFirstFrameNotified) {
            //Need to put test case check
            Handler(Looper.getMainLooper()).post {
                isFirstFrameNotified = true
//                listener.onFirstFrameCaptured()
            }

        }

        if(session.isCompleted() || session.isErrorNotified()) {
            image.close()
            return
        }

        try {
            // Return an intermediate result if available
            val processingStatus = session.getProcessingStatus()

            // If the processing is in progress and the session is not completed
            if (processingStatus.isProcessing && !session.isCompleted()) {
                // Then just use the intermediate result
                val intermediateResult = processingStatus.intermediateResult

                intermediateResult?.let { result ->
                    listener.onProcessingResultAvailable(result)
                }
                image.close()
                return
            } else if (session.isCompleted()) {
                // If the session is complete, then return
                image.close()
                return
            } else if (session.isErrorNotified()) {
                image.close()
                return
            }
        } catch (e: SenseCryptSdkException) {
            // If there is an error, it would have already been notified
            // to the listener through the process method
            image.close()
            return
        }

        // Use CoroutineScope for launching coroutines
        CoroutineScope(Dispatchers.IO).launch {
            // crop image to 480 * 480
            val startTime = System.currentTimeMillis()
            val centerCropImage = ImageUtil.getCenterCropBitmap(image, true)
            val endTime = System.currentTimeMillis()
            Log.d("ActiveFaceCaptureAnalyzer", "Center crop time: ${endTime - startTime} ms")

            // Get raw ARGB8888 data from bitmap
            // Prepare a ByteBuffer to hold the pixel data
            val byteBuffer = ByteBuffer.allocate(centerCropImage.byteCount)
            // Copy the bitmap's pixel data directly to the buffer
            centerCropImage.copyPixelsToBuffer(byteBuffer)
            image.close()
            // pass image to sdk
            processFrame(byteBuffer.array(), centerCropImage.width, centerCropImage.height)
        }
    }

    /**
     * Processes the frame using the SenseCrypt SDK
     *
     * @param imgBytes The image bytes to be processed (As JPEG bytes)
     */
    private suspend fun processFrame(imgBytes: ByteArray, width: Int, height: Int) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                // If session is already completed or error is notified
                // then it can't process any more frames
                if(session.isCompleted() || session.isErrorNotified()) {
                    return@withContext
                }
                try {
                    val params = FrameParams(frameFormat = FrameFormat.A_RGB8888, width = width, height = height)
                    val result = session.processWithParams(imgBytes, params)
                    if(isResultChanged(result)) {
                        listener.onProcessingResultAvailable(result)
                    }
                } catch (e: SenseCryptSdkException) {
                    if (!session.isErrorNotified()) {
                        listener.onProcessingError(
                            e,
                        )
                    }
                }
            }
        }
    }
}