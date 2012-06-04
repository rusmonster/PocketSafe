package com.softmo.smssafe.sec;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

public class CMSha256 implements IMSha256 {

	public String getHash(String _data) throws MyException {
		
		if (_data==null)
			throw new MyException(TTypMyException.EErrSha256NullArgument);
		
       MessageDigest digest=null;
       try {
           digest = MessageDigest.getInstance("SHA-256");
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
           throw new MyException(TTypMyException.EErrSha256);
       }
       
       digest.reset();
       byte[] sha = digest.digest(_data.getBytes());
       
       String res = String.format("%0" + (sha.length*2) + "X", new BigInteger(1, sha));
       return res;
	}

}
