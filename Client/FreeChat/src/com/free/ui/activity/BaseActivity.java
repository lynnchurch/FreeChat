package com.free.ui.activity;

import com.free.ui.Constant;
import com.free.ui.FCApplication;
import com.free.ui.FCConfig;
import com.free.ui.R;
import com.free.ui.service.ChatService;
import com.free.ui.service.ContactsService;
import com.free.ui.view.BadgeView;
import com.free.ui.view.CustomToast;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseActivity extends Activity implements IBaseActivity {
	protected Context context = null;
	protected SharedPreferences preferences;
	protected FCApplication application;
	protected FCConfig config;
	protected NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		config = FCConfig.getInstance();
		preferences = getSharedPreferences(Constant.CONFIG,
				Context.MODE_PRIVATE);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		application = (FCApplication) getApplication();
		application.addActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 取消notification显示
		application.ifShowNotification=false;
		notificationManager.cancel(0);
	}

	public void showToast(String message) {
		CustomToast.showShortToast(context, message);
	}

	@Override
	public FCApplication getFCApplication() {
		return application;
	}

	public FCConfig getConfig() {
		return config;
	}

	public void setConfig(FCConfig config) {
		this.config = config;
	}

	@Override
	public void startService() {
		// 好友联系人服务
		Intent contactsServer = new Intent(context, ContactsService.class);
		context.startService(contactsServer);
		// 聊天服务
		Intent chatServer = new Intent(context, ChatService.class);
		context.startService(chatServer);

	}

	@Override
	public void stopService() {
		// 好友联系人服务
		Intent server = new Intent(context, ContactsService.class);
		context.stopService(server);
		// 聊天服务
		Intent chatServer = new Intent(context, ChatService.class);
		context.stopService(chatServer);

	}

	@Override
	public boolean hasValidateInternet() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			openWirelessSet();
			return false;
		} else {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		openWirelessSet();
		return false;
	}

	public void openWirelessSet() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder
				.setMessage("现在设置无线网络吗？")
				.setPositiveButton("是的", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(
								Settings.ACTION_WIRELESS_SETTINGS);
						context.startActivity(intent);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		dialogBuilder.show();
	}

	@Override
	public boolean hasInternetConnected() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo network = manager.getActiveNetworkInfo();
			if (network != null && network.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void exit() {
		new AlertDialog.Builder(context).setTitle("确定退出吗?")
				.setNeutralButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						stopService();
						application.exit();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						;
					}
				}).show();
	}

	@Override
	public boolean hasLocationGPS() {
		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (manager != null
				&& manager
						.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasLocationNetWork() {
		LocationManager manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (manager != null
				&& manager
						.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void checkSDMemoryCard() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			new AlertDialog.Builder(context)
					.setMessage("请检查SD卡")
					.setPositiveButton("检查",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Intent intent = new Intent(
											Settings.ACTION_SETTINGS);
									context.startActivity(intent);
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									application.exit();
								}
							}).show();
		}
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public SharedPreferences getSharedPreConfig() {
		return preferences;

	}

	@Override
	public boolean getUserOnlineState() {
		return preferences.getBoolean(Constant.IS_ONLINE, true);
	}

	@Override
	public void setUserOnlineState(boolean isOnline) {
		preferences.edit().putBoolean(Constant.IS_ONLINE, isOnline).commit();

	}

	/**
	 * 关闭键盘
	 */
	public void closeInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && this.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
