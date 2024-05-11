package com.example.roplabda_bajnoksag;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "match_notification_channel";
    private final int NOTIFICATION_ID = 0;


    private NotificationManager mManager;
    private Context mContext;

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Match Notification", NotificationManager.IMPORTANCE_HIGH);


        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.RED);
        channel.setDescription("Notification from Championship app!");

        this.mManager.createNotificationChannel(channel);

    }

    public void send (String message) {
        Intent intent = new Intent(mContext, MatchListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_MUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Bajnoksági értesítés\nNőtt a kosár tartalma")
                .setContentText(message)
                .setSmallIcon(R.drawable.vollexball_icon)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID)                ;

        this.mManager.notify(NOTIFICATION_ID, builder.build());

    }

    public void cancel() {
        this.mManager.cancel(NOTIFICATION_ID);
    }
}
