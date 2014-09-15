package com.free.ui.model;

import java.io.Serializable;

public class NewFriend implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int UNDO = 0;// 未处理
	public static final int AGREE = 1;// 同意
	public static final int REJECT = 2;// 拒绝
	public static final int AGREED = 3;// 被同意
	public static final int REJECTED = 4;// 被拒绝
	public static final int UNREAD = 0;// 未读
	public static final int READ = 1;// 已读

	private String id; // 主键
	private String username;// 用户名
	private String nickname;// 昵称
	private String content;// 验证内容
	private int status; // 读取状态
	private int condition;// 处理情况
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

}
