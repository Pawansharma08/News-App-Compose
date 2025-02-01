package com.pawan.newsapp.notification

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import coil.compose.rememberImagePainter

fun sendNewsNotification(context: Context, title: String, imageUrl: String?) {
    val channelId = "news_channel"

    // Check if the permission is granted for posting notifications
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            // Request permission if not granted
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101 // Request code for the permission
            )
            return // Exit the function as permission has to be granted first
        }
    }

    // Create the notification channel for Android 8 and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "News Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for news updates"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Load the image asynchronously using Coil
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .target { drawable ->
            val bitmap = drawable.toBitmap()

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setContentTitle(title)
                .setContentText("New article: $title")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap)) // Set the image

            with(NotificationManagerCompat.from(context)) {
                notify(0, notificationBuilder.build())  // 0 is the notification id
            }
        }
        .build()

    imageLoader.enqueue(request)
}


