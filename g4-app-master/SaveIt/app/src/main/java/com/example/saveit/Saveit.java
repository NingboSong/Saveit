package com.example.saveit;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * This class build 2 Channel for BoardCast
 * @author Zilin.Song
 */
public class Saveit extends Application {
    /**
     * The constant CHANNEL_1_ID.
     */
    public static final String CHANNEL_1_ID = "channel1";
    /**
     * The constant CHANNEL_2_ID.
     */
    public static final String CHANNEL_2_ID = "channel2";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("This is Channel 2");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}