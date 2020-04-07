package world.best.musicplayer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import world.best.musicplayer.R;
import world.best.musicplayer.adapters.AlbumsAdapter;
import world.best.musicplayer.adapters.ArtistsAdapter;
import world.best.musicplayer.adapters.SongsAdapter;
import world.best.musicplayer.layoutmanagers.ContentLayoutManager;
import world.best.musicplayer.ui.FlowLayout;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.MusicUtils;
import world.best.musicplayer.utils.TagUtils;

public class SearchActivity extends AppCompatActivity {

    private static final int CONTENT_ROW_HEIGHT = 216;

    private static final int SEARCH_ROW_SONGS = 0;
    private static final int SEARCH_ROW_ARTISTS = 1;
    private static final int SEARCH_ROW_ALBUMS = 2;
    private static final int SEARCH_ROW_TAGS = 3;

    private boolean mSongsMatched = false;
    private boolean mArtistsMatched = false;
    private boolean mAlbumsMatched = false;
    // private boolean mTagsMatched = false;

    private CardView mResultsHolder;

    private LinearLayout mSongsContainer;
    private LinearLayout mArtistsContainer;
    private LinearLayout mAlbumsContainer;
    private LinearLayout mTagsContainer;

    private TextView mSongsHeader;
    private TextView mArtistsHeader;
    private TextView mAlbumsHeader;

    private RecyclerView mSongResultsView;
    private RecyclerView mArtistResultsView;
    private RecyclerView mAlbumResultsView;
    private FlowLayout mTagResultsView;

    private ImageView mBackButton;
    private EditText mSearchEditText;
    private TextView mNoResult;

    private SongsAdapter mSongsAdapter;
    private ArtistsAdapter mArtistsAdapter;
    private AlbumsAdapter mAlbumsAdapter;

    private Context mContext;

    private SearchTask mSearchTask;

    private LayoutInflater mInflater;

    private String mSongsHeaderText;
    private String mArtistsHeaderText;
    private String mAlbumsHeaderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = getApplicationContext();
        mSearchTask = new SearchTask();

        Resources resources = getApplicationContext().getResources();
        mSongsHeaderText = resources.getString(R.string.tracks_title);
        mArtistsHeaderText = resources.getString(R.string.artists_title);
        mAlbumsHeaderText = resources.getString(R.string.albums_title);

        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifyStoragePermissions(this);
        setupViews();
        setupInteractions();
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        return;
    }

    private void setupViews() {
        mResultsHolder = (CardView) findViewById(R.id.results_container);

        mSongsContainer = (LinearLayout) findViewById(R.id.songs_container);
        mArtistsContainer = (LinearLayout) findViewById(R.id.artists_container);
        mAlbumsContainer = (LinearLayout) findViewById(R.id.albums_container);
        mTagsContainer = (LinearLayout) findViewById(R.id.tags_container);

        mSongsHeader = (TextView) findViewById(R.id.songs_header);
        mArtistsHeader = (TextView) findViewById(R.id.artists_header);
        mAlbumsHeader = (TextView) findViewById(R.id.albums_header);

        mSongResultsView = (RecyclerView) findViewById(R.id.song_results);
        mArtistResultsView = (RecyclerView) findViewById(R.id.artist_results);
        mAlbumResultsView = (RecyclerView) findViewById(R.id.album_results);
        mTagResultsView = (FlowLayout) findViewById(R.id.tag_results);

        mBackButton = (ImageView) findViewById(R.id.back);
        mSearchEditText = (EditText) findViewById(R.id.search);
        mNoResult = (TextView) findViewById(R.id.no_result);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // supportFinishAfterTransition();
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupInteractions() {
        mSongResultsView.setNestedScrollingEnabled(false);
        mArtistResultsView.setNestedScrollingEnabled(false);
        mAlbumResultsView.setNestedScrollingEnabled(false);

        if (mSongsAdapter == null) {
            mSongsAdapter = new SongsAdapter(mContext,
                                                this,
                                                R.layout.content_item,
                                                null,
                                                false,
                                                false);
        }

        if (mArtistsAdapter == null) {
            mArtistsAdapter = new ArtistsAdapter(mContext,
                                                this,
                                                R.layout.content_item,
                                                null,
                                                false,
                                                false);
        }

        if (mAlbumsAdapter == null) {
            mAlbumsAdapter = new AlbumsAdapter(mContext,
                                                this,
                                                R.layout.content_item,
                                                null);
        }

        mSongResultsView.setLayoutManager(new ContentLayoutManager(mContext));
        mArtistResultsView.setLayoutManager(new ContentLayoutManager(mContext));
        mAlbumResultsView.setLayoutManager(new ContentLayoutManager(mContext));

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSearchEditText.addTextChangedListener(mSearchQueryListener);

        // ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
        //         .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private final TextWatcher mSearchQueryListener = new TextWatcher() {
        private String previousQuery;

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            String query = s.toString();
            if (!query.equals(previousQuery)) {
                previousQuery = query;
                if (mSearchTask != null) {
                    mSearchTask.cancel(true);
                }
                mSearchTask = new SearchTask();
                mSearchTask.execute(query.replace("'", "''"));
            }
        }
    };

    private void setupTagViews(ArrayList<String> searchResults) {
        mTagResultsView.removeAllViews();
        mTagResultsView.setVisibility(View.VISIBLE);
        for (int i = 0; i < searchResults.size(); i++) {
            mTagResultsView.addView(getTagView(mContext, searchResults.get(i)));
        }
    }

    private View getTagView(Context context, final String name) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final View tagView = View.inflate(context,R.layout.tag_item, null);
        TextView tagName = (TextView) tagView.findViewById(R.id.tag_name);
        RelativeLayout container = (RelativeLayout) tagView.findViewById(R.id.container);
        tagView.setLayoutParams(params);
        tagName.setText(name);
        tagName.setVisibility(View.VISIBLE);
        tagName.setBackgroundResource(R.drawable.tagged_bg);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Long> idObjs = TagUtils.getCachedSongsForTag(name);
                if (!idObjs.isEmpty()) {
                    long[] songIds = new long[idObjs.size()];
                    for (int i = 0; i < songIds.length ;i++) {
                        songIds[i] = idObjs.get(i);
                    }
                    Intent intent = new Intent(mContext, TagActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.EXTRA_TAG_NAME, name);
                    bundle.putInt(Constants.EXTRA_TAG_TYPE, Constants.TAG_NORMAL);
                    bundle.putLongArray(Constants.EXTRA_SONGS_IDS, songIds);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });

        return tagView;
    }

    public void setupPlayer(long id, String title, String artist) {
        Intent i = new Intent(mContext, NowPlayingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // TODO: CHange this later
        i.putExtra("id", id);
        i.putExtra("title", title);
        i.putExtra("artist", artist);
        startActivity(i);
    }

    private class SearchTask extends AsyncTask<String, Void, String> {

        Cursor mSongsCursor;
        Cursor mArtistsCursor;
        Cursor mAlbumsCursor;
        ArrayList<String> mTagsResults;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSongsMatched = false;
            mArtistsMatched = false;
            mAlbumsMatched = false;
            mSongsCursor = null;
            mArtistsCursor = null;
            mAlbumsCursor = null;
            mTagsResults = null;
        }

        @Override
        protected String doInBackground(String... strings) {
            String query = strings[0];
            if (!query.isEmpty()) {
                mSongsCursor = MusicUtils.searchTracks(mSongsAdapter.getQueryHandler(), query);
                mArtistsCursor = MusicUtils.searchArtists(mArtistsAdapter.getQueryHandler(), query);
                mAlbumsCursor = MusicUtils.searchAlbums(mAlbumsAdapter.getQueryHandler(), query);
                mTagsResults = TagUtils.searchTags(query);

                if (mSongsCursor != null && mSongsCursor.getCount() > 0) {
                    if (mSongsCursor.moveToFirst()) {
                        try {
                            String songName = mSongsCursor.getString(mSongsCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                            if (songName.toLowerCase().startsWith(query)) {
                                mSongsMatched = true;
                            }
                        } catch (Exception e) {
                            // noop
                        }
                    }
                }

                if (mArtistsCursor != null && mArtistsCursor.getCount() > 0) {
                    if (mArtistsCursor.moveToFirst()) {
                        try {
                            String artistName = mArtistsCursor.getString(mArtistsCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
                            if (artistName.toLowerCase().startsWith(query)) {
                                mArtistsMatched = true;
                            }
                        } catch (Exception e) {
                            // noop
                        }
                    }
                }

                if (mAlbumsCursor != null && mAlbumsCursor.getCount() > 0) {
                    if (mAlbumsCursor.moveToFirst()) {
                        try {
                            String albumName = mAlbumsCursor.getString(mAlbumsCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
                            if (albumName.toLowerCase().startsWith(query)) {
                                mAlbumsMatched = true;
                            }
                        } catch (Exception e) {
                            // noop
                        }
                    }
                }
            } else {
                mSongsCursor = null;
                mArtistsCursor = null;
                mAlbumsCursor = null;
                mTagsResults = new ArrayList<>();
            }

            return query;
        }

        protected void onPostExecute(String query) {
            super.onPostExecute(query);
            LinearLayout.LayoutParams params = new
                 LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            if (mSongsMatched) {
                setSongsInSongs(mSongsCursor, params);
                if (mArtistsMatched) {
                    setArtistsInArtists(mArtistsCursor, params);
                    setAlbumsInAlbums(mAlbumsCursor, params);
                } else if (mAlbumsMatched) {
                    setAlbumsInArtists(mAlbumsCursor, params);
                    setArtistsInAlbums(mArtistsCursor, params);
                } else {
                    setArtistsInArtists(mArtistsCursor, params);
                    setAlbumsInAlbums(mAlbumsCursor, params);
                }
            } else if (mArtistsMatched) {
                setArtistsInSongs(mArtistsCursor, params);

                if (mAlbumsMatched) {
                    setAlbumsInArtists(mAlbumsCursor, params);
                    setSongsInAlbums(mSongsCursor, params);
                } else {
                    setSongsInArtists(mSongsCursor, params);
                    setAlbumsInAlbums(mAlbumsCursor, params);
                }
            } else if (mAlbumsMatched) {
                setAlbumsInSongs(mAlbumsCursor, params);
                setSongsInArtists(mSongsCursor, params);
                setArtistsInAlbums(mArtistsCursor, params);
            } else {
                setSongsInSongs(mSongsCursor, params);
                setArtistsInArtists(mArtistsCursor, params);
                setAlbumsInAlbums(mAlbumsCursor, params);
            }

            if (!mTagsResults.isEmpty()) {
                mTagsContainer.setVisibility(View.VISIBLE);
                setupTagViews(mTagsResults);
            } else {
                mTagsContainer.setVisibility(View.GONE);
            }

            if ((mSongsCursor != null && mSongsCursor.getCount() > 0)
                    || (mArtistsCursor != null && mArtistsCursor.getCount() > 0)
                    || (mAlbumsCursor != null && mAlbumsCursor.getCount() > 0)
                    || (!mTagsResults.isEmpty())
                    || (query == null || query.length() < 1)) {
                mNoResult.setVisibility(TextView.GONE);
            } else {
                mNoResult.setVisibility(TextView.VISIBLE);
            }
        }
    }

    private void setSongsInSongs(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mSongsContainer.setVisibility(View.VISIBLE);
            mSongsHeader.setText(mSongsHeaderText);
            mSongsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mSongResultsView.setLayoutParams(params);
            mSongResultsView.setAdapter(mSongsAdapter);
        } else {
            mSongsContainer.setVisibility(View.GONE);
        }
    }

    private void setSongsInArtists(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mArtistsContainer.setVisibility(View.VISIBLE);
            mArtistsHeader.setText(mSongsHeaderText);
            mSongsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mArtistResultsView.setLayoutParams(params);
            mArtistResultsView.setAdapter(mSongsAdapter);
        } else {
            mArtistResultsView.setVisibility(View.GONE);
        }
    }

    private void setSongsInAlbums(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mAlbumsContainer.setVisibility(View.VISIBLE);
            mAlbumsHeader.setText(mSongsHeaderText);
            mSongsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mAlbumResultsView.setLayoutParams(params);
            mAlbumResultsView.setAdapter(mSongsAdapter);
        } else {
            mAlbumsContainer.setVisibility(View.GONE);
        }
    }

    private void setArtistsInArtists(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mArtistsContainer.setVisibility(View.VISIBLE);
            mArtistsHeader.setText(mArtistsHeaderText);
            mArtistsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mArtistResultsView.setLayoutParams(params);
            mArtistResultsView.setAdapter(mArtistsAdapter);
        } else {
            mArtistsContainer.setVisibility(View.GONE);
        }
    }

    private void setArtistsInSongs(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mSongsContainer.setVisibility(View.VISIBLE);
            mSongsHeader.setText(mArtistsHeaderText);
            mArtistsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mSongResultsView.setLayoutParams(params);
            mSongResultsView.setAdapter(mArtistsAdapter);
        } else {
            mSongsContainer.setVisibility(View.GONE);
        }
    }

    private void setArtistsInAlbums(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mAlbumsContainer.setVisibility(View.VISIBLE);
            mAlbumsHeader.setText(mArtistsHeaderText);
            mArtistsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mAlbumResultsView.setLayoutParams(params);
            mAlbumResultsView.setAdapter(mArtistsAdapter);
        } else {
            mAlbumsContainer.setVisibility(View.GONE);
        }
    }

    private void setAlbumsInAlbums(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mAlbumsContainer.setVisibility(View.VISIBLE);
            mAlbumsHeader.setText(mAlbumsHeaderText);
            mAlbumsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mAlbumResultsView.setLayoutParams(params);
            mAlbumResultsView.setAdapter(mAlbumsAdapter);
        } else {
            mAlbumsContainer.setVisibility(View.GONE);
        }
    }

    private void setAlbumsInSongs(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mSongsContainer.setVisibility(View.VISIBLE);
            mSongsHeader.setText(mAlbumsHeaderText);
            mAlbumsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mSongResultsView.setLayoutParams(params);
            mSongResultsView.setAdapter(mAlbumsAdapter);
        } else {
            mSongsContainer.setVisibility(View.GONE);
        }
    }

    private void setAlbumsInArtists(Cursor cursor, LinearLayout.LayoutParams params) {
        if (cursor != null && cursor.getCount() > 0) {
            mArtistsContainer.setVisibility(View.VISIBLE);
            mArtistsHeader.setText(mAlbumsHeaderText);
            mAlbumsAdapter.changeCursor(cursor, MediaStore.Audio.Media._ID);
            params.height = cursor.getCount() * CONTENT_ROW_HEIGHT;
            mArtistResultsView.setLayoutParams(params);
            mArtistResultsView.setAdapter(mAlbumsAdapter);
        } else {
            mArtistsContainer.setVisibility(View.GONE);
        }
    }
}
