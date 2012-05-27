package com.softmo.smssafe.main;

public interface IMDispatcher {
	
	void addListener(IMListener listener);
	void delListener(IMListener listener);

}
