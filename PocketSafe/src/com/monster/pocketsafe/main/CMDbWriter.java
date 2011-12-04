package com.monster.pocketsafe.main;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

public class CMDbWriter implements IMDbWriterInternal {

	private IMLocator mLocator;
	private IMDispatcherSender mDispatcher;
	private IMDbEngine mDbEngine;

	public CMDbWriter(IMLocator locator) {
		mLocator = locator;
	}
	
	public void SetDispatcher(IMDispatcherSender dispatcher) {
		mDispatcher = dispatcher;
	}

	public void SetDbEngine(IMDbEngine dbEngine) {
		mDbEngine = dbEngine;
	}
	
	public void SmsUpdate(IMSms sms) throws MyException {
		mDbEngine.TableSms().Update(sms);
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsUpdated);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);
	}

	public void SmsDelAll() throws MyException {
		mDbEngine.TableSms().Clear();
		
		IMEvent ev = mLocator.createEvent();
		ev.setTyp(TTypEvent.ESmsDelMany);
		mDispatcher.pushEvent(ev);
		
	}

	public void SmsDeleteByPhone(String phone)  throws MyException  {
		mDbEngine.TableSms().DeleteByPhone(phone);
		
		IMEvent ev = mLocator.createEvent();
		ev.setTyp(TTypEvent.ESmsDelMany);
		mDispatcher.pushEvent(ev);
	}

	public void SmsDelete(int sms_id) throws MyException {
		mDbEngine.TableSms().Delete(sms_id);
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsDeleted);
		ev.setId(sms_id);
		mDispatcher.pushEvent(ev);	
	}

}
