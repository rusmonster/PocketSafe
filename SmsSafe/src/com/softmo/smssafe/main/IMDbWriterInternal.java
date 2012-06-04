package com.softmo.smssafe.main;

import com.softmo.smssafe.dbengine.IMDbEngine;

public interface IMDbWriterInternal extends IMDbWriter {
	void SetDispatcher(IMDispatcherSender dispatcher);
	void SetDbEngine(IMDbEngine dbEngine);
}
