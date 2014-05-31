package com.softmo.smssafe2.main;

public interface IMDispatcherSender extends IMDispatcher {
	void pushEvent(IMEvent event);
}
