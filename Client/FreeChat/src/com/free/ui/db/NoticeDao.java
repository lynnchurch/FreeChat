package com.free.ui.db;

import java.util.ArrayList;
import java.util.List;

import com.free.ui.model.Notice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NoticeDao {
	public static final String TABLE_NAME = "notices";
	public static final String ID = "_id";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String CONTENT = "content";
	public static final String FROM = "_from";
	public static final String TO = "_to";
	public static final String TIME = "time";
	public static final String STATUS = "status";
	public static final String WHOSE = "whose";

	private static NoticeDao noticeDao = null;
	private MySQLiteOpenHelper dbHelper;

	private NoticeDao(Context context) {
		dbHelper = MySQLiteOpenHelper.getInstance(context);
	}

	public static NoticeDao getInstance(Context context) {

		if (noticeDao == null) {
			noticeDao = new NoticeDao(context);
		}

		return noticeDao;
	}

	/**
	 * 保存通知
	 * 
	 * @param notice
	 * @param whose
	 * @return
	 */
	public long saveNotice(Notice notice, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = 0;
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(TYPE, notice.getType());
			values.put(TITLE, notice.getTitle());
			values.put(CONTENT, notice.getContent());
			values.put(FROM, notice.getFrom());
			values.put(TO, notice.getTo());
			values.put(TIME, notice.getTime());
			values.put(STATUS, notice.getStatus());
			values.put(WHOSE, whose);
			id = db.insert(TABLE_NAME, null, values);
		}
		db.close();
		return id;
	}

	/**
	 * 获取所有未读通知
	 * 
	 * @param whose
	 * @return
	 */
	public List<Notice> getUnReadNoticeList(String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Notice> notices = new ArrayList<Notice>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where " + WHOSE + "=?", new String[] { whose });
			while (cursor.moveToNext()) {
				Notice notice = new Notice();
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				notice.setId(id);
				int type = cursor.getInt(cursor.getColumnIndex(TYPE));
				notice.setType(type);
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				notice.setTitle(title);
				String content = cursor.getString(cursor
						.getColumnIndex(CONTENT));
				notice.setContent(content);
				String from = cursor.getString(cursor.getColumnIndex(FROM));
				notice.setFrom(from);
				String to = cursor.getString(cursor.getColumnIndex(TO));
				notice.setTo(to);
				String time = cursor.getString(cursor.getColumnIndex(TIME));
				notice.setTime(time);
				int status = cursor.getInt(cursor.getColumnIndex(STATUS));
				notice.setStatus(status);

				notices.add(notice);
			}
			cursor.close();
			db.close();
		}
		return notices;
	}

	/**
	 * 根据id更新消息状态为已读
	 * 
	 * @param id
	 * @param whose
	 */
	public void setRead(String id, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(STATUS, Notice.READ);
			db.update(TABLE_NAME, values, STATUS + "=? and " + ID + "=? and "
					+ WHOSE + "=?", new String[] { "" + Notice.UNREAD, id,
					whose });
			db.close();
		}
	}

	/**
	 * 根据标题获取通知未读条数.
	 * 
	 * @param title
	 * @param whose
	 * @return
	 */
	public int getUnReadNoticeCount(String title, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select " + ID + " from " + TABLE_NAME
					+ " where " + TITLE + "=? and " + STATUS + "=? and "
					+ WHOSE + "=?", new String[] { title, "" + Notice.UNREAD,
					whose });
			while (cursor.moveToNext()) {
				count++;
			}
			db.close();
		}
		return count;
	}

	/**
	 * 判断是否存在同一标题的通知
	 * 
	 * @param type
	 * @param whose
	 * @return
	 */
	public boolean isExistType(String type, String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where " + TYPE + "=? and " + WHOSE + "=?",
					new String[] { type, whose });
			if (cursor.moveToNext()) {
				cursor.close();
				db.close();
				return true;
			}

			cursor.close();
			db.close();
			return false;
		}
		return false;
	}

	/**
	 * 判断是否存在同一发送者的通知
	 * 
	 * @param from
	 * @param whose
	 * @return
	 */
	public boolean isExistFrom(String from, String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where " + FROM + "=? and " + WHOSE + "=?",
					new String[] { from, whose });
			if (cursor.moveToNext()) {
				cursor.close();
				db.close();
				return true;
			}

			cursor.close();
			db.close();
			return false;
		}
		return false;
	}

	/**
	 * 获取通知
	 * 
	 * @param from
	 * @param whose
	 * @return
	 */
	public List<Notice> getNotices(String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Notice> notices = new ArrayList<Notice>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where " + WHOSE + "=?", new String[] { whose });
			while (cursor.moveToNext()) {
				Notice notice = new Notice();
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				notice.setId(id);
				int type = cursor.getInt(cursor.getColumnIndex(TYPE));
				notice.setType(type);
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				notice.setTitle(title);
				String content = cursor.getString(cursor
						.getColumnIndex(CONTENT));
				notice.setContent(content);
				String from = cursor.getString(cursor.getColumnIndex(FROM));
				notice.setFrom(from);
				String to = cursor.getString(cursor.getColumnIndex(TO));
				notice.setTo(to);
				String time = cursor.getString(cursor.getColumnIndex(TIME));
				notice.setTime(time);
				int status = cursor.getInt(cursor.getColumnIndex(STATUS));
				notice.setStatus(status);

				notices.add(notice);
			}
			cursor.close();
			db.close();
		}
		return notices;
	}

	/**
	 * 根据类型删除通知记录
	 * 
	 * @param type
	 * @param whose
	 */
	public void delNoticeByType(String type, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, TYPE + "=? and " + WHOSE + "=?",
					new String[] { type, whose });
			db.close();
		}
	}

	/**
	 * 根据发送者删除通知记录
	 * 
	 * @param from
	 * @param whose
	 */
	public void delNoticeByFrom(String from, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, FROM + "=? and " + WHOSE + "=?",
					new String[] { from, whose });
			db.close();
		}
	}

	/**
	 * 删除全部通知记录
	 * 
	 * @param whose
	 */
	public void delAllNotices(String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, WHOSE + "=?", new String[] { whose });
			db.close();
		}
	}

}
