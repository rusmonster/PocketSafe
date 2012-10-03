package com.softmo.smssafe.views;

import com.softmo.smssafe.main.IMMain;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;

public interface IMBaseActivity {
	IMLocator getLocator();
	IMMain getMain() throws MyException;
	void lockNow();
	void importSms();
}
