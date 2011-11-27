package com.monster.pocketsafe.main;

public interface IMDispatcher {
	
	void addListener(IMListener listener);
	void delListener(IMListener listener);

}
