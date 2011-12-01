package com.monster.pocketsafe.sms.sender;

public interface IMSmsSenderObserver {

	void SmsSenderSent(CMSmsSender sender, int tag);

	void SmsSenderSentError(CMSmsSender sender, int tag, int err);

	void SmsSenderDelivered(CMSmsSender sender, int tag);

	void SmsSenderDeliverError(CMSmsSender sender, int tag, int err);

}
