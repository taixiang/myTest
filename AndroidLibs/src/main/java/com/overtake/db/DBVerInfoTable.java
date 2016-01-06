// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

// Decompiler options: fullnames definits braces deadcode fieldsfirst 

package com.overtake.db;

import android.content.ContentValues;
import android.database.Cursor;

final class DBVerInfoTable {

	private static final String DB_VERSION = "1.0"; // just for test

	private DBVerInfoTable() {
	}

	public static boolean checkDbVersion(SqliteDB db) {
		int i;
		if (db != null) {
			String sql = "CREATE TABLE IF NOT EXISTS DBInfoTableV2 ( key TEXT, version TEXT )";
			if (db.execSQL(sql)) {
				String versionInDB = "0";
				Cursor cursor = db.query("DBInfoTableV2");
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						versionInDB = cursor.getString(1);
					}
					cursor.close();
				}

				if (versionInDB.equals(DB_VERSION)) {
					return true;
				} else if (versionInDB.equals("")) {
					DBVerInfoTable.updateVersion(db);
					return true;
				} else if (versionInDB.equals("0")) {
					DBVerInfoTable.updateVersion(db);
					return true;
				}

				return false;
			} else {
				i = 0;
			}
		} else {
			return true;
		}
		return i == 1;
	}

	private static void updateVersion(SqliteDB dbUtil) {
		android.content.ContentValues contentvalues = new ContentValues();
		contentvalues.put("version", DB_VERSION);
		dbUtil.replace("DBInfoTableV2", null, contentvalues);
	}
}
