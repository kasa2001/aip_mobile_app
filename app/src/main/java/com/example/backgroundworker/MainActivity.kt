package com.example.backgroundworker


import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.backgroundworker.builder.RestBuilder
import com.example.backgroundworker.dto.ConfigDto
import com.example.backgroundworker.dto.MacDto
import com.example.backgroundworker.rest.Rest
import com.example.backgroundworker.worker.LightWorker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val retrofit = RestBuilder.buildService(Rest::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getConfig(findViewById(R.id.get_config))
    }

    fun getMac(): String {
        return "02:00:00:00:00:00"
    }

    fun getConfig(view: View) {
        val macDto = MacDto (getMac())

        retrofit.config(macDto).enqueue(
            object : Callback<ConfigDto> {
                override fun onFailure(call: Call<ConfigDto>, t: Throwable) {
                    println(t.message)
                }

                override fun onResponse(
                    call: Call<ConfigDto>,
                    response: Response<ConfigDto>
                ) {
                    WorkManager.getInstance(applicationContext)
                        .cancelAllWork()

                    val data = Data.Builder()

                    val currentDate = Calendar.getInstance()
                    val dueDate = Calendar.getInstance()
                    val dueDateEnd = Calendar.getInstance()
                    val startDate : String = response.body()?.action?.get(0)?.start.toString()
                    val endDate : String = response.body()?.action?.get(0)?.end.toString()

                    println(startDate)

                    val hour = StringBuilder()
                    val minute = StringBuilder()
                    val second = StringBuilder()

                    val hourEnd = StringBuilder()
                    val minuteEnd = StringBuilder()
                    val secondEnd = StringBuilder()

                    hour.append(startDate[0])
                    hour.append(startDate[1])
                    minute.append(startDate[3])
                    minute.append(startDate[4])
                    second.append(startDate[6])
                    second.append(startDate[7])

                    hourEnd.append(endDate[0])
                    hourEnd.append(endDate[1])
                    minuteEnd.append(endDate[3])
                    minuteEnd.append(endDate[4])
                    secondEnd.append(endDate[6])
                    secondEnd.append(endDate[7])

                    data.putInt("hour", hour.toString().toInt())
                    data.putInt("minute", minute.toString().toInt())
                    data.putInt("second", second.toString().toInt())

                    data.putInt("hourEnd", hourEnd.toString().toInt())
                    data.putInt("minuteEnd", minuteEnd.toString().toInt())
                    data.putInt("secondEnd", secondEnd.toString().toInt())

                    dueDate.set(Calendar.HOUR_OF_DAY, hour.toString().toInt())
                    dueDate.set(Calendar.MINUTE, minute.toString().toInt())
                    dueDate.set(Calendar.SECOND, second.toString().toInt())

                    dueDateEnd.set(Calendar.HOUR_OF_DAY, hourEnd.toString().toInt())
                    dueDateEnd.set(Calendar.MINUTE, minuteEnd.toString().toInt())
                    dueDateEnd.set(Calendar.SECOND, secondEnd.toString().toInt())


                    if (dueDate.before(currentDate)) {
                        dueDate.add(Calendar.HOUR_OF_DAY, 24)
                    }

                    val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

                    val sleepTime = dueDateEnd.timeInMillis - dueDate.timeInMillis

                    println(sleepTime)

                    data.putLong("sleepTime", sleepTime)

                    val dailyWorkRequest = OneTimeWorkRequestBuilder<LightWorker>().setInitialDelay(
                        timeDiff,
                        TimeUnit.MILLISECONDS
                    ).setInputData(data.build())
                        .addTag("LIGHT_TAG")
                        .build()


                    WorkManager.getInstance(applicationContext)
                        .enqueue(dailyWorkRequest)

                }
            }
        )
    }
}