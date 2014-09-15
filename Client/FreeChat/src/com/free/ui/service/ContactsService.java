package com.free.ui.service;

import java.util.Collection;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.free.chat.core.FCContactsManager;
import com.free.chat.core.XmppConnectionManager;
import com.free.exception.FCException;
import com.free.ui.Constant;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.activity.NewFriendsActivity;
import com.free.ui.db.NewFriendDao;
import com.free.ui.db.NoticeDao;
import com.free.ui.db.UserDao;
import com.free.ui.model.NewFriend;
import com.free.ui.model.Notice;
import com.free.ui.model.User;
import com.free.util.DateUtils;
import com.free.util.NameUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ContactsService extends Service {

	private Context context;
	private FCConfig config;
	private XMPPConnection connection;
	private Roster roster;
	private NoticeDao noticeDao;
	private NewFriendDao newFriendDao;

	@Override
	public void onCreate() {
		context = this;
		config = FCConfig.getInstance();
		noticeDao = NoticeDao.getInstance(context);
		newFriendDao = NewFriendDao.getInstance(context);
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		connection = XmppConnectionManager.getConnection();
		addPacketFilterListener();
		initRoster();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化花名册 服务重启时，更新花名册
	 */
	private void initRoster() {
		roster = FCContactsManager.getRoster();
		roster.removeRosterListener(rosterListener);
		roster.addRosterListener(rosterListener);

		// 如果SQLite中不存在当前登录用户的联系人列表，则保存联系人列表到SQLite（减少网络数据请求，节省流量）
		UserDao userDao = UserDao.getInstance(context);
		if (!userDao.isExist(config.getUsername())) {

			userDao.saveContacts(FCContactsManager.getContacts(),
					config.getUsername());
		}

	}

	@Override
	public void onDestroy() {

//		connection.removePacketListener(packetListener);

		super.onDestroy();
	}

	/**
	 * 添加数据包监听器。
	 */
	private void addPacketFilterListener() {

		PacketFilter filter = new AndFilter(
				new PacketTypeFilter(Presence.class));

		connection.addPacketListener(packetListener, filter); // 注册监听
	}

	PacketListener packetListener = new PacketListener() {
		String request = "请求加你为好友。";
		String agree = "同意加你为好友。";
		String reject = "拒绝加你为好友。";

		@Override
		public void processPacket(Packet packet) {
			Log.e("packet", packet.toXML());
			if (packet instanceof Presence) {
				Presence presence = (Presence) packet;
				// Presence还有很多方法，可查看API
				String from = presence.getFrom();// 发送方jid
				String username = NameUtils.getUsername(from);// 发送方用户名
				String nickname = NameUtils.getNickname(username);// 发送方昵称

				String to = presence.getTo();// 接收方jid

				if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
					Log.e("type", "subscribe");
					if (!newFriendDao.isExist( // 避免重复接收到服务器发来的未处理好友请求包
							username, NewFriend.UNDO, config.getUsername())) {
						NewFriend newFriend = new NewFriend();
						Notice notice = new Notice();
						if (FCContactsManager.isFriendAgree(username)) {
//							sendNotification(R.drawable.noti_freechat, "好友同意",
//									nickname + agree, NewFriendsActivity.class);
							newFriend.setContent(agree);
							newFriend.setCondition(NewFriend.AGREED);
							notice.setContent(nickname + " 已同意你的好友请求。");
							// 收到好友同意，将好友添加到花名册。
							FCContactsManager.sendFriendAgree(username);
							FCContactsManager.addContact(username, nickname,
									"我的好友");
							// 将好友信息保存到数据库
							String whose = FCConfig.getInstance().getUsername();
							User user = FCContactsManager.getContact(username,
									nickname, "我的好友");
							UserDao.getInstance(context).saveContact(user,
									whose);
						} else {
//							sendNotification(R.drawable.noti_freechat, "好友申请",
//									nickname + request,
//									NewFriendsActivity.class);
							newFriend.setContent(request);
							newFriend.setCondition(NewFriend.UNDO);

							notice.setContent(nickname + " 请求加你为好友。");

						}
						newFriend.setUsername(username);
						newFriend.setNickname(nickname);
						newFriend.setStatus(NewFriend.UNREAD);

						notice.setType(Notice.NEWFRIEND);
						notice.setFrom(username);
						notice.setTo(to);
						notice.setStatus(Notice.UNREAD);
						notice.setTime(DateUtils.getCurDateStr());
						notice.setTitle("新朋友");

						String whose = config.getUsername();
						// 保存到数据库中
						if (noticeDao.isExistType("" + Notice.NEWFRIEND, whose)) {
							noticeDao.delNoticeByType("" + Notice.NEWFRIEND,
									whose);
						}
						noticeDao.saveNotice(notice, whose);
						newFriendDao.saveNewFriend(newFriend, whose);
						// 发送通知广播
						Intent intent = new Intent();
						intent.setAction(Constant.ACTION_ROSTER_SUBSCRIPTION);
						intent.putExtra("notice", notice);
						sendBroadcast(intent);
					}

				} else if (presence.getType().equals(Presence.Type.subscribed)) {// 同意添加好友
					Log.e("type", "subscribed");
				} else if (presence.getType().equals(Presence.Type.unsubscribe)) {// 拒绝添加好友
																					// 和
																					// 删除好友
					Log.e("type", "unsubscribe");
				} else if (presence.getType()
						.equals(Presence.Type.unsubscribed)) {
					Log.e("type", "unsubscribed");
				} else if (presence.getType().equals(Presence.Type.unavailable)) {// 好友下线
																					// 要更新好友列表，可以在这收到包后，发广播到指定页面
																					// 更新列表
					Log.e("type", "unavailable");
				} else {// 好友上线
					Log.e("type", "available");
					// sendNotification(R.drawable.noti_freechat,"通知","上线了！",MainActivity.class);
				}
			}
		}
	};

	private RosterListener rosterListener = new RosterListener() {

		@Override
		public void entriesAdded(Collection<String> arg0) {
			Log.e("added", "added");

		}

		@Override
		public void entriesDeleted(Collection<String> arg0) {
			Log.e("deleted", "deleted");

		}

		@Override
		public void entriesUpdated(Collection<String> arg0) {
			Log.e("updated", "updated");

		}

		@Override
		public void presenceChanged(Presence arg0) {
			Log.e("changed", "changed");

		}

	};

}
