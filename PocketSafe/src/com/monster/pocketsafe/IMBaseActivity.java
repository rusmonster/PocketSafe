package com.monster.pocketsafe;

import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

public interface IMBaseActivity {
	IMLocator getLocator();
	IMMain getMain() throws MyException;
	void lockNow();
}
