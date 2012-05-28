package com.softmo.libsafe.main;

public interface IMDispatcher {
	
	void addListener(IMListener listener);
	void delListener(IMListener listener);

}
