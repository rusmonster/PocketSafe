package com.softmo.smssafe.main.notificator;

public interface IMNotificatorSound extends IMSmsNotificator {
	void setType(TTypNotification typ);
	void Cancel();
}
