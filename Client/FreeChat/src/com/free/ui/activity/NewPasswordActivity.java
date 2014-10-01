package com.free.ui.activity;

import org.jivesoftware.smack.util.StringUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.free.exception.FCException;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.view.CustomProgressDialog;
import com.free.util.FCHttpClient;

public class NewPasswordActivity extends BaseActivity {
	private EditText pwdEditText;
	private EditText confirmPwdEditText;
	private TextView titleTV;
	String user;
	String pwd;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_new_password);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_out);
		titleTV = (TextView) findViewById(R.id.title);
		titleTV.setText("设置新密码");
		pwdEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
	}

	public boolean matches(String str) {
		if (str.matches(".{6,16}") && str.matches("\\S*")
				&& str.matches("(.*\\D.*){1,8}|.{9,}"))
			return true;
		else
			return false;
	}

	public void confirm(View view) {
		if (!hasInternetConnected()) {
			showToast("当前网络不可用，请检查网络设置！");
			return;
		}
		user = getIntent().getStringExtra("emailAddress");
		pwd = pwdEditText.getText().toString().trim();
		String confirmPwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(pwd)) {
			showToast("密码不能为空！");
			pwdEditText.requestFocus();
			return;
		}
		if (!matches(pwd)) {
			showToast("密码不符合要求！");
			confirmPwdEditText.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(confirmPwd)) {
			showToast("确认密码不能为空！");
			confirmPwdEditText.requestFocus();
			return;
		}
		if (!pwd.equals(confirmPwd)) {
			showToast("两次输入的密码不一致，请重新输入！");
			confirmPwdEditText.requestFocus();
			return;
		}
		if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pwd)) {
			final CustomProgressDialog cpd = CustomProgressDialog
					.createDialog(this);
			cpd.setMessage("正在处理中...");
			cpd.show();

			new Thread(new Runnable() {
				public void run() {
					try {
						// 修改密码
						FCHttpClient.getInstance().doPost(
								FCConfig.USER_SERVICE_URL
										+ "?type=update&secret=4TkN2iia&"
										+ "username=" + user + "&password="
										+ pwd, null);

						runOnUiThread(new Runnable() {
							public void run() {
								if (!NewPasswordActivity.this.isFinishing())
									cpd.dismiss();
								// 保存用户名
								config.setUsername(user);
								application.saveConfig(config);
								showToast("设置成功!");
								Intent intent = new Intent(
										NewPasswordActivity.this,
										LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
							}
						});
					} catch (Exception e) {
						Log.e("e",e.getMessage());
						
						runOnUiThread(new Runnable() {
							public void run() {
								cpd.dismiss();
								showToast("设置失败:未知异常！");

							}
						});
					}
				}
			}).start();

		}
	}

	/**
	 * 捕获返回键
	 */
	@Override
	public void onBackPressed() {
		showDialog();
	}

	public void back(View view) {
		showDialog();
	}

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setMessage("确定放弃设置新密码吗？")// 设置对话框内容
				.setPositiveButton("确定", new OnClickListener() {// 设置对话框[肯定]按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();// 关闭对话框
								Intent intent = new Intent(
										NewPasswordActivity.this,
										LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
						}).setNegativeButton("取消", new OnClickListener() {// 设置对话框[否定]按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();// 关闭对话框
							}
						});
		builder.show();// 创建对话框并且显示该对话框
	}

}
