package com.ee.cigarette

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class workmanager(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object{
        const val CHANNEL_ID = "channel_id"
        const val Notifiy = 1
    }

    override fun doWork(): Result {
        val skooor = inputData.getInt("skorbildir",0)
        println("$skooor workmanager")
        //showNotification(skooor)
        return Result.success()

    }


    //bildirim çubuğu oluşturacağız

       private fun showNotification(cigarette :Int) {

           val intent = Intent(applicationContext,MainActivity::class.java).apply {
               flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
           }

           val pendingIntent = PendingIntent.getActivity(applicationContext,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_smoking_rooms_24)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.icon))
            .setContentTitle(" Smoke  -> $cigarette")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Sigara Bildirimi"
            val descriptionText = "Bir günde içtiğiniz sigara sayısı bildirimi"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
           with(NotificationManagerCompat.from(applicationContext)) {
               // notificationId is a unique int for each notification that you must define
               notify(Notifiy, builder.build())
           }

        }

    }


    //24 saatte bir sigara sayacı sıfırlanacak..


