package com.free.ui.service;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import com.free.chat.core.FCChatManager;
import com.free.chat.core.FCContactsManager;
import com.free.chat.core.XmppConnectionManager;
import com.free.exception.FCException;
import com.free.ui.Constant;
import com.free.ui.FCApplication;
import com.free.ui.FCConfig;
import com.free.ui.activity.ChatActivity;
import com.free.ui.activity.MainActivity;
import com.free.ui.db.FCMessageDao;
import com.free.ui.db.NoticeDao;
import com.free.ui.db.UserDao;
import com.free.ui.model.FCMessage;
import com.free.ui.model.Notice;
import com.free.util.DateUtils;
import com.free.util.NameUtils;
import com.free.util.NotificationUtils;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service {

	private Context context;
	private NotificationManager notificationManager;
	private FCConfig config;
	private NoticeDao noticeDao;
	private FCMessageDao fCMessageDao;
	private ChatManager chatManager;
	private FCApplication application;

	@Override
	public void onCreate() {
		context = this;
		config = FCConfig.getInstance();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		noticeDao = NoticeDao.getInstance(context);
		fCMessageDao = FCMessageDao.getInstance(context);
		application = (FCApplication) getApplication();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		XmppConnectionManager.getConnection();
		addChatListener();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		chatManager.removeChatListener(chatListener);
		super.onDestroy();
	}

	/**
	 * 添加会话监听器
	 */
	public void addChatListener() {
		chatManager = FCChatManager.getChatManager();
		chatManager.addChatListener(chatListener);
	}

	ChatManagerListener chatListener = new ChatManagerListener() {

		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			// 添加消息监听器
			chat.addMessageListener(new MessageListener() {

				@Override
				public void processMessage(Chat chat, Message message) {
					// 处理单聊消息
					if (message.getType() == Message.Type.chat) {
						FCMessage fCMessage = new FCMessage();
						Notice notice = new Notice();
						String whose = config.getUsername();

						String from = message.getFrom();// 发送方jid
						String username = NameUtils.getUsername(from);// 发送方用户名
						String nickname = NameUtils.getNickname(username);// 发送方昵称

						String content = message.getBody();// 消息内容
//						Log.e("content", content);
						fCMessage.setWith(username);
						fCMessage.setContent(content);
						fCMessage.setCondition(FCMessage.SUCCESS);
						fCMessage.setType(FCMessage.RECEIVE);
						fCMessage.setStatus(FCMessage.UNREAD);
						fCMessage.setTime(DateUtils.getCurDateStr());
						// 保存聊天记录
						fCMessageDao.saveFCMessage(fCMessage, whose);

						notice.setType(Notice.CHAT_MSG);
						notice.setFrom(username);
						notice.setContent(content);
						notice.setTo(config.getUsername());
						notice.setStatus(Notice.UNREAD);
						notice.setTime(DateUtils.getCurDateStr());
						notice.setTitle(nickname);
						// Log.e("save", "save");

						// 保存消息通知到数据库中
						if (noticeDao.isExistFrom(username, whose)) {
							noticeDao.delNoticeByFrom(username, whose);
							// Log.e("del", "del");
						}
						noticeDao.saveNotice(notice, whose);
						// 发送聊天消息广播
						Intent intent = new Intent();
						intent.setAction(Constant.ACTION_CHAT_MESSAGE);
						intent.putExtra("notice", notice);
						intent.putExtra("message", fCMessage);
						sendBroadcast(intent);
						
						// 在系统通知栏显示通知
						if (NotificationUtils.isAppToBackground(context)) {
							NotificationUtils
									.sendNotification(context,
											MainActivity.class, nickname,
											content, null);
						}
					}
				}
			});
		}

	};

}
