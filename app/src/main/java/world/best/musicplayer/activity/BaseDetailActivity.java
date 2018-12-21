package world.best.musicplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import world.best.musicplayer.adapters.SongsAdapter;
import world.best.musicplayer.cursorloaders.CursorLoaderCallBack;
import world.best.musicplayer.layoutmanagers.ContentLayoutManager;
import world.best.musicplayer.MediaPlaybackService;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.MusicUtils;

import java.util.concurrent.TimeUnit;

public abstract class BaseDetailActivity extends BaseActivity implements CursorLoaderCallBack {

    protected static final String TAG = AlbumActivity.class.getSimpleName();

    protected Toolbar mToolbar;
    protected FloatingActionButton mSearchFab;
    protected RecyclerView mRecyclerView;
    protected SongsAdapter mSongsAdapter;
    protected ContentLayoutManager mContentLayoutManager;

    protected RelativeLayout mControlContainer;
    protected RelativeLayout mCollapsedControlLayout;
    protected ImageView mCollapsedArtwork;
    protected TextView mCollapsedTitle;
    protected TextView mCollapsedArtist;
    protected ImageView mCollapsedPrevious;
    protected ImageView mCollapsedPlayPause;
    protected ImageView mCollapsedNext;
    private ProgressBar mProgress;

    protected Activity mActivity;

    private long mPosOverride = -1;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MediaPlaybackService.PLAYSTATE_CHANGED:
                    setupCollapsedControl();
                    break;
                case MediaPlaybackService.META_CHANGED:
                    setupCollapsedControl();
                    break;
                case MediaPlaybackService.SHUFFLE_CHANGED:
                    break;
                case MediaPlaybackService.REPEAT_CHANGED:
                    break;
                case MediaPlaybackService.QUEUE_CHANGED:
                    break;
            }
        }
    };

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            if (MusicUtils.isPlaying()) {
                setProgress();
                mProgress.postDelayed(mUpdateProgress, 1000);
            } else {
                mProgress.removeCallbacks(mUpdateProgress);
            }

        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // setupViews();
        // setupInteractions();
    }

    protected void setupBaseViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchFab = (FloatingActionButton) findViewById(R.id.fab);

        mControlContainer = (RelativeLayout) findViewById(R.id.control_container);
        mCollapsedControlLayout = (RelativeLayout) findViewById(R.id.collapsed_control_layout);
        mCollapsedArtwork = (ImageView) findViewById(R.id.artwork);
        mCollapsedTitle = (TextView) findViewById(R.id.title);
        mCollapsedArtist = (TextView) findViewById(R.id.artist);
        mCollapsedPlayPause = (ImageView) findViewById(R.id.play_pause);
        mCollapsedNext = (ImageView) findViewById(R.id.next);
        mCollapsedPrevious = (ImageView) findViewById(R.id.previous);
        mProgress = (ProgressBar) findViewById(R.id.song_progress);
    }

    protected void setupBaseInteractions() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });

        if (MusicUtils.isMusicLoaded() || MusicUtils.isPlaying()) {
            mCollapsedControlLayout.setVisibility(View.VISIBLE);
        }

        mCollapsedControlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, NowPlayingActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Pair<View, String> title = Pair.create((View) mCollapsedTitle, "title");
                Pair<View, String> detail = Pair.create((View) mCollapsedArtist, "detail");
                Pair<View, String> play = Pair.create((View) mCollapsedPlayPause, "play");
                Pair<View, String> previous = Pair.create((View) mCollapsedPrevious, "previous");
                Pair<View, String> next = Pair.create((View) mCollapsedNext, "next");
                Pair<View, String> artwork = Pair.create((View) mCollapsedArtwork, "artwork");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(mActivity, title, detail, play, previous, next, artwork);
                startActivity(i, options.toBundle());
                // startActivity(i);
            }
        });

        mCollapsedPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicUtils.isPlaying()) {
                    ((ImageView) view).setImageResource(R.drawable.ic_play);
                } else {
                    ((ImageView) view).setImageResource(R.drawable.ic_pause);
                }
                MusicUtils.playPause();
            }
        });

        mCollapsedNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicUtils.skipToNext();
            }
        });

        mCollapsedPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicUtils.skipToPrevious();
            }
        });
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mServiceConnected = true;
        if (MusicUtils.isMusicLoaded() || MusicUtils.isPlaying()) {
            setupCollapsedControl();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mServiceConnected = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPermissionGranted) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
            filter.addAction(MediaPlaybackService.META_CHANGED);
            filter.addAction(MediaPlaybackService.SHUFFLE_CHANGED);
            filter.addAction(MediaPlaybackService.REPEAT_CHANGED);
            filter.addAction(MediaPlaybackService.QUEUE_CHANGED);

            filter.addCategory(MediaPlaybackService.CATEGORY);
            registerReceiver(mServiceReceiver, filter);
            super.onResume();
            if (mServiceConnected) {
                if (MusicUtils.isMusicLoaded() || MusicUtils.isPlaying()) {
                   setupCollapsedControl();
                }
            }
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiverSafe(mServiceReceiver);
    }

    @Override
    protected void showNoPermissionView() {

    }

    @Override
    protected void loadViews() {

    }

    public void setupCollapsedControl() {
        if (MusicUtils.isMusicLoaded()) {
            if (mSongsAdapter != null) {
                mSongsAdapter.setPlaying(MusicUtils.isPlaying());
                mSongsAdapter.setCurrentPlayingId(MusicUtils.getCurrentAudioId());
                mSongsAdapter.notifyDataSetChanged();
            }

            mCollapsedControlLayout.setVisibility(View.VISIBLE);
            mPicasso.load(ContentUris.withAppendedId(sArtworkUri, MusicUtils.getCurrentAlbumId())).into(mCollapsedArtwork);
            mCollapsedTitle.setText(MusicUtils.getCurrentTrackName());
            mCollapsedArtist.setText(MusicUtils.getCurrentArtistName());

            mProgress.setMax((int) MusicUtils.duration());
            setProgress();
            mProgress.postDelayed(mUpdateProgress, 10);

            if (MusicUtils.isPlaying()) {
                mCollapsedPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                mCollapsedPlayPause.setImageResource(R.drawable.ic_play);
            }
        }
    }

    public void showDeleteSnackBar(final long[] songIds) {
        Snackbar snackbar = Snackbar.make(mControlContainer, R.string.delete_snackbar_message, Constants.SNACK_BAR_SHOW_TIME)
                .setAction(R.string.snack_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            MusicUtils.deleteTracks(getApplicationContext(), songIds);
                            if (mSongsAdapter.getItemCount() == 0) {
                                mCollapsedControlLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                snackbar.setActionTextColor(getColor(android.R.color.white));
                View snackView = snackbar.getView();
                TextView snackMessage = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
                snackMessage.setTextColor(getColor(android.R.color.darker_gray));
                snackbar.show();
    }

    @Override
    public void onCursorLoaderReset(int cursorType) {
       //.swapCursor(null, "");
    }

    protected void showSearchFab() {
        mSearchFab.clearAnimation();
        mSearchFab.animate().translationY(mControlContainer.getHeight());
    }

    protected void hideSearchFab() {
        mSearchFab.clearAnimation();
        mSearchFab.animate().translationY(0);
    }

    public String getHeaderText(Context context, Cursor cursor) {
        String headerText = "";
        if (cursor != null) {
            long duration = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                    .moveToNext()) {
                duration += cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DURATION));
            }

            Resources resources = context.getResources();
            int numberOfSongs = cursor.getCount();
            String songsKey = resources.getQuantityString(
                    R.plurals.songs_count, numberOfSongs, numberOfSongs);
            int durationOfAlbum = (int) TimeUnit.MILLISECONDS
                    .toMinutes(duration);
            String durationKey = resources.getQuantityString(
                    R.plurals.song_duration_count, durationOfAlbum,
                    durationOfAlbum);
            headerText = songsKey + ", " + durationKey;
        }

        return headerText;
    }

    private void setProgress() {
        long trackPosition = mPosOverride < 0 ? MusicUtils.position() : mPosOverride;
        long mTrackLength = MusicUtils.duration();
        if ((trackPosition >= 0) && (mTrackLength > 0)) {
            int progress = (int) (1000 * trackPosition / mTrackLength);
            mProgress.setProgress((int)trackPosition);
        } else {
            mProgress.setProgress(1000);
        }
    }
}
