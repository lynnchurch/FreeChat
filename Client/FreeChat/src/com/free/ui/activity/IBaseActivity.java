package com.free.ui.activity;

import com.free.ui.FCApplication;

import android.content.Context;
import android.content.SharedPreferences;

public interface IBaseActivity {
	/**
	 * 初始化View
	 */
	public void initView();

	/**
	 * 获取FCApplication
	 */
	public FCApplication getFCApplication();
	
	/**
	 * 显示Toast
	 * @param content
	 */
	public void showToast(String content);

	/**
	 * 停止服务
	 */
	public void stopService();

	/**
	 * 开启服务
	 */
	public void startService();

	/**
	 * 校验网络-如果没有网络就弹出设置
	 */
	public boolean hasValidateInternet();

	/**
	 * 校验网络
	 */
	public boolean hasInternetConnected();

	/**
	 * 退出应用
	 */
	public void exit();

	/**
	 * 判断GPS是否已经开启
	 */
	public boolean hasLocationGPS();

	/**
	 * 判断基站是否已经开启
	 */
	public boolean hasLocationNetWork();

	/**
	 * 检查SD卡
	 */
	public void checkSDMemoryCard();

	/**
	 * 返回当前Activity上下文
	 */
	public Context getContext();

	/**
	 * 获取用户的配置
	 */
	public SharedPreferences getSharedPreConfig();

	/**
	 * 用户是否在线
	 */
	public boolean getUserOnlineState();

	/**
	 * 设置用户在线状态
	 */
	public void setUserOnlineState(boolean isOnline);

}
