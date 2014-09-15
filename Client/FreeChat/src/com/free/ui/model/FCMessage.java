package com.free.ui.model;

import java.util.Date;

import com.free.ui.Constant;
import com.free.util.DateUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class FCMessage implements Parcelable, Comparable<FCMessage> {
	public static final String KEY_TIME="time";
	public static final int SEND = 1;
	public static final int RECEIVE = 0;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;
	public static final int READ = 1;
	public static final int UNREAD = 0;

	private String id; // 主键
	private String content; // 内容
	private String time; // 时间
	private String with; // 聊天对象
	private int type; // 发送或接收
	private int condition; // 成功或失败
	private int status; // 已读或未读

	public FCMessage() {

	}

	public FCMessage(String content, String time, String with, int type,
			int condition, int status) {
		this.content = content;
		this.time = time;
		this.with = with;
		this.type = type;
		this.condition = condition;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWith() {
		return with;
	}

	public void setWith(String with) {
		this.with = with;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(with);
		dest.writeInt(type);
		dest.writeInt(condition);
		dest.writeInt(status);
	}

	public static final Parcelable.Creator<FCMessage> CREATOR = new Parcelable.Creator<FCMessage>() {

		@Override
		public FCMessage createFromParcel(Parcel source) {
			FCMessage message = new FCMessage();
			message.setContent(source.readString());
			message.setTime(source.readString());
			message.setWith(source.readString());
			message.setType(source.readInt());
			message.setCondition(source.readInt());
			message.setStatus(source.readInt());
			return message;
		}

		@Override
		public FCMessage[] newArray(int size) {
			return new FCMessage[size];
		}

	};

	@Override
	public int compareTo(FCMessage oth) {
		if (null == this.getTime() || null == oth.getTime()) {
			return 0;
		}
		String format = null;
		String time1 = "";
		String time2 = "";
		if (this.getTime().length() == oth.getTime().length()
				&& this.getTime().length() == 23) {
			time1 = this.getTime();
			time2 = oth.getTime();
			format = Constant.MS_FORMART;
		} else {
			time1 = this.getTime().substring(0, 19);
			time2 = oth.getTime().substring(0, 19);
		}
		Date da1 = DateUtils.str2Date(time1, format);
		Date da2 = DateUtils.str2Date(time2, format);
		if (da1.before(da2)) {
			return -1;
		}
		if (da2.before(da1)) {
			return 1;
		}

		return 0;
	}
}
