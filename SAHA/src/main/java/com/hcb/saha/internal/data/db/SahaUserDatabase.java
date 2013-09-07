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
		public static final String FIRST_NAME_FIELD = "firstname";
		public static final String SURNAME_FIELD = "surname";
		public static final String GOOGLE_ACCOUNT_FIELD = "googleaccount";
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

	private static ContentValues getContentValues(User user) {
		ContentValues values = new ContentValues();
		values.put(UsersColumns.FIRST_NAME_FIELD, user.getFirstName());
		values.put(UsersColumns.SURNAME_FIELD, user.getSurName());
		values.put(UsersColumns.GOOGLE_ACCOUNT_FIELD, user.getGoogleAccount());
		return values;
	}

	public static long addUser(User user) {
		SQLiteDatabase db = getSahaOpenHelper().getWritableDatabase();
		ContentValues values = getContentValues(user);
		long id = db.insert(USERS_TABLE, null, values);
		user.setId((int) id);
		db.close();
		return id;
	}

	public static void deleteUser(User user) {
		SQLiteDatabase db = getSahaOpenHelper().getWritableDatabase();
		db.execSQL("delete from " + USERS_TABLE + " where "
				+ UsersColumns.ID_FIELD + " = " + user.getId() + ";");
		db.close();
	}

	public static void deleteAllUsers() {
		SQLiteDatabase db = getSahaOpenHelper().getWritableDatabase();
		db.execSQL("delete from " + USERS_TABLE + ";");
		db.close();
	}

	private static User getUserFromCursor(Cursor cursor) {
		User user = new User();
		user.setId(cursor.getInt(cursor.getColumnIndex(UsersColumns.ID_FIELD)));
		user.setFirstName(cursor.getString(cursor
				.getColumnIndex(UsersColumns.FIRST_NAME_FIELD)));
		user.setSurName(cursor.getString(cursor
				.getColumnIndex(UsersColumns.SURNAME_FIELD)));
		user.setGoogleAccount(cursor.getString(cursor
				.getColumnIndex(UsersColumns.GOOGLE_ACCOUNT_FIELD)));
		return user;
	}

	public static void updateUser(User user) {
		SQLiteDatabase db = getSahaOpenHelper().getWritableDatabase();
		ContentValues values = getContentValues(user);
		db.update(USERS_TABLE, values,
				UsersColumns.ID_FIELD + "="
				+ user.getId(), null);
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
					.append(UsersColumns.FIRST_NAME_FIELD)
					.append(" TEXT NOT NULL, ")
					.append(UsersColumns.SURNAME_FIELD)
					.append(" TEXT NOT NULL, ")
					.append(UsersColumns.GOOGLE_ACCOUNT_FIELD)
					.append(" TEXT);")
					.toString();
			db.execSQL(query);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createUsersTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
			onCreate(db);
		}
	}

}
