package com.example.sdksetup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream
import java.io.Serializable

class PersonDetailActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_FACE_CROP = "extra_face_crop"
        private const val EXTRA_METADATA = "extra_metadata"

        // Factory method to create intent
        fun newIntent(
            activity: AppCompatActivity,
            faceCrop: ByteArray,
            metadata: Serializable
        ) = android.content.Intent(activity, PersonDetailActivity::class.java).apply {
            putExtra(EXTRA_FACE_CROP, faceCrop)
            putExtra(EXTRA_METADATA, metadata)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create ScrollView as root to allow scrolling for many metadata entries
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        scrollView.addView(mainLayout)

        // Get faceCrop and convert to Bitmap
        val faceCropBytes = intent.getByteArrayExtra(EXTRA_FACE_CROP)
        faceCropBytes?.let {
            val bitmap: Bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(it))
            val imageView = ImageView(this)
            imageView.setImageBitmap(bitmap)
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600 // fixed height for face image
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            mainLayout.addView(imageView)
        }

        // Get metadata
        val metadata = intent.getSerializableExtra(EXTRA_METADATA) as? HashMap<String, String>
        metadata?.forEach { (key, value) ->
            val textView = TextView(this)
            textView.text = "$key: $value"
            textView.textSize = 18f
            textView.setPadding(0, 16, 0, 0)
            mainLayout.addView(textView)
        }

        setContentView(scrollView)
    }
}
