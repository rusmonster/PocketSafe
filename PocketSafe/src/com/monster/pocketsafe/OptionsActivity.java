package com.monster.pocketsafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.TTypEvent;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class OptionsActivity extends CMBaseListActivity {

	private static final int IDD_PASSTIMOUT = 1001;
	
	private static final int SET_PASS_RESULT = 1010;
	private static final int ENTER_PASS_RESULT = 1011;	

	private final static String TITLE_KEY = "title";
    private final static String DESCRIPTION_LEY = "description";	
	
    private String[] mTimout_labels;
    private String[] mTimout_vals;
    
    private String mEnteredPass;
    private String mNewPass;
    
	private IMLocator mLocator = new CMLocator();
	
	private Dialog mDlg;
	
	
	@Override
	protected void onStart() {
		mTimout_labels = getResources().getStringArray(R.array.text_passtimout);
		mTimout_vals = getResources().getStringArray(R.array.text_passtimout_vals);
		
		super.onStart();
	}

	private void createListAdapter() throws MyException {
      
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        IMSetting set = mLocator.createSetting();
        getMain().DbReader().QuerySetting().getById(set, TTypSetting.EPassTimout);
        String val = set.getStrVal();
        
        for (int i=0; i<mTimout_vals.length; i++)
        	if (mTimout_vals[i].equals(val)) {
        		val = mTimout_labels[i];
        		break;
        	}

        
        
		String str = getResources().getString(R.string.opt_changepass);
		
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        item.put(DESCRIPTION_LEY, "");
        data.add(item);
        
        str = getResources().getString(R.string.opt_passtimeout);
        item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        
        item.put(DESCRIPTION_LEY, val);
        data.add(item);       
        
        str = getResources().getString(R.string.opt_locknow);
        item = new HashMap<String, String>();
        item.put(TITLE_KEY, str);
        item.put(DESCRIPTION_LEY, "");
        data.add(item);            

        setListAdapter(new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] { TITLE_KEY,
                        DESCRIPTION_LEY }, new int[] { android.R.id.text1, android.R.id.text2 }));    
	}
		
	public void onMainBind() throws MyException {
		createListAdapter();
		
		String enter = mEnteredPass;
		String newpass = mNewPass;
		
		mEnteredPass=null;
		mNewPass=null;
		
		if (enter!=null && newpass!=null) {
			getMain().changePass(enter, newpass);
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
			lockNow();
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
	
	private void lockNow() {
		try {
			getMain().lockNow();
		} catch (MyException e) {
			e.printStackTrace();
			ErrorDisplayer.displayError(this, e);
		}
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dlg = null;
		
		switch (id) {
		case IDD_PASSTIMOUT:
			if (mDlg!=null) 
				dismissDialog(IDD_PASSTIMOUT);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.opt_passtimeout);

			
			builder.setItems(mTimout_labels, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					String val = mTimout_vals[which];
					try {
						getMain().DbWriter().UpdateSetting(TTypSetting.EPassTimout, val);
					} catch (MyException e) {
						e.printStackTrace();
						ErrorDisplayer.displayError(OptionsActivity.this, e);
					}
				}
			});
			
			mDlg = builder.create();
			dlg = mDlg;
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
	
	
}
