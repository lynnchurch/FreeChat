package com.free.util;

import org.jivesoftware.smack.util.StringUtils;

import com.free.chat.core.FCContactsManager;
import com.free.exception.FCException;

public class NameUtils {

	/**
	 * 把jid转换成username
	 * 
	 * @param jid
	 * @return
	 */
	public static String getUsername(String jid) {
		return StringUtils.unescapeNode(jid.substring(0, jid.indexOf("@")));
	}

	/**
	 * 把username转换成昵称
	 * 
	 * @param jid
	 * @return
	 */
	public static String getNickname(String username) {
		String nickname = null;
		try {
			nickname = FCContactsManager.search(username).get("Name");
		} catch (FCException e) {
		}
		return nickname;
	}
	
}
