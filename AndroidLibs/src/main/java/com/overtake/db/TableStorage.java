package com.overtake.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.database.Cursor;

import com.overtake.db.SqliteDB;
import com.overtake.utils.CloseUtil;
import com.overtake.utils.OTTextUtil;
import com.overtake.utils.OTTextUtil.IGetText;
import com.overtake.utils.OTLog;

public abstract class TableStorage<T> {

	public static class IColumn {
		String columnName;

		int index;

		String columnDesc;

		public IColumn(String columnName, int index, String columnDesc) {
			this.columnName = columnName;
			this.index = index;
			this.columnDesc = columnDesc;
		}

		public String getColumnName() {
			return columnName;
		}

		public int getSelectFlag() {
			return 1 << index;
		}

		public int getIndex() {
			return index;
		}

		public String getColumnDesc() {
			return columnDesc;
		}
	}

	public static String getCreateTableSQL(String tableName, IColumn[] tokens) {
		StringBuilder bf = new StringBuilder();
		bf.append("create table IF NOT EXISTS ").append(tableName).append(" (");
		OTTextUtil.join(bf, ",", tokens, new IGetText<IColumn>() {

			@Override
			public String getText(IColumn col) {
				return new StringBuffer(col.getColumnName()).append(" ").append(col.getColumnDesc()).toString();
			}
		});
		bf.append(");");
		return bf.toString();
	}

	protected SqliteDB db;

	protected final String tableName;

	public TableStorage(SqliteDB db, String tableName) {
		this.db = db;
		this.tableName = tableName;
	}

	public String[] getColumsByFlag(int flag, IColumn[] values) {
		List<String> list = new ArrayList<String>();
		for (IColumn column : values) {
			if ((column.getSelectFlag() & flag) != 0) {
				list.add(column.getColumnName());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public long insertValue(T obj, int flag) {
		checkValue(obj);
		int ticket = db.beginTransaction();

		ContentValues values = getContentValue(obj, flag);
		long rowid = db.insert(getTableName(), null, values);

		db.setTransactionSuccessful(ticket);
		db.endTransaction(ticket);
		OTLog.w(getClass().getName(), "insertValue:" + getEqualCondition(obj) + ", result: " + rowid);
		return rowid;
	}

	public long updateValue(T obj, int flag) {

		checkValue(obj);

		ContentValues values = getContentValue(obj, flag);
		int ticket = db.beginTransaction();
		int update = db.update(getTableName(), values, getEqualCondition(obj), null);

		db.setTransactionSuccessful(ticket);
		db.endTransaction(ticket);
		OTLog.w(getClass().getName(), "updateValue:" + getEqualCondition(obj) + ", result: " + update);
		return update;
	}

	protected ReentrantLock lock = new ReentrantLock();

	public long insertOrUpdateValue(T obj) {
		try {
			lock.lock();
			return insertOrUpdateValue(obj, getFlagOfSelectAllColumns());
		} finally {
			lock.unlock();
		}
	}

	public long insertOrUpdateValue(T obj, int flag) {
		try {
			lock.lock();
			Cursor cursor = null;
			try {
				if (!checkValue(obj)) {
					return -1;
				}

				cursor = db.query(getTableName(), getEqualCondition(obj), null, null);
				if (cursor != null && cursor.getCount() > 0) {
					OTLog.w(getClass().getName(), "updateValue:" + getEqualCondition(obj));
					return updateValue(obj, flag);
				} else {
					OTLog.w(getClass().getName(), "insertValue:" + getEqualCondition(obj));
					return insertValue(obj, flag);
				}
			} catch (Exception ex) {
				OTLog.e("tablestorage", "insertOrUpdateValue:", ex);
				return -1;
			} finally {
				CloseUtil.close(cursor);
			}
		} finally {
			lock.unlock();
		}
	}

	public IColumn[] sort(IColumn[] columns) {
		Arrays.sort(columns, new Comparator<IColumn>() {

			@Override
			public int compare(IColumn object1, IColumn object2) {
				return object1.getIndex() - object2.getIndex();
			}

		});
		return columns;
	}

	public int getFlag(int flag, IColumn column) {
		return flag | column.getSelectFlag();
	}

	protected abstract boolean checkValue(T obj);

	public String getTableName() {
		return tableName;
	};

	public String getColumnName(IColumn col) {
		return tableName + "." + col.getColumnName();
	}

	public abstract IColumn[] getColumns();

	public int getFlagOfSelectAllColumns() {
		return (1 << getColumns().length) - 1;
	};

	protected abstract String getEqualCondition(T obj);

	public abstract ContentValues getContentValue(T obj, int flag);

	public abstract T valueOfRow(Cursor cursor);
}
