package com.rz.tbcheck.activity

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rz.tbcheck.R
import com.rz.tbcheck.data.IntentSend
import com.rz.tbcheck.data.ListHistoryItem
import com.rz.tbcheck.databinding.ActivityDetailHistoryBinding
import com.rz.tbcheck.viewmodel.DetailHistoryViewModel
import kotlinx.coroutines.*
import java.util.*


class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private val model: DetailHistoryViewModel by viewModels()

    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private lateinit var accuracy: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        val from = intent.getStringExtra(INTENT_FROM).toString()
        if (from == "1") {
            val dataIntent =
                intent.getParcelableExtra<IntentSend>(INTENT_FROM1) as IntentSend

            accuracy = (dataIntent.accuracy * 100).toString()
            val isTb = dataIntent.accuracy > 0.8

            binding.tvAccuracy.text = "${accuracy.substring(0, 4)}%"
            if (isTb) {
                binding.tvStatus.text = "Tuberculosis"
            } else {
                binding.tvStatus.text = "Normal"
            }

            filePath = dataIntent.filePath
            binding.ivImage.setImageURI(filePath)
        } else {
            val dataIntent =
                intent.getParcelableExtra<ListHistoryItem>(INTENT_FROM2) as ListHistoryItem

            binding.tvAccuracy.text = "${dataIntent.accuracy}%"
            binding.tvStatus.text = dataIntent.status
            binding.tvDate.text = dataIntent.createdAt!!.substring(0, 10)

            Glide.with(this@DetailHistoryActivity)
                .load(dataIntent.image)
                .override(100)
                .into(binding.ivImage)

            binding.btnSave.visibility = View.GONE
        }

        model.history.observe(this) {
            binding.btnSave.isEnabled = false
            Toast.makeText(
                this@DetailHistoryActivity,
                resources.getString(R.string.str_saved),
                Toast.LENGTH_LONG
            ).show()
        }

        model.isLoading.observe(this) {
            binding.llProcess.visibility = View.GONE
        }

        setClick()
    }

    private fun setClick() {
        binding.apply {
            btnSave.setOnClickListener {
                if (filePath != null) {
                    llProcess.visibility = View.VISIBLE
                    btnSave.isEnabled = false
                    runBlocking {
                        withContext(Dispatchers.Default) {
                            val aa = storageReference?.child(
                                "myImages/" + UUID.randomUUID().toString()
                            )
                            aa?.putFile(filePath!!)?.addOnSuccessListener {
                                aa.downloadUrl.addOnSuccessListener {
                                    model.addHistory(
                                        ListHistoryItem(
                                            null,
                                            it.toString(),
                                            accuracy,
                                            tvStatus.text.toString(),
                                            "",
                                            null
                                        )
                                    )
                                    Log.d(TAG, "setClick: $it")
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@DetailHistoryActivity,
                        "Please Upload an Image",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {
        const val INTENT_FROM = "from"
        const val INTENT_FROM1 = "intent_from1"
        const val INTENT_FROM2 = "intent_from2"
    }
}