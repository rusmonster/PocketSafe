package com.monster.pocketsafe.sms.sender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

public class CMSmsSender implements IMSmsSender {
	
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private static final String SMS_ID = "SMS_ID";
    private static final String PART_NUM = "PART_NUM";
    
	private IMSmsSenderObserver mObserver;
	private Context mContext;
	private final SmsManager mSmsManager = SmsManager.getDefault();
	
	private enum TSmsSenderState {
		EClosed,
		EOpened
	};
	
	private TSmsSenderState mState = TSmsSenderState.EClosed;
	
	private BroadcastReceiver mRecieverSent = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {
        	
        	if (mState == TSmsSenderState.EClosed) return;
         	
        	int tag = intent.getIntExtra(SMS_ID, -1);
        	if (tag == -1) return;
        	
        	int part = intent.getIntExtra(PART_NUM, -1);
        	if (part == -1) return;
        	
        	Log.d("!!!", String.format("Sent recieved: tag=%d; part=%d", tag, part));
        	      	
        	
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
               		mObserver.SmsSenderSent(CMSmsSender.this, tag);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                	mObserver.SmsSenderSentError(CMSmsSender.this, tag, TTypMyException.ESmsErrSentGeneric.Value);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                	mObserver.SmsSenderSentError(CMSmsSender.this, tag,  TTypMyException.ESmsErrSentNoService.Value);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                	mObserver.SmsSenderSentError(CMSmsSender.this, tag,  TTypMyException.ESmsErrSentNullPdu.Value);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                	mObserver.SmsSenderSentError(CMSmsSender.this, tag,  TTypMyException.ESmsErrSentRadioOff.Value);
                    break;
                default:
                	mObserver.SmsSenderSentError(CMSmsSender.this, tag,  TTypMyException.ESmsErrSentGeneral.Value);
                	break;
            }
        }
    };

    private BroadcastReceiver mRecieverDeliver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {

        	if (mState == TSmsSenderState.EClosed) return;
        	
        	int tag = intent.getIntExtra(SMS_ID, -1);
        	if (tag == -1) return;
        	
        	int part = intent.getIntExtra(PART_NUM, -1);
        	if (part == -1) return;
        	
        	Log.d("!!!",String.format("Deliv recieved: tag=%d; part=%d", tag, part));
        	
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                	mObserver.SmsSenderDelivered(CMSmsSender.this, tag);
                    break;
                default:
                	mObserver.SmsSenderDeliverError(CMSmsSender.this, tag, TTypMyException.ESmsErrDeliverGeneral.Value);
                    break;                        
            }
        }
    };

	
	public void SetObserver(IMSmsSenderObserver observer) {
		mObserver = observer;
	}

	public void sendSms(String phone, String text, int tag) throws MyException {
		
		if (mState == TSmsSenderState.EClosed)
			throw new MyException(TTypMyException.ESmsErrSenderClosed);
		
		ArrayList<String> parts = mSmsManager.divideMessage(text);
		ArrayList<PendingIntent> SentPIs= new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> DeliveredPIs= new ArrayList<PendingIntent>();
		
		int cnt = parts.size();
		for (int i=0; i<cnt; i++) {
			Intent sendI = new Intent(SENT);
			sendI.putExtra(SMS_ID, tag);
			sendI.putExtra(PART_NUM, i);
			PendingIntent SentPI = PendingIntent.getBroadcast(mContext, 0, sendI, PendingIntent.FLAG_UPDATE_CURRENT);
			SentPIs.add(SentPI);
			
			Intent delivI = new Intent(DELIVERED);
			delivI.putExtra(SMS_ID, tag);
			delivI.putExtra(PART_NUM, i);
			PendingIntent DeliveredPI = PendingIntent.getBroadcast(mContext, 0, delivI, PendingIntent.FLAG_UPDATE_CURRENT);
			DeliveredPIs.add(DeliveredPI);
		}
		
		mSmsManager.sendMultipartTextMessage(phone, null, parts, SentPIs, DeliveredPIs);
	}


	public void SetContext(Context context) {
		mContext = context;
	}

	public void open() throws MyException {
		if (mState != TSmsSenderState.EClosed)
			throw new MyException(TTypMyException.ESmsErrSenderAlreadyOpened);
		
		if (mObserver == null )
			throw new MyException(TTypMyException.ESmsErrSenderObserverIsNull);
		
		if (mContext == null )
			throw new MyException(TTypMyException.ESmsErrSenderContextIsNull);
		

		
		mContext.registerReceiver(mRecieverSent, new IntentFilter(SENT));
		mContext.registerReceiver(mRecieverDeliver, new IntentFilter(DELIVERED));
		
		mState = TSmsSenderState.EOpened;
	}
	
	public void close() {
		if (mState == TSmsSenderState.EClosed) return;
		
		mContext.unregisterReceiver(mRecieverDeliver);
		mContext.unregisterReceiver(mRecieverSent);
		
		mState = TSmsSenderState.EClosed;
	}

}
