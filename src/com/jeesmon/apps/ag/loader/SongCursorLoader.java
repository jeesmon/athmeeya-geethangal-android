package com.jeesmon.apps.ag.loader;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jeesmon.apps.ag.domain.Song;
import com.jeesmon.apps.ag.provider.SongContentProvider;

public class SongCursorLoader extends SimpleCursorLoader {
	private static final String TAG = "SongCursorLoader";
	
	private Context context;

	public SongCursorLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		Log.i(TAG, "loadInBackground");
		Cursor cursor = this.context.getContentResolver().query(SongContentProvider.CONTENT_URI,
				new String[] { Song.SONG_ID + " as " + Song.ID, Song.TITLE_ML, Song.TITLE_EN, Song.FILENAME_ML, Song.FILENAME_EN }, null, null, null);
		return cursor;
	}
}
