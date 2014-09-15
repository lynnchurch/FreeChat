package com.free.util;

import java.util.List;
import java.util.Map;

import com.free.ui.R;
import com.free.ui.activity.MainActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

public class NotificationUtils {
	/**
	 * 发送Notification
	 * 
	 * @param context
	 * @param activity
	 * @param title
	 * @param content
	 * @param data
	 */
	public static void sendNotification(Context context, Class activity,
			String title, String content, Map<String, String> data) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(context, activity);
		if (data != null) {
			for (Map.Entry<String, String> entry : data.entrySet()) {
				intent.putExtra(entry.getKey(), entry.getValue());
			}
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent appIntent = PendingIntent.getActivity(context, 0, intent,
				0);

		notificationManager.cancel(0);
		Notification noti = new Notification();
		noti.icon = R.drawable.noti_freechat;
		noti.tickerText = title;

		noti.ledARGB = Color.GREEN;
		noti.ledOffMS = 1500;
		noti.ledOnMS = 1500;
		noti.flags = Notification.FLAG_SHOW_LIGHTS;

		noti.defaults = Notification.DEFAULT_SOUND;
		noti.setLatestEventInfo(context, title, content, appIntent);

		notificationManager.notify(0, noti);
	}

	/**
	 * 判断当前应用程序处于前台还是后台
	 */
	public static boolean isAppToBackground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
