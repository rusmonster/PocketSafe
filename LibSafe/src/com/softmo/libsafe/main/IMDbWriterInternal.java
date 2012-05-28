package com.softmo.libsafe.main;

import com.softmo.libsafe.dbengine.IMDbEngine;

public interface IMDbWriterInternal extends IMDbWriter {
	void SetDispatcher(IMDispatcherSender dispatcher);
	void SetDbEngine(IMDbEngine dbEngine);
}
