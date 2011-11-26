package com.monster.pocketsafe.dbengine.provider;

import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CMSQLiteOnenHelper extends SQLiteOpenHelper implements BaseColumns {
	
	public static final String DB_NAME = "soft-mo.db";
	public static final int DB_VERSION = 1;
	
	public static final String TABLE_SETTING = "M__SETTING";
	public static final String SETTING_VAL = "VAL";
	
	public static final String TABLE_SMS = "M__SMS";
	public static final String SMS_DIRECTION = "DIRECTION";
	public static final String SMS_FOLDER = "FOLDER";
	public static final String SMS_ISNEW = "ISNEW";
	public static final String SMS_PHONE = "PHONE";
	public static final String SMS_TEXT = "TXT";
	public static final String SMS_DATE = "DAT";
	
	

	public CMSQLiteOnenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_SETTING);;
		db.execSQL("CREATE TABLE "+TABLE_SETTING+"("+
				_ID + " INTEGER PRIMARY KEY, "
				+SETTING_VAL+" VARCHAR(250))");
		
		ContentValues values = new ContentValues();
        
        values.put(_ID, +TTypSetting.EDbPassTimout.ordinal());
        values.put(SETTING_VAL, "300");
        db.insert(TABLE_SETTING, SETTING_VAL, values);
		
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_SMS);
		db.execSQL("CREATE TABLE "+TABLE_SMS+"("+
				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				SMS_DIRECTION + " INTEGER," +
				SMS_FOLDER + "  INTEGER," +
				SMS_ISNEW + " INTEGER," +
				SMS_PHONE + " VARCHAR(50)," +
				SMS_TEXT + " TEXT," +
				SMS_DATE + " DATETIME)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
