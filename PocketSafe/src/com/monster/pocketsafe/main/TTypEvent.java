package com.monster.pocketsafe.main;

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
    ESmsSendError(7);

 
    /**
     * Value for this difficulty
     */
    public final int Value;
 
    private TTypEvent(int value)
    {
        Value = value;
    }
 
    // Mapping difficulty to difficulty id
    private static final Map<Integer, TTypEvent> _map = new HashMap<Integer, TTypEvent>();
    static
    {
        for (TTypEvent typevent : TTypEvent.values())
            _map.put(typevent.Value, typevent);
    }
 
    /**
     * Get difficulty from value
     * @param value Value
     * @return Difficulty
     */
    public static TTypEvent from(int value)
    {
        return _map.get(value);
    }
}
