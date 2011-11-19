package com.monster.pocketsafe.dbengine;

import java.util.Date;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;


public class CMSms implements IMSms {

	private int mId;
	private TTypDirection mDirection;
	private TTypIsNew mIsNew;
	private String mPhone;
	private String mText;
	private Date mDate;
	
	public Date getDate() {
		return mDate;
	}

	public TTypDirection getDirection() {
		return mDirection;
	}

	public int getId() {
		return mId;
	}

	public TTypIsNew getIsNew() {
		return mIsNew;
	}

	public String getPhone() {
		return mPhone;
	}

	public String getText() {
		return mText;
	}

	public void setDate(Date dat) {
		mDate=dat;
	}

	public void setDirection(TTypDirection direction) {
		mDirection = direction;
	}

	public void setId(int id) throws MyException {
		if (id<0)
			throw new MyException(TTypMyException.EInvalidDbId);
		mId = id;
	}

	public void setIsNew(TTypIsNew isNew) {
		mIsNew = isNew;
	}

	public void setPhone(String phone) throws MyException {
		phone = phone.trim();
		
		if (phone.length()>50)
			throw new MyException(TTypMyException.EPhoneTooLong);
		
		if (!phone.matches("^[+]{0,1}[0-9]{1,}$"))
			throw new MyException(TTypMyException.EPhoneInvalid);
		
		mPhone = phone;
	}

	public void setText(String Text) {
		mText=Text;
	}

}
