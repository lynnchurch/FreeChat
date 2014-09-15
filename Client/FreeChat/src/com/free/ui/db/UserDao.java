package com.free.ui.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.free.chat.core.FCContactsManager;
import com.free.exception.FCException;
import com.free.ui.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String USERNAME = "username";
	public static final String NICKNAME = "nickname";
	public static final String EMAIL = "email";
	public static final String SIGNATURE = "signature";
	public static final String GROUP = "_group";
	public static final String WHOSE = "whose";

	private MySQLiteOpenHelper dbHelper;
	private static UserDao instance;

	private UserDao(Context context) {
		dbHelper = MySQLiteOpenHelper.getInstance(context);
	}

	public static UserDao getInstance(Context context) {
		if (instance == null) {
			instance = new UserDao(context);
		}
		return instance;
	}

	/**
	 * 保存联系人列表
	 * 
	 * @param contacts
	 * @param whose
	 */
	public void saveContacts(List<User> contacts, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (User user : contacts) {
				ContentValues values = new ContentValues();
				values.put(USERNAME, user.getUsername());
				values.put(NICKNAME, user.getNickname());
				values.put(EMAIL, user.getEmail());
				values.put(SIGNATURE, user.getSignature());
				values.put(GROUP, user.getGroup());
				values.put(WHOSE, whose);
				db.insert(TABLE_NAME, null, values);
			}
			db.close();
		}
	}

	/**
	 * 获取联系人列表
	 * 
	 * @return
	 */
	public List<User> getContacts(String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<User> users = new ArrayList<User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where whose=?", new String[] { whose });
			while (cursor.moveToNext()) {
				User user = new User();
				String username = cursor.getString(cursor
						.getColumnIndex(USERNAME));
				user.setUsername(username);
				String nickname = cursor.getString(cursor
						.getColumnIndex(NICKNAME));
				user.setNickname(nickname);
				String email = cursor.getString(cursor.getColumnIndex(EMAIL));
				user.setEmail(email);
				String signature = cursor.getString(cursor
						.getColumnIndex(SIGNATURE));
				user.setSignature(signature);
				String group = cursor.getString(cursor.getColumnIndex(GROUP));
				user.setGroup(group);

				users.add(user);
			}
			cursor.close();
			db.close();
		}
		return users;
	}

	/**
	 * 更新联系人列表
	 * 
	 * @param whose
	 */
	public void updateContacts(String whose) {
		List<User> users = getContacts(whose);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (User user : users) {
				String username = user.getUsername();
				Map<String, String> result = null;
				try {
					result = FCContactsManager.search(username);
				} catch (FCException e) {
					Log.e("exception", e.getMessage());
				}
				ContentValues values = new ContentValues();
				String nickname = result.get("Name");
				values.put(NICKNAME, nickname);
				String signature = result.get("Signature");
				values.put(SIGNATURE, signature);
				db.update(TABLE_NAME, values, USERNAME + "=? and " + WHOSE
						+ "=?", new String[] { username, whose });
			}
			db.close();
		}
	}

	/**
	 * 判断当前登录用户的联系人列表是否已存在
	 * 
	 * @param whose
	 * @return
	 */
	public boolean isExist(String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where whose=?", new String[] { whose });
			if (cursor.moveToNext()) {
				cursor.close();
				return true;
			}
			cursor.close();
			db.close();
		}

		return false;
	}

	/**
	 * 删除一个联系人
	 * 
	 * @param username
	 * @param whose
	 */
	public void deleteContact(String username, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, USERNAME + " = ? and " + WHOSE + "=?",
					new String[] { username, whose });
			db.close();
		}
	}

	/**
	 * 保存一个联系人
	 * 
	 * @param user
	 * @param whose
	 */
	public void saveContact(User user, String whose) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(USERNAME, user.getUsername());
			values.put(NICKNAME, user.getNickname());
			values.put(EMAIL, user.getEmail());
			values.put(SIGNATURE, user.getSignature());
			values.put(GROUP, user.getGroup());
			values.put(WHOSE, whose);
			db.insert(TABLE_NAME, null, values);
			db.close();
		}
	}
	
	/**
	 * 根据用户名获得昵称
	 * 
	 * @param username
	 * @param whose
	 * @return
	 */
	public String getNickname(String username, String whose) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String nickname = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
					+ " where username=? and whose=?", new String[] { username,
					whose });
			cursor.moveToNext();

			nickname = cursor.getString(cursor.getColumnIndex(NICKNAME));

			cursor.close();
		}
		return nickname;
	}
}
