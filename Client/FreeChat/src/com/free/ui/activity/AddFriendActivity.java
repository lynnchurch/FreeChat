package com.free.ui.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.free.chat.core.FCContactsManager;
import com.free.ui.R;
import com.free.ui.model.User;

public class AddFriendActivity extends BaseActivity {
	private EditText emailAddressEditText;
	private String emailAddress;
	private TextView titleTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		emailAddressEditText.setText("");
	}

	@Override
	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_add_friend);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_in);
		titleTV=(TextView)findViewById(R.id.title);
		titleTV.setText("添加好友");
		emailAddressEditText = (EditText) findViewById(R.id.email_address);
		emailAddressEditText.requestFocus();
	}

	public void back(View view) {
		finish();
	}

	public void search(View view) {
		if (!hasInternetConnected()) {
			showToast("当前网络不可用，请检查网络设置！");
			return;
		}
		emailAddress = emailAddressEditText.getText().toString().trim();
		String EMAIL_REGEX = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		if (TextUtils.isEmpty(emailAddress)) {
			showToast("请输入邮箱地址！");
			return;
		}
		if (!emailAddress.matches(EMAIL_REGEX)) {
			showToast("邮箱地址格式有误！");
			return;
		}

		
		new Thread(new Runnable() {
			public void run() {
				try {
					final Map<String, String> result = FCContactsManager
							.search(emailAddress);
					runOnUiThread(new Runnable() {
						public void run() {
							if (result.size() != 0) {
								User user = new User();
								String username = result.get("Username");
//								Log.e("username",username);
								username = username.substring(0,
										username.length());
								user.setUsername(username);
								String nickname = result.get("Name");
//								Log.e("nickname",nickname);
								user.setNickname(nickname);
								
								String signature = result.get("Signature");
//								Log.e("signature",signature);
								user.setSignature(signature);
								Intent intent = new Intent(context,
										FriendActivity.class);
								intent.putExtra("userInfo", user);
								startActivity(intent);
							} else {
								showToast("该用户不存在!");
							}
						}
					});

				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							showToast("查找失败:未知异常！");
						}
					});
				}
			}
		}).start();
	}
}
