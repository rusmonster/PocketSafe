package com.monster.pocketsafe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class ErrorDisplayer {
	
	private static int getResId(TTypMyException typ) {
		switch(typ) {
		case ENoError: return R.string.ENoError;
		case EInvalidDbId: return R.string.EInvalidDbId; 
		case EPhoneTooLong: return R.string.EPhoneTooLong; 
		case EPhoneInvalid: return R.string.EPhoneInvalid; 
		case EDbAlreadyOpened: return R.string.EDbAlreadyOpened; 
		case EDbNotOpened: return R.string.EDbNotOpened;
		case EDbVersionError: return R.string.EDbVersionError; 
		case EDbErrCreateFolder: return R.string.EDbErrCreateFolder; 
		case EDbIdNotFoundSetting: return R.string.EDbIdNotFoundSetting; 
		case EDbErrorGetLastID: return R.string.EDbErrorGetLastID; 
		case EDbIdNotFoundSms: return R.string.EDbIdNotFoundSms; 
		case EDbErrGetCountSms: return R.string.EDbErrGetCountSms; 
		case EDbErrGetCountSetting: return R.string.EDbErrGetCountSetting; 
		case EDbErrInsertSms: return R.string.EDbErrInsertSms; 
		case EErrServiceNotBinded: return R.string.EErrServiceNotBinded; 
		case EDbErrGetCountContact: return R.string.EDbErrGetCountContact; 
		case ETimerNotReady: return R.string.ETimerNotReady; 
		case ESmsErrSentGeneric: return R.string.ESmsErrSentGeneric; 
		case ESmsErrSentNoService: return R.string.ESmsErrSentNoService; 
		case ESmsErrSentNullPdu: return R.string.ESmsErrSentNullPdu; 
		case ESmsErrSentRadioOff: return R.string.ESmsErrSentRadioOff; 
		case ESmsErrSentGeneral: return R.string.ESmsErrSentGeneral; 
		case ESmsErrDeliverGeneral: return R.string.ESmsErrDeliverGeneral;
		case ESmsErrSenderAlreadyOpened: return R.string.ESmsErrSenderAlreadyOpened; 
		case ESmsErrSenderObserverIsNull: return R.string.ESmsErrSenderObserverIsNull; 
		case ESmsErrSenderContextIsNull: return R.string.ESmsErrSenderContextIsNull; 
		case ESmsErrSenderClosed: return R.string.ESmsErrSenderClosed; 
		case ESmsErrSenderAlreadySending: return R.string.ESmsErrSenderAlreadySending; 
		case ESmsErrSendNoPhone: return R.string.ESmsErrSendNoPhone; 
		case ESmsErrSendNoText: return R.string.ESmsErrSendNoText;
		case EDbErrGetCountSmsNew: return R.string.EDbErrGetCountSmsNew; 
		case EErrPhoneFormat: return R.string.EErrPhoneFormat;
		case ESmsErrSendGeneral: return R.string.ESmsErrSendGeneral; 
		case ERsaInvalidKeyFormat: return R.string.ERsaInvalidKeyFormat; 
		case ERsaErrGeneratingKeyPair: return R.string.ERsaErrGeneratingKeyPair; 
		case ERsaErrEncrypt: return R.string.ERsaErrEncrypt;
		case ERsaErrDecrypt: return R.string.ERsaErrDecrypt; 
		case ERsaNotReady: return R.string.ERsaNotReady; 
		case EAesErrEncrypt: return R.string.EAesErrEncrypt;
		case EAesErrDecrypt: return R.string.EAesErrDecrypt; 
		case EPassNotMatch: return R.string.EPassNotMatch; 
		case EPassEmpty: return R.string.EPassEmpty; 
		case EPassInvalid: return R.string.EPassInvalid; 
		case EPassExpired: return R.string.EPassExpired; 
		case EErrStringEncode: return R.string.EErrStringEncode; 
		case EErrSha256: return R.string.EErrSha256; 
		case EErrSha256NullArgument: return R.string.EErrSha256NullArgument; 
		case EErrBase64Encode: return R.string.EErrBase64Encode;
		case EErrBase64Decode: return R.string.EErrBase64Decode;
		case EErrUnknown: return R.string.ErrUnknown;
		case ESmsErrResend: return R.string.ESmsErrResend;
		case EDbErrGetCountSmsByHash: return R.string.EDbErrGetCountSmsByHash;
		default:
			return R.string.ErrUnknown;
		}
	}
	
	public static String getErrStr(Context context, int err) {
		TTypMyException e = TTypMyException.from(err);
		int id = getResId(e);
		Log.e("!!!", "ERROR for display: "+e+"("+id+")");
		String str = "Error: "+e;
		
		try {
			str = context.getResources().getString(id);
		} catch(Exception e1) {
			Log.e("!!!", "Error reading resource: "+e1.getMessage());
		}
		
		return str;
	}
	
	public static void displayError(Context context, int err) {
		String errstr = getErrStr(context, err);
		Toast.makeText(context, errstr, Toast.LENGTH_SHORT).show();
	}
	
	public static void displayError(Context context, MyException e) {
		displayError(context, e.getId().getValue());
	}
}
