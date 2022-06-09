package com.rz.tbcheck.activity

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rz.tbcheck.R
import com.rz.tbcheck.config.rotateBitmap
import com.rz.tbcheck.databinding.ActivityMainBinding
import com.rz.tbcheck.ml.Model
import com.rz.tbcheck.viewmodel.MainViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var getFile: File? = null
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityMainBinding

    private var isHaveImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClick()

        mainViewModel.floatArrayResult.observe(this) {
            Log.d(TAG, "onCreate: " + it[0])
        }

        mainViewModel.isLoading.observe(this) {
            if (!it) binding.btnCheck.isEnabled = true
        }
    }

    private fun setClick() {
        binding.apply {
            ivImage.setOnClickListener {
                if (!allPermissionsGranted()) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        REQUIRED_PERMISSIONS,
                        REQUEST_CODE_PERMISSIONS
                    )
                } else {
                    showDialog()
                }
            }

            btnCheck.setOnClickListener {
                if (isHaveImage) {
                    val model = Model.newInstance(this@MainActivity)
                    mainViewModel.checkIt(model, bitmap)
                    btnCheck.isEnabled = false
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "You must have to choose image.",
                        Toast.LENGTH_LONG
                    ).show()
                    showDialog()
                }
            }
        }
    }

    private fun showDialog() {
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose media:")

        // add a list
        val listMedia = arrayOf(
            resources.getString(R.string.str_take_using_camera),
            resources.getString(R.string.str_pick_from_gallery)
        )

        builder.setItems(listMedia) { _, which ->
            when (which) {
                0 -> {
                    val intent = Intent(this@MainActivity, OpenCameraActivity::class.java)
                    launchForResult.launch(intent)
                }
                1 -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    launchForResult.launch(intent)
                }
            }
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private lateinit var inputImageBuffer: TensorImage
    private fun loadImage(bitmap: Bitmap): TensorImage {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap)

        // Creates processor for the TensorImage.
        val cropSize = Math.min(bitmap.width, bitmap.height)

        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(227, 227, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()
        return imageProcessor.process(inputImageBuffer)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.str_didnt_have_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launchForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == TAKE_IMAGE_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            bitmap = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.ivImage.setImageBitmap(bitmap)
            isHaveImage = true
        } else {
            val imageUri = it.data?.data

            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    imageUri
                )
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                ImageDecoder.decodeBitmap(source)
            }

            try {
                binding.ivImage.setImageBitmap(bitmap)
                isHaveImage = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val TAKE_IMAGE_RESULT = 201

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}