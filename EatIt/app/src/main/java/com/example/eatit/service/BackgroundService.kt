package com.example.eatit.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.eatit.MainActivity
import com.example.eatit.R
import com.example.eatit.data.CartRepository
import com.example.eatit.model.Order
import com.example.eatit.viewModel.CartViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackgroundService : Service() {
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val ordersCollection = FirebaseFirestore.getInstance().collection("orders")
        val query = ordersCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        ordersListenerRegistration = query.addSnapshotListener { querySnapshot, exception ->
            if (exception != null) {
                // Gestisci l'errore nell'ascoltatore
                return@addSnapshotListener
            }
            val updatedOrders = querySnapshot?.documents?.mapNotNull { documentSnapshot ->
                val order = documentSnapshot.toObject(Order::class.java)
                order?.id = documentSnapshot.id
                order
            }
            if (updatedOrders != null && updatedOrders.isNotEmpty()) {
                // I dati degli ordini sono cambiati, puoi inviare una notifica
                sendOrderUpdateNotification()
            }
        }
    }

    private fun sendOrderUpdateNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Ordine Aggiornato")
            .setContentText("Uno o più ordini sono stati aggiornati.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = 1
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}