package com.example.eatit.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.eatit.MainActivity
import com.example.eatit.R
import com.example.eatit.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class OrderService : Service() {
    private lateinit var ordersListenerRegistration: ListenerRegistration
    private lateinit var notificationManager: NotificationManager
    private val channelId = "OrderUpdatesChannel"
    private val channelName = "Order Updates"

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startOrdersListener()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startOrdersListener() {
        FirebaseFirestore.getInstance().collection("orders")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d(TAG, "Current data: ${snapshot.documents}")
                    sendOrderUpdateNotification()
                } else {
                    Log.d(TAG, "Current data: null")
                }
        }
    }

    private fun sendOrderUpdateNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Ordine Aggiornato")
            .setContentText("Uno o più ordini sono stati aggiornati.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = 1
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
}
