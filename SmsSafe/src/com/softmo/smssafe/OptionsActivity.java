package com.softmo.smssafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softmo.smssafe.R;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe.main.IMEvent;
import com.softmo.smssafe.main.TTypEvent;
import com.softmo.smssafe.utils.MyException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class OptionsActivity extends CMBaseListActivity {

	private static final int IDD_PASSTIMOUT = 1001;
	private static final int IDD_IMPORTCONF = 1002;
	
	private static final int SET_PASS_RESULT = 1010;
	private static final int ENTER_PASS_RESULT = 1011;	

	private final static String TITLE_KEY = "title";
    private final static String DESCRIPTION_KEY = "description";	
	
    private String[] mTimout_labels;
    private String[] mTimout_vals;
    
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
		
		super.onStart();
	}

	private void createListAdapter() throws MyException {
      
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        IMSetting set = getHelper().getLocator().createSetting();
        getHelper().getMain().DbReader().QuerySetting().getById(set, TTypSetting.EPassTimout);
        String val = set.getStrVal();
        
        for (int i=0; i<mTimout_vals.length; i++)
        	if (mTimout_vals[i].equals(val)) {
        		val = mTimout_labels[i];
        		break;
        	}

        
        
		String str = getResources().getString(R.string.opt_changepass);
		
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        item.put(DESCRIPTION_KEY, "");
        data.add(item);
        
        str = getResources().getString(R.string.opt_passtimeout);
        item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        item.put(DESCRIPTION_KEY, val);
        data.add(item);
        
        str = getResources().getString(R.string.opt_import);
        item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        item.put(DESCRIPTION_KEY, "");
        data.add(item);       
        
        str = getResources().getString(R.string.opt_locknow);
        item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        item.put(DESCRIPTION_KEY, "");
        data.add(item);            

        setListAdapter(new SimpleAdapter(this, data, R.layout.listbaseitem, 
        		new String[] { TITLE_KEY, DESCRIPTION_KEY }, new int[] { R.id.text1, R.id.text2 }));    
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
		
		switch (position) {
		case 0:
			changePass();
			break;
		case 1:
			passTimout();
			break;
		case 2:
			showDialog(IDD_IMPORTCONF);
			break;
		case 3:
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
	
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dlg = null;
		
		if (mDlg!=null) mDlg.dismiss(); mDlg=null;
		
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
		mDlg=dialog;
		super.onPrepareDialog(id, dialog);
	}	
	
	
	
}
