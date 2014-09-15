package com.free.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.free.chat.core.XmppConnectionManager;
import com.free.ui.R;

public class FindPasswordActivity extends BaseActivity {
	private TextView titleTV;
	private EditText emailAddressEditText;
	private String emailAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_find_password);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_out);
		titleTV=(TextView)findViewById(R.id.title);
		titleTV.setText("找回密码");
		emailAddressEditText = (EditText) findViewById(R.id.email_address);
	}

	public void back(View view) {
		new Thread(new Runnable() {
			public void run() {
				XmppConnectionManager.disConnect();
			}
		}).start();
		finish();
	}

	public void next(View view) {
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
		Intent intent = new Intent(FindPasswordActivity.this,
				ValidateCodeActivity.class);
		intent.putExtra("emailAddress", emailAddress);
		intent.putExtra("target", "find_password");
		startActivity(intent);
	}

}
