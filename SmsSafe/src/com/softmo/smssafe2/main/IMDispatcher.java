package com.softmo.smssafe2.main;

public interface IMDispatcher {
	
	void addListener(IMListener listener);
	void delListener(IMListener listener);

}
