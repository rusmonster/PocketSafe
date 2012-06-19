package com.softmo.smssafe.main;

import java.util.HashMap;
import java.util.Map;

public enum TTypEvent
{
    ESmsRecieved(1),
    ESmsUpdated(2), 
    ESmsSendStart(3),
    ESmsOutboxAdded(4),
    ESmsSent(5), 
    EErrMyException(6), 
    ESmsSendError(7),
    ESmsDelivered(8), 
    ESmsDeliverError(9), 
    ESmsDelMany(10), 
    ESmsDeleted(11), 
    ERsaKeyPairGenerateStart(12),
    ERsaKeyPairGenerated(13),
    ERsaKeyPairGenerateError(14), 
    EPassExpired(15), 
    ESettingUpdated(16), 
    EImportStart(17),
    EImportProgress(18),
    EImportFinish(19), 
    EImportError(20)
    ;
 
    private final int mValue;
 
    private TTypEvent(int value)
    {
        mValue = value;
    }
 
    public int getValue() {
    	return mValue;
    }
    
    private static final Map<Integer, TTypEvent> _map = new HashMap<Integer, TTypEvent>();
    static
    {
        for (TTypEvent typevent : TTypEvent.values())
            _map.put(typevent.mValue, typevent);
    }
 
    public static TTypEvent from(int value)
    {
        return _map.get(value);
    }
}
