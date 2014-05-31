package com.softmo.smssafe2.utils;

public interface IMTimer {
	void SetObserver(IMTimerObserver observer);
	void startTimer(long ms) throws MyException;
	void cancelTimer();
}
