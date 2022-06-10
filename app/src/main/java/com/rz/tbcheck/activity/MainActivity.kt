package com.rz.tbcheck.activity

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.rz.tbcheck.R
import com.rz.tbcheck.data.ApiResponse
import com.rz.tbcheck.databinding.ActivityMainBinding
import com.rz.tbcheck.ml.Model
import com.rz.tbcheck.viewmodel.MainViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var isHaveImage = false
    private var getFile: File? = null
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClick()

        mainViewModel.floatArrayResult.observe(this) {
            Log.d(TAG, "onCreate: " + it[0])
            val intent = Intent(this, DetailHistoryActivity::class.java)
            intent.putExtra("from", "1")
            intent.putExtra("data", ApiResponse.IntentSend(it[0]))
            startActivity(intent)
        }

        mainViewModel.isLoading.observe(this) {
            if (!it) {
                binding.btnCheck.isEnabled = true
                binding.llProcess.visibility = View.GONE
            }
        }

        initialDialog()
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
                    dialog.show()
                }
            }

            btnCheck.setOnClickListener {
                if (isHaveImage) {
                    val model = Model.newInstance(this@MainActivity)
                    mainViewModel.checkIt(model, bitmap)

                    btnCheck.isEnabled = false
                    llProcess.visibility = View.VISIBLE
                } else {
                    dialog.show()
                    Toast.makeText(
                        this@MainActivity,
                        "You must have to choose image.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initialDialog() {
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
        dialog = builder.create()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launchForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == TAKE_IMAGE_RESULT) {
            try {
                val myFile = it.data?.getSerializableExtra("picture") as File

                getFile = myFile
                bitmap = BitmapFactory.decodeFile(getFile?.path)

                Log.d(TAG, "masuk: ")

                binding.ivImage.setImageBitmap(bitmap)
                binding.tvDescChoose.visibility = View.GONE
                isHaveImage = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            if (it.data != null) {
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

                binding.ivImage.setImageBitmap(bitmap)
                binding.tvDescChoose.visibility = View.GONE
                isHaveImage = true
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }
    }

    companion object {
        const val TAKE_IMAGE_RESULT = 201

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}