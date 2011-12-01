package com.monster.pocketsafe.utils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class MyException extends Exception {


	public enum TTypMyException {
		ENoError(0),
		EInvalidDbId(1), 
		EPhoneTooLong(2), 
		EPhoneInvalid(3), 
		EDbAlreadyOpened(4), 
		EDbNotOpened(5), 
		EDbVersionError(6), 
		EDbErrCreateFolder(7), 
		EDbIdNotFoundSetting(8), 
		EDbErrorGetLastID(9), 
		EDbIdNotFoundSms(10), 
		EDbErrGetCountSms(11), 
		EDbErrGetCountSetting(12), 
		EDbErrInsertSms(13), 
		EErrServiceNotBinded(14), 
		EDbErrGetCountContact(15), 
		ETimerNotReady(16), 
		ESmsErrSentGeneric(17), 
		ESmsErrSentNoService(18), 
		ESmsErrSentNullPdu(19), 
		ESmsErrSentRadioOff(20), 
		ESmsErrSentGeneral(21), 
		ESmsErrDeliverGeneral(22),
		ESmsErrSenderAlreadyOpened(23), 
		ESmsErrSenderObserverIsNull(24), 
		ESmsErrSenderContextIsNull(25), 
		ESmsErrSenderClosed(26), 
		ESmsErrSenderAlreadySending(27), 
		ESmsErrSendNoPhone(28), 
		ESmsErrSendNoText(29),
		EDbErrGetCountSmsNew(30);
		
		   /**
	     * Value for this difficulty
	     */
	    public final int Value;
	 
	    private TTypMyException(int value)
	    {
	        Value = value;
	    }
	 
	    // Mapping difficulty to difficulty id
	    private static final Map<Integer, TTypMyException> _map = new HashMap<Integer, TTypMyException>();
	    static
	    {
	        for (TTypMyException typ : TTypMyException.values())
	            _map.put(typ.Value, typ);
	    }
	 
	    /**
	     * Get difficulty from value
	     * @param value Value
	     * @return Difficulty
	     */
	    public static TTypMyException from(int value)
	    {
	        return _map.get(value);
	    }
	}
	
	private TTypMyException mId;
	
	public MyException(TTypMyException id) {
		super();
		mId = id;
	}	
	
	public TTypMyException getId() {
		return mId;
	}
}
