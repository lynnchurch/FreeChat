package com.free.ui.activity;

import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.free.chat.core.FCContactsManager;
import com.free.ui.R;
import com.free.ui.db.UserDao;
import com.free.ui.model.User;

public class FriendActivity extends BaseActivity {
	private TextView titleTV;
	private TextView nicknameTV, usernameTV, signatureTV;
	private User user;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra("userInfo");
		usernameTV.setText(user.getUsername());
		nicknameTV.setText(user.getNickname());
		signatureTV.setText(user.getSignature());
	}

	@Override
	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_friend);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_in);
		titleTV=(TextView)findViewById(R.id.title);
		titleTV.setText("用户资料");
		nicknameTV = (TextView) findViewById(R.id.nickname_value);
		usernameTV = (TextView) findViewById(R.id.username_value);
		signatureTV = (TextView) findViewById(R.id.signature_value);
	}

	public void back(View view) {
		finish();
	}

	public void addFriend(View view) {
		if (!hasInternetConnected()) {
			showToast("当前网络不可用，请检查网络设置！");
			return;
		}
		 username = user.getUsername();
		//Log.e("username",username);
		if (username.equals(config.getUsername())) {
			showToast("不能添加自己！");
			return;
		}

		List<User> users=UserDao.getInstance(context).getContacts(config.getUsername());
		for(User user:users){
			if(user.getUsername().equals(username)){
				showToast("该用户已是你的好友！");
				return;
			}
		}

		new Thread(new Runnable() {
			public void run() {
				FCContactsManager.addContact(username, user.getNickname(), "我的好友");
				runOnUiThread(new Runnable() {
					public void run() {
						showToast("已成功发送好友添加请求！");
						finish();
					}
				});
			}
		}).start();
	}
}
