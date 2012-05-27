package com.softmo.smssafe.main;

public interface IMDispatcherSender extends IMDispatcher {
	void pushEvent(IMEvent event);
}
