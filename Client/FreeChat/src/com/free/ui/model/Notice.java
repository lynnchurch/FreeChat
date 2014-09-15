package com.free.ui.model;

import java.io.Serializable;
import java.util.Date;

import com.free.ui.Constant;
import com.free.util.DateUtils;

public class Notice implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int NEWFRIEND = 1; // 新朋友
	public static final int CHAT_MSG = 2;// 聊天消息
	public static final int READ = 0;// 已读
	public static final int UNREAD = 1;// 未读
	public static final int All = 2;

	private String id; // 主键
	private String title; // 标题
	private String content; // 内容
	private int status; // 状态
	private String from; // 通知来源
	private String to; // 通知去向
	private String time; // 通知时间
	private int type; // 通知类型

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
