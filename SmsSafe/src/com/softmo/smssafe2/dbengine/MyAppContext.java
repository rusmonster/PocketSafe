package com.softmo.smssafe2.dbengine;

import android.app.Application;
import android.content.Context;

/*
In Android Manifest file declare following

<application android:name="com.xyz.MyApplication">

</application>
then write the class
*/

public class MyAppContext extends Application{

    private static Context mContext;

    public void onCreate(){
    	super.onCreate();
    	MyAppContext.mContext=getApplicationContext();
    }

    public static Context getAppContext() {
    	return MyAppContext.mContext;
    }
}



