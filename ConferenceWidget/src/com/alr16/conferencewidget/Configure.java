package com.alr16.conferencewidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;


public class Configure extends Activity{
	
	private Configure context;
	private int widgetID;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.configure);
		setResult(RESULT_CANCELED);
		context = this;
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			this.widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		final AppWidgetManager widgetManager = AppWidgetManager.getInstance(this.context);
		final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		
		Button button1 = (Button) this.findViewById(R.id.button1);
		Button button2 = (Button) this.findViewById(R.id.button2);
		
		button1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Intent handleConference = new Intent(context, ConferenceHandler.class);
				handleConference.setAction(getString(R.string.url_1));
				handleConference.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
				startActivityForResult(handleConference, R.string.conference_1);
			}			
		});
		
		button2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				Intent handleConference = new Intent(context, ConferenceHandler.class);
				handleConference.setAction(getString(R.string.url_2));
				handleConference.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
				startActivityForResult(handleConference, R.string.conference_2);
			}
			
		});
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		setContentView(R.layout.configure);
		setResult(RESULT_CANCELED);
		Bundle extras = data.getExtras();
		if(extras != null){
			widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		setResult(RESULT_OK, resultValue);
		finish();
	}


}
