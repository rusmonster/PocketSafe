package com.monster.pocketsafe.main;

import java.math.BigInteger;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.main.notificator.IMSmsNotificator;
import com.monster.pocketsafe.sec.IMAes;
import com.monster.pocketsafe.sec.IMBase64;
import com.monster.pocketsafe.sec.IMRsa;
import com.monster.pocketsafe.sec.IMRsaObserver;
import com.monster.pocketsafe.sec.IMSha256;
import com.monster.pocketsafe.sms.sender.IMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSenderObserver;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMMain implements IMMain, IMSmsSenderObserver, IMListener, IMRsaObserver, IMPassHolderObserver {
	
	private Context mContext;
	private IMLocator mLocator;
	private IMDbEngine mDbEngine;
	private IMDbWriterInternal mDbWriter;
	private IMDispatcherSender mDispatcher;
	private IMSmsSender mSmsSender;
	private IMSmsNotificator mSmsNotificator;
	private Handler mHandler;
	private IMRsa mRsa;
	private IMSha256 mSha;
	private IMPassHolder mPassHolder;
	
	private Runnable mRunUpdateNotificator = new Runnable() {
		public void run() {
			mSmsNotificator.Update( GetCountNewSms() );
		}
	};

	public CMMain(IMLocator locator) {
		super();
		mLocator = locator;
		mDbEngine = mLocator.createDbEngine();
		mDispatcher = mLocator.createDispatcher();
		
		mDbWriter = mLocator.createDbWriter();
		mDbWriter.SetDbEngine(mDbEngine);
		mDbWriter.SetDispatcher(mDispatcher);
		
		mSmsSender = mLocator.createSmsSender();
		
		mSmsNotificator = mLocator.createSmsNotificator();
		mHandler = new Handler();
		
		mRsa = mLocator.createRsa();
		mRsa.setObserver(this);
		
		mSha = mLocator.createSha256();
		
		mPassHolder = mLocator.createPassHolder();
		mPassHolder.setObserever(this);
    }

	public void Open(Context context) throws MyException {
		mContext = context;
		
		mDbEngine.Open(mContext.getContentResolver());
		
		mSmsSender.SetObserver(this);
		mSmsSender.SetContext(mContext);
		mSmsSender.open();
		
		mSmsNotificator.Init(mContext /*getApplicationContext(); */);
		
		mDispatcher.addListener(this);
		
		IMSetting set = mLocator.createSetting();
		
		mDbEngine.TableSetting().getById(set, TTypSetting.EPassTimout);
		long tim = set.getIntVal();
		mPassHolder.setInterval(tim*1000);
		
		mDbEngine.TableSetting().getById(set, TTypSetting.ERsaPub);
		
		String pub = set.getStrVal();
		if (pub.length()>0) {
			mRsa.setPublicKey(pub);
			
			mDbEngine.TableSetting().getById(set, TTypSetting.ERsaPriv);
			mPassHolder.setKey(set.getStrVal());
		}
		
		mRunUpdateNotificator.run();
	}
	
	public IMDbReader DbReader() {
		return mDbEngine;
	}
	
	public IMDbWriter DbWriter() {
		return mDbWriter;
	}

	public void SendSms(String phone, String text) throws MyException {
		
		if (phone==null || phone.length()==0)
			throw new MyException(TTypMyException.ESmsErrSendNoPhone);
		
		Log.d("!!!","SendSms phone: "+phone);
		
		if (!phone.matches("^[+]{0,1}[0-9]+$"))
			throw new MyException(TTypMyException.EErrPhoneFormat);
		
		if (text==null || text.length()==0)
			throw new MyException(TTypMyException.ESmsErrSendNoText);
		
		IMSms sms = mLocator.createSms();
		
		sms.setHash( mSha.getHash(phone) );
		
		byte[] cPhone = mRsa.EncryptBuffer(phone.getBytes());
		byte[] cText = mRsa.EncryptBuffer(text.getBytes());
		
		try {
			String encoding = IMDbEngine.ENCODING;
			sms.setPhone(new String(cPhone, encoding));
			sms.setText(new String(cText, encoding));
		} catch (MyException e) {
			throw e;
		} catch (Exception e) {
			throw new MyException(TTypMyException.EErrStringEncode);
		}
		
		sms.setDate( new Date() );
		sms.setDirection( TTypDirection.EOutgoing );
		sms.setFolder( TTypFolder.EOutbox );
		sms.setIsNew(TTypIsNew.ENew);
		
		int id = mDbEngine.TableSms().Insert(sms);
		sms.setId(id);
		
		mSmsSender.sendSms(phone, text, sms.getId());
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsSendStart);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);
		
		IMEventSimpleID insertEv = mLocator.createEventSimpleID();
		insertEv.setTyp(TTypEvent.ESmsOutboxAdded);
		insertEv.setId(sms.getId());
		mDispatcher.pushEvent(insertEv);
	}

	private void pushMyException(MyException e) {
		IMEventSimpleID ev = mLocator.createEventSimpleID();
        ev.setTyp(TTypEvent.EErrMyException);
        ev.setId(ev.getId());
        mDispatcher.pushEvent(ev);	
	}
	
	private int GetCountNewSms() {
		int new_cnt = -1;
		try {
			new_cnt = mDbEngine.TableSms().getCountNew();
		} catch (MyException e) {
			pushMyException(e);
	    }	
		return new_cnt;
	}
	
	public void handleSmsRecieved(int smsId) {	
        try {
        	IMSms sms = mLocator.createSms();
			mDbEngine.TableSms().getById(sms, smsId);
			sms.setIsNew(TTypIsNew.ENew);
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
	        pushMyException(e);
		}
        
        IMEventSimpleID ev = mLocator.createEventSimpleID();
        ev.setTyp(TTypEvent.ESmsRecieved);
        ev.setId(smsId);
        mDispatcher.pushEvent(ev);
	}



	public IMDispatcher Dispatcher() {
		return mDispatcher;
	}



	public void SmsSenderSent(IMSmsSender sender, int tag) {
		
		IMSms sms = mLocator.createSms();

		try {
			mDbEngine.TableSms().getById(sms, tag);
			sms.setFolder( TTypFolder.ESent );
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
			pushMyException(e);
		}
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsSent);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);
		
	}



	public void SmsSenderSentError(IMSmsSender sender, int tag,  int err) {
		IMEventErr ev = mLocator.createEventErr();
		ev.setTyp(TTypEvent.ESmsSendError);
		ev.setId(tag);
		ev.setErr(err);
		mDispatcher.pushEvent(ev);
	}



	public void SmsSenderDelivered(IMSmsSender sender, int tag) {
		IMSms sms = mLocator.createSms();

		try {
			mDbEngine.TableSms().getById(sms, tag);
			sms.setIsNew( TTypIsNew.EOld );
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
			pushMyException(e);
		}
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsDelivered);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);		
	}



	public void SmsSenderDeliverError(IMSmsSender sender, int tag, int err) {
		IMEventErr ev = mLocator.createEventErr();
		ev.setTyp(TTypEvent.ESmsDeliverError);
		ev.setId(tag);
		ev.setErr(err);
		mDispatcher.pushEvent(ev);		
	}

	public void Close() {
		mSmsSender.close();
	}

	public void listenerEvent(IMEvent event) throws Exception {
		switch(event.getTyp()) {
		case ESmsRecieved:
			mSmsNotificator.Popup( GetCountNewSms() );
			break;
		case ESmsUpdated:
		case ESmsDelMany:
		case ESmsDeleted:
			mHandler.removeCallbacks(mRunUpdateNotificator);
			mHandler.postDelayed(mRunUpdateNotificator, 500);
			break;
		case ESettingUpdated:
			int id = ((IMEventSimpleID)event).getId();
			SettingUpdated( TTypSetting.from(id) );
			break;
		}
		
	}

	private void SettingUpdated(TTypSetting typ) {
		try {
			IMSetting set = mLocator.createSetting();
			mDbEngine.QuerySetting().getById(set, typ);
			
			switch (typ) {
			case EPassTimout:
				mPassHolder.setInterval(set.getIntVal()*1000);
				break;
			}
		} catch (MyException e) {
			IMEventErr ev = mLocator.createEventErr();
			ev.setTyp(TTypEvent.EErrMyException);
			ev.setErr(e.getId().getValue());
			mDispatcher.pushEvent(ev);
		}
	}
	
	public void changePass(String oldPass, String newPass) throws MyException {
		
		IMSetting set = mLocator.createSetting();
		mDbEngine.TableSetting().getById(set, TTypSetting.ERsaPub);
		String pub = set.getStrVal();

		
		if (pub.length()>0) {
			mDbEngine.TableSetting().getById(set, TTypSetting.ERsaPriv);
			String priv = set.getStrVal();
			
			IMAes aes = mLocator.createAes();
			priv = aes.decrypt(oldPass, priv);
			priv = aes.encrypt(newPass, priv);
			
			set.setStrVal(priv);
			mDbEngine.TableSetting().Update(set);
			
			mPassHolder.setKey(priv);
			mPassHolder.setPass(newPass);
		}
		else {
			mPassHolder.setPass(newPass);
			mRsa.startGenerateKeyPair();
			IMEvent ev = mLocator.createEvent();
			ev.setTyp(TTypEvent.ERsaKeyPairGenerateStart);
			mDispatcher.pushEvent(ev);
			
		}
	}

	public void RsaKeyPairGenerated(IMRsa _sender, BigInteger _key) throws Exception {
		String pub = mRsa.getPublicKey();
		
		IMBase64 b64 = mLocator.createBase64();
		String priv = new String( b64.encode(_key.toByteArray()) );
		
		IMAes aes = mLocator.createAes();
		priv = aes.encrypt(mPassHolder.getPass(), priv);
		mPassHolder.setKey(priv);
		
		IMSetting set = mLocator.createSetting();
		
		set.setId(TTypSetting.ERsaPriv.getValue());
		set.setStrVal(priv);
		mDbEngine.TableSetting().Update(set);
		
		set.setId(TTypSetting.ERsaPub.getValue());
		set.setStrVal(pub);
		mDbEngine.TableSetting().Update(set);
		
		IMEvent ev = mLocator.createEvent();
		ev.setTyp(TTypEvent.ERsaKeyPairGenerated);
		mDispatcher.pushEvent(ev);
		
	}

	public void RsaKeyPairGenerateError(IMRsa _sender, int _err) throws Exception {
		IMEventErr ev = mLocator.createEventErr();
		ev.setTyp(TTypEvent.ERsaKeyPairGenerateError);
		ev.setErr(_err);
		mDispatcher.pushEvent(ev);
	}

	public void enterPass(String _pass) throws MyException {
		mPassHolder.setPass(_pass);
	}

	public String decryptString(String data) throws MyException {
		byte[] buf=null;
		try {
			buf = data.getBytes(IMDbEngine.ENCODING);
		} catch (Exception e) {
			//never hapens
			Log.e("!!!", "Error in getBytes: "+e.getMessage());
			throw new MyException(TTypMyException.EErrStringEncode);
		}
		byte[] resBuf = mRsa.DecryptBuffer(mPassHolder.getKey(), buf);
		String res = new String(resBuf);
		return res;
	}

	public boolean isPassValid() {
		return mPassHolder.isPassValid();
	}

	public void passExpired(IMPassHolder sender) {
		IMEvent ev = mLocator.createEvent();
		ev.setTyp(TTypEvent.EPassExpired);
		mDispatcher.pushEvent(ev);
		Log.d("!!!", "EPassExpired pushed");
	}

	public void lockNow() {
		mPassHolder.clearPass();
		//passExpired(null);
	}
}
