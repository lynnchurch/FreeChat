package com.free.ui.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static MySQLiteOpenHelper instance;
	// 创建用户表SQL语句
	private static final String USERS_TABLE_CREATE = "CREATE TABLE "
			+ UserDao.TABLE_NAME + " ("
			+ "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ UserDao.USERNAME + " TEXT, " + UserDao.NICKNAME + " TEXT, "
			+ UserDao.EMAIL + " TEXT, " + UserDao.SIGNATURE + " TEXT, "
			+ UserDao.GROUP + " TEXT, " + UserDao.WHOSE + " TEXT); ";

	// 创建通知表SQL语句
	private static final String NOTICES_TABLE_CREATE = "CREATE TABLE "
			+ NoticeDao.TABLE_NAME + " ("
			+ "_id INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT,"
			+ NoticeDao.TYPE + " INTEGER," + NoticeDao.TITLE + " TEXT,"
			+ NoticeDao.CONTENT + " TEXT," + NoticeDao.FROM + " TEXT,"
			+ NoticeDao.TO + " TEXT," + NoticeDao.TIME + " TEXT,"
			+ NoticeDao.STATUS + " INTEGER," + NoticeDao.WHOSE + " TEXT);";

	// 创建新朋友表SQL语句
	private static final String NEW_FRIENDS_TABLE_CREATE = "CREATE TABLE "
			+ NewFriendDao.TABLE_NAME + " ("
			+ "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ NewFriendDao.USERNAME + " TEXT, " + NewFriendDao.NICKNAME
			+ " TEXT, " + NewFriendDao.CONTENT + " TEXT, "
			+ NewFriendDao.STATUS + " TEXT, " + NewFriendDao.CONDITION
			+ " TEXT, " + UserDao.WHOSE + " TEXT); ";

	// 创建消息记录表SQL语句
	private static final String FCMESSAGE_TABLE_CREATE = "CREATE TABLE "
			+ FCMessageDao.TABLE_NAME + " ("
			+ "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
			+ FCMessageDao.CONTENT + " TEXT, " + FCMessageDao.TIME + " TEXT, "
			+ FCMessageDao.WITH + " TEXT, " + FCMessageDao.TYPE + " INTEGER, "
			+ FCMessageDao.CONDITION + " INTEGER, " + FCMessageDao.STATUS
			+ " INTEGER, " + UserDao.WHOSE + " TEXT); ";

	private MySQLiteOpenHelper(Context context) {
		super(context, "free_chat_db", null, DATABASE_VERSION);
	}

	public static MySQLiteOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new MySQLiteOpenHelper(context.getApplicationContext());
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(USERS_TABLE_CREATE);
		db.execSQL(NOTICES_TABLE_CREATE);
		db.execSQL(NEW_FRIENDS_TABLE_CREATE);
		db.execSQL(FCMESSAGE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// private void dropTable(SQLiteDatabase db) {
	// String sql = "DROP TABLE IF EXISTS chatRecord";
	// db.execSQL(sql);
	// }

}