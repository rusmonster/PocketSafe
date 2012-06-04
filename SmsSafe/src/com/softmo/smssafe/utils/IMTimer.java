package com.softmo.smssafe.utils;

public interface IMTimer {
	void SetObserver(IMTimerObserver observer);
	void startTimer(long ms) throws MyException;
	void cancelTimer();
}
