package com.free.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.free.ui.R;
import com.free.util.FCCountDownTimer;
import com.free.util.FCValidateCodeEmail;

public class ValidateCodeActivity extends BaseActivity {
	private TextView emailAddressTextView;
	private Button resendMailButton;
	private EditText validateCodeEditText;
	private String validateCode;
	private TextView titleTV;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			validateCode = msg.getData().getString("validateCode")
					.toUpperCase();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		sendEmail(getIntent().getStringExtra("emailAddress"));
	}

	@Override
	public void initView() {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_validate_code);
		// 加载自定义标题栏
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_out);
		titleTV=(TextView)findViewById(R.id.title);
		titleTV.setText("填写验证码");
		emailAddressTextView = (TextView) findViewById(R.id.email_address_hint);
		emailAddressTextView
				.setText(getIntent().getStringExtra("emailAddress"));
		resendMailButton = (Button) findViewById(R.id.resend_mail);
		validateCodeEditText = (EditText) findViewById(R.id.validate_code);
	}

	public void next(View view) {
		if (!hasInternetConnected()) {
			showToast("当前网络不可用，请检查网络设置！");
			return;
		}
		String validateCode = validateCodeEditText.getText().toString().trim()
				.toUpperCase();
		if (TextUtils.isEmpty(validateCode)) {
			showToast("请输入验证码！");
			return;
		}
		if (!validateCode.equals(this.validateCode)) {
			showToast("验证码错误！");
			return;
		}
		Intent intent=null;
		if(getIntent().getStringExtra("target").equals("register")){
		intent = new Intent(ValidateCodeActivity.this,
				PasswordActivity.class);
		}else{
			intent = new Intent(ValidateCodeActivity.this,
					NewPasswordActivity.class);
		}
		intent.putExtra("emailAddress",
				getIntent().getStringExtra("emailAddress"));
		startActivity(intent);
	}

	public void resend(View view) {
		sendEmail(getIntent().getStringExtra("emailAddress"));
	}

	/**
	 * 
	 * 发送验证码邮件
	 * 
	 * @param emailAddress
	 * @return
	 */
	public void sendEmail(final String emailAddress) {
		new FCCountDownTimer(60000, 1000, resendMailButton).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					String validateCode = FCValidateCodeEmail.getInstance()
							.sendEmail(emailAddress);
					Message msg = handler.obtainMessage();
					Bundle bundle = new Bundle();
					bundle.putString("validateCode", validateCode);
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (e != null && e.getMessage() != null) {
								String errorMsg = e.getMessage();
								if (errorMsg.indexOf("网络异常，请检查网络") != -1) {
									showToast("网络异常，请检查网络重试");
								} else {
									showToast("未知异常!");
								}
							} else {
								showToast("出现未知异常!");
							}
						}
					});
				}
			}
		}).start();

	}

	/**
	 * 捕获返回键
	 */
	@Override
	public void onBackPressed(){
		showDialog();
	}

	public void back(View view) {
		showDialog();
	}

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setMessage("验证码邮件可能略有延迟，确定返回并重新开始？")// 设置对话框内容
				.setPositiveButton("确定", new OnClickListener() {// 设置对话框[肯定]按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();// 关闭对话框
								finish();// 结束当前Activity
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
