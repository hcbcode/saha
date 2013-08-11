package com.hcb.saha.internal.data.db;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.hcb.saha.internal.core.SahaConfig.Database;
import com.hcb.saha.internal.data.model.User;

/**
 * Saha user database handler
 *
 * @author Andreas Borglin
 */
@Singleton
public final class SahaUserDatabase {

	@Inject
	private static Provider<Application> contextProvider;

	private SahaUserDatabase() {
		// Not intended
	}

	private static final String USERS_TABLE = "users";

	private static final class UsersColumns implements BaseColumns {

		public static final String ID_FIELD = BaseColumns._ID;
		public static final String NAME_FIELD = "name";
		public static final String DIRECTORY_FIELD = "dir";
	}

	private static SahaOpenHelper getSahaOpenHelper() {
		return new SahaOpenHelper(contextProvider.get());
	}

	public static List<User> getAllUsers() {
		SQLiteDatabase db = getSahaOpenHelper().getReadableDatabase();
		Cursor cursor = db.query(USERS_TABLE, null, null, null, null, null,
				null);
		boolean more = cursor.moveToFirst();
		List<User> users = new ArrayList<User>(cursor.getCount());
		while (more) {
			users.add(getUserFromCursor(cursor));
			more = cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return users;
	}

	public static User getUserFromId(int id) {
		SQLiteDatabase db = getSahaOpenHelper().getReadableDatabase();
		Cursor cursor = db
				.query(USERS_TABLE, null, UsersColumns.ID_FIELD + " = ?",
						new String[] { String.valueOf(id) }, null, null, null);
		User user = null;
		if (cursor.moveToFirst()) {
			user = getUserFromCursor(cursor);
		}
		cursor.close();
		db.close();
		return user;
	}

	public static long addUser(User user) {
		SQLiteDatabase db = getSahaOpenHelper().getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(UsersColumns.NAME_FIELD, user.getName());
		values.put(UsersColumns.DIRECTORY_FIELD, user.getDirectory());
		long id = db.insert(USERS_TABLE, null, values);
		user.setId((int) id);
		db.close();
		return id;
	}

	public static void deleteAllUsers() {
		SQLiteDatabase db = getSahaOpenHelper().getWritableDatabase();
		db.execSQL("delete from " + USERS_TABLE + ";");
		db.close();
	}

	private static User getUserFromCursor(Cursor cursor) {
		User user = new User();
		user.setId(cursor.getInt(cursor.getColumnIndex(UsersColumns.ID_FIELD)));
		user.setName(cursor.getString(cursor
				.getColumnIndex(UsersColumns.NAME_FIELD)));
		user.setDirectory(cursor.getString(cursor
				.getColumnIndex(UsersColumns.DIRECTORY_FIELD)));
		return user;
	}

	private static class SahaOpenHelper extends SQLiteOpenHelper {

		public SahaOpenHelper(Context context) {
			super(context, Database.NAME, null, Database.VERSION);
		}

		private void createUsersTable(SQLiteDatabase db) {
			final String query = new StringBuilder().append("CREATE TABLE ")
					.append(USERS_TABLE).append(" (")
					.append(UsersColumns.ID_FIELD)
					.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
					.append(UsersColumns.NAME_FIELD).append(" TEXT, ")
					.append(UsersColumns.DIRECTORY_FIELD).append(" TEXT);")
					.toString();
			db.execSQL(query);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createUsersTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Not implemented
		}
	}

}
