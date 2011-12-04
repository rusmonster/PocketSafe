package com.monster.pocketsafe.main.notificator;

import android.content.Context;

public interface IMSmsNotificator {
	void Init(Context context);
	void Popup(int cnt_newsms);
	void Update(int cnt_newsms);

}
