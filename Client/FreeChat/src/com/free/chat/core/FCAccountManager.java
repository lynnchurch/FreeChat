package com.free.chat.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.util.Log;

import com.free.exception.FCException;
import com.free.exception.FCNetworkException;
import com.free.exception.FCServerException;
import com.free.exception.FCXMPPException;

public class FCAccountManager {
	private static XMPPConnection connection;
	private static AccountManager am;

	/**
	 * 刷新连接，与XmppConnectionManager中的连接保持一致
	 */
	private static void refreshConnection() {
		connection = XmppConnectionManager.getConnection();
		am = new AccountManager(connection);
	}

	/**
	 * 用户注册
	 * 
	 * @param username
	 * @param pwd
	 * @param nickname
	 * @throws FCNetworkException
	 * @throws FCServerException
	 * @throws FCXMPPException
	 */
	public static void register(String username, String pwd, String nickname)
			throws FCXMPPException {
		try {
			XmppConnectionManager.init();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
		refreshConnection();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("email", username);
		attributes.put("name", nickname);
		username = escapeNode(username);

		if (am.supportsAccountCreation()) {
			try {
				am.createAccount(username, pwd, attributes);
			} catch (XMPPException e) {
				throw new FCXMPPException();
			}
			XmppConnectionManager.disConnect();
		}

	}

	/**
	 * 用户登录
	 * 
	 * @param username
	 * @param pwd
	 * @throws IOException
	 * @throws SmackException
	 * @throws XMPPException
	 * @throws SaslException
	 */
	public static void login(String user, String pwd) throws XMPPException {
		try {
			XmppConnectionManager.init();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}

		refreshConnection();
		user = escapeNode(user);
		connection.login(user, pwd);
		
	}

	
	/**
	 * 判断邮箱地址是否已注册
	 * 
	 * @param emailAddress
	 * @throws FCException
	 * @throws NotConnectedException
	 */
	public static boolean isRegistered(String emailAddress) throws FCException {

		try {
			if (XmppConnectionManager.getConnection() == null
					|| !XmppConnectionManager.isConnected()) {
				XmppConnectionManager.init();
			}
			if (!XmppConnectionManager.getConnection().isAuthenticated()) {
				XmppConnectionManager.getConnection().loginAnonymously();
			}
			refreshConnection();
			UserSearchManager search = new UserSearchManager(connection);
			Form searchForm = search.getSearchForm("search."
					+ connection.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Email", true); // 查找的字段
			answerForm.setAnswer("search", emailAddress);// 查找的内容
			ReportedData data = search.getSearchResults(answerForm, "search."
					+ connection.getServiceName());
			Iterator<Row> it = data.getRows();
			if (it.hasNext())
				return true;
			return false;
		} catch (Exception e) {
			throw new FCException();
		}
	}


	/**
	 * 邮箱作为用户名中的@与JID中的@有冲突，得进行换码
	 * 
	 * @param user
	 * @return
	 */
	public static String escapeNode(String user) {
		return StringUtils.escapeNode(user);
	}

}
