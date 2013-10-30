package com.jeesmon.apps.ag.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.jeesmon.apps.ag.domain.Song;
import com.jeesmon.apps.ag.sqlite.SDCardSQLiteOpenHelper;

public class SongContentProvider extends ContentProvider {
	private static final String TAG = "SongContentProvider";

	public static final String AUTHORITY = "com.jeesmon.apps.ag.provider.songcontentprovider";
	public static final String TABLE = "songs";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.malayalamchristiansongs.songs";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.malayalamchristiansongs.songs";

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "songs.db";

	private Context mContext;
	private static final UriMatcher sUriMatcher;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private String mDbPath;
	private String mInternalDbPath;

	private static final int SONGS = 1;
	private static final int SONGS_BY_ID = 2;
	private static final int SONGS_BY_TEXT = 3;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, TABLE, SONGS);
		sUriMatcher.addURI(AUTHORITY, TABLE + "/#", SONGS_BY_ID);
		sUriMatcher.addURI(AUTHORITY, TABLE + "/*", SONGS_BY_TEXT);
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case SONGS:
			return CONTENT_TYPE;
		case SONGS_BY_ID:
			return CONTENT_ITEM_TYPE;
		case SONGS_BY_TEXT:
			return CONTENT_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		Log.i(TAG, "onCreate");

		mContext = getContext();
		mInternalDbPath = mContext.getFilesDir().getPath() + "/databases";
		mDbHelper = new DatabaseHelper(mContext, getDbPath());
		mDb = mDbHelper.getReadableDatabase();
		
		checkVersion();

		Log.i(TAG, "dbOpened");
		return true;
	}

	private void checkVersion() {
		Log.i(TAG, "checkVersion");
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables("version");
		Cursor c = qb.query(mDb, null, null, null, null,
				null, null);
		if(c != null && !c.isClosed()) {
			if(c.moveToFirst()) {
				int version = c.getInt(0);
				if(version < DATABASE_VERSION) {
					Log.i(TAG, "version " + version + " != " + DATABASE_VERSION);
					
					mDb.close();
					mDbHelper.copyDatabase();
					mDb = mDbHelper.getReadableDatabase();
				}
			}
			c.close();
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.i(TAG, "query: " + uri.toString());
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case SONGS:
			qb.setTables(TABLE);
			break;
		case SONGS_BY_ID:
			qb.setTables(TABLE);
			qb.appendWhere(Song.ID + " = " + uri.getPathSegments().get(1));
			break;
		case SONGS_BY_TEXT:
			qb.setTables(TABLE);
			qb.appendWhere(Song.TITLE_EN + " LIKE '%" + uri.getPathSegments().get(1) + "%'");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		if (mDb == null || !mDb.isOpen()) {
			mDb = mDbHelper.getReadableDatabase();
		}

		Cursor c = qb.query(mDb, projection, selection, selectionArgs, null,
				null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	private String getDbPath() {
		if (mDbPath == null) {
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mDbPath = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/Android/data/com.jeesmon.apps.ag/db";
			} else {
				mDbPath = mInternalDbPath;
			}

			File dir = new File(mDbPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}

		return mDbPath;
	}

	private static class DatabaseHelper extends SDCardSQLiteOpenHelper {
		private Context context;
		private String dbPath;

		public DatabaseHelper(Context context, String dbPath) {
			super(dbPath, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
			this.dbPath = dbPath;
			
			File file = new File(this.dbPath + "/"
					+ DATABASE_NAME);
			if(!file.exists()) {
				copyDatabase();
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "DatabaseHelper.onCreate");
		}

		public void copyDatabase() {
			try {
				File file = new File(this.dbPath + "/"
						+ DATABASE_NAME);
				if(file.exists()) {
					file.delete();
				}
				
				file = new File(this.dbPath + "/"
						+ DATABASE_NAME + "-journal");
				if(file.exists()) {
					file.delete();
				}
				
				InputStream is = context.getAssets().open(
						"databases/" + DATABASE_NAME);
				OutputStream os = new FileOutputStream(this.dbPath + "/"
						+ DATABASE_NAME);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) != -1) {
					os.write(buffer, 0, count);
				}
				is.close();
				os.flush();
				os.close();
				
				Log.i(TAG, "Songs database copied to " + this.dbPath + "/"
						+ DATABASE_NAME);
				
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG, "DatabaseHelper.onUpgrade");
		}
	}
}
