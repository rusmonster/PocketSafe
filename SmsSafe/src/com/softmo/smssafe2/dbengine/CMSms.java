package com.softmo.smssafe2.dbengine;

import java.util.Date;

import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;


public class CMSms implements IMSms {

	private int mId;
	private int mDirection;
	private int mFolder;
	private int mIsNew;
	private String mHash;
	private String mPhone;
	private String mText;
	private Date mDate;
	private int mStatus;
	private int mSmsId=-1;
	
	public Date getDate() {
		return mDate;
	}

	public int getDirection() {
		return mDirection;
	}

	public int getId() {
		return mId;
	}

	public int getIsNew() {
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

	public void setDirection(int direction) {
		mDirection = direction;
	}

	public void setId(int id) throws MyException {
		if (id<0)
			throw new MyException(TTypMyException.EInvalidDbId);
		mId = id;
	}

	public void setIsNew(int isNew) {
		mIsNew = isNew;
	}

	public void setPhone(String phone) throws MyException {
		mPhone = phone; //phone.trim();
	}

	public void setText(String Text) {
		mText=Text;
	}

	public int getFolder() {
		return mFolder;
	}

	public void setFolder(int folder) {
		mFolder = folder;
	}

	public String getHash() {
		return mHash;
	}

	public void setHash(String Hash) {
		mHash = Hash;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		mStatus = status;
	}

	public int getSmsId() {
		return mSmsId;
	}

	public void setSmsId(int smsId) {
		mSmsId = smsId;
	}

}
