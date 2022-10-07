package kr.co.dreameut.monthfood

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        SP.setData(baseContext, SP.FCM_ID, s)
    }


    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        val tarUrl: String? = msg.getData().get("tarUrl")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notiId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("tarUrl", tarUrl)
        val pendingIntent = PendingIntent.getActivity(
            this, notiId /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "monthfood_channel")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(msg.data.get("title"))
            .setContentText(msg.data.get("msg"))
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
//            .setStyle(NotificationCompat.BigPictureStyle().bigPicture())
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.push))
            .setContentIntent(pendingIntent)
        val notification: Notification = notificationBuilder.build()
        notificationManager.notify(notiId, notification)
    }
}