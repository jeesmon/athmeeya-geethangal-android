package com.jeesmon.apps.ag;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

import com.jeesmon.apps.ag.adapter.SongListCursorAdapter;
import com.jeesmon.apps.ag.domain.Song;
import com.jeesmon.apps.ag.provider.SongContentProvider;

/**
 * A list fragment representing a list of Songs. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link SongDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SongListFragment extends ListFragment implements LoaderCallbacks<Cursor>, OnQueryTextListener, OnCloseListener {
	private static final String TAG = "SongListFragment";
	
	private SongListCursorAdapter mAdapter;
	private SearchView mSearchView;
	private MenuItem mSearchMenuItem;
	private String mCurFilter;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private static final String STATE_ACTIVATED_SEARCH = "activated_search";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(Song song);
	}
	
	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Song song) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SongListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "onViewCreated");
		
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null) {
			if(savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
				setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
			}
			if(savedInstanceState.containsKey(STATE_ACTIVATED_SEARCH)) {
				mCurFilter = savedInstanceState.getString(STATE_ACTIVATED_SEARCH);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach");
		
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "onDetach");
		
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		
		super.onActivityCreated(savedInstanceState);
		
		setEmptyText("No Songs");
		setHasOptionsMenu(true);
		
		mAdapter = new SongListCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null,
                new String[] { Song.TITLE_ML },
                new int[] { android.R.id.text1 }, 0);
		
		setListAdapter(mAdapter);
		setListShown(false);
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		Log.d(TAG, "onListItemClick");
		
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected((Song) view.getTag());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState");
		
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
		outState.putString(STATE_ACTIVATED_SEARCH, mCurFilter);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		Log.d(TAG, "setActivateOnItemClick");
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		Log.d(TAG, "setActivatedPosition");
		
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "onCreateOptionsMenu: " + mCurFilter);
		
        // Place an action bar item for searching.
		inflater.inflate(R.menu.menu, menu);
        mSearchMenuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setQueryHint("Search in English");
        
        if(!TextUtils.isEmpty(mCurFilter)) {
			mSearchView.setQuery(mCurFilter, false);
		}
        
        mSearchMenuItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
            	Log.d(TAG, "onMenuItemActionCollapse");
            	mSearchView.setQuery("", true);
            	mSearchView.clearFocus();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
            	Log.d(TAG, "onMenuItemActionExpand");
                return true;  // Return true to expand action view
            }
        });
    }
	
	@Override
	public boolean onQueryTextChange(String newText) {
		Log.d(TAG, "onQueryTextChange: " + newText);
		
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
	
	@Override public boolean onQueryTextSubmit(String query) {
		getLoaderManager().restartLoader(0, null, this);
        mSearchView.clearFocus();
        return true;
    }
	
	@Override
    public boolean onClose() {
		Log.d(TAG, "onClose: " + mCurFilter);
        return true;
    }
	
	// These are the Contacts rows that we will retrieve.
    static final String[] SONG_PROJECTION = new String[] {
        Song.SONG_ID + " as " + Song.ID,
        Song.TITLE_ML,
        Song.TITLE_EN,
        Song.FILENAME_ML,
        Song.FILENAME_EN
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(SongContentProvider.CONTENT_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = SongContentProvider.CONTENT_URI;
        }

        return new CursorLoader(getActivity(), baseUri,
        		SONG_PROJECTION, null, null,
        		Song.TITLE_ML + " COLLATE LOCALIZED ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
