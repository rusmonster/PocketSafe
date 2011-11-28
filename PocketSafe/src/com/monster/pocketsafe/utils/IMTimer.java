package com.monster.pocketsafe.utils;

public interface IMTimer {
	void SetObserver(IMTimerObserver observer);
	void startTimer(long ms) throws MyException;
	void cancelTimer();
}
