package com.free.ui;

public class FCConfig {
	// 邮箱验证码服务地址
	public static final String EMAIL_URL = "http://chat.lynnchurch.net:9090/plugins/validateCode/vcservlet";
	public static final String USER_SERVICE_URL ="http://chat.lynnchurch.net:9090/plugins/userService/userservice";
	public static final String XMPP_HOST = "121.40.99.230";// 地址
	public static final Integer XMPP_PORT = 5222;// 端口
	public static final String XMPP_SERVICE_NAME = "iz23ds9vkjvz";// 服务器名称
	private static FCConfig instance = null;
	private String username;// 用户名
	private String nickname;// 昵称
	private String password;// 密码
	private String sessionId;// 会话id
	private boolean isRemember;// 是否记住密码
	private boolean isAutoLogin;// 是否自动登录
	private boolean isNovisible;// 是否隐藏登录
	private boolean isOnline;// 用户连接成功
	private boolean isFirstStart;// 是否首次启动

	private FCConfig() {

	}

	public static FCConfig getInstance() {
		if (instance == null) {
			instance = new FCConfig();
		}
		return instance;
	}
	
	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isRemember() {
		return isRemember;
	}

	public void setRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}

	public boolean isAutoLogin() {
		return isAutoLogin;
	}

	public void setAutoLogin(boolean isAutoLogin) {
		this.isAutoLogin = isAutoLogin;
	}

	public boolean isNovisible() {
		return isNovisible;
	}

	public void setNovisible(boolean isNovisible) {
		this.isNovisible = isNovisible;
	}

	public boolean isFirstStart() {
		return isFirstStart;
	}

	public void setFirstStart(boolean isFirstStart) {
		this.isFirstStart = isFirstStart;
	}

}
