package com.monster.pocketsafe.sec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import android.os.Handler;
import android.util.Log;

import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMRsa implements IMRsa {
	
	private IMLocator mLocator;
	private IMRsaObserver mObserver;
	
	private BigInteger mModulus;
	private BigInteger mPublicExp;
	private BigInteger mPrivateExp;
	
	private Handler mHandler;
	
	private enum TRsaState {
		EIdle,
		EGenerating
	}
	private TRsaState mState;
	
	public CMRsa(IMLocator _locator) {
		mState = TRsaState.EIdle;
		mLocator = _locator;
	}
	
	public void setObserver(IMRsaObserver _observer) {
		mObserver = _observer;
	}

	public String getPrivateKey() throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
			
		
		IMBase64 base64 = mLocator.createBase64();
		
		byte[] modul = mModulus.toByteArray();
		String m = new String( base64.encode(modul) );
		
		byte[] exp = mPrivateExp.toByteArray();
		String e = new String( base64.encode(exp) );
		
		String res = new String(m+" "+e);
		return res;
	}

	public void setPrivateKey(String _key) throws MyException {		
	
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		String[] strs = _key.split(" ");
		
		if (strs.length != 2)
			throw new MyException(TTypMyException.ERsaInvalidKeyFormat);
		
		String m = strs[0];
		String e = strs[1];
		
		IMBase64 base64 = mLocator.createBase64();
		
		byte[] modul = base64.decode(m.getBytes());
		byte[] exp = base64.decode(e.getBytes());
		
		mModulus = new BigInteger(modul);
		mPrivateExp = new BigInteger(exp);
	}

	public String getPublicKey() throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		IMBase64 base64 = mLocator.createBase64();
		
		byte[] modul = mModulus.toByteArray();
		String m = new String( base64.encode(modul) );
		
		byte[] exp = mPublicExp.toByteArray();
		String e = new String( base64.encode(exp) );
		
		String res = new String(m+" "+e);
		return res;
	}

	public void setPublicKey(String _key) throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		String[] strs = _key.split(" ");
		
		if (strs.length != 2)
			throw new MyException(TTypMyException.ERsaInvalidKeyFormat);
		
		String m = strs[0];
		String e = strs[1];
		
		IMBase64 base64 = mLocator.createBase64();
		
		byte[] modul = base64.decode(m.getBytes());
		byte[] exp = base64.decode(e.getBytes());
		
		mModulus = new BigInteger(modul);
		mPublicExp = new BigInteger(exp);
	}
	
	private Runnable mRunGenerate = new Runnable() {
		
		public void run() {
			try {
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		        kpg.initialize(2048);
		        KeyPair kp = kpg.genKeyPair();
	
		        KeyFactory fact = KeyFactory.getInstance("RSA");
		        RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
		        RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);
		        
		        mModulus = pub.getModulus();
		        mPublicExp = pub.getPublicExponent();
		        mPrivateExp = priv.getPrivateExponent();
		        
		        mHandler.post(new Runnable() {
					public void run() { GenerateFinish(TTypMyException.ENoError.getValue()); }
				});
			} catch (Exception e) {
		        mHandler.post(new Runnable() {
					public void run() {	GenerateFinish(TTypMyException.ERsaErrGeneratingKeyPair.getValue()); }
				});
			}
		}
	};
	
	private void GenerateFinish(int _err) {
		mState = TRsaState.EIdle;
		
		try {
			if (_err==0)
				mObserver.RsaKeyPairGenerated(this);
			else
				mObserver.RsaKeyPairGenerateError(this, _err);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void startGenerateKeyPair() throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		try {
			mHandler = new Handler();
			Thread GenTh = new Thread(mRunGenerate);
			GenTh.start();

			mState = TRsaState.EGenerating;
		} catch(Exception e) {
			throw new MyException(TTypMyException.ERsaErrGeneratingKeyPair);
		}
		
	}

	public byte[] EncryptBuffer(byte[] _data) throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		try {
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic( new RSAPublicKeySpec(mModulus, mPublicExp) );
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		    byte[] cipherData = cipher.doFinal(_data);
		    return cipherData;
		    
		} catch(Exception e) {
			throw new MyException(TTypMyException.ERsaErrEncrypt);
		}
	}

	public byte[] DecryptBuffer(byte[] _data) throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		try {
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate( new RSAPrivateKeySpec(mModulus, mPrivateExp) );
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] cipherData = cipher.doFinal(_data);
			return cipherData;
		    
		} catch(Exception e) {
			Log.w("!!!", "DecryptBuffer: "+e.getMessage());
			throw new MyException(TTypMyException.ERsaErrDecrypt);
		}
	}

}
