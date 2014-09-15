package com.free.ui.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.free.ui.model.NewFriend;
import com.free.ui.model.Notice;
import com.free.ui.model.User;

public class NewFriendDao {
	public static final String TABLE_NAME = "new_friends";
	public static final String ID = "_id";
	public static final String USERNAME = "username";
	public static final String NICKNAME = "nickname";
	public static final String CONTENT = "content";
	public static final String STATUS = "status";
	public static final String CONDITION = "condition";
	public static final String WHOSE = "whose";

	private MySQLiteOpenHelper dbHelper;
	private static NewFriendDao instance;

	private NewFriendDao(Context context) {
		dbHelper = MySQLiteOpenHelper.getInstance(context);
	}

	public static NewFriendDao getInstance(Context context) {
		if (instance == null) {
			instance = new NewFriendDao(context);
		}
		return instance;
	}

	/**
	 * 保存新朋友
	 * 
	 * @param newFriend
	 * @param whose
	 */
	public void saveNewFriend(NewFriend newFriend, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {

			ContentValues values = new ContentValues();
			values.put(USERNAME, newFriend.getUsername());
			values.put(NICKNAME, newFriend.getNickname());
			values.put(CONTENT, newFriend.getContent());
			values.put(STATUS, newFriend.getStatus());
			values.put(CONDITION, newFriend.getCondition());
			values.put(WHOSE, whose);
			db.insert(TABLE_NAME, null, values);
		}
	}

	/**
	 * 获取新朋友列表
	 * 
	 * @param whose
	 * @return
	 */
	public List<NewFriend> getNewFriends(String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<NewFriend> newFriends = new ArrayList<NewFriend>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where whose=?", new String[] { whose });
			while (cursor.moveToNext()) {
				NewFriend newFriend = new NewFriend();
				String id = cursor.getString(cursor.getColumnIndex(ID));
				newFriend.setId(id);
				String username = cursor.getString(cursor
						.getColumnIndex(USERNAME));
				newFriend.setUsername(username);
				String nickname = cursor.getString(cursor
						.getColumnIndex(NICKNAME));
				newFriend.setNickname(nickname);
				String content = cursor.getString(cursor
						.getColumnIndex(CONTENT));
				newFriend.setContent(content);
				int status = cursor.getInt(cursor.getColumnIndex(STATUS));
				newFriend.setStatus(status);
				int condition = cursor.getInt(cursor.getColumnIndex(CONDITION));
				newFriend.setCondition(condition);
				newFriends.add(newFriend);
			}
			cursor.close();
		}
		return newFriends;
	}

	

	/**
	 * 删除一条新朋友通知
	 * 
	 * @param id
	 * @param whose
	 */
	public void deleteNewFriend(String id, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, ID + "= ? and " + WHOSE + "=?", new String[] {
					id, whose });
			db.close();
		}
	}

	/**
	 * 更新新朋友处理状态
	 * 
	 * @param id
	 * @param condition
	 * @param whose
	 */
	public void updateCondition(String id, int condition, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(CONDITION, condition);
			db.update(TABLE_NAME, values, ID + "=? and " + WHOSE + "=?",
					new String[] { id, whose });
			db.close();
		}
	}

	/**
	 * 设置新朋友通知读取状态为已读
	 * 
	 * @param whose
	 */
	public void setRead(String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(STATUS, NewFriend.READ);
			db.update(TABLE_NAME, values, STATUS + "=? and " + WHOSE + "=?",
					new String[] { "" + NewFriend.UNREAD, whose });
			db.close();
		}
	}

	/**
	 * 新朋友未读取通知条数.
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
					new String[] { "" + NewFriend.UNREAD, whose });
			while (cursor.moveToNext()) {
				count++;
			}
			db.close();
		}
		return count;
	}

	/**
	 * 判断是否有同一好友某一状态的请求
	 * 
	 * @param username
	 * @param status
	 * @param whose
	 * @return
	 */
	public boolean isExist(String username, int status, String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where " + WHOSE + "=? and " + USERNAME + "=? and "
					+ STATUS + "=?", new String[] { whose, username,
					"" + status });
			if (cursor.moveToNext()) {
				cursor.close();
				return true;
			}
			cursor.close();
			db.close();
		}

		return false;
	}

}
