package com.syncrown.android_workmanager_demo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

class CompressingWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        try {
            for (i in 0..300) {
                Log.i("MYTAG", "Compressing $i")
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }

    }
}