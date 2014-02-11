package com.example.teambhoj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class AlarmService extends Service {

    private int NOTIFICATIONID = 12345;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("ESMS", " Showing notification");
        NotificationManager mManager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        Intent contentIntent = new Intent(this.getApplicationContext(), AuthenticatorActivity.class);

        contentIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext())
                .setContentTitle("Byte2Eat")
                .setContentText("Hungry Kya ! Team Bhoj is taking orders right now. Counter open till 1600 hrs")
                .setSmallIcon(R.drawable.ic_launcher);

        Intent order1 = new Intent(this.getApplicationContext(), AuthenticatorActivity.class);
        order1.putExtra("Order",1);
        Intent order2 = new Intent(this.getApplicationContext(), AuthenticatorActivity.class);
        order1.putExtra("Order",2);
        Intent order3 = new Intent(this.getApplicationContext(), AuthenticatorActivity.class);
        order1.putExtra("Order",3);

        PendingIntent pending1 = PendingIntent.getActivity(this.getApplicationContext(), 0, order1, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pending2 = PendingIntent.getActivity(this.getApplicationContext(), 0, order2, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pending3 = PendingIntent.getActivity(this.getApplicationContext(), 0, order3, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.InboxStyle inboxStyle  = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Byte2Eat");
        inboxStyle.addLine("Hungry Kya ? Team Bhoj is taking orders ");
        inboxStyle.addLine(" right now. Counter is open till 1600 hrs.");
        builder.setStyle(inboxStyle);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingNotificationIntent)
                .setTicker("Byte2Eat taking orders now!")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.addAction(R.drawable.foodicon,"Order 1", pending1);
        builder.addAction(R.drawable.foodicon,"Order 2", pending2);
        builder.addAction(R.drawable.foodicon,"Order 3", pending3);
        Notification notification = builder.build();

        mManager.notify(NOTIFICATIONID,notification);
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
