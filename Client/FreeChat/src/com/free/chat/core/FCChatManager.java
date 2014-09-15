package com.free.chat.core;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import com.free.exception.FCXMPPException;

public class FCChatManager {
	private static XMPPConnection connection;
	private static ChatManager chatManager;

	/**
	 * 初始化连接与会话管理器
	 */
	public static void init() {
		connection = XmppConnectionManager.getConnection();
		chatManager = connection.getChatManager();
	}

	public static ChatManager getChatManager() {
		init();
		return chatManager;
	}

	/**
	 * 发送消息
	 * 
	 * @param username
	 * @param message
	 * @throws FCXMPPException
	 */
	public static void sendMessage(String username, Message message)
			throws FCXMPPException {
		init();
		String jid = StringUtils.escapeNode(username) + "@iz23ds9vkjvz";
		Chat chat = chatManager.createChat(jid, null);
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			throw new FCXMPPException();
		}
	}
}
