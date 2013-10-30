package com.jeesmon.apps.ag.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.jeesmon.apps.ag.control.FontControl;
import com.jeesmon.apps.ag.domain.Song;

@SuppressWarnings("unused")
public class SongListCursorAdapter extends SimpleCursorAdapter {
	private static final String TAG = "SongListCursorAdapter";

	private Context context;
	private int layout;

	public SongListCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);

		this.context = context;
		this.layout = layout;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);

		TextView tvTitle = (TextView) v.findViewById(android.R.id.text1);
		if (tvTitle != null) {
			tvTitle.setTypeface(FontControl.getInstance(context).getTypeface());
			tvTitle.setSingleLine(true);
		}

		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		if(c == null || c.isClosed()) {
			return;
		}
		
		Song song = new Song(c.getInt(c.getColumnIndex(Song.ID)), c.getString(c.getColumnIndex(Song.TITLE_ML)),
				c.getString(c.getColumnIndex(Song.TITLE_EN)), c.getString(c
						.getColumnIndex(Song.FILENAME_ML)), c.getString(c.getColumnIndex(Song.FILENAME_EN)));
		
		TextView tvTitle = (TextView) v.findViewById(android.R.id.text1);
		if (tvTitle != null) {
			tvTitle.setText(song.getTitleMl());
			tvTitle.setTag(song);
		}
	}
}
