package com.softmo.smssafe2.views;

import com.softmo.smssafe2.main.IMMain;
import com.softmo.smssafe2.utils.IMLocator;
import com.softmo.smssafe2.utils.MyException;

public interface IMBaseActivity {
	IMLocator getLocator();
	IMMain getMain() throws MyException;
	void lockNow();
	void importSms();
}
