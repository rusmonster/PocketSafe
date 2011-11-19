package com.monster.pocketsafe.utils;

@SuppressWarnings("serial")
public class MyException extends Exception {


	public enum TTypMyException {
		ENoError,
		EInvalidDbId, 
		EPhoneTooLong, EPhoneInvalid, EDbAlreadyOpened, EDbNotOpened, EDbVersionError, EDbErrCreateFolder, EDbIdNotFoundSetting
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
