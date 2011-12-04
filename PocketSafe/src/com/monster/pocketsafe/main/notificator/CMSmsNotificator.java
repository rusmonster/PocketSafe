package com.monster.pocketsafe.main.notificator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.monster.pocketsafe.R;
import com.monster.pocketsafe.SmsMainActivity;
import com.monster.pocketsafe.main.TTypEvent;
import com.monster.pocketsafe.main.TTypEventStrings;

public class CMSmsNotificator implements IMSmsNotificator {
	
	private Context mContext;
	private NotificationManager mNotifyMgr;
	private Notification mNotification;
	private Intent mNotificationIntent;
	private PendingIntent mNotificationPendingIntent;
	private CharSequence mTickerText;
	
	public void Init(Context context) {
		mContext = context;
		
		mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	
        mNotificationIntent = new Intent(context, SmsMainActivity.class);
        mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mNotificationIntent.putExtra(TTypEventStrings.EVENT_TYP, TTypEvent.ESmsRecieved.Value);
        
        mNotificationPendingIntent = PendingIntent.getActivity(context, 0, mNotificationIntent, 0);
        
        mTickerText =   mContext.getResources().getText( R.string.sms_new );
	}
	
	public void Popup(int cnt_newsms) {
	    int icon = R.drawable.android_happy;
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, mTickerText, when);
	    mNotification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
	    
	    CharSequence contentText=mContext.getResources().getText( R.string.sms_new_cnt )+Integer.toString(cnt_newsms);
        mNotification.setLatestEventInfo(mContext, mTickerText, contentText, mNotificationPendingIntent);
        mNotifyMgr.cancel(TTypEvent.ESmsRecieved.Value);
		mNotifyMgr.notify(TTypEvent.ESmsRecieved.Value, mNotification);
	}

	public void Update(int cnt_newsms) {
		if (cnt_newsms==0) {
			mNotifyMgr.cancel(TTypEvent.ESmsRecieved.Value);
			mNotification=null;
		} else if (mNotification != null) {
			CharSequence contentText=mContext.getResources().getText( R.string.sms_new_cnt )+Integer.toString(cnt_newsms);
	        mNotification.setLatestEventInfo(mContext, mTickerText, contentText, mNotificationPendingIntent);
		}
	}

}
