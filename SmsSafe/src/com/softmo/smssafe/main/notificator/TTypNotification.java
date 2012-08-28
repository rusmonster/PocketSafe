package com.softmo.smssafe.main.notificator;

import java.util.HashMap;
import java.util.Map;

public enum TTypNotification {
	ESoundAndIcon(0),
	EIconOnly(1),
	ESoundOnly(2),
	ENone(3);
	
    private final int mValue;
	 
    private TTypNotification(int value)
    {
        mValue = value;
    }
    
    public int getValue() {
    	return mValue;
    }
 
    private static final Map<Integer, TTypNotification> _map = new HashMap<Integer, TTypNotification>();
    static
    {
        for (TTypNotification typ : TTypNotification.values())
            _map.put(typ.mValue, typ);
    }
 
    public static TTypNotification from(int value)
    {
        return _map.get(value);
    }	
}
