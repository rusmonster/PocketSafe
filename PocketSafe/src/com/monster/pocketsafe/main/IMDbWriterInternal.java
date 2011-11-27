package com.monster.pocketsafe.main;

import com.monster.pocketsafe.dbengine.IMDbEngine;

public interface IMDbWriterInternal extends IMDbWriter {
	void SetDispatcher(IMDispatcherSender dispatcher);
	void SetDbEngine(IMDbEngine dbEngine);
}
