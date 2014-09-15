package com.free.ui.activity;

import com.free.ui.Constant;
import com.free.ui.R;
import com.free.ui.task.LoginTask;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private TextView userRegisterTextView;
	private boolean isExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	public void initView() {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		String username = preferences.getString(Constant.USERNAME, "");
		usernameEditText.setText(username);
		if (!TextUtils.isEmpty(username)) {
			passwordEditText.requestFocus();
		}
		// 自定义超链接
		userRegisterTextView = (TextView) findViewById(R.id.tv_userregister);
		String userRegisterStr = "用户注册";
		SpannableString usrrgtSpanStr = new SpannableString(userRegisterStr);
		usrrgtSpanStr.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setColor(ds.linkColor);
				ds.setUnderlineText(false); // 去掉下划线
			}

			@Override
			public void onClick(View widget) {
				if (!hasInternetConnected()) {
					showToast("当前网络不可用，请检查网络设置！");
					return;
				}
				// 启动RegisterActivity进行注册
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);

			}
		}, 0, userRegisterStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		userRegisterTextView.setText(usrrgtSpanStr);
		userRegisterTextView
				.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void findPassword(View view){
		Intent intent=new Intent(this,FindPasswordActivity.class);
		startActivity(intent);
	}
	
	/*
	 * 用户登录
	 */
	public void login(View view) {
		if (!hasInternetConnected()) {
			showToast("当前网络不可用，请检查网络设置！");
			return;
		}
		final String user = usernameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString();

		if (TextUtils.isEmpty(user)) {
			showToast("请输入用户名！");
			return;
		}
		if (TextUtils.isEmpty(pwd)) {
			showToast("请输入密码！");
			return;
		}
		// 执行异步登录任务
		LoginTask loginTask = new LoginTask(this, user, pwd);
		loginTask.execute();

	}


	/**
	 * 捕获返回键
	 */
	@Override
	public void onBackPressed() {
		application.exit();
		isExit = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (config.isFirstStart()) {
			config.setFirstStart(false);
		}
		if (isExit) {
			System.exit(0);
		}

	}
}
