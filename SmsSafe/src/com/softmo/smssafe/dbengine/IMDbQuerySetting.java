package com.softmo.smssafe.dbengine;

import java.util.HashMap;
import java.util.Map;

import com.softmo.smssafe.utils.MyException;

public interface IMDbQuerySetting extends IMDbDataSet {
	public enum TTypSetting {
		EDbZeroSetting(0),
		EDbVersion(1),
		EPassTimout(2),
		ERsaPub(3),
		ERsaPriv(4),
		ENotification(5);
		
	    private final int mValue;
		 
	    private TTypSetting(int value)
	    {
	        mValue = value;
	    }
	    
	    public int getValue() {
	    	return mValue;
	    }
	 
	    private static final Map<Integer, TTypSetting> _map = new HashMap<Integer, TTypSetting>();
	    static
	    {
	        for (TTypSetting typsetting : TTypSetting.values())
	            _map.put(typsetting.mValue, typsetting);
	    }
	 
	    public static TTypSetting from(int value)
	    {
	        return _map.get(value);
	    }		
	}
	
	void getById(IMSetting dest, TTypSetting id) throws MyException;
}
