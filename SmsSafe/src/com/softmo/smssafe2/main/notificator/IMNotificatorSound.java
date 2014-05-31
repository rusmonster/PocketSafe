package com.softmo.smssafe2.main.notificator;

public interface IMNotificatorSound extends IMSmsNotificator {
	void setType(TTypNotification typ);
	void Cancel();
}
