package com.monster.pocketsafe;

import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.utils.MyException;

public interface IMBaseActivity {
	IMMain getMain() throws MyException;
}
