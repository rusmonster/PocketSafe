package com.softmo.smssafe.sec;

import java.util.HashMap;
import java.util.Map;

import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import android.util.Log;

public class CMBase64 implements IMBase64 {
	
	private static final String BASE64_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final byte[] BASE64_BYTES = BASE64_STR.getBytes();
	private static final byte	BASE64_ZERO = '=';
	private static final Map<Byte, Byte> mMap;
	static {
		mMap = new HashMap<Byte,Byte>(BASE64_BYTES.length);
		for (byte i=0; i<BASE64_BYTES.length; i++)
			mMap.put(new Byte(BASE64_BYTES[i]), new Byte(i));
	}
	

	public byte[] encode(byte[] _data) throws MyException {
		try {
			int cnt = _data.length;
			int cnt_res = (cnt/3 + ((cnt%3==0)?0:1))*4;
			byte[] res = new byte[cnt_res];
			Log.d("!!!", "encode: cnt_res="+cnt_res);
			
			int len=cnt-2;
			int k=0;
			
			for (int i=0; i<len; i+=3) {
				int tmp = ((_data[i]&0xFF) << 16) | ((_data[i+1]&0xFF)<< 8) | (_data[i+2]&0xFF);
				
				res[k++] = BASE64_BYTES[(int) (tmp>>18)];
				res[k++] = BASE64_BYTES[(int) ((tmp>>12)&0x3F)];
				res[k++] = BASE64_BYTES[(int) ((tmp>>6)&0x3F)];
				res[k++] = BASE64_BYTES[(int) (tmp&0x3F)];
			}
			
			len = (k/4)*3;
			
			if (len<cnt) {
				int n=len;
				
				int tmp = (_data[n++]&0xFF) << 8;
				if (n<cnt) tmp|=_data[n++]&0xFF;
				tmp <<= 8;
				if (n<cnt) tmp|=_data[n++]&0xFF;
				
				res[k++] = BASE64_BYTES[(int) (tmp>>18)];
				res[k++] = BASE64_BYTES[(int) ((tmp>>12)&0x3F)];
				res[k++] = BASE64_BYTES[(int) ((tmp>>6)&0x3F)];
				res[k++] = BASE64_BYTES[(int) (tmp&0x3F)];
				
				for (int i=0; i<3-(cnt-len); i++) 
					res[--k] = BASE64_ZERO;
			}
		
			return res;
		} catch (Exception e) {
			throw new MyException(TTypMyException.EErrBase64Encode);
		}
	}

	public byte[] decode(byte[] _data) throws MyException {
		try {
			int cnt = _data.length;
			int cnt_res = cnt/4*3;
			
			int k=cnt;
			while(k>0 && _data[--k] == BASE64_ZERO)
				cnt_res--;
			
			byte[] res = new byte[cnt_res];
			if (cnt_res==0) return res;
			
			k=0;
			int len=cnt-7;
			
			for (int i=0; i<len; i+=4) {
				int tmp = (mMap.get(_data[i])<<18) | (mMap.get(_data[i+1])<<12) | (mMap.get(_data[i+2])<<6) | mMap.get(_data[i+3]);
				
				res[k++] = (byte) (tmp >> 16);
				res[k++] = (byte) ((tmp>>8)&0xFF);
				res[k++] = (byte) (tmp&0xFF);
			}
			
			len=cnt-4;
			
			int tmp =  (mMap.get(_data[len++])<<18);
			tmp |= (mMap.get(_data[len++])<<12);
			
			if (_data[len] != BASE64_ZERO)
				tmp |= (mMap.get(_data[len++]) << 6);
			
			if (_data[len] != BASE64_ZERO)
				tmp |= mMap.get(_data[len++]);
			
			res[k++] = (byte) (tmp >> 16);
			
			if (k<cnt_res)
				res[k++] = (byte) ((tmp>>8)&0xFF);
			
			if (k<cnt_res)
				res[k++] = (byte) (tmp&0xFF);
			
			return res;
		} catch (Exception e) {
			throw new MyException(TTypMyException.EErrBase64Decode);
		}			
	}

}