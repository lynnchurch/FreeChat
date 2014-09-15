package com.free.ui.task;

import org.jivesoftware.smack.packet.Presence;

import com.free.chat.core.FCAccountManager;
import com.free.chat.core.FCOfflineMsgManager;
import com.free.ui.FCApplication;
import com.free.ui.FCConfig;
import com.free.ui.activity.BaseActivity;
import com.free.ui.activity.MainActivity;
import com.free.ui.view.CustomProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private static final int SUCCESS = 1;
	private static final int ERROR = 2;
	private static final int EXCEPTION = 3;

	private Context context;
	private BaseActivity baseActivity;
	private FCApplication application;
	private FCConfig config;

	private CustomProgressDialog cpd;
	private String username;
	private String pwd;

	public LoginTask(BaseActivity baseActivity, String username, String pwd) {
		this.baseActivity = baseActivity;
		config = baseActivity.getConfig();
		application = baseActivity.getFCApplication();
		context = baseActivity.getContext();
		cpd = CustomProgressDialog.createDialog(context);
		this.username = username;
		this.pwd = pwd;
	}

	@Override
	protected void onPreExecute() {
		cpd.setMessage("正在登录中...");
		cpd.show();
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return login();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		cpd.dismiss();
		switch (result) {
		case SUCCESS:
			// 开启各项服务
			baseActivity.startService();
			Intent intent = new Intent(context, MainActivity.class);
			baseActivity.startActivity(intent);
			break;
		case ERROR:
			baseActivity.showToast("用户名或密码错误！");
			break;
		case EXCEPTION:
			baseActivity.showToast("未知异常！");
			break;
		}
		super.onPostExecute(result);
	}

	private Integer login() {
		try {
			FCAccountManager.login(username, pwd);
			// 保存用户名
			config.setUsername(username);
			application.saveConfig(config);
			// 获取离线消息
			FCOfflineMsgManager.dealOfflineMsg(context);
			return SUCCESS;

		} catch (final Exception e) {

			if (e != null && e.getMessage() != null) {
				return ERROR;
			} else {
				return EXCEPTION;
			}

		}
	}
}
