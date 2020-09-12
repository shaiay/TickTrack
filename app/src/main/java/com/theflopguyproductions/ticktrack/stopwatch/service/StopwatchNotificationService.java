package com.theflopguyproductions.ticktrack.stopwatch.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.SoYouADeveloperHuh;
import com.theflopguyproductions.ticktrack.application.TickTrack;
import com.theflopguyproductions.ticktrack.stopwatch.StopwatchData;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;

import java.util.ArrayList;

public class StopwatchNotificationService extends Service {

    public static final String ACTION_STOP_STOPWATCH_SERVICE = "ACTION_STOP_STOPWATCH_SERVICE";
    public static final String ACTION_START_STOPWATCH_SERVICE = "ACTION_START_STOPWATCH_SERVICE";
    public static final String ACTION_RESUME_STOPWATCH_SERVICE = "ACTION_RESUME_STOPWATCH_SERVICE";
    public static final String ACTION_RESET_STOPWATCH_SERVICE = "ACTION_RESET_STOPWATCH_SERVICE";
    public static final String ACTION_LAP_STOPWATCH_SERVICE = "ACTION_LAP_STOPWATCH_SERVICE";
    public static final String ACTION_PAUSE_STOPWATCH_SERVICE = "ACTION_PAUSE_STOPWATCH_SERVICE";


    private NotificationCompat.Builder notificationBuilder;
    private NotificationManagerCompat notificationManagerCompat;

    private static ArrayList<StopwatchData> stopwatchData;
    private TickTrackNotificationStopwatch tickTrackNotificationStopwatch;

    private boolean isLapOn = false;

    public StopwatchNotificationService() {
    }

    private void baseLineNotificationLayout(Context context){

        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        Intent resultIntent = new Intent(this, SoYouADeveloperHuh.class);
        new TickTrackDatabase(context).storeCurrentFragmentNumber(3);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(TickTrack.STOPWATCH_NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(this, StopwatchNotificationService.class);
        deleteIntent.setAction(StopwatchNotificationService.ACTION_STOP_STOPWATCH_SERVICE);
        PendingIntent deletePendingIntent = PendingIntent.getService(this,
                TickTrack.STOPWATCH_NOTIFICATION_ID,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder = new NotificationCompat.Builder(this, TickTrack.STOPWATCH_NOTIFICATION)
                .setSmallIcon(R.drawable.stopwatch_notification_mini_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(this, R.color.Accent));

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            notificationBuilder.setChannelId(TickTrack.STOPWATCH_NOTIFICATION);
        }

    }

    private void setupLapPause(){


        Intent lapIntent = new Intent(this, StopwatchNotificationService.class);
        lapIntent.setAction(ACTION_LAP_STOPWATCH_SERVICE);
        PendingIntent pendingLapIntent = PendingIntent.getService(this, TickTrack.STOPWATCH_NOTIFICATION_ID, lapIntent, 0);
        NotificationCompat.Action lapAction = new NotificationCompat.Action(R.drawable.ic_round_flag_light_24, "Lap", pendingLapIntent);

        Intent pauseIntent = new Intent(this, StopwatchNotificationService.class);
        pauseIntent.setAction(ACTION_PAUSE_STOPWATCH_SERVICE);
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, TickTrack.STOPWATCH_NOTIFICATION_ID, pauseIntent, 0);
        NotificationCompat.Action pauseAction = new NotificationCompat.Action(R.drawable.ic_round_pause_white_24, "Pause", pendingPauseIntent);

        notificationBuilder.addAction(pauseAction);
        notificationBuilder.addAction(lapAction);

        notificationManagerCompat.notify(TickTrack.STOPWATCH_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void setupResumeReset(){

        Intent resumeIntent = new Intent(this, StopwatchNotificationService.class);
        resumeIntent.setAction(ACTION_RESUME_STOPWATCH_SERVICE);
        PendingIntent pendingPlusIntent = PendingIntent.getService(this, TickTrack.STOPWATCH_NOTIFICATION_ID, resumeIntent, 0);
        NotificationCompat.Action resumeAction = new NotificationCompat.Action(R.drawable.ic_round_play_white_24, "Resume", pendingPlusIntent);

        Intent resetIntent = new Intent(this, StopwatchNotificationService.class);
        resetIntent.setAction(ACTION_RESET_STOPWATCH_SERVICE);
        PendingIntent pendingMinusIntent = PendingIntent.getService(this, TickTrack.STOPWATCH_NOTIFICATION_ID, resetIntent, 0);
        NotificationCompat.Action resetAction = new NotificationCompat.Action(R.drawable.ic_stop_white_24, "Reset", pendingMinusIntent);

        notificationBuilder.setContentText("Paused");

        notificationBuilder.addAction(resumeAction);
        notificationBuilder.addAction(resetAction);

        notificationManagerCompat.notify(TickTrack.STOPWATCH_NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tickTrackNotificationStopwatch = new TickTrackNotificationStopwatch(this);

        baseLineNotificationLayout(this);
        Log.d("TAG_TIMER_SERVICE", "My foreground service onCreate().");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {

            String action = intent.getAction();

            initializeValues();

            assert action != null;
            switch (action) {
                case ACTION_START_STOPWATCH_SERVICE:
                    startForegroundService();
                    break;
                case ACTION_STOP_STOPWATCH_SERVICE:
                    stopStopwatchService();
                    break;
                case ACTION_RESUME_STOPWATCH_SERVICE:
                    resumeStopwatchService();
                    break;
                case ACTION_RESET_STOPWATCH_SERVICE:
                    resetStopwatchService();
                    break;
                case ACTION_LAP_STOPWATCH_SERVICE:
                    lapStopwatchService();
                    break;
                case ACTION_PAUSE_STOPWATCH_SERVICE:
                    pauseStopwatchService();
                    break;
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initializeValues(){

        TickTrackDatabase tickTrackDatabase = new TickTrackDatabase(this);
        stopwatchData = tickTrackDatabase.retrieveStopwatchData();
        tickTrackNotificationStopwatch.setupNotificationStuff(notificationManagerCompat, notificationBuilder);

    }
    private void startForegroundService() {

        setupLayout();

        startForeground(TickTrack.STOPWATCH_NOTIFICATION_ID, notificationBuilder.build());
        Toast.makeText(this, "Stopwatch Notification created!", Toast.LENGTH_SHORT).show();

    }
    private void setupLayout(){

        notificationBuilder.clearActions();

        if(stopwatchData.size()>0){
            if(stopwatchData.get(0).isPause()){
                setupResumeReset();
                isLapOn = false;
                tickTrackNotificationStopwatch.pauseInit();
            } else {
                setupLapPause();
                isLapOn = true;
                tickTrackNotificationStopwatch.resumeInit();
            }
        }
    }

    private void pauseStopwatchService() {
        tickTrackNotificationStopwatch.pause();
        if(isLapOn){
            notificationBuilder.clearActions();
            setupResumeReset();
            isLapOn = false;
        }
    }

    private void lapStopwatchService() {
        tickTrackNotificationStopwatch.lap();
        if(!isLapOn){
            notificationBuilder.clearActions();
            setupLapPause();
            isLapOn = true;
        }
    }

    private void resetStopwatchService() {
        tickTrackNotificationStopwatch.stop();
        stopStopwatchService();
    }

    private void resumeStopwatchService() {
        if(stopwatchData.get(0).isPause()){
            tickTrackNotificationStopwatch.resume();
            if(!isLapOn){
                notificationBuilder.clearActions();
                setupLapPause();
                isLapOn = true;
            }
        }
    }

    private void stopStopwatchService() {
        stopForeground(false);
        stopSelf();
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tickTrackNotificationStopwatch.killStopwatch();
    }

}
