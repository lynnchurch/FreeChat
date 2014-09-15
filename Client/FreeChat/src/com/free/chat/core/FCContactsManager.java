package com.free.chat.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Column;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.util.StringUtils;

import android.util.Log;

import com.free.exception.FCException;
import com.free.ui.model.User;

public class FCContactsManager {
	private static XMPPConnection connection;
	private static Roster roster;

	/**
	 * 初始化连接与花名册
	 */
	public static void init() {
		connection = XmppConnectionManager.getConnection();
		roster = connection.getRoster();
	}

	public static Roster getRoster() {
		init();
		if (roster == null) {
			roster = connection.getRoster();
		}
		return roster;
	}

	/**
	 * 查找联系人
	 * 
	 * @param emailAddress
	 * @return
	 * @throws FCException
	 */
	public static Map<String, String> search(String emailAddress)
			throws FCException {
		Map<String, String> result = new HashMap<String, String>();
		init();

		try {
			UserSearchManager search = new UserSearchManager(connection);
			Form searchForm = search.getSearchForm("search."
					+ connection.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Email", true); // 查找的字段
			answerForm.setAnswer("search", emailAddress);// 查找的内容
			ReportedData data = search.getSearchResults(answerForm, "search."
					+ connection.getServiceName());

			Iterator<Row> rowIte = data.getRows(); // 获取记录迭代器
			Iterator<Column> colIte = data.getColumns(); // 获取字段迭代器
			while (rowIte.hasNext()) {
				Row row = rowIte.next();
				Iterator<String> it1 = row.getValues("Email");
				String email = null;

				while (it1.hasNext()) {
					email = it1.next();
					// Log.e("Email", str);
					result.put("Email", email);
				}
				// Log.e("email",email);
				// 确保完全匹配
				if (!email.equals(emailAddress)) {
					continue;
				}

				Iterator<String> it2 = row.getValues("Username");
				while (it2.hasNext()) {
					result.put("Username", it2.next());
				}
				VCard me = new VCard();
				me.load(connection,StringUtils.escapeNode(emailAddress)+"@iz23ds9vkjvz");
				String nickname = me.getNickName();
				if (nickname == null) {
					Iterator<String> it3 = row.getValues("Name");
					while (it3.hasNext()) {
						result.put("Name", it3.next());
					}
				} else {
					result.put("Name", nickname);
				}
				String signature = me.getMiddleName();
				if(signature==null){
					signature="暂未设置个性签名";
				}
//				Log.e("signature", signature);
				result.put("Signature", signature);
			}

			return result;
		} catch (XMPPException e) {
			Log.e("exception", e.getMessage());
			throw new FCException();
		}
	}

	/**
	 * 添加联系人到花名册
	 * 
	 * @param username
	 * @param nickname
	 * @param groupname
	 * @throws FCException
	 */
	public static void addContact(String username, String nickname,
			String groupname) {
		init();
		username = escapeNode(username) + "@iz23ds9vkjvz";
		String[] strs = { groupname };
		try {
			roster.createEntry(username, nickname, strs);
		} catch (Exception e) {
			Log.e("exception", e.getMessage());
		}
	}

	/**
	 * 获取联系人花名册
	 * 
	 * @return
	 */
	public static List<User> getContacts() {
		init();
		List<User> users = new ArrayList<User>();
		Collection<RosterEntry> collection = roster.getEntries();
		Iterator<RosterEntry> iterator = collection.iterator();
		while (iterator.hasNext()) {

			RosterEntry rosterEntry = iterator.next();
			if ((rosterEntry.getType() == RosterPacket.ItemType.both || rosterEntry
					.getType() == RosterPacket.ItemType.from)) {
				// Log.e("status",""+rosterEntry.getStatus());
				User user = new User();
				// getUser()返回的为jid
				String jid = rosterEntry.getUser();
				String username = unescapeNode(jid.split("@")[0]);
				// Log.e("username", username);
				user.setUsername(username);
				user.setEmail(username);
				String nickname = rosterEntry.getName();
				// Log.e("nickname", nickname);
				user.setNickname(nickname);
				Collection<RosterGroup> groups = rosterEntry.getGroups();
				Iterator<RosterGroup> it = groups.iterator();
				while (it.hasNext()) {
					String group = it.next().getName();
					// Log.e("group", group);
					user.setGroup(group);
				}
				Map<String, String> result = null;
				try {
					result = search(username);

				} catch (FCException e) {
					Log.e("exception", e.getMessage());
				}
				String signature = result.get("Signature");
//				Log.e("signature", signature);
				user.setSignature(signature);
				users.add(user);
			}

		}
		return users;
	}

	/**
	 * 获取某一联系人信息
	 * 
	 * @param username
	 * @param nickname
	 * @param group
	 * @return
	 */
	public static User getContact(String username, String nickname, String group) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(username);
		user.setNickname(nickname);
		user.setGroup(group);
		Map<String, String> result = null;
		try {
			result = search(username);

		} catch (FCException e) {
			Log.e("exception", e.getMessage());
		}
		String signature = result.get("Signature");
		// Log.e("signature", signature);
		user.setSignature(signature);

		return user;
	}


	/**
	 * 发送同意好友请求消息（添加订阅字段的信息）
	 * 
	 * @param username
	 * @throws FCException
	 */
	public static void sendFriendAgree(String username) {
		init();
		username = escapeNode(username);
		Presence presence = new Presence(Presence.Type.subscribed);
		presence.setTo(username + "@iz23ds9vkjvz/Smack");

		connection.sendPacket(presence);

	}

	/**
	 * 判断是否为好友请求已同意
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isFriendAgree(String username) {
		init();
		Collection<RosterEntry> collection = roster.getEntries();
		Iterator<RosterEntry> iterator = collection.iterator();
		while (iterator.hasNext()) {

			RosterEntry rosterEntry = iterator.next();
			if ((rosterEntry.getType() == RosterPacket.ItemType.to)) {
				// getUser()返回的为jid
				String jid = rosterEntry.getUser();
				String _username = unescapeNode(jid.split("@")[0]);
				if (_username.equals(username)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 邮箱作为用户名中的@与JID中的@有冲突，得进行编码
	 * 
	 * @param user
	 * @return
	 */
	public static String escapeNode(String user) {
		return StringUtils.escapeNode(user);
	}

	/**
	 * 解码
	 * 
	 * @param jid
	 * @return
	 */
	public static String unescapeNode(String jid) {
		return StringUtils.unescapeNode(jid);
	}
}
