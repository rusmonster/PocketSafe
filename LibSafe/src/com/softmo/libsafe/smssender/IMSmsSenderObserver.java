package com.softmo.libsafe.smssender;

public interface IMSmsSenderObserver {

	void SmsSenderSent(IMSmsSender sender, int tag);

	void SmsSenderSentError(IMSmsSender sender, int tag, int err);

	void SmsSenderDelivered(IMSmsSender sender, int tag);

	void SmsSenderDeliverError(IMSmsSender sender, int tag, int err);

}
