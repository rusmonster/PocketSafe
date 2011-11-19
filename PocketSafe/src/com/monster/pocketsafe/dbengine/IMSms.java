package com.monster.pocketsafe.dbengine;

import java.util.Date;

import com.monster.pocketsafe.utils.MyException;


public interface IMSms extends IMDbItem {
	public int getDirection();
	public void setDirection(int direction);
	public int getFolder();
	public void setFolder(int folder);
	public int getIsNew();
	public void setIsNew(int is_new);
	public String getPhone();
	public void setPhone(String phone) throws MyException;
	public String getText();
	public void setText(String Text);
	public Date getDate();
	public void setDate(Date dat);
}
