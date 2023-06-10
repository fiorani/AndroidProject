package com.example.eatit.service

import android.annotation.SuppressLint
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
import androidx.core.app.NotificationManagerCompat
import com.example.eatit.MainActivity
import com.example.eatit.R
import com.example.eatit.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class OrderService : Service() {
    private val notificationId = 101
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startOrdersListener()
        return START_STICKY
    }
    private fun createNotificationChannel() {
        val name =  "OrderChannel"
        val descriptionText =  "OrderChannel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("OrderChannel", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startOrdersListener() {
        FirebaseFirestore.getInstance().collection("orders")
            .whereEqualTo("userId", FirebaseAuth.getInstance().currentUser?.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.MODIFIED) {
                            Log.d(TAG, "Modify: ${dc.document.data}")
                            sendNotification(dc.document.toObject(Order::class.java))
                        }
                    }

                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(order: Order) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, "OrderChannel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Order status changed")
            .setContentText("status:"+order.status)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            with(NotificationManagerCompat.from(this)) {
                notify(notificationId, builder.build())
            }
    }

}
