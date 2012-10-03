package com.softmo.smssafe.views.smsadapter;

import java.util.HashMap;
import java.util.Map;

public enum TTypSmsList {
	EFlat(0),
	EChat(1);
	
    private final int mValue;
	 
    private TTypSmsList(int value)
    {
        mValue = value;
    }
    
    public int getValue() {
    	return mValue;
    }
 
    private static final Map<Integer, TTypSmsList> _map = new HashMap<Integer, TTypSmsList>();
    static
    {
        for (TTypSmsList typ : TTypSmsList.values())
            _map.put(typ.mValue, typ);
    }
 
    public static TTypSmsList from(int value)
    {
        return _map.get(value);
    }	
}
