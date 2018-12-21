package world.best.musicplayer.activity;

import android.animation.LayoutTransition;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import world.best.musicplayer.dialog.AddTagDialog;
import world.best.musicplayer.MediaPlaybackService;
import world.best.musicplayer.R;
import world.best.musicplayer.ui.FlowLayout;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.MusicUtils;
import world.best.musicplayer.utils.TagUtils;

import java.util.ArrayList;
import java.util.List;

public class NowPlayingActivity extends BaseActivity implements ServiceConnection {

    private long mPosOverride = -1;

    private ImageView mAddTags;
    private RelativeLayout mTagsContainer;
    private ImageView mExpandTags;
    private FlowLayout mTagsLayout;

    private TextView mCurrentTime;
    private TextView mTotalTime;
    private ImageView mArtwork;
    private ImageView mPlayPause;
    private ImageView mSkipToNext;
    private ImageView mSkipToPrevious;
    private ImageView mShuffle;
    private ImageView mRepeat;

    private TextView mTrack;
    private TextView mArtist;

    private SeekBar mSeekbar;

    private int mControlColorFilter;

    private Window mWindow;

    private long mTrackLength;
    private boolean mExpanded;

    private Context mContext;

    private String mFilePath;

    private long mFileMediaId;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (MusicUtils.isPlaying()) {
                setProgress();
            }
            mHandler.postDelayed(mRunnable, 500);
        }
    };

    BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MediaPlaybackService.PLAYSTATE_CHANGED:
                    setupPlayPauseControls();
                    setupThemeAndTitle();
                    setupTrackSeeker();
                    break;
                case MediaPlaybackService.META_CHANGED:
                    setupPlayPauseControls();
                    setupThemeAndTitle();
                    setupTrackSeeker();
                    setupTags();
                    break;
                case MediaPlaybackService.SHUFFLE_CHANGED:
                    setupShuffleComponent();
                    break;
                case MediaPlaybackService.REPEAT_CHANGED:
                    setupRepeatComponent();
                    break;
                case MediaPlaybackService.QUEUE_CHANGED:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mContext = getApplicationContext();

        mWindow = getWindow();
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        mControlColorFilter = ContextCompat.getColor(getApplicationContext(), R.color.control_disabled_color);

        AsyncQueryHandler mAsyncQueryHandler = new AsyncQueryHandler(mContext.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                if (cursor != null) {
                    MusicUtils.playAll(getApplicationContext(), cursor);
                }
            }
        };

        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                String scheme = uri.getScheme();
                if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                    if (uri.getAuthority() == MediaStore.AUTHORITY) {
                        // try to get title and artist from the media content provider
                        mAsyncQueryHandler.startQuery(0, null, uri, new String [] { MediaStore.Audio.Media._ID },
                                null, null, null);
                    }
                } else if (scheme.equals("file")) {
                    // check if this file is in the media database (clicking on a download
                    // in the download manager might follow this path
                    String path = uri.getPath();
                    mAsyncQueryHandler.startQuery(0, null,  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String [] { MediaStore.Audio.Media._ID },
                            MediaStore.Audio.Media.DATA + "=?", new String [] { path }, null);
                }
            }
        }

        setupViews();

        getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mRepeat.animate().alpha(1f);
                mShuffle.animate().alpha(1f);
                mTagsContainer.animate().alpha(1f);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        setupInteractions();
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

    private void setupViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCurrentTime = (TextView) findViewById(R.id.currenttime);
        mTotalTime = (TextView) findViewById(R.id.totaltime);
        mAddTags = (ImageView) findViewById(R.id.add_tags);
        mTagsContainer = (RelativeLayout) findViewById(R.id.tags_container);

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        mTagsContainer.setLayoutTransition(transition);

        mExpandTags = (ImageView) findViewById(R.id.expand);
        mTagsLayout = (FlowLayout) findViewById(R.id.tags_layout);
        mTagsLayout.setAddInnerPadding(true);

        mArtwork = (ImageView) findViewById(R.id.artwork);
        mPlayPause = (ImageView) findViewById(R.id.play_pause);
        mSkipToNext = (ImageView) findViewById(R.id.next);
        mSkipToPrevious = (ImageView) findViewById(R.id.previous);
        mShuffle = (ImageView) findViewById(R.id.shuffle);
        mRepeat = (ImageView) findViewById(R.id.repeat);
        mSeekbar = (SeekBar) findViewById(R.id.track_progress);
        mTrack = (TextView) findViewById(R.id.track);
        mArtist = (TextView) findViewById(R.id.artist);
    }

    private void setupInteractions() {
        mPlayPause.setColorFilter(mControlColorFilter, PorterDuff.Mode.SRC_IN);
        mSkipToNext.setColorFilter(mControlColorFilter, PorterDuff.Mode.SRC_IN);
        mSkipToPrevious.setColorFilter(mControlColorFilter, PorterDuff.Mode.SRC_IN);

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MusicUtils.isPlaying()) {
                    mPlayPause.setImageResource(R.drawable.ic_play);

                } else {
                    mPlayPause.setImageResource(R.drawable.ic_pause);
                }
                MusicUtils.playPause();
            }
        });

        mSkipToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicUtils.skipToNext();
                setupThemeAndTitle();
                setupTags();
            }
        });

        mSkipToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicUtils.skipToPrevious();
                setupThemeAndTitle();
                setupTags();
            }
        });

        mShuffle.setImageResource(R.drawable.ic_shuffle);
        mShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MusicUtils.toggleShuffleMode()) {
                    case MediaPlaybackService.SHUFFLE_NONE:
                        mShuffle.setColorFilter(
                            ContextCompat.getColor(mContext, R.color.control_disabled_color),
                            PorterDuff.Mode.SRC_IN);
                        break;
                    case MediaPlaybackService.SHUFFLE_NORMAL:
                        mShuffle.setColorFilter(
                            Color.WHITE,
                            PorterDuff.Mode.SRC_IN);
                }
            }
        });

        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MusicUtils.toggleRepeatMode()) {
                    case MediaPlaybackService.REPEAT_NONE:
                        mRepeat.setImageResource(R.drawable.ic_repeat);
                        mRepeat.setColorFilter(
                            ContextCompat.getColor(mContext, R.color.control_disabled_color),
                            PorterDuff.Mode.SRC_IN);
                        break;
                    case MediaPlaybackService.REPEAT_ALL:
                        mRepeat.setImageResource(R.drawable.ic_repeat);
                        mRepeat.setColorFilter(
                            Color.WHITE,
                            PorterDuff.Mode.SRC_ATOP);
                        break;
                    case MediaPlaybackService.REPEAT_CURRENT:
                        mRepeat.setImageResource(R.drawable.ic_repeat_one);
                        break;
                }
            }
        });

        mSeekbar.setOnSeekBarChangeListener(mSeekListener);
        mSeekbar.setMax(1000);

        mAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagsDialog();
            }
        });

        mExpandTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpanded) {
                    mExpanded = false;
                    mTagsLayout.getLayoutParams().height
                            = getResources().getDimensionPixelSize(R.dimen.tags_bar_collapsed_size);
                    mTagsLayout.requestLayout();
                    mExpandTags.setImageResource(R.drawable.ic_down);
                } else {
                    mExpanded = true;
                    mTagsLayout.getLayoutParams().height
                            = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    mTagsLayout.requestLayout();
                    mExpandTags.setImageResource(R.drawable.ic_up);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionGranted) {
            if (mServiceConnected) {
                mHandler = new Handler();
                mHandler.postDelayed(mRunnable, 500);
            }

            if (mServiceConnected) {
                setupContent();
            }

            final IntentFilter filter = new IntentFilter();
            filter.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
            filter.addAction(MediaPlaybackService.META_CHANGED);
            filter.addAction(MediaPlaybackService.SHUFFLE_CHANGED);
            filter.addAction(MediaPlaybackService.REPEAT_CHANGED);
            filter.addAction(MediaPlaybackService.QUEUE_CHANGED);

            filter.addCategory(MediaPlaybackService.CATEGORY);
            registerReceiver(mServiceReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mHandler.removeCallbacks(mRunnable);
            unregisterReceiverSafe(mServiceReceiver);
        } catch (Exception e) {
            // noop
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // supportFinishAfterTransition();
                // NavUtils.navigateUpFromSameTask(this);
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mServiceConnected = true;
        setupContent();
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mServiceConnected = false;
    }

    private void setupContent() {
        setupThemeAndTitle();
        setupPlayPauseControls();
        setupShuffleComponent();
        setupRepeatComponent();
        setupTrackSeeker();
        setupTags();
    }

    private void setupThemeAndTitle() {
        mPicasso.load(ContentUris.withAppendedId(sArtworkUri, MusicUtils.getCurrentAlbumId())).into(mArtwork);
        mTrack.setText(MusicUtils.getCurrentTrackName());
        mArtist.setText(MusicUtils.getCurrentArtistName());

        try {
            Palette.from(MusicUtils.getArtwork(getApplicationContext(),
                            MusicUtils.getCurrentAudioId(),
                            MusicUtils.getCurrentAlbumId()))
                    .generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette p) {
                            int defCol = ContextCompat.getColor(mContext, R.color.colorPrimary);
                            mWindow.setStatusBarColor(p.getVibrantColor(defCol));
                            ((LinearLayout) findViewById(R.id.control_layout)).setBackgroundColor(p.getVibrantColor(defCol));
                        }
                    }
                );
        } catch (IllegalArgumentException e) {
            // noop.Let it display default theme
        }

    }

    private void setupPlayPauseControls() {
        if (MusicUtils.isPlaying()) {
            mPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            mPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void setupShuffleComponent() {
        switch (MusicUtils.getCurrentShuffleMode()) {
            case MediaPlaybackService.SHUFFLE_NONE:
                mShuffle.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.control_disabled_color),
                    PorterDuff.Mode.SRC_IN);
                break;
            default :
                mShuffle.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    private void setupRepeatComponent() {
        switch (MusicUtils.getRepeatMode()) {
            case MediaPlaybackService.REPEAT_NONE:
                mRepeat.setImageResource(R.drawable.ic_repeat);
                mRepeat.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.control_disabled_color),
                    PorterDuff.Mode.SRC_IN);
                break;
            case MediaPlaybackService.REPEAT_ALL:
                mRepeat.setImageResource(R.drawable.ic_repeat);
                mRepeat.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.SRC_IN);
                break;
            case MediaPlaybackService.REPEAT_CURRENT:
                mRepeat.setImageResource(R.drawable.ic_repeat_one);
                break;
        }
    }

    private void setupTrackSeeker() {
        mTrackLength = MusicUtils.duration();

        long trackPosition = mPosOverride < 0 ? MusicUtils.position() : mPosOverride;
        if ((trackPosition >= 0) && (mTrackLength > 0)) {
            mCurrentTime.setText(MusicUtils.makeTimeString(this, trackPosition / 1000));
            mSeekbar.setProgress((int) (1000 * trackPosition / mTrackLength));
            long remainTime =  mTrackLength - trackPosition ;
            mTotalTime.setText((remainTime >= 1000 ? "-" : "") + MusicUtils.makeTimeString(this, remainTime / 1000));
        } else {
            mCurrentTime.setText("--:--");
            mTotalTime.setText("--:--");
            mSeekbar.setProgress(1000);
        }

    }

    private void setupTags() {
        mFilePath = MusicUtils.getSongPath(mContext, MusicUtils.getCurrentAudioId());
        if (mFilePath != null && !mFilePath.isEmpty()) {
            List<String> tags = TagUtils.getTagsForSong(mContext, mFilePath);
            if (tags != null && tags.size() > 0) {
                mTagsContainer.setVisibility(View.VISIBLE);
                setupTagViews(tags);
            } else {
                mTagsContainer.setVisibility(View.GONE);
            }
        } else {
            mTagsContainer.setVisibility(View.GONE);
        }
    }

    private void setupTagViews(List<String> tags) {
        mTagsLayout.removeAllViews();
        mTagsLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < tags.size(); i++) {
            mTagsLayout.addView(getTagView(tags.get(i)));
        }
        mTagsLayout.post(new Runnable() {
            @Override
            public void run() {
                mExpandTags.setVisibility(mTagsLayout.getRowCount() > 1 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private View getTagView(final String name) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final View tagView = View.inflate(mContext, R.layout.tag_item_np, null);
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

    private void showAddTagsDialog() {
        final List<String> filePaths = new ArrayList<String>();
        filePaths.add(mFilePath);
        AddTagDialog addTagsDialog = new AddTagDialog(this, filePaths,
                new AddTagDialog.OnTagChangeListener() {
                    @Override
                    public void onTagDelete(String tagToDelete) {
                        TagUtils.deleteTagForSongs(mContext, filePaths, tagToDelete, MusicUtils.getCurrentAudioId());
                        setupTags();
                    }

                    @Override
                    public void onTagAdded(String tagName) {
                        TagUtils.addTagForSongs(mContext, filePaths, tagName, MusicUtils.getCurrentAudioId());
                        setupTags();
                    }
                });
        addTagsDialog.show();
    }

    SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser || !mServiceConnected) {
                return;
            }

            MusicUtils.seek((mTrackLength * progress) / 1000);
            setupTrackSeeker();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void setProgress() {
        long trackPosition = mPosOverride < 0 ? MusicUtils.position() : mPosOverride;
        if ((trackPosition >= 0) && (mTrackLength > 0)) {
            int progress = (int) (1000 * trackPosition / mTrackLength);
            mSeekbar.setProgress(progress);
            mCurrentTime.setText(MusicUtils.makeTimeString(this, trackPosition / 1000));
            long remainTime =  mTrackLength - trackPosition ;
            mTotalTime.setText((remainTime >= 1000 ? "-" : "") + MusicUtils.makeTimeString(this, remainTime / 1000));
        } else {
            mCurrentTime.setText("--:--");
            mTotalTime.setText("--:--");
            mSeekbar.setProgress(1000);
        }

    }
}
