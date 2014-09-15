package com.free.ui.activity;

import java.util.List;

import org.jivesoftware.smack.packet.Message;

import com.free.chat.core.FCChatManager;
import com.free.exception.FCXMPPException;
import com.free.ui.Constant;
import com.free.ui.R;
import com.free.ui.adapter.FCMessageAdapter;
import com.free.ui.db.FCMessageDao;
import com.free.ui.db.NoticeDao;
import com.free.ui.model.FCMessage;
import com.free.ui.model.Notice;
import com.free.util.DateUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends BaseActivity {
	private ListView listView;
	private List<FCMessage> messages;
	private TextView nicknameTV;
	private EditText chatEditET;
	private String username;
	private String nickname;
	private FCMessageAdapter adapter;
	private FCMessageReceiver receiver;
	private String whose;
	private NoticeDao noticeDao;
	private FCMessageDao fCMessageDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nickname = getIntent().getStringExtra("nickname");
		username = getIntent().getStringExtra("username");
		whose = config.getUsername();
		noticeDao = NoticeDao.getInstance(context);
		fCMessageDao=FCMessageDao.getInstance(context);
		receiver = new FCMessageReceiver();
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		// 好友订阅
		filter.addAction(Constant.ACTION_CHAT_MESSAGE);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onStop() {
		super.onStop();
//		unregisterReceiver(receiver);
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_chat);
		listView = (ListView) findViewById(R.id.listview);
		nicknameTV = (TextView) findViewById(R.id.nickname);
		chatEditET = (EditText) findViewById(R.id.chat_edit);

		nicknameTV.setText(nickname);
		messages = FCMessageDao.getInstance(context).getFCMessages(username,
				whose);
		adapter = new FCMessageAdapter(this, messages, nickname);
		listView.setAdapter(adapter);
	}

	public void sendMessage(View view) {
		String content = chatEditET.getText().toString();
		String time=DateUtils.getCurDateStr();
		FCMessage message = new FCMessage(content,time ,
				username, FCMessage.SEND, FCMessage.SUCCESS, FCMessage.READ);
		Message _message = new Message();
		_message.setProperty(FCMessage.KEY_TIME, time);
		_message.setBody(content);
		try {
			FCChatManager.sendMessage(username, _message);
		} catch (FCXMPPException e) {
			message.setCondition(FCMessage.FAILURE);
		}
		// 保存聊天记录
		FCMessageDao.getInstance(context).saveFCMessage(message, whose);
		messages.add(message);
		adapter.notifyDataSetChanged();
		chatEditET.setText("");

		// 保存消息通知
		Notice notice = new Notice();
		notice.setType(Notice.CHAT_MSG);
		notice.setContent(content);
		notice.setFrom(username);
		notice.setTo(whose);
		notice.setStatus(Notice.READ);
		notice.setTime(DateUtils.getCurDateStr());
		notice.setTitle(nickname);
		
		// 保存消息通知到数据库中
		if (noticeDao.isExistFrom(username, whose)) {
			noticeDao.delNoticeByFrom(username, whose);
		}
		noticeDao.saveNotice(notice, whose);
	}

	private class FCMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			FCMessage message = (FCMessage) intent.getParcelableExtra("message");
			String action = intent.getAction();

			// 更新聊天界面消息
			if (action.endsWith(Constant.ACTION_CHAT_MESSAGE)) {
				fCMessageDao.setRead(username,whose);
				messages.add(message);
				adapter.notifyDataSetChanged();
				
			}

		}
	}

	public void back(View view) {
		finish();
	}
}
