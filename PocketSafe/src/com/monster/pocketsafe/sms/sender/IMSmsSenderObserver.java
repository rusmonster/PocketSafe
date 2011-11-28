package com.monster.pocketsafe.sms.sender;

import com.monster.pocketsafe.utils.MyException.TTypMyException;

public interface IMSmsSenderObserver {

	void SmsSenderSent(CMSmsSender sender);

	void SmsSenderSentError(CMSmsSender sender, int err);

	void SmsSenderDelivered(CMSmsSender sender);

	void SmsSenderDeliverError(CMSmsSender sender, int err);

}
