package com.softmo.smssafe.dbengine;

import java.util.Date;

import com.softmo.smssafe.utils.MyException;


public interface IMSms extends IMDbItem {
	public int getDirection();
	public void setDirection(int direction);
	public int getFolder();
	public void setFolder(int folder);
	public int getIsNew();
	public void setIsNew(int is_new);
	public String getHash();
	public void setHash(String Hash);
	public String getPhone();
	public void setPhone(String phone) throws MyException;
	public String getText();
	public void setText(String Text);
	public Date getDate();
	public void setDate(Date dat);
	public int getStatus();
	public void setStatus(int status);
	public int getSmsId();
	public void setSmsId(int smsId);
}
