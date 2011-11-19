package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;


public class CMDbEngine implements IMDbEngine {
	static private final int CUR_DB_VERSION = 2;
	
	private IMLocator mLocator;
	private IMSdkDbConection mConn;
	private IMDbTableSetting mTabSetting;
	private IMDbTableSms mTabSms;
	
	public CMDbEngine(IMLocator locator) {
		mLocator = locator;
	}
	
	public void Close() {
		if (mConn != null) {
			mTabSetting = null;
			mTabSms = null;
			mConn.Close();
			mConn = null;
		}
	}

	public void Open(String filename) throws MyException {
		if (mConn != null)
			throw new MyException(TTypMyException.EDbAlreadyOpened);
		
		mConn = mLocator.createSdkDbConnection();
		mConn.Open(filename);
		
		mTabSetting = mLocator.createDbTableSetting();
		mTabSetting.SetConnection(mConn);
		
		mTabSms = mLocator.createDbTableSms();
		mTabSms.SetConnection(mConn);
		
		int db_ver = 0;
		IMSetting set = mLocator.createSetting();
		try {
			if ( mTabSetting.getById(set, TTypSetting.EDbVersion) )
				db_ver = set.getIntVal();
		} catch (Exception e)
		{}
		
		if (db_ver<1) {
			mConn.ExecSQL("DROP TABLE IF EXISTS M__SETTING");
			mConn.ExecSQL("CREATE TABLE M__SETTING(ID INTEGER PRIMARY KEY, VAL VARCHAR(250))");
			
			mConn.ExecSQL("DROP TABLE IF EXISTS M__SMS");
			mConn.ExecSQL("CREATE TABLE M__SMS(ID INTEGER PRIMARY KEY AUTOINCREMENT)");

			mConn.ExecSQL("INSERT INTO M__SETTING (ID,VAL) VALUES("+TTypSetting.EDbVersion.ordinal()+",'1')");
			db_ver = 1;
		}
		
		if (db_ver<2) {
			mConn.ExecSQL("DROP TABLE IF EXISTS M__SMS");
			mConn.ExecSQL("CREATE TABLE M__SMS(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
					"DIRECTION INTEGER," +
					"ISNEW INTEGER," +
					"PHONE VARCHAR(50)," +
					"TXT TEXT," +
					"DAT DATETIME)");
			
			mConn.ExecSQL("UPDATE M__SETTING SET VAL='2' WHERE ID="+TTypSetting.EDbVersion.ordinal());
			db_ver = 2;
		}
		
		if (db_ver != CUR_DB_VERSION)
			throw new MyException(TTypMyException.EDbVersionError);
		
	}

	public IMDbTableSms TableSms() {
		return mTabSms;
	}

	public IMDbQuerySms QuerySms() {
		return mTabSms;
	}

}
