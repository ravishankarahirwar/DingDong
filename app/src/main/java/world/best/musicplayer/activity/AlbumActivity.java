package world.best.musicplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import world.best.musicplayer.adapters.SongsAdapter;
import world.best.musicplayer.cursorloaders.CursorType;
import world.best.musicplayer.cursorloaders.CursorLoaderManager;
import world.best.musicplayer.layoutmanagers.ContentLayoutManager;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.MusicUtils;

public class AlbumActivity extends BaseDetailActivity {

    private static final String TAG = AlbumActivity.class.getSimpleName();
    private static final int START_POSITION = 0;

    private Context mContext;

    private long mAlbumId;
    private String mAlbumName;

    private TextView mCountAndDuration;
    private LinearLayout mHeader;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_album);

        mContext = getApplicationContext();
        mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constants.EXTRA_ALBUM_ID)) {
                mAlbumId = bundle.getLong(Constants.EXTRA_ALBUM_ID , -1);
                mAlbumName = bundle.getString(Constants.EXTRA_ALBUM_TITLE , "Album");
            }
        } else {
            Log.d("CursorLoader", "Bundle Null");
        }

        setupBaseViews();
        setupBaseInteractions();
        setupViews();
        setupInteractions();
    }

    private void setupViews() {
        mCountAndDuration = (TextView) findViewById(R.id.song_count_time);
        mHeader = (LinearLayout) findViewById(R.id.header);
        mRecyclerView = (RecyclerView) findViewById(R.id.media_recycler_view);
        mContentLayoutManager = new ContentLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mContentLayoutManager);
    }

    private void setupInteractions() {
        getSupportActionBar().setTitle(mAlbumName);
            mSongsAdapter = new SongsAdapter(mContext,
                    mActivity,
                    R.layout.content_item,
                    null,
                    false,
                    false);
             Bundle bundle = new Bundle();
             bundle.putLong(Constants.EXTRA_ALBUM_ID, mAlbumId);
             CursorLoaderManager cursorLoaderManager = new CursorLoaderManager(mContext, this);
             getSupportLoaderManager().initLoader(CursorType.ALBUM_DETAIL_CURSOR , bundle, cursorLoaderManager);

        /**
         * As soon as user click on Header view
         * we start playing first song of this album
         */
        mHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCursor != null && mCursor.getCount() > 0) {
                    MusicUtils.playAll(mContext, mCursor, START_POSITION);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    showSearchFab();
                } else {
                    hideSearchFab();
                }
            }
        });
    }

    @Override
    public void onCursorLoadingFinished(Cursor cursor, int cursorType) {
        switch(cursorType) {
        case CursorType.ALBUM_DETAIL_CURSOR :
            mCursor = cursor;
            mSongsAdapter.swapCursor(cursor, MediaStore.Audio.Media._ID, true);
            mRecyclerView.setAdapter(mSongsAdapter);
            mCursor = cursor;
            updateHeader();
            break;
        }
    }

    @Override
    public void onCursorLoaderReset(int cursorType) {
         mSongsAdapter.swapCursor(null, "", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServiceConnected) {
            if (MusicUtils.isMusicLoaded() || MusicUtils.isPlaying()) {
               setupCollapsedControl();
            }
        }
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

    private void updateHeader() {
        String headerText = getHeaderText(mContext, mCursor);
        mCountAndDuration.setText(headerText);
    }
}