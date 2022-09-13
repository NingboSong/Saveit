package alarmmanager;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.saveit.R;

/**
 * This Class build a  Notification channel
 * @author Zilin.Song
 */
public class NotificationHelper extends ContextWrapper {
    /**
     * The constant channelID.
     */
    public static final String channelID = "channelID";
    /**
     * The constant channelName.
     */
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

    /**
     * Instantiates a new Notification helper.
     *
     * @param base the base
     */
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    /**
     * Gets channel notification.
     *
     * @return the channel notification
     */
    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Save it!")
                .setContentText("Time to record today's income and cost!")
                .setSmallIcon(R.drawable.ic_stat_name);
    }
}