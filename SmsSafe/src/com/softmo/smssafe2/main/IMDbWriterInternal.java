package com.softmo.smssafe2.main;

import com.softmo.smssafe2.dbengine.IMDbEngine;

public interface IMDbWriterInternal extends IMDbWriter {
	void SetDispatcher(IMDispatcherSender dispatcher);
	void SetDbEngine(IMDbEngine dbEngine);
}
