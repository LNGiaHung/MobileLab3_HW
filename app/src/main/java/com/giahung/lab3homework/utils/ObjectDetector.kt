package com.giahung.lab3homework.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject as MLKitDetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await

data class DetectedObject(
    val label: String,
    val confidence: Float,
    val boundingBox: RectF
)

class ObjectDetector(private val context: Context) {
    private val imageLoader = ImageLoader(context)
    private val detector: com.google.mlkit.vision.objects.ObjectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
    )

    suspend fun detectObjects(imageUrl: String): List<DetectedObject> {
        return try {
            // Download the bitmap using Coil
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            val result = imageLoader.execute(request)
            val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                ?: return emptyList()

            // Prepare the image for ML Kit
            val image = InputImage.fromBitmap(bitmap, 0)

            // Process the image for object detection
            val results = detector.process(image).await()

            // Map the results to our custom DetectedObject
            results.mapNotNull { detectedObject: MLKitDetectedObject ->
                detectedObject.labels.firstOrNull()?.let { label: MLKitDetectedObject.Label ->
                    DetectedObject(
                        label = label.text,
                        confidence = label.confidence,
                        boundingBox = RectF(detectedObject.boundingBox)
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}