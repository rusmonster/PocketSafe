package com.softmo.smssafe2.utils;


import com.softmo.smssafe2.utils.MyException.TTypMyException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

public class CMTimerWakeup extends BroadcastReceiver implements IMTimerWakeup {
	
	private static String TAG = "com.softmo.smssafe2.utils.CMTimerWakeup.TAG";
	private static String ACTION = "com.softmo.smssafe2.utils.CMTimerWakeup.ACTION";
	
	private Context mContext;
	private IMTimerObserver mObserver;
	private enum TTimerWakeupState {
		EWait,
		EBusy
	}
	private TTimerWakeupState mState = TTimerWakeupState.EWait;
	
	public void setContext(Context context) {
		mContext = context;
		mContext.registerReceiver(this, new IntentFilter(ACTION));
	}

	public void SetObserver(IMTimerObserver observer) {
		mObserver = observer;
	}

	public void startTimer(long ms) throws MyException {
		if (mState != TTimerWakeupState.EWait)
			throw new MyException(TTypMyException.ETimerNotReady);
		

		AlarmManager am=(AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, 0);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ms, pi);
        
		mState = TTimerWakeupState.EBusy;
		Log.d("!!!", "CMTimerWakeup setted: "+ms+"; mObserver: "+mObserver);
	}

	public void cancelTimer() {
		if (mState == TTimerWakeupState.EBusy) {
			mState = TTimerWakeupState.EWait;
			
			Intent intent = new Intent(ACTION);
	        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);
	        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(sender);
	        
			Log.d("!!!", "CMTimerWakeup canceled");
		}
	}

	@Override
	public void onReceive(Context arg0, Intent arg1) {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        
        wl.acquire();

        if (mState == TTimerWakeupState.EBusy) {
	        mState = TTimerWakeupState.EWait;
	        
	        if (mObserver!=null)
	        {
	        	Log.d("!!!", "CMTimerWakeup fire");
	        	try {
					mObserver.timerEvent(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
        }
        
        wl.release();
	}

}
