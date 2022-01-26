package com.example.backgroundworker.worker

import android.content.Context
import android.hardware.Camera
import android.icu.util.Calendar
import androidx.work.*
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit

class LightWorker (appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val cam = Camera.open()

        val p: Camera.Parameters = cam.getParameters()
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
        cam.setParameters(p)
        cam.startPreview()

        sleep(inputData.getLong("sleepTime", 0))

        cam.stopPreview()
        cam.release()

        val currentDate = Calendar.getInstance()

        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, inputData.getInt("hour", 0))
        dueDate.set(Calendar.MINUTE, inputData.getInt("minute", 0))
        dueDate.set(Calendar.SECOND, inputData.getInt("second", 0))

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }

        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyWorkRequest = OneTimeWorkRequestBuilder<LightWorker>().setInitialDelay(
            timeDiff,
            TimeUnit.MILLISECONDS
        ).setInputData(inputData)
            .addTag("LIGHT_TAG")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)

        return Result.success()
    }
}
