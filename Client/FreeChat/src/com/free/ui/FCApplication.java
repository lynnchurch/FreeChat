package com.free.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.free.chat.core.FCContactsManager;
import com.free.exception.FCException;
import com.free.ui.adapter.FCMessageAdapter;
import com.free.ui.db.UserDao;
import com.free.ui.model.FCMessage;
import com.free.ui.model.Notice;
import com.free.ui.model.User;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.widget.BaseAdapter;

public class FCApplication extends Application {
	public SharedPreferences preferences;
	private List<Activity> activityList = new LinkedList<Activity>();
	public boolean ifShowNotification;
    
	public void onCreate() {
		super.onCreate();
		preferences = getSharedPreferences(Constant.CONFIG,
				Context.MODE_PRIVATE);
		setConfig();
		FCConfig config = FCConfig.getInstance();
	    // 将配置信息保存到SharedPreferences
		saveConfig(config);
	}

	/**
	 * 把配置信息加载到FCConfig对象中（从strings.xml或SharedPreferences获取配置信息）
	 */
	public void setConfig() {
		FCConfig config = FCConfig.getInstance();
		Resources resources = getResources();
		config.setUsername(preferences.getString(Constant.USERNAME, null));
		config.setPassword(preferences.getString(Constant.PASSWORD, null));
		config.setAutoLogin(preferences.getBoolean(Constant.IS_AUTOLOGIN,
				resources.getBoolean(R.bool.is_autologin)));
		config.setNovisible(preferences.getBoolean(Constant.IS_NOVISIBLE,
				resources.getBoolean(R.bool.is_novisible)));
		config.setRemember(preferences.getBoolean(Constant.IS_REMEMBER,
				resources.getBoolean(R.bool.is_remember)));
		config.setFirstStart(preferences.getBoolean(Constant.IS_FIRSTSTART,
				true));
	}

	/**
	 * 保存配置信息到SharedPreferences中
	 * 
	 * @param config
	 */
	public void saveConfig(FCConfig config) {
		Editor editor = preferences.edit();
		editor.putString(Constant.XMPP_HOST, FCConfig.XMPP_HOST).commit();
		editor.putInt(Constant.XMPP_PORT, FCConfig.XMPP_PORT).commit();
		editor.putString(Constant.XMPP_SEIVICE_NAME, FCConfig.XMPP_SERVICE_NAME)
				.commit();
		editor.putString(Constant.USERNAME, config.getUsername()).commit();
		editor.putString(Constant.PASSWORD, config.getPassword()).commit();
		editor.putBoolean(Constant.IS_AUTOLOGIN, config.isAutoLogin()).commit();
		editor.putBoolean(Constant.IS_NOVISIBLE, config.isNovisible()).commit();
		editor.putBoolean(Constant.IS_REMEMBER, config.isRemember()).commit();
		editor.putBoolean(Constant.IS_ONLINE, config.isOnline()).commit();
		editor.putBoolean(Constant.IS_FIRSTSTART, config.isFirstStart())
				.commit();

	}

	
	/**
	 * 添加Activity到容器中
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
//		Log.i("activityList", activity.toString());
//		Log.i("size", "" + activityList.size());
	}

	/**
	 * 遍历所有Activity并finish
	 */
	public void exit() {
		
		for (Activity activity : activityList) {
			activity.finish();
		}
	}
}