// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

// Decompiler options: fullnames definits braces deadcode fieldsfirst 

package com.overtake.db;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.overtake.utils.FileUtil;
import com.overtake.utils.OTLog;

@SuppressLint("UseSparseArrays")
public final class SqliteDB {

	private static String TAG = "SqliteDB";

	public static interface ITableSQLs {
		public String[] getCreateTableSQLs();

		public String[] getDropTableSQLs();
	}

	private HashMap<Integer, ITableSQLs> createTableSQLMap = new HashMap<Integer, ITableSQLs>();

	private static int ticket = 0;

	public SQLiteDatabase db = null;

	public SqliteDB() {
		db = null;
	}

	public void add(ITableSQLs sqls, int step) {
		createTableSQLMap.put(Integer.valueOf(step), sqls);
	}

	private boolean checkDbTables() {
		int tocket = beginTransaction();
		for (Iterator<ITableSQLs> iterator = createTableSQLMap.values().iterator(); iterator.hasNext();) {
			String as[] = iterator.next().getCreateTableSQLs();
			for (int i = 0; i < as.length; i++) {
				if (!execSQL(as[i])) {
					break;
				}
			}
		}

		setTransactionSuccessful(tocket);
		endTransaction(tocket);
		return true;

	}

	public final int endTransaction(int pTicket) {
		int ret = 0;
		if (pTicket == ticket) {
			if (db == null) {
				OTLog.e(TAG, "db == null");
			}
			db.endTransaction();
			ticket = 0;
		} else {
			ret = -1;
		}
		return ret;
	}

	public final int update(String table, ContentValues contentvalues, String whereClause, String whereArgs[]) {
		int rows;
		try {

			// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
			// null);
			if (db == null) {
				OTLog.e(TAG, "db == null");
			}
			rows = db.update(table, contentvalues, whereClause, whereArgs);

		} catch (Exception e) {
			rows = -1;
		}
		return rows;
	}

	public final int delete(String table, String where, String whereArgs[]) {
		int i;
		// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
		// null);
		if (db == null) {
			OTLog.e(TAG, "db == null");
		}
		i = db.delete(table, where, whereArgs);
		return i;
	}

	public final long insert(String table, String nullColumnHack, ContentValues contentvalues) {
		// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
		// null);
		if (db == null) {
			OTLog.e(TAG, "db == null");
		}
		return db.insert(table, nullColumnHack, contentvalues);
	}

	public final Cursor query(String table, String selection, String[] selectionArgs, String orderBy) {
		// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
		// null);
		if (db == null) {
			OTLog.e(TAG, "table" + table + " db == null");
		}
		Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, orderBy);
		return cursor;
	}

	public final Cursor queryByWhere(String table, String selection, String[] selectionArgs) {
		// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
		// null);
		if (db == null) {
			OTLog.e(TAG, "table" + table + " db == null");
		}
		Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);
		return cursor;
	}

	public final Cursor query(String table) {
		// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
		// null);
		if (db == null) {
			OTLog.e(TAG, "table" + table + " db == null");
		}
		Cursor cursor = db.query(table, null, null, null, null, null, null);
		return cursor;
	}

	public final Cursor query(String sql, String selectionArgs[]) {
		// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
		// null);
		if (db == null) {
			OTLog.e(TAG, " db == null");
		}
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

	public final void clear() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	@SuppressWarnings("deprecation")
	public final boolean checkDatabase(String dbCachePath) {
		boolean flag = false;

		if (db != null) {
			db.close();
		}
		try {
			FileUtil.makeDirExist(new File(dbCachePath).getParent());
			db = SQLiteDatabase.openOrCreateDatabase(dbCachePath, null);
			db.setLockingEnabled(true);
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
		if (!DBVerInfoTable.checkDbVersion(this)) {
			db.close();
			new File(dbCachePath).delete();
			db = SQLiteDatabase.openOrCreateDatabase(dbCachePath, null);
			db.setLockingEnabled(true);
			checkDbTables();
			return true;
		} else if (!checkDbTables()) {
			int tocket = beginTransaction();
			for (Iterator<ITableSQLs> iterator = createTableSQLMap.values().iterator(); iterator.hasNext();) {
				String as[] = iterator.next().getDropTableSQLs();
				for (int i = 0; i < as.length; i++) {
					if (!execSQL(as[i])) {
						break;
					}
				}
			}

			setTransactionSuccessful(tocket);
			endTransaction(tocket);
			return true;
		} else {
			flag = true;
		}

		return flag;
	}

	public final int beginTransaction() {
		int i;
		if (ticket == 0) {
			// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
			// null);
			if (db == null) {
				OTLog.e(TAG, "db == null");
			}
			db.beginTransaction();
			i = (int) System.currentTimeMillis();

			ticket = i;
			if (i < 0) {
				ticket = 0x7fffffff & ticket;
			}

			i = ticket;
		} else {

			i = -1;
		}
		return i;
	}

	public final int setTransactionSuccessful(int transactionTicket) {
		int byte0 = 0;
		if (ticket == transactionTicket) {
			// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
			// null);
			if (db == null) {
				OTLog.e(TAG, " db == null");
			}
			db.setTransactionSuccessful();
		} else {
			byte0 = -1;
		}
		return byte0;
	}

	public final long replace(String table, String nullColumnHack, ContentValues contentvalues) {
		long l;
		try {
			// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
			// null);
			if (db == null) {
				OTLog.e(TAG, "table" + table + " db == null");
			}
			l = db.replace(table, nullColumnHack, contentvalues);
		} catch (Exception e) {
			l = -1;
		}

		return l;
	}

	public final boolean execSQL(String sql) {
		boolean flag = true;
		try {
			// junit.framework.Assert.assertTrue("SQLiteDatabase is null", db !=
			// null);
			if (db == null) {
				OTLog.e(TAG, "db == null");
			}
			db.execSQL(sql);
		} catch (SQLException e) {
			flag = false;
		}

		return flag;
	}

	protected final void finalize() {
		if (db != null) {
			db.close();
			db = null;
		}
	}
}
