package com.free.util;

import java.util.Random;

public class FCValidateCode {
	private String randString = "01CDEFG2789abcdefUVWXMNOPghijklqr3456stuvwxyzABHIJmnopKLQRSTYZ"; // 随机产生的字符集
	private int charNum = 6; // 随机产生字符数量
	private Random random = new Random();
	static private FCValidateCode ranVC=null;
	
	private FCValidateCode(){
		
	}
	public String getValidateCode(){
		String str=""; // 最终生成的验证码
		for(int i=0;i<charNum;i++){
			str+=getRandomChar();
		}
		return str;
	}
	
    /*
     * 获取随机的字符
     */
    private String getRandomChar(){
        return ""+randString.charAt(random.nextInt(randString.length()));
    }
    
    static public FCValidateCode getInstance(){
    	if(ranVC==null){
    		ranVC=new FCValidateCode();
    	}
    	return ranVC;
    }
}
