package world.best.musicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import world.best.musicplayer.adapters.AlbumsAdapter;
import world.best.musicplayer.adapters.SongsAdapter;
import world.best.musicplayer.cursorloaders.CursorLoaderManager;
import world.best.musicplayer.cursorloaders.CursorType;
import world.best.musicplayer.layoutmanagers.ContentLayoutManager;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.Constants;

public class ArtistActivity extends BaseDetailActivity {

    private static final String TAG = AlbumActivity.class.getSimpleName();
    private static final int CONTENT_ROW_HEIGHT = 216;

    private LinearLayout mSongsContainer;
    private LinearLayout mAlbumsContainer;

    private RecyclerView mSongResultsView;
    private RecyclerView mAlbumResultsView;

    private Context mContext;

    private AlbumsAdapter mAlbumsAdapter;

    private long mArtistId ;
    private String mArtistName;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_artist);

        mContext = getApplicationContext();
        mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mArtistId = bundle.getLong(Constants.EXTRA_ARTIST_ID);
            mArtistName = bundle.getString(Constants.EXTRA_ARTIST_NAME);
            Log.e("Artist", "" + mArtistId);
            Log.e("Artist", "" + mArtistName);
            setupBaseViews();
            setupBaseInteractions();
            setupViews();
            setupInteractions();
        } else {
            Log.d("CursorLoader", "Bundle Null");
        }
    }

    private void setupViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSongsContainer = (LinearLayout) findViewById(R.id.songs_container);
        mAlbumsContainer = (LinearLayout) findViewById(R.id.albums_container);

        mSongResultsView = (RecyclerView) findViewById(R.id.song_results);
        mAlbumResultsView = (RecyclerView) findViewById(R.id.album_results);

        // mCollapsedControlLayout = (RelativeLayout) findViewById(R.id.collapsed_control_layout);
        // mCollapsedArtwork = (ImageView) findViewById(R.id.artwork);
        // mCollapsedTitle = (TextView) findViewById(R.id.title);
        // mCollapsedArtist = (TextView) findViewById(R.id.artist);
        // mCollapsedPlayPause = (ImageView) findViewById(R.id.play_pause);
        // mCollapsedNext = (ImageView) findViewById(R.id.next);
        // mCollapsedPrevious = (ImageView) findViewById(R.id.previous);
    }

    private void setupInteractions() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mArtistName);

        mSongResultsView.setNestedScrollingEnabled(false);
        mAlbumResultsView.setNestedScrollingEnabled(false);
        // mCollapsedControlLayout.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         Intent i = new Intent(getApplicationContext(), NowPlayingActivity.class);
        //         i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //         startActivity(i);
        //     }
        // });

        // mCollapsedPlayPause.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         if (MusicUtils.isPlaying()) {
        //             ((ImageView) view).setImageResource(R.drawable.ic_play);
        //         } else {
        //             ((ImageView) view).setImageResource(R.drawable.ic_pause);
        //         }
        //         MusicUtils.playPause();
        //     }
        // });

        // mCollapsedNext.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         MusicUtils.skipToNext();
        //     }
        // });

        // mCollapsedPrevious.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         MusicUtils.skipToPrevious();
        //     }
        // });

        if (mSongsAdapter == null) {
            mSongsAdapter = new SongsAdapter(mContext,
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
        mAlbumResultsView.setLayoutManager(new ContentLayoutManager(mContext));

        // Need to fix container views before adding this here.
        // mSongResultsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //     @Override
        //     public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //         super.onScrollStateChanged(recyclerView, newState);
        //     }

        //     @Override
        //     public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //         if (dy > 0) {
        //             showSearchFab();
        //         } else {
        //             hideSearchFab();
        //         }
        //     }
        // });

        // mAlbumResultsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //     @Override
        //     public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //         super.onScrollStateChanged(recyclerView, newState);
        //     }

        //     @Override
        //     public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //         if (dy > 0) {
        //             showSearchFab();
        //         } else {
        //             hideSearchFab();
        //         }
        //     }
        // });

        Bundle bundle = new Bundle();
        bundle.putLong(Constants.EXTRA_ARTIST_ID, mArtistId);
        CursorLoaderManager cursorLoaderManager = new CursorLoaderManager(getApplicationContext(), this);
        getSupportLoaderManager().initLoader(CursorType.ARTIST_DETAIL_ALBUM_CURSOR , bundle, cursorLoaderManager);
        getSupportLoaderManager().initLoader(CursorType.ARTIST_DETAIL_SONG_CURSOR , bundle, cursorLoaderManager);
    }

    @Override
    protected void showNoPermissionView() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void loadViews() {

    }

    @Override
    public void onCursorLoadingFinished(Cursor cursor, int cursorType) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        switch (cursorType) {
            case CursorType.ARTIST_DETAIL_ALBUM_CURSOR:
                if (cursor != null && cursor.getCount() > 0) {
                    mAlbumsContainer.setVisibility(View.VISIBLE);
                    mAlbumsAdapter.swapCursor(cursor, MediaStore.Audio.Media._ID, false);
                    params.height = (cursor.getCount() + 1) * CONTENT_ROW_HEIGHT;
                    mAlbumResultsView.setLayoutParams(params);
                    mAlbumResultsView.setAdapter(mAlbumsAdapter);
                } else {
                    mAlbumsContainer.setVisibility(View.GONE);
                }
                break;
            case CursorType.ARTIST_DETAIL_SONG_CURSOR:
                if (cursor != null && cursor.getCount() > 0) {
                    mSongsContainer.setVisibility(View.VISIBLE);
                    mSongsAdapter.swapCursor(cursor, MediaStore.Audio.Media._ID, false);
                    params.height = (cursor.getCount() + 2) * CONTENT_ROW_HEIGHT;
                    mSongResultsView.setLayoutParams(params);
                    mSongResultsView.setAdapter(mSongsAdapter);
                } else {
                    mSongsContainer.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onCursorLoaderReset(int cursorType) {
        switch (cursorType) {
        case CursorType.ARTIST_DETAIL_ALBUM_CURSOR:
            // mAlbumsAdapter.swapCursor(null, "", false);
            break;
        case CursorType.ARTIST_DETAIL_SONG_CURSOR:
            // mSongsAdapter.swapCursor(null, "", false);
            break;
        }
        // switch (cursorType) {
        // case CursorType.ARTIST_DETAIL_CURSOR:
        //     mAlbumsAdapter.swapCursor(null, "", false);
        //     break;
        // case CursorType.ARTIST_DETAIL_SONG_CURSOR:
        //     mSongsAdapter.swapCursor(null, "", false);
        //     break;
        // }
    }
}