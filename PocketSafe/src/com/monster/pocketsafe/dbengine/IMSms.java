package com.monster.pocketsafe.dbengine;

import java.util.Date;

import com.monster.pocketsafe.utils.MyException;


public interface IMSms extends IMDbItem {
	public TTypDirection getDirection();
	public void setDirection(TTypDirection direction);
	public TTypIsNew getIsNew();
	public void setIsNew(TTypIsNew is_new);
	public String getPhone();
	public void setPhone(String phone) throws MyException;
	public String getText();
	public void setText(String Text);
	public Date getDate();
	public void setDate(Date dat);
}
