package com.softmo.libsafe.sec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import com.softmo.libsafe.utils.IMLocator;
import com.softmo.libsafe.utils.MyException;
import com.softmo.libsafe.utils.MyException.TTypMyException;

public class CMRsa implements IMRsa {
	
	private static int LEN_MAX_BLOCK = 245; //245;
	
	private IMLocator mLocator;
	private IMRsaObserver mObserver;
	
	private BigInteger mModulus;
	private BigInteger mPublicExp;
	
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
		
		if (_key==null)
			throw new MyException(TTypMyException.ERsaInvalidKeyFormat);
		
		String[] strs = _key.split(" ");
		
		if (strs.length != 2)
			throw new MyException(TTypMyException.ERsaInvalidKeyFormat);
		
		try {
			String m = strs[0];
			String e = strs[1];
			
			IMBase64 base64 = mLocator.createBase64();
			
			byte[] modul = base64.decode(m.getBytes());
			byte[] exp = base64.decode(e.getBytes());
			
			mModulus = new BigInteger(modul);
			mPublicExp = new BigInteger(exp);
			
		}
		catch (Exception e) {
			Log.e("!!!", "Error in setPublicKey: "+e.getMessage()+"("+e.toString()+")");
			throw new MyException(TTypMyException.ERsaInvalidKeyFormat);	
		}
		
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
		        final BigInteger privateExp = priv.getPrivateExponent();
		        
		        mHandler.post(new Runnable() {
					public void run() { GenerateFinish(TTypMyException.ENoError.getValue(), privateExp); }
				});
			} catch (Exception e) {
		        mHandler.post(new Runnable() {
					public void run() {	GenerateFinish(TTypMyException.ERsaErrGeneratingKeyPair.getValue(), null); }
				});
			}
		}
	};
	
	private void GenerateFinish(int _err, BigInteger _key) {
		mState = TRsaState.EIdle;
		
		try {
			if (_err==0)
				mObserver.RsaKeyPairGenerated(this, _key);
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
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic( new RSAPublicKeySpec(mModulus, mPublicExp) );
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			int len_data = _data.length;
			
			int offset=0;
			int len = (len_data>LEN_MAX_BLOCK)?LEN_MAX_BLOCK:len_data;
			
			byte[] cipherData;
			do {
				
				synchronized (this) {
					cipherData = cipher.doFinal(_data,offset,len);	
				}
				
			    int len_ci = cipherData.length;
			    
			    baos.write((len_ci >> 8)&0xFF);
			    baos.write(len_ci&0xFF);
			    baos.write(cipherData);
			    
			    offset+=len;
			    len = len_data-offset;
			    if (len>LEN_MAX_BLOCK) len =LEN_MAX_BLOCK;
			} while(len>0);
		    
		    return baos.toByteArray();
		    
		} catch(Exception e) {
			Log.e("!!!", "EncryptBuffer: "+e.getMessage());
			throw new MyException(TTypMyException.ERsaErrEncrypt);
		} finally {
			try {
				baos.close();
			} catch (IOException e) {}
		}
	}

	public byte[] DecryptBuffer(BigInteger _key, byte[] _data) throws MyException {
		
		if (mState!=TRsaState.EIdle)
			throw new MyException(TTypMyException.ERsaNotReady);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = kf.generatePrivate( new RSAPrivateKeySpec(mModulus, _key) );
			
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			int len_data = _data.length;
			int offset = 0;
			
			//Log.d("!!!", "tid: "+Thread.currentThread().getId()+"; len_data: "+len_data);
			byte[] cipherData;
			
			while (offset<len_data) {			
				int len = _data[offset++] << 8;
				len |= _data[offset++];
				
				//Log.d("!!!", "tid: "+Thread.currentThread().getId()+"; dec len: "+len);
				synchronized (this) {
					cipherData = cipher.doFinal(_data, offset, len);	
				}
				
				//Log.d("!!!", "tid: "+Thread.currentThread().getId()+"; dec data: "+new String(cipherData));
				
				baos.write(cipherData);
				
				offset += len;
			}

			return baos.toByteArray();
		    
		} catch(Exception e) {
			Log.e("!!!", "DecryptBuffer: "+e.getMessage());
			throw new MyException(TTypMyException.ERsaErrDecrypt);
		} finally {
			try {
				baos.close();
			} catch (IOException e) {}
		}
	}

}
