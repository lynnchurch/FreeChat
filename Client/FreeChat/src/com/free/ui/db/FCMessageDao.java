package com.free.ui.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.free.ui.model.FCMessage;

public class FCMessageDao {

	public static final String TABLE_NAME = "messages";
	public static final String ID = "_id";
	public static final String CONTENT = "content";
	public static final String TIME = "time";
	public static final String WITH = "with";
	public static final String TYPE = "type";
	public static final String CONDITION = "condition";
	public static final String STATUS = "status";
	public static final String WHOSE = "whose";

	private MySQLiteOpenHelper dbHelper;
	private static FCMessageDao instance;

	private FCMessageDao(Context context) {
		dbHelper = MySQLiteOpenHelper.getInstance(context);
	}

	public static FCMessageDao getInstance(Context context) {
		if (instance == null) {
			instance = new FCMessageDao(context);
		}
		return instance;
	}

	/**
	 * 保存消息
	 * 
	 * @param fCMessage
	 * @param whose
	 */
	public void saveFCMessage(FCMessage fCMessage, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {

			ContentValues values = new ContentValues();
			values.put(CONTENT, fCMessage.getContent());
			values.put(TIME, fCMessage.getTime());
			values.put(WITH, fCMessage.getWith());
			values.put(TYPE, fCMessage.getType());
			values.put(CONDITION, fCMessage.getCondition());
			values.put(STATUS, fCMessage.getStatus());
			values.put(WHOSE, whose);
			db.insert(TABLE_NAME, null, values);
		}
	}

	/**
	 * 获取消息记录
	 * 
	 * @param with
	 * @param whose
	 * @return
	 */
	public List<FCMessage> getFCMessages(String with, String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<FCMessage> FCMessages = new ArrayList<FCMessage>();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery("select * from " + TABLE_NAME
							+ " where with=? and whose=?", new String[] { with,
							whose });
			while (cursor.moveToNext()) {
				FCMessage FCMessage = new FCMessage();
				String id = cursor.getString(cursor.getColumnIndex(ID));
				FCMessage.setId(id);
				String content = cursor.getString(cursor
						.getColumnIndex(CONTENT));
				FCMessage.setContent(content);
				String time = cursor.getString(cursor.getColumnIndex(TIME));
				FCMessage.setTime(time);
				FCMessage.setWith(with);
				int type = cursor.getInt(cursor.getColumnIndex(TYPE));
				FCMessage.setType(type);
				int condition = cursor.getInt(cursor.getColumnIndex(CONDITION));
				FCMessage.setCondition(condition);
				int status = cursor.getInt(cursor.getColumnIndex(STATUS));
				FCMessage.setStatus(status);
				FCMessages.add(FCMessage);
			}
			cursor.close();
		}
		return FCMessages;
	}

	/**
	 * 删除一条消息
	 * 
	 * @param username
	 */
	public void deleteFCMessage(String id, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, ID + "= ? and " + WHOSE + "=?", new String[] {
					id, whose });
			db.close();
		}
	}

	/**
	 * 设置消息读取状态为已读
	 * 
	 * @param with
	 * @param whose
	 */
	public void setRead(String with, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(STATUS, FCMessage.READ);
			db.update(TABLE_NAME, values, WITH + "=? and " + STATUS + "=? and "
					+ WHOSE + "=?", new String[] { with, "" + FCMessage.UNREAD,
					whose });
			db.close();
		}
	}

	/**
	 * 某好友未读消息条数.
	 * 
	 * @param with
	 * @param whose
	 * @return
	 */
	public int getUnReadCountByWith(String with, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		if (db.isOpen()) {

			Cursor cursor = db
					.rawQuery("select " + ID + " from " + TABLE_NAME
							+ " where " + WITH + "=? and " + STATUS + "=? and "
							+ WHOSE + "=?", new String[] { with,
							"" + FCMessage.UNREAD, whose });
			while (cursor.moveToNext()) {
				count++;
			}
			db.close();
		}
		return count;
	}

	/**
	 * 未读消息总数.
	 * 
	 * @param whose
	 * @return
	 */
	public int getUnReadCount(String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		if (db.isOpen()) {

			Cursor cursor = db.rawQuery("select " + ID + " from " + TABLE_NAME
					+ " where " + STATUS + "=? and " + WHOSE + "=?",
					new String[] { "" + FCMessage.UNREAD, whose });
			while (cursor.moveToNext()) {
				count++;
			}
			db.close();
		}
		return count;
	}
}
