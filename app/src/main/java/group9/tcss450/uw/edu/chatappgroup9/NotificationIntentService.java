package group9.tcss450.uw.edu.chatappgroup9;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class NotificationIntentService extends IntentService {


    public static final String RECEIVED_UPDATE = "New notification from Husky Mingle";
    //60 seconds - 1 minute is the minimum...
    private static final int POLL_INTERVAL = 60_000;
    private static final String TAG = "NotificationIntentService";
    private NotificationManager notifManager;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Performing the service");
            checkWebservice(intent.getBooleanExtra(getString(R.string.keys_is_foreground),
                    false));
        }
    }

    public static void startServiceAlarm(Context context, boolean isInForeground) {
        Intent i = new Intent(context, NotificationIntentService.class);
        i.putExtra(context.getString(R.string.keys_is_foreground), isInForeground);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int startAfter = isInForeground ? POLL_INTERVAL : POLL_INTERVAL * 2;

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                , startAfter
                , POLL_INTERVAL, pendingIntent);
    }

    public static void stopServiceAlarm(Context context) {
        Intent i = new Intent(context, NotificationIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public void createNotification(String aMessage) {
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            Notification notification = builder.build();
            notifManager.notify(NOTIFY_ID, notification);

        } else {
             builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_project_icon_husky)
                            .setContentTitle("Phish setlist")
                            .setContentText("A new Setlist to view!");

            // Creates an Intent for the Activity
            Intent notifyIntent =
                    new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            notifyIntent.putExtra(getString(R.string.keys_extra_results), aMessage);

            // Sets the Activity to start in a new, empty task
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Creates the PendingIntent
            PendingIntent notifyPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            notifyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            // Puts the PendingIntent into the notification builder
            builder.setContentIntent(notifyPendingIntent);
            builder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFY_ID, builder.build());
        }


    }



    /**
     * methods to handle the call to the webservice */
    private boolean checkWebservice(boolean isInForeground) {
        SharedPreferences pref = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE );
        String memberId = pref.getString(getString(R.string.keys_shared_prefs_memberid), "-1");
        //check a webservice in the background...
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_notification_message))
                .appendQueryParameter("memberId", memberId)
                .build();


        HttpURLConnection urlConnection = null;

        //go out and ask for new messages
        StringBuilder response = new StringBuilder();

        String url = retrieve.toString() + "&after=" + pref.getString(getString(R.string.keys_prefs_time_stamp), "0");
        Log.e(TAG, url);
        try {

            URL urlObject = new URL(url);
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            InputStream content = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s;
            while ((s = buffer.readLine()) != null) {
                response.append(s);
            }

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        if (isInForeground) {
            Intent i = new Intent(RECEIVED_UPDATE);
            //add bundle to send the response to any receivers
            i.putExtra(getString(R.string.keys_extra_results), response.toString());
            sendBroadcast(i);
        } else {
            createNotification(response.toString());
        }
        return true;
    }


}
