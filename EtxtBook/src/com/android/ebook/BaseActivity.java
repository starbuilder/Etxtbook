package com.android.ebook;



import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.org.ebook.R;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	public Tracker getTracker(){
		    		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		    		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
				Tracker mTracker = analytics.newTracker(R.xml.ga);
				return mTracker;
	}
	public void sendScreenName(String screenName){
//	    //�s�WGoogleAnalytics����
		GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
		//���o�l�ܪ���
		Tracker mTracker = analytics.newTracker(R.xml.ga);
		//�]�w��e�e���W��
		mTracker.setScreenName(screenName);
	    //�e�X�T��
		mTracker.send(new HitBuilders.AppViewBuilder().build());
	}

}
