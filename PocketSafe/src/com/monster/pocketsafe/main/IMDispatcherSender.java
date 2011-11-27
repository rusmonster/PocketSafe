package com.monster.pocketsafe.main;

public interface IMDispatcherSender extends IMDispatcher {
	void pushEvent(IMEvent event);
}
