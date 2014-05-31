package com.softmo.smssafe2.views;

import com.softmo.smssafe2.R;
import com.softmo.smssafe2.R.array;
import com.softmo.smssafe2.R.layout;
import com.softmo.smssafe2.R.string;
import com.softmo.smssafe2.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe2.main.IMEvent;
import com.softmo.smssafe2.main.TTypEvent;
import com.softmo.smssafe2.utils.MyException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class OptionsActivity extends CMBaseListActivity {

	private static final int IDD_PASSTIMOUT = 1001;
	private static final int IDD_IMPORTCONF = 1002;
	private static final int IDD_SELECTNOTIFICATION = 1003;
	private static final int IDD_SELECTSMSLIST = 1004;
	
	private static final int SET_PASS_RESULT = 1010;
	private static final int ENTER_PASS_RESULT = 1011;	

    public String[] mTimout_labels;
    public String[] mTimout_vals;
    public String[] mNotification_labels;
    public String[] mSmsListTyp_labels;
    
    private String mEnteredPass;
    private String mNewPass;
    private Dialog mDlg;
    
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listbase);
	}

	@Override
	protected void onStart() {
		mTimout_labels = getResources().getStringArray(R.array.text_passtimout);
		mTimout_vals = getResources().getStringArray(R.array.text_passtimout_vals);
		mNotification_labels = getResources().getStringArray(R.array.text_opt_notif);
		mSmsListTyp_labels = getResources().getStringArray(R.array.text_opt_sms_list);
		
		super.onStart();
	}

	private void createListAdapter() {
        setListAdapter(new OptionsAdapter(this));    
	}
		
	public void onMainBind() throws MyException {
		createListAdapter();
		
		String enter = mEnteredPass;
		String newpass = mNewPass;
		
		if (enter!=null && newpass!=null) {
			mEnteredPass=null;
			mNewPass=null;
			
			getHelper().getMain().changePass(enter, newpass);
			Toast.makeText(this, R.string.pass_changed, Toast.LENGTH_SHORT).show();
		}
		
	}

	public void listenerEvent(IMEvent event) throws Exception {
		TTypEvent typ = event.getTyp();
		
		switch(typ) {
		case ESettingUpdated:
			createListAdapter();
			break;
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		switch (OptionsAdapter.ITEMS.values()[position]) {
		case EChangePass:
			changePass();
			break;
		case EPassTimeout:
			passTimout();
			break;
		case ENotification:
			selectNotification();
			break;
		case ESmsListType:
			selectSmsListTyp();
			break;
		case EImport:
			showDialog(IDD_IMPORTCONF);
			break;
		case ELockNow:
			getHelper().lockNow();
			break;
		}
	}	
	
	private void changePass() {
		Intent i = new Intent(this, EnterPassActivity.class);
		startActivityForResult(i, ENTER_PASS_RESULT);
	}
	
	private void passTimout() {
		showDialog(IDD_PASSTIMOUT);
	}

	private void selectNotification() {
		showDialog(IDD_SELECTNOTIFICATION);
	}

	private void selectSmsListTyp() {
		showDialog(IDD_SELECTSMSLIST);
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		Log.d("!!!", "OptionsActivity onCreateDialog: "+id);
		Dialog dlg = super.onCreateDialog(id);
		if (dlg!=null)
			return dlg;
		
		switch (id) {
		case IDD_PASSTIMOUT:
			{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.opt_passtimeout);

			
			builder.setItems(mTimout_labels, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					String val = mTimout_vals[which];
					try {
						getHelper().getMain().DbWriter().UpdateSetting(TTypSetting.EPassTimout, val);
					} catch (MyException e) {
						e.printStackTrace();
						ErrorDisplayer.displayError(OptionsActivity.this, e);
					}
				}
			});
			
			dlg =  builder.create();
			}
			break;
		case IDD_SELECTNOTIFICATION:
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.opt_notificator_title);
			
			
			builder.setItems(mNotification_labels, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					try {
						getHelper().getMain().DbWriter().UpdateSetting(TTypSetting.ENotification, String.valueOf(which));
					} catch (MyException e) {
						e.printStackTrace();
						ErrorDisplayer.displayError(OptionsActivity.this, e);
					}
				}
			});
			
			dlg =  builder.create();
		}
		break;
		case IDD_SELECTSMSLIST:
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.opt_sms_list);
			
			
			builder.setItems(mSmsListTyp_labels, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					try {
						getHelper().getMain().DbWriter().UpdateSetting(TTypSetting.ESmsListTyp, String.valueOf(which));
					} catch (MyException e) {
						e.printStackTrace();
						ErrorDisplayer.displayError(OptionsActivity.this, e);
					}
				}
			});
			
			dlg =  builder.create();
		}
		break;
		case IDD_IMPORTCONF:
			{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.confimport);
			
			builder.setPositiveButton(android.R.string.yes, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					getHelper().importSms();
				}
			});
			
			builder.setNegativeButton(android.R.string.no, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					//nothing
				}
			});
			
			dlg =  builder.create();
			}
			break;
		}
		
		return dlg;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==RESULT_OK) {
			switch (requestCode) {
			case ENTER_PASS_RESULT:
				mEnteredPass = data.getStringExtra(EnterPassActivity.PASS);
				Intent i = new Intent(this, SetPassActivity.class);
				startActivityForResult(i, SET_PASS_RESULT);
				break;
			case SET_PASS_RESULT:
				mNewPass = data.getStringExtra(SetPassActivity.PASS);
				break;
			}
		}
		else {
			mEnteredPass=null;
			mNewPass=null;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		if (mDlg!=null){  
			mDlg.dismiss(); 
			mDlg=null;
		}
		super.onPause();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		Log.d("!!!", "OptionsActivity onPrepareDialog: "+id);
		if (mDlg!=null) mDlg.dismiss();
		mDlg=dialog;
		super.onPrepareDialog(id, dialog);
	}	
	
	
	
}
