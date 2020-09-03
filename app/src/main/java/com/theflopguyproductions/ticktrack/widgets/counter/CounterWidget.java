package com.theflopguyproductions.ticktrack.widgets.counter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.theflopguyproductions.ticktrack.R;
import com.theflopguyproductions.ticktrack.counter.CounterData;
import com.theflopguyproductions.ticktrack.counter.activity.CounterActivity;
import com.theflopguyproductions.ticktrack.counter.notification.CounterNotificationService;
import com.theflopguyproductions.ticktrack.utils.database.TickTrackDatabase;
import com.theflopguyproductions.ticktrack.widgets.counter.data.CounterWidgetData;

import java.util.ArrayList;

public class CounterWidget extends AppWidgetProvider {

    public static final String ACTION_WIDGET_CLICK_PLUS = "ACTION_WIDGET_CLICK_PLUS";
    public static final String ACTION_WIDGET_CLICK_MINUS = "ACTION_WIDGET_CLICK_MINUS";

    private TickTrackDatabase tickTrackDatabase;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int[] appWidgetIds) {

        tickTrackDatabase = new TickTrackDatabase(context);
        ArrayList<CounterWidgetData> counterWidgetDataArrayList = tickTrackDatabase.retrieveCounterWidgetList();
        ArrayList<CounterData> counterDataArrayList = tickTrackDatabase.retrieveCounterList();

        int counterIntId = getCounterIntId(appWidgetId, counterWidgetDataArrayList);
        String counterStringId = getCounterStringId(appWidgetId, counterWidgetDataArrayList);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.counter_widget);

        System.out.println("UPDATE HAPPENED");

        if(counterIntId!=-1 && counterStringId!=null){

            Intent intent = new Intent(context, CounterActivity.class);
            intent.putExtra("currentCounterPosition", counterStringId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, counterIntId, intent, 0);

            System.out.println("UPDATE "+counterDataArrayList.get(getCurrentPosition(counterStringId)).getCounterValue());

            views.setOnClickPendingIntent(R.id.counterWidgetRootRelativeLayout, pendingIntent);
            views.setOnClickPendingIntent(R.id.counterWidgetPlusButton, getPendingSelfIntent(context, ACTION_WIDGET_CLICK_PLUS, counterIntId, counterStringId, appWidgetIds ));
            views.setOnClickPendingIntent(R.id.counterWidgetMinusButton, getPendingSelfIntent(context, ACTION_WIDGET_CLICK_MINUS, counterIntId, counterStringId, appWidgetIds ));
            views.setTextViewText(R.id.counterWidgetCountText, ""+counterDataArrayList.get(getCurrentPosition(counterStringId)).getCounterValue());
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private int getCounterIntId(int appWidgetId, ArrayList<CounterWidgetData> counterWidgetDataArrayList) {
        for(int i=0; i<counterWidgetDataArrayList.size(); i++){
            if(counterWidgetDataArrayList.get(i).getCounterWidgetId()==appWidgetId){
                return counterWidgetDataArrayList.get(i).getCounterIdInteger();
            }
        }
        return -1;
    }
    private String getCounterStringId(int appWidgetId, ArrayList<CounterWidgetData> counterWidgetDataArrayList) {
        for(int i=0; i<counterWidgetDataArrayList.size(); i++){
            if(counterWidgetDataArrayList.get(i).getCounterWidgetId()==appWidgetId){
                return counterWidgetDataArrayList.get(i).getCounterIdString();
            }
        }
        return null;
    }
    private int getCurrentWidgetId(String counterID, ArrayList<CounterWidgetData> counterWidgetDataArrayList) {
        for(int i=0; i<counterWidgetDataArrayList.size(); i++){
            if(counterWidgetDataArrayList.get(i).getCounterIdString()==counterID){
                return counterWidgetDataArrayList.get(i).getCounterWidgetId();
            }
        }
        return -1;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        tickTrackDatabase = new TickTrackDatabase(context);
        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private PendingIntent getPendingSelfIntent(Context context, String action, int counterID, String counterIdString, int[] appWidgetIds) {

        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.putExtra("counterID", counterIdString);
        return PendingIntent.getBroadcast(context, counterID, intent, 0);
    }

    ArrayList<CounterData> counterDataArrayList;
    ArrayList<CounterWidgetData> counterWidgetDataArrayList;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        tickTrackDatabase = new TickTrackDatabase(context);

        counterDataArrayList = tickTrackDatabase.retrieveCounterList();
        counterWidgetDataArrayList = tickTrackDatabase.retrieveCounterWidgetList();

        System.out.println("RECEIVED SOMETHING");
        Bundle extras = intent.getExtras();

        String counterID = intent.getStringExtra("counterID");

        if(counterID!=null && extras!=null){
            int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            int currentCount = counterDataArrayList.get(getCurrentPosition(counterID)).getCounterValue();
            if (ACTION_WIDGET_CLICK_PLUS.equals(intent.getAction())) {
                System.out.println("PLUS SOMETHING");
                if(currentCount<999999999) {
                    currentCount += 1;
                    counterDataArrayList.get(getCurrentPosition(counterID)).setCounterValue(currentCount);
                    counterDataArrayList.get(getCurrentPosition(counterID)).setCounterTimestamp(System.currentTimeMillis());
                    tickTrackDatabase.storeCounterList(counterDataArrayList);
                    refreshNotificationStatus(context, counterID);
                }
            } else if (ACTION_WIDGET_CLICK_MINUS.equals(intent.getAction())) {
                System.out.println("MINUS SOMETHING");
                if(currentCount>=1){
                    currentCount-=1;
                    counterDataArrayList.get(getCurrentPosition(counterID)).setCounterValue(currentCount);
                    counterDataArrayList.get(getCurrentPosition(counterID)).setCounterTimestamp(System.currentTimeMillis());
                    tickTrackDatabase.storeCounterList(counterDataArrayList);
                    refreshNotificationStatus(context, counterID);
                }
            }
            if(appWidgetIds!=null){
                System.out.println("ON UPDATE HAPPENED");
                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
    }



    private void refreshNotificationStatus(Context context, String counterId) {
        if(counterDataArrayList.get(getCurrentPosition(counterId)).isCounterPersistentNotification()){
            Intent intent = new Intent(context, CounterNotificationService.class);
            intent.setAction(CounterNotificationService.ACTION_REFRESH_SERVICE);
            context.startService(intent);
        }
    }

    private int getCurrentPosition(String counterID) {
        for(int i = 0; i < counterDataArrayList.size(); i ++){
            if(counterDataArrayList.get(i).getCounterID().equals(counterID)){
                return i;
            }
        }
        return 0;
    }
}

