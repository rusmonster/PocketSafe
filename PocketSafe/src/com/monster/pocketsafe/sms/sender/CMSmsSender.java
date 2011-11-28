package com.monster.pocketsafe.sms.sender;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

public class CMSmsSender implements IMSmsSender {
	
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    
    private PendingIntent mSentPI;// = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
    private PendingIntent mDeliveredPI;// = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
 
	private IMSmsSenderObserver mObserver;
	private Context mContext;
	private final SmsManager mSmsManager = SmsManager.getDefault();
	
	private enum TSmsSenderState {
		EClosed,
		EOpened,
		ESending
	};
	
	private TSmsSenderState mState = TSmsSenderState.EClosed;
	
	private BroadcastReceiver mRecieverSent = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {
        	
        	if (mState != TSmsSenderState.ESending) return;
        	mState = TSmsSenderState.EOpened;
        	
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    mObserver.SmsSenderSent(CMSmsSender.this);
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                	mObserver.SmsSenderSentError(CMSmsSender.this, TTypMyException.ESmsErrSentGeneric.Value);
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                	mObserver.SmsSenderSentError(CMSmsSender.this, TTypMyException.ESmsErrSentNoService.Value);
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                	mObserver.SmsSenderSentError(CMSmsSender.this, TTypMyException.ESmsErrSentNullPdu.Value);
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                	mObserver.SmsSenderSentError(CMSmsSender.this, TTypMyException.ESmsErrSentRadioOff.Value);
                    break;
                default:
                	mObserver.SmsSenderSentError(CMSmsSender.this, TTypMyException.ESmsErrSentGeneral.Value);
                	break;
            }
        }
    };

    private BroadcastReceiver mRecieverDeliver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent arg1) {

        	if (mState == TSmsSenderState.EClosed) return;
        	
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                	mObserver.SmsSenderDelivered(CMSmsSender.this);
                    break;
                default:
                	mObserver.SmsSenderDeliverError(CMSmsSender.this, TTypMyException.ESmsErrDeliverGeneral.Value);
                    break;                        
            }
        }
    };

	
	public void SetObserver(IMSmsSenderObserver observer) {
		mObserver = observer;
	}

	public void sendSms(String phone, String text) throws MyException {
		
		if (mState == TSmsSenderState.EClosed)
			throw new MyException(TTypMyException.ESmsErrSenderClosed);
		
		if (mState == TSmsSenderState.ESending)
			throw new MyException(TTypMyException.ESmsErrSenderAlreadySending);
		
		mState = TSmsSenderState.ESending;
        mSmsManager.sendTextMessage(phone, null, text, mSentPI, mDeliveredPI);   
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
		
		mSentPI = PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0);
		mDeliveredPI = PendingIntent.getBroadcast(mContext, 0, new Intent(DELIVERED), 0);
		
		mContext.registerReceiver(mRecieverSent, new IntentFilter(SENT));
		mContext.registerReceiver(mRecieverDeliver, new IntentFilter(DELIVERED));
		
		mState = TSmsSenderState.EOpened;
	}
	
	public void close() {
		if (mState == TSmsSenderState.EClosed) return;
		
		mContext.unregisterReceiver(mRecieverDeliver);
		mContext.unregisterReceiver(mRecieverSent);
		
		mDeliveredPI = null;
		mSentPI = null;
		
		mState = TSmsSenderState.EClosed;
	}

}
