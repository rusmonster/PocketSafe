package com.monster.pocketsafe.main;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.dbengine.IMSetting;
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

	public void SmsDeleteByHash(String hash)  throws MyException  {
		mDbEngine.TableSms().DeleteByHash(hash);
		
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

	public void UpdateSetting(TTypSetting typ, String val) throws MyException {
		IMSetting setting = mLocator.createSetting();
		
		setting.setId(typ.getValue());
		setting.setStrVal(val);
		
		mDbEngine.TableSetting().Update(setting);
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESettingUpdated);
		ev.setId(typ.getValue());
		mDispatcher.pushEvent(ev);			
	}

}
