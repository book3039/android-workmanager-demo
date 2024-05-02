package com.syncrown.android_workmanager_demo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        const val KEY_COUNT_VALUE = "key_count"
    }

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        textView = findViewById(R.id.textView)
        button.setOnClickListener {
//            setOneTimeWorkRequest()
            setPeriodicWorkRequest()
        }
    }

    private fun setOneTimeWorkRequest() {
        val workManager = WorkManager.getInstance(applicationContext)
        val data: Data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .build()
        val constrains = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constrains)
            .setInputData(data)
            .build()

        val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()

        val compressingRequest = OneTimeWorkRequest.Builder(CompressingWorker::class.java)
            .build()

        val downloadingRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
            .build()

        val parallelWorks = mutableListOf<OneTimeWorkRequest>()
        parallelWorks.add(downloadingRequest)
        parallelWorks.add(filteringRequest)

        workManager
            .beginWith(parallelWorks)
            .then(compressingRequest)
            .then(uploadRequest)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(this) {
                textView.text = it.state.name
                if (it.state.isFinished) {
                    val data = it.outputData
                    val message = data.getString(UploadWorker.KEY_WORKER)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setPeriodicWorkRequest() {
        val periodicWorkRequest = PeriodicWorkRequest
            .Builder(DownloadingWorker::class.java, 16, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(periodicWorkRequest)
    }
}
