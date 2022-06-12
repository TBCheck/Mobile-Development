package com.rz.tbcheck.viewmodel

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rz.tbcheck.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {

    private val _floatArrayResult = MutableLiveData<FloatArray>()
    val floatArrayResult: LiveData<FloatArray> = _floatArrayResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    fun checkIt(model: Model, bitmap: Bitmap) {
        // Creates inputs for reference.
        inputImageBuffer = TensorImage(DataType.FLOAT32)
        inputImageBuffer = loadImage(bitmap)
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 227, 227, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(inputImageBuffer.buffer)

        Log.d("shape", inputFeature0.buffer.toString())

        // Runs model inference and gets result.
        executor.execute {
            try {
                val outputs = (model.process(inputFeature0))
                _floatArrayResult.postValue(outputs.outputFeature0AsTensorBuffer.floatArray)
                _isLoading.postValue(false)
                // Releases model resources if no longer used.
                model.close()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private lateinit var inputImageBuffer: TensorImage
    private fun loadImage(bitmap: Bitmap): TensorImage {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = bitmap.width.coerceAtMost(bitmap.height)

        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(227, 227, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()
        return imageProcessor.process(inputImageBuffer)
    }
}