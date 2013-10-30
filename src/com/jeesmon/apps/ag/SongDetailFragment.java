package com.jeesmon.apps.ag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.jeesmon.apps.ag.domain.Song;

/**
 * A fragment representing a single Song detail screen. This fragment is either
 * contained in a {@link SongListActivity} in two-pane mode (on tablets) or a
 * {@link SongDetailActivity} on handsets.
 */
public class SongDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM = "song_item";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Song mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SongDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM)) {
			mItem = (Song) getArguments().getSerializable(ARG_ITEM);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_song_detail,
				container, false);

		if (mItem != null) {
			WebView webView = (WebView) rootView.findViewById(R.id.song_detail);
			webView.getSettings().setDefaultFontSize(20);
			webView.setBackgroundColor(0x00000000);
			webView.loadUrl("file:///android_asset/content/ml/"
					+ mItem.getFilenameMl());
		}

		return rootView;
	}
}
