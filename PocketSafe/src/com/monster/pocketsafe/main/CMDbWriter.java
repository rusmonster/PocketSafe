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

}
