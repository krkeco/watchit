package com.krkeco.watchit;

import android.content.*;
import android.database.sqlite.*;

public class SQLASync extends SQLiteOpenHelper
{
		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		private static final String SQL_CREATE_ENTRIES =
    "CREATE TABLE " + FavoriteContract.FavoriteColumns.TABLE_NAME + " (" +
    FavoriteContract.FavoriteColumns.MOVIE_ID + " INTEGER PRIMARY KEY" +
		" )";

		private static final String SQL_DELETE_ENTRIES =
    "DROP TABLE IF EXISTS " + FavoriteContract.FavoriteColumns.TABLE_NAME;

		public static final int DATABASE_VERSION = 3;
		public static final String DATABASE_NAME = "Favorites.db";

		public SQLASync(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		public void onCreate(SQLiteDatabase db) {
				db.execSQL(SQL_CREATE_ENTRIES);
		}
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				db.execSQL(SQL_DELETE_ENTRIES);
				onCreate(db);
		}
		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				onUpgrade(db, oldVersion, newVersion);
		}



		public class FavDataBaseHelper extends SQLiteOpenHelper {
		
				public static final int DATABASE_VERSION = 1;
				public static final String DATABASE_NAME = "FeedReader.db";

				public FavDataBaseHelper(Context context) {
						super(context, DATABASE_NAME, null, DATABASE_VERSION);
				}
				public void onCreate(SQLiteDatabase db) {
						db.execSQL(SQL_CREATE_ENTRIES);
				}
				public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
						db.execSQL(SQL_DELETE_ENTRIES);
						onCreate(db);
				}
				public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
						onUpgrade(db, oldVersion, newVersion);
				}

		}
}
