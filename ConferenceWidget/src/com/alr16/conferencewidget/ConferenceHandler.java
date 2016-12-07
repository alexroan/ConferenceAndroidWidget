package com.alr16.conferencewidget;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


public class ConferenceHandler extends Activity{
	
	private int widgetID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setResult(RESULT_CANCELED);
		
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.main);
		final AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras != null){
			this.widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		String action = intent.getAction();
		Intent resultIntent;
		
		XMLHandler xmlHandle = new XMLHandler(action);
		xmlHandle.retrieveXML();
		String message = xmlHandle.getCurrentMessage();
		Date currentTime = xmlHandle.getCurrentTime();
		String time = currentTime.getHours() + ":" + currentTime.getMinutes();
		views.setTextViewText(R.id.currentMessageText, time + " - " + message);
		if(xmlHandle.retrieveCurrentAndNextSessions()){
			HashMap<String, String> thisSession = xmlHandle.getCurrentSession();
			resultIntent = new Intent();
			resultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
			if(thisSession != null){
				Log.e("retrieved session title", thisSession.get(XMLHandler.KEY_SESSION_TITLE));				
				views.setTextViewText(R.id.textView1, thisSession.get(XMLHandler.KEY_SESSION_TITLE));
				views.setTextViewText(R.id.textView3, thisSession.get(XMLHandler.KEY_SESSION_START_TIME) + " - " + thisSession.get(XMLHandler.KEY_SESSION_END_TIME));
				
			}
			else{
				Log.e("current session", "not retrieved");
			}
			
			thisSession = xmlHandle.getNextSession();
			if(thisSession != null){
				Log.e("retrieved session title", thisSession.get(XMLHandler.KEY_SESSION_TITLE));
				views.setTextViewText(R.id.textView2, thisSession.get(XMLHandler.KEY_SESSION_TITLE));
				views.setTextViewText(R.id.textView4, thisSession.get(XMLHandler.KEY_SESSION_START_TIME) + " - " + thisSession.get(XMLHandler.KEY_SESSION_END_TIME));
			}
			else{
				Log.e("current session", "not retrieved");
			}
			widgetManager.updateAppWidget(widgetID, views);
			setResult(RESULT_OK, resultIntent);
			finish();
		}
				
	}
}
