package com.free.chat.core;

import java.util.Iterator;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import com.free.ui.FCConfig;
import com.free.ui.db.FCMessageDao;
import com.free.ui.db.NoticeDao;
import com.free.ui.model.FCMessage;
import com.free.ui.model.Notice;
import com.free.util.DateUtils;
import com.free.util.NameUtils;

import android.content.Context;
import android.util.Log;


public class FCOfflineMsgManager {
	
	public static void dealOfflineMsg(Context context) {
	    FCConfig config=FCConfig.getInstance();
		XMPPConnection	connection = XmppConnectionManager.getConnection();
		NoticeDao noticeDao= NoticeDao.getInstance(context);
		FCMessageDao fCMessageDao=FCMessageDao.getInstance(context);
		
		OfflineMessageManager offlineManager = new OfflineMessageManager(
				connection);
		try {
			Iterator<Message> it = offlineManager
					.getMessages();
			while (it.hasNext()) {
				Message message = it.next();
				if (message != null && message.getBody() != null) {
					FCMessage fCMessage = new FCMessage();
					Notice notice = new Notice();
					
					String whose = config.getUsername();
					String time = (String) message
							.getProperty(FCMessage.KEY_TIME);
					if(time==null){
						time=DateUtils.getCurDateStr();
					}
//					Log.e("time",""+time);
					String content=message.getBody();
//					Log.e("offlineContent",content);
					
					String from=message.getFrom();
					String username = NameUtils.getUsername(from);// 发送方用户名
					String nickname = NameUtils.getNickname(username);// 发送方昵称
					
					fCMessage.setWith(username);
					fCMessage.setContent(content);
					fCMessage.setCondition(FCMessage.SUCCESS);
					fCMessage.setType(FCMessage.RECEIVE);
					fCMessage.setStatus(FCMessage.UNREAD);
					fCMessage.setTime(time);
					// 保存聊天记录
					fCMessageDao.saveFCMessage(
							fCMessage, whose);

					notice.setType(Notice.CHAT_MSG);
					notice.setFrom(username);
					notice.setContent(content);
					notice.setTo(config.getUsername());
					notice.setStatus(Notice.UNREAD);
					notice.setTime(DateUtils.getCurDateStr());
					notice.setTitle(nickname);

					// 保存消息通知到数据库中
					if (noticeDao.isExistFrom(username, whose)) {
						noticeDao.delNoticeByFrom(username, whose);
					}
					noticeDao.saveNotice(notice, whose);
				}
			}
			// 删除离线消息
			offlineManager.deleteMessages();
			// 更新状态为在线
			connection.sendPacket(new Presence(Presence.Type.available));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
