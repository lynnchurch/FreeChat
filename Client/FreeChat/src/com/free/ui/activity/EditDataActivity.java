package com.free.ui.activity;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.free.chat.core.XmppConnectionManager;
import com.free.ui.R;

public class EditDataActivity extends BaseActivity {
	private EditText nicknameET;
	private EditText signatureET;
	private TextView titleTV;
	private String nickname;
	private String signature;
	private XMPPConnection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_edit_data);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_in);
		titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("编辑资料");

		nicknameET = (EditText) findViewById(R.id.et_nickname);
		signatureET = (EditText) findViewById(R.id.et_signature);
	}

	public void onFinish(View view) {
		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nicknameET.getWindowToken(), 0);

		nickname = nicknameET.getText().toString();
		signature = signatureET.getText().toString();
		connection = XmppConnectionManager.getConnection();
		VCard me = new VCard();
		try {
			me.setNickName(nickname);
			me.setMiddleName(signature);
			me.save(connection);
		} catch (XMPPException e) {
			Log.e("exception", e.getMessage());
		}
		finish();
	}

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = getIntent();
		nickname = intent.getStringExtra("nickname");
		nicknameET.setText(nickname);
		signature = intent.getStringExtra("signature");
		signatureET.setText(signature);
	}

	public void back(View view) {
		finish();
	}
}
