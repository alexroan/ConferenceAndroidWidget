package com.alr16.conferencewidget;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class Main extends AppWidgetProvider{

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for(int i=0; i<appWidgetIds.length; i++){
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, Configure.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setOnClickPendingIntent(R.id.changeConferenceButton, pending);
			
			appWidgetManager.updateAppWidget(appWidgetId, views);			

		}

	}
	
	
}
