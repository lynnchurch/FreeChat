package com.free.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.free.exception.FCException;
import com.free.exception.FCNetworkException;
import com.free.ui.FCConfig;

public class FCValidateCodeEmail {
	static private FCValidateCodeEmail fCValidateCodeEmail=null;
	private FCValidateCodeEmail(){
		
	}
	public String sendEmail(String emailAddress) throws FCException{
		String validateCode;
		validateCode=FCValidateCode.getInstance().getValidateCode();
		Map<String,String> map=new HashMap<String,String>();
		map.put("emailAddress", emailAddress);
		map.put("validateCode", validateCode);
		Log.e("validateCode", validateCode);
		FCHttpClient.getInstance().doPost(FCConfig.EMAIL_URL,map);
		return validateCode;
	}
	
	static public FCValidateCodeEmail getInstance(){
		if(fCValidateCodeEmail==null){
			fCValidateCodeEmail=new FCValidateCodeEmail();
		}
		return fCValidateCodeEmail;
	}
}
