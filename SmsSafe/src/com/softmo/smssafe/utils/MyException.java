package com.softmo.smssafe.utils;

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
		EDbErrGetCountSmsNew(30), 
		EErrPhoneFormat(31),
		ESmsErrSendGeneral(32), 
		ERsaInvalidKeyFormat(33), 
		ERsaErrGeneratingKeyPair(34), 
		ERsaErrEncrypt(35),
		ERsaErrDecrypt(36), 
		ERsaNotReady(37), 
		EAesErrEncrypt(38),
		EAesErrDecrypt(39), 
		EPassNotMatch(40), 
		EPassEmpty(41), 
		EPassInvalid(42), 
		EPassExpired(43), 
		EErrStringEncode(44), 
		EErrSha256(45), 
		EErrSha256NullArgument(46), 
		EErrBase64Encode(47),
		EErrBase64Decode(48), 
		EErrUnknown(49), 
		ESmsErrResend(50),
		EDbErrGetCountSmsByHash(51),
		EDbIdNotFoundGroup(52),
		EDbErrInsertGroup(53),
		EPassNotDigital(54), 
		EImporterErrBusy(55), 
		EImporterNullParam(56), 
		EDbErrExecSQL(57), 
		EImporterCancelled(58),
		EImporterErrGeneral(59),
		EErrBusy(60)
		;
		
	    private final int mValue;
	    
	 
	    private TTypMyException(int value)
	    {
	        mValue = value;
	    }
	    
	    public int getValue() {
	    	return mValue;
	    }
	 
	    private static final Map<Integer, TTypMyException> _map = new HashMap<Integer, TTypMyException>();
	    static
	    {
	        for (TTypMyException typ : TTypMyException.values())
	            _map.put(typ.mValue, typ);
	    }
	 
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
	
	public String toString() {
		return new String("MyException: "+getId().toString());
	}
}
