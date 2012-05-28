package com.softmo.libsafe.view;

import com.softmo.libsafe.main.IMMain;
import com.softmo.libsafe.utils.IMLocator;
import com.softmo.libsafe.utils.MyException;

public interface IMBaseActivity {
	IMLocator getLocator();
	IMMain getMain() throws MyException;
	void lockNow();
}
