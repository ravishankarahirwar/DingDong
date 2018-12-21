package world.best.musicplayer.activity;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import world.best.musicplayer.adapters.SongsAdapter;
import world.best.musicplayer.adapters.ArtistsAdapter;
import world.best.musicplayer.adapters.AlbumsAdapter;
import world.best.musicplayer.adapters.TagsAdapter;
import world.best.musicplayer.adapters.ViewSelectionListAdapter;
import world.best.musicplayer.cursorloaders.CursorLoaderCallBack;
import world.best.musicplayer.cursorloaders.CursorType;
import world.best.musicplayer.cursorloaders.CursorLoaderManager;
import world.best.musicplayer.cursorloaders.TagsLoader;
import world.best.musicplayer.layoutmanagers.ContentLayoutManager;
import world.best.musicplayer.MediaPlaybackService;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.MusicUtils;
import world.best.musicplayer.utils.TagUtils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                            ServiceConnection,
                                                            CursorLoaderCallBack,
                                                            OnItemClickListener {


    public static final int CODE_WRITE_SETTINGS_PERMISSION = 101;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private ListPopupWindow mViewSelectionList;

    private Toolbar mToolbar;
    private FloatingActionButton mSearchFab;
    private DrawerLayout mNavigationDrawer;
    private NavigationView mNavigationView;
    private RelativeLayout mNavigationSpinner;
    private TextView mNavigationKey;

    private RelativeLayout mSortHeader;
    private TextView mSortDesc;
    private FrameLayout mMostPlayedSortButton;
    private FrameLayout mRecentlyAddedSortButton;
    private FrameLayout mAlphabeticalSortButton;
    private ImageView mMostPlayedSortIcon;
    private ImageView mRecentlyAddedSortIcon;
    private ImageView mAlphabeticalSortIcon;

    private LinearLayout mAmLayout;
    private ImageView mCloseActionMode;
    private ImageView mAmBack;
    private TextView mAmCount;
    private TextView mEdit;
    private ImageView mAmTag;
    private ImageView mAmDelete;

    private RelativeLayout mNoPermissionContainer;
    private RelativeLayout mContentContainer;
    private Button mGrantPermissionButton;

    private RelativeLayout mRecyclerViewsContainer;
    private RecyclerView mSongsRecyclerView;
    private RecyclerView mAlbumsRecyclerView;
    private RecyclerView mArtistsRecyclerView;
    private RecyclerView mTagsRecyclerView;

    private SongsAdapter mSongsAdapter;
    private ArtistsAdapter mArtistsAdapter;
    private AlbumsAdapter mAlbumsAdapter;
    private TagsAdapter mTagsAdapter;

    private ViewSelectionListAdapter mViewSelectionListAdapter;

    private RelativeLayout mSecondaryControl;
    private RelativeLayout mControlContainer;
    private RelativeLayout mCollapsedControlLayout;
    private ImageView mCollapsedArtwork;
    private TextView mCollapsedTitle;
    private TextView mCollapsedArtist;
    private ImageView mCollapsedPrevious;
    private ImageView mCollapsedPlayPause;
    private ImageView mCollapsedNext;
    private ProgressBar mProgress;

    private ImageView mNavDrawerArtwork;
    private TextView mNavDrawerTitle;
    private TextView mNavDrawerArtist;

    private CursorLoaderManager mCursorLoaderManager;

    private ActionBarDrawerToggle mDrawerToggle;

    private static int CURRENT_CONTENT = 0;
    private static final int SONGS_CONTENT = 0;
    private static final int ARTISTS_CONTENT = 1;
    private static final int ALBUMS_CONTENT = 2;
    private static final int TAGS_CONTENT = 3;

    private static final int ALPHABETICAL_SORT = 0;
    private static final int RECENTLY_ADDED_SORT = 1;
    private static final int MOST_PLAYED_SORT = 2;

    private static int CURRENT_SONGS_SORT = ALPHABETICAL_SORT;
    private static int CURRENT_ARTISTS_SORT = ALPHABETICAL_SORT;
    private static int CURRENT_ALBUMS_SORT = ALPHABETICAL_SORT;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private boolean mInActionMode = false;

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
                case MediaPlaybackService.TAGS_UPDATED:
                    mTagsAdapter.setData(TagUtils.getCachedTags());
                    break;
            }
        }
    };

    private Context mContext;
    private Activity mActivity;

    private boolean mPendingIntroAnimation;

    private long mPosOverride = -1;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mActivity = this;

        setupViews();

        if (savedInstanceState == null) {
            mPendingIntroAnimation = false;
            // change to resources later
            // mControlContainer.setTranslationY((int) (Constants.CONTROL_CONTAINER_OFFSET * Resources.getSystem().getDisplayMetrics().density));
            // int actionbarSize = (int) (Constants.ACTION_BAR_OFFSET * Resources.getSystem().getDisplayMetrics().density);
            // int contentOffset = (int) (Constants.CONTENT_OFFSET * Resources.getSystem().getDisplayMetrics().density);

            // mExpandableHeaderView.setAlpha(0f);
            // mRecyclerView.setAlpha(0f);
            // mRecyclerView.setTranslationY(contentOffset);

            // mToolbar.setTranslationY(-actionbarSize);
            // mNavigationSpinner.setTranslationY(-actionbarSize);
        } else {
            mPendingIntroAnimation = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPermissionGranted) {
            setupInteractions();

            if (mServiceConnected) {
                if (MusicUtils.isMusicLoaded() || MusicUtils.isPlaying()) {
                    setupCollapsedControl();
                }
            }

            mTagsAdapter.setData(TagUtils.getCachedTags());
            mTagsAdapter.notifyDataSetChanged();

            final IntentFilter filter = new IntentFilter();
            filter.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
            filter.addAction(MediaPlaybackService.META_CHANGED);
            filter.addAction(MediaPlaybackService.SHUFFLE_CHANGED);
            filter.addAction(MediaPlaybackService.REPEAT_CHANGED);
            filter.addAction(MediaPlaybackService.QUEUE_CHANGED);
            filter.addAction(MediaPlaybackService.TAGS_UPDATED);

            filter.addCategory(MediaPlaybackService.CATEGORY);
            registerReceiver(mServiceReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mInActionMode) {
            resetOptions();
            hideSecondaryActionMode();
        }
        unregisterReceiverSafe(mServiceReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // if (mPendingIntroAnimation) {
        //     mPendingIntroAnimation = false;
        //     startIntroAnimation();
        // }

        return true;
    }

    private void startIntroAnimation() {
        mToolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        mNavigationSpinner.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);

        // mExpandableHeaderView.animate()
        //             .alpha(1f)
        //             .setDuration(ANIM_DURATION_TOOLBAR)
        //             .setStartDelay(300);
        // mRecyclerView.animate()
        //             .alpha(1f)
        //             .translationY(0)
        //             .setDuration(ANIM_DURATION_TOOLBAR)
        //             .setInterpolator(new DecelerateInterpolator())
        //             .setStartDelay(400)
        //             .setListener(new AnimatorListenerAdapter() {
        //                 @Override
        //                 public void onAnimationEnd(Animator animation) {
        //                     startContentAnimation();
        //                 }
        //             })
        //             .start();
    }

    private void startContentAnimation() {
        mControlContainer.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(400)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    private void setupViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mContentContainer = (RelativeLayout) findViewById(R.id.content_container);

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);

        mNoPermissionContainer = (RelativeLayout) findViewById(R.id.permission_layout);
        mGrantPermissionButton = (Button) findViewById(R.id.grant_permission_button);
        mGrantPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPermissionDialog(mActivity);
            }
        });

        mSortHeader = (RelativeLayout) findViewById(R.id.sort_header);
        mSortDesc = (TextView) findViewById(R.id.sort_desc);
        mMostPlayedSortButton = (FrameLayout) findViewById(R.id.most_played);
        mRecentlyAddedSortButton = (FrameLayout) findViewById(R.id.recently_added);
        mAlphabeticalSortButton = (FrameLayout) findViewById(R.id.alphabetical);
        mMostPlayedSortIcon = (ImageView) findViewById(R.id.sort_most_played);
        mRecentlyAddedSortIcon = (ImageView) findViewById(R.id.sort_recent);
        mAlphabeticalSortIcon = (ImageView) findViewById(R.id.sort_alpha);

        mSearchFab = (FloatingActionButton) findViewById(R.id.fab);

        mNavigationDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationSpinner = (RelativeLayout) findViewById(R.id.nav_spinner);
        mNavigationKey = (TextView) findViewById(R.id.selected_view_key);

        mSecondaryControl = (RelativeLayout) findViewById(R.id.secondary_control);

        mAmLayout = (LinearLayout) findViewById(R.id.am_layout);
        mAmCount = (TextView) findViewById(R.id.am_count);
        mAmTag = (ImageView) findViewById(R.id.am_tag);
        mAmDelete = (ImageView) findViewById(R.id.am_delete);
        mEdit = (TextView) findViewById(R.id.edit);
        mCloseActionMode = (ImageView) findViewById(R.id.close_action_mode);

        mRecyclerViewsContainer = (RelativeLayout) findViewById(R.id.recycler_views_container);
        mSongsRecyclerView = (RecyclerView) findViewById(R.id.songs_recycler_view);
        mAlbumsRecyclerView = (RecyclerView) findViewById(R.id.albums_recycler_view);
        mArtistsRecyclerView = (RecyclerView) findViewById(R.id.artists_recycler_view);
        mTagsRecyclerView = (RecyclerView) findViewById(R.id.tags_recycler_view);

        ContentLayoutManager songsLayoutManager = new ContentLayoutManager(getApplicationContext());
        ContentLayoutManager albumsLayoutManager = new ContentLayoutManager(getApplicationContext());
        ContentLayoutManager artistsLayoutManager = new ContentLayoutManager(getApplicationContext());
        ContentLayoutManager tagsLayoutManager = new ContentLayoutManager(getApplicationContext());
        mSongsRecyclerView.setLayoutManager(songsLayoutManager);
        mArtistsRecyclerView.setLayoutManager(artistsLayoutManager);
        mAlbumsRecyclerView.setLayoutManager(albumsLayoutManager);
        mTagsRecyclerView.setLayoutManager(tagsLayoutManager);

        mControlContainer = (RelativeLayout) findViewById(R.id.control_container);
        mCollapsedControlLayout = (RelativeLayout) findViewById(R.id.collapsed_control_layout);
        mCollapsedArtwork = (ImageView) findViewById(R.id.artwork);
        mCollapsedTitle = (TextView) findViewById(R.id.title);
        mCollapsedArtist = (TextView) findViewById(R.id.artist);
        mCollapsedPlayPause = (ImageView) findViewById(R.id.play_pause);
        mCollapsedNext = (ImageView) findViewById(R.id.next);
        mCollapsedPrevious = (ImageView) findViewById(R.id.previous);
        mProgress = (ProgressBar) findViewById(R.id.song_progress);

        View navDrawerHeader = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        mNavDrawerArtwork = (ImageView) navDrawerHeader.findViewById(R.id.artwork);
        mNavDrawerTitle = (TextView) navDrawerHeader.findViewById(R.id.title);
        mNavDrawerArtist = (TextView) navDrawerHeader.findViewById(R.id.artist);

        if (mSongsAdapter == null) {
            mSongsAdapter = new SongsAdapter(getApplicationContext(),
                                                this,
                                                R.layout.content_item,
                                                null,
                                                false,
                                                false);
        }

        if (mArtistsAdapter == null) {
            mArtistsAdapter = new ArtistsAdapter(getApplicationContext(),
                                                this,
                                                R.layout.content_item,
                                                null,
                                                false,
                                                false);
        }

        if (mTagsAdapter == null) {
            mTagsAdapter = new TagsAdapter(getApplicationContext());
            mTagsAdapter.setData(TagUtils.getCachedTags());
        }

        if (mAlbumsAdapter == null) {
            mAlbumsAdapter = new AlbumsAdapter(getApplicationContext(),
                                                this,
                                                R.layout.content_item,
                                                null);
        }

        mSongsRecyclerView.setAdapter(mSongsAdapter);
        mAlbumsRecyclerView.setAdapter(mAlbumsAdapter);
        mArtistsRecyclerView.setAdapter(mArtistsAdapter);
        mTagsRecyclerView.setAdapter(mTagsAdapter);
    }

    private void setupInteractions() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = new Bundle();
        mCursorLoaderManager = new CursorLoaderManager(getApplicationContext(), this);
        mReScanHandler.sendEmptyMessage(0);

        mSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mNavigationDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavigationDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mSongsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mArtistsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mAlbumsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        mTagsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mViewSelectionList = new ListPopupWindow(getApplicationContext());
        mViewSelectionList.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
        mViewSelectionListAdapter = new ViewSelectionListAdapter(mContext,
                                                                R.layout.song_menu_list_item,
                                                                getResources().getStringArray(R.array.nav_values));
        mViewSelectionList.setAdapter(mViewSelectionListAdapter);
        mViewSelectionList.setOnItemClickListener(this);
        mViewSelectionList.setWidth((int) getResources().getDimension(R.dimen.view_selection_list_width));
        mViewSelectionList.setHeight((int) getResources().getDimension(R.dimen.view_selection_list_height));
        mViewSelectionList.setModal(true);
        mViewSelectionList.setAnchorView(mNavigationSpinner);
        // H . A . C . K . ! . ! . !
        mViewSelectionList.setVerticalOffset(-96);
        mNavigationSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewSelectionList.show();
            }
        });

        setViewSelection(CURRENT_CONTENT);

        mMostPlayedSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortOption(MOST_PLAYED_SORT);
            }
        });

        mRecentlyAddedSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortOption(RECENTLY_ADDED_SORT);
            }
        });

        mAlphabeticalSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSortOption(ALPHABETICAL_SORT);
            }
        });

        mCloseActionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetOptions();
                setSecondaryActionMode();
            }
        });

        mAmTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURRENT_CONTENT == SONGS_CONTENT && mSongsAdapter != null) {
                    mSongsAdapter.tagSongs();
                }
            }
        });

        mAmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURRENT_CONTENT == SONGS_CONTENT && mSongsAdapter != null) {
                    mSongsAdapter.deleteSongs();
                } else if (CURRENT_CONTENT == ALBUMS_CONTENT && mAlbumsAdapter != null) {
                    mAlbumsAdapter.deleteAlbums();
                }
            }
        });

        if (MusicUtils.isMusicLoaded()) {
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setSortOption(int position) {
        switch (position) {
            case ALPHABETICAL_SORT:
                mSortDesc.setText(R.string.sort_alpha);
                mAlphabeticalSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_selected));
                mRecentlyAddedSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_unselected));
                mMostPlayedSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_unselected));
                mAlphabeticalSortIcon.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.SRC_IN);
                mRecentlyAddedSortIcon.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.sort_icon_color),
                    PorterDuff.Mode.SRC_IN);
                mMostPlayedSortIcon.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.sort_icon_color),
                    PorterDuff.Mode.SRC_IN);
                switch (CURRENT_CONTENT) {
                    case SONGS_CONTENT:
                        if (CURRENT_SONGS_SORT != ALPHABETICAL_SORT) {
                            CURRENT_SONGS_SORT = ALPHABETICAL_SORT;
                            getSupportLoaderManager().initLoader(CursorType.SONG_CURSOR, new Bundle(), mCursorLoaderManager).forceLoad();
                        }
                        break;
                    case ARTISTS_CONTENT:
                        if (CURRENT_ARTISTS_SORT != ALPHABETICAL_SORT) {
                            CURRENT_ARTISTS_SORT = ALPHABETICAL_SORT;
                            getSupportLoaderManager().initLoader(CursorType.ARTIST_CURSOR, new Bundle(), mCursorLoaderManager).forceLoad();
                        }
                        break;
                    case ALBUMS_CONTENT:
                        if (CURRENT_ALBUMS_SORT != ALPHABETICAL_SORT) {
                            CURRENT_ALBUMS_SORT = ALPHABETICAL_SORT;
                            getSupportLoaderManager().initLoader(CursorType.ALBUM_CURSOR, new Bundle(), mCursorLoaderManager).forceLoad();
                        }
                        break;
                }
                break;
            case RECENTLY_ADDED_SORT:
                mSortDesc.setText(R.string.sort_recent);
                mAlphabeticalSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_unselected));
                mRecentlyAddedSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_selected));
                mMostPlayedSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_unselected));
                mAlphabeticalSortIcon.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.sort_icon_color),
                    PorterDuff.Mode.SRC_IN);
                mRecentlyAddedSortIcon.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.SRC_IN);
                mMostPlayedSortIcon.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.sort_icon_color),
                    PorterDuff.Mode.SRC_IN);
                switch (CURRENT_CONTENT) {
                    case SONGS_CONTENT:
                        CURRENT_SONGS_SORT = RECENTLY_ADDED_SORT;
                        mSongsAdapter.swapCursor(MusicUtils.getAllTracksRecentlyAddedCursor(mSongsAdapter.getQueryHandler(), null, false), MediaStore.Audio.Media._ID, true);
                        mSongsRecyclerView.setAdapter(mSongsAdapter);
                        break;
                    case ARTISTS_CONTENT:
                        CURRENT_ARTISTS_SORT = RECENTLY_ADDED_SORT;
                        mArtistsAdapter.swapCursor(MusicUtils.getAllArtistsRecentlyAddedCursor(mArtistsAdapter.getQueryHandler(), null, false), MediaStore.Audio.Media.ARTIST_ID, true);
                        mArtistsRecyclerView.setAdapter(mArtistsAdapter);
                        break;
                    case ALBUMS_CONTENT:
                        CURRENT_ALBUMS_SORT = RECENTLY_ADDED_SORT;
                        mAlbumsAdapter.swapCursor(MusicUtils.getAllAlbumsRecentlyAddedCursor(mAlbumsAdapter.getQueryHandler(), null, false), MediaStore.Audio.Media.ALBUM_ID, true);
                        mAlbumsRecyclerView.setAdapter(mAlbumsAdapter);
                        break;
                }
                break;
            case MOST_PLAYED_SORT:
                mSortDesc.setText(R.string.sort_most_played);
                mAlphabeticalSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_unselected));
                mRecentlyAddedSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_unselected));
                mMostPlayedSortButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sort_background_selected));
                mAlphabeticalSortIcon.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.sort_icon_color),
                    PorterDuff.Mode.SRC_IN);
                mRecentlyAddedSortIcon.setColorFilter(
                    ContextCompat.getColor(mContext, R.color.sort_icon_color),
                    PorterDuff.Mode.SRC_IN);
                mMostPlayedSortIcon.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.SRC_IN);
                switch (CURRENT_CONTENT) {
                    case SONGS_CONTENT:
                        CURRENT_SONGS_SORT = MOST_PLAYED_SORT;
                        mSongsAdapter.swapCursor(MusicUtils.getAllTracksMostPlayedCursor(mSongsAdapter.getQueryHandler(), null, false), MediaStore.Audio.Media._ID, true);
                        mSongsRecyclerView.setAdapter(mSongsAdapter);
                        break;
                    case ARTISTS_CONTENT:
                        CURRENT_ARTISTS_SORT = MOST_PLAYED_SORT;
                        mArtistsAdapter.swapCursor(MusicUtils.getAllArtistsMostPlayedCursor(mArtistsAdapter.getQueryHandler(), null, false), MediaStore.Audio.Media._ID, true);
                        mSongsRecyclerView.setAdapter(mArtistsAdapter);
                        break;
                    case ALBUMS_CONTENT:
                        CURRENT_ALBUMS_SORT = MOST_PLAYED_SORT;
                        mAlbumsAdapter.swapCursor(MusicUtils.getAllAlbumsMostPlayedCursor(mAlbumsAdapter.getQueryHandler(), null, false), MediaStore.Audio.Media._ID, true);
                        mAlbumsRecyclerView.setAdapter(mAlbumsAdapter);
                        break;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mInActionMode) {
                resetOptions();
                hideSecondaryActionMode();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_songs) {
            setViewSelection(0);
        } else if (id == R.id.nav_artists) {
            setViewSelection(1);
        } else if (id == R.id.nav_albums) {
            setViewSelection(2);
        } else if (id == R.id.nav_tags) {
//            Intent intent = new Intent();
//            intent.setAction("android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL");
//            if((intent.resolveActivity(getPackageManager()) != null)) {
//                startActivityForResult(intent, 100);
//                // REQUEST_EQ is an int of your choosing
//            } else {
//                // No equalizer found :(
//            }
            setViewSelection(3);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiverSafe(mScanListener);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mServiceConnected = true;
        IntentFilter f = new IntentFilter();
        f.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

        f.addDataScheme("file");
        registerReceiver(mScanListener, f);
        if (MusicUtils.isMusicLoaded() || MusicUtils.isPlaying()) {
            setupCollapsedControl();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mServiceConnected = false;
        unregisterReceiverSafe(mScanListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        mViewSelectionList.dismiss();
        setViewSelection(position);
    }

    @Override
    protected void showNoPermissionView() {
        mContentContainer.setVisibility(View.GONE);
        mNoPermissionContainer.setVisibility(View.VISIBLE);
        mNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mSearchFab.setVisibility(View.GONE);
        mNavigationSpinner.setVisibility(View.GONE);
    }

    @Override
    protected void loadViews() {
        mContentContainer.setVisibility(View.VISIBLE);
        mNoPermissionContainer.setVisibility(View.GONE);
        mNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mSearchFab.setVisibility(View.VISIBLE);
        mNavigationSpinner.setVisibility(View.VISIBLE);
        setupInteractions();
    }

    private void setViewSelection(int position) {
        switch (position) {
            case 0:
                mNavigationKey.setText(R.string.tracks_title);
                mSecondaryControl.setVisibility(View.VISIBLE);
                mSongsRecyclerView.setVisibility(View.VISIBLE);
                mAlbumsRecyclerView.setVisibility(View.GONE);
                mArtistsRecyclerView.setVisibility(View.GONE);
                mTagsRecyclerView.setVisibility(View.GONE);
                CURRENT_CONTENT = SONGS_CONTENT;
                mSortHeader.setVisibility(View.VISIBLE);
                setSortOption(CURRENT_SONGS_SORT);
                break;

            case 1:
                mNavigationKey.setText(R.string.artists_title);
                mSecondaryControl.setVisibility(View.VISIBLE);
                mSongsRecyclerView.setVisibility(View.GONE);
                mAlbumsRecyclerView.setVisibility(View.GONE);
                mArtistsRecyclerView.setVisibility(View.VISIBLE);
                mTagsRecyclerView.setVisibility(View.GONE);
                CURRENT_CONTENT = ARTISTS_CONTENT;
                mSortHeader.setVisibility(View.VISIBLE);
                setSortOption(CURRENT_ARTISTS_SORT);
                break;

            case 2:
                mNavigationKey.setText(R.string.albums_title);
                mSecondaryControl.setVisibility(View.VISIBLE);
                mSongsRecyclerView.setVisibility(View.GONE);
                mAlbumsRecyclerView.setVisibility(View.VISIBLE);
                mArtistsRecyclerView.setVisibility(View.GONE);
                mTagsRecyclerView.setVisibility(View.GONE);
                CURRENT_CONTENT = ALBUMS_CONTENT;
                mSortHeader.setVisibility(View.VISIBLE);
                setSortOption(CURRENT_ALBUMS_SORT);
                break;

            case 3:
                mNavigationKey.setText(R.string.tags);
                mSecondaryControl.setVisibility(View.GONE);
                mSongsRecyclerView.setVisibility(View.GONE);
                mAlbumsRecyclerView.setVisibility(View.GONE);
                mArtistsRecyclerView.setVisibility(View.GONE);
                mTagsRecyclerView.setVisibility(View.VISIBLE);
                mSortHeader.setVisibility(View.GONE);
                getSupportLoaderManager().initLoader(CursorType.TAG_CURSOR, null, mTagsCallbacks).forceLoad();
                CURRENT_CONTENT = TAGS_CONTENT;
                break;

            default:
                mNavigationKey.setText(R.string.tracks_title);
                mSecondaryControl.setVisibility(View.VISIBLE);
                mSongsRecyclerView.setVisibility(View.VISIBLE);
                mAlbumsRecyclerView.setVisibility(View.GONE);
                mArtistsRecyclerView.setVisibility(View.GONE);
                mTagsRecyclerView.setVisibility(View.GONE);
                mSortHeader.setVisibility(View.VISIBLE);
                CURRENT_CONTENT = SONGS_CONTENT;
                setSortOption(CURRENT_SONGS_SORT);
                break;
        }
    }

    private BroadcastReceiver mScanListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mReScanHandler.sendEmptyMessage(0);
        }
    };

    private Handler mReScanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mCursorLoaderManager != null) {
                if (CURRENT_SONGS_SORT == ALPHABETICAL_SORT) {
                    getSupportLoaderManager().initLoader(CursorType.SONG_CURSOR, new Bundle(), mCursorLoaderManager);
                }

                if (CURRENT_ARTISTS_SORT == ALPHABETICAL_SORT) {
                    getSupportLoaderManager().initLoader(CursorType.ARTIST_CURSOR, new Bundle(), mCursorLoaderManager);
                }

                if (CURRENT_ALBUMS_SORT == ALPHABETICAL_SORT) {
                    getSupportLoaderManager().initLoader(CursorType.ALBUM_CURSOR, new Bundle(), mCursorLoaderManager);
                }
            }
        }
    };

    private void showSearchFab() {
        mSearchFab.clearAnimation();
        mSearchFab.animate().translationY(mControlContainer.getHeight());
    }

    private void hideSearchFab() {
        mSearchFab.clearAnimation();
        mSearchFab.animate().translationY(0);
    }

    public void setupCollapsedControl() {
        if (MusicUtils.isMusicLoaded()) {
            if (mSongsAdapter != null) {
                mSongsAdapter.setPlaying(MusicUtils.isPlaying());
                mSongsAdapter.setCurrentPlayingId(MusicUtils.getCurrentAudioId());
                mSongsAdapter.notifyDataSetChanged();
            }

            mCollapsedControlLayout.setVisibility(View.VISIBLE);
            long albumId = MusicUtils.getCurrentAlbumId();
            String title = MusicUtils.getCurrentTrackName();
            String artist = MusicUtils.getCurrentArtistName();
            mPicasso.load(ContentUris.withAppendedId(sArtworkUri, albumId))
                    .resize(216, 216)
                    .into(mCollapsedArtwork);

            mCollapsedTitle.setText(title);
            mCollapsedArtist.setText(artist);

            mProgress.setMax((int) MusicUtils.duration());
            setProgress();
            mProgress.postDelayed(mUpdateProgress, 10);

            if (MusicUtils.isPlaying()) {
                mCollapsedPlayPause.setImageResource(R.drawable.ic_pause);
            } else {
                mCollapsedPlayPause.setImageResource(R.drawable.ic_play);
            }

            setupNavDrawer(title, artist, albumId);
        }
    }

    private void setupNavDrawer(String title, String artist, long albumId) {
        mPicasso.load(ContentUris.withAppendedId(sArtworkUri, albumId)).into(mNavDrawerArtwork);
        mNavDrawerTitle.setText(title);
        mNavDrawerArtist.setText(artist);
    }

    @Override
    public void onCursorLoadingFinished(Cursor cursor, int cursorType) {
        switch(cursorType) {
            case CursorType.SONG_CURSOR :
                if (CURRENT_SONGS_SORT == ALPHABETICAL_SORT) {
                    mSongsAdapter.swapCursor(cursor, MediaStore.Audio.Media._ID, true);
                    mSongsRecyclerView.setAdapter(mSongsAdapter);
                }

                if (mSongsAdapter.getItemCount() == 0) {
                    mCollapsedControlLayout.setVisibility(View.GONE);
                }
                break ;
            case CursorType.ARTIST_CURSOR :
                if (CURRENT_ARTISTS_SORT == ALPHABETICAL_SORT) {
                    mArtistsAdapter.swapCursor(cursor, MediaStore.Audio.Media._ID, true);
                    mArtistsRecyclerView.setAdapter(mArtistsAdapter);
                }
                break ;
            case CursorType.ALBUM_CURSOR :
                if (CURRENT_ALBUMS_SORT == ALPHABETICAL_SORT) {
                    mAlbumsAdapter.swapCursor(cursor,
                            MediaStore.Audio.Albums._ID, true);
                    mAlbumsRecyclerView.setAdapter(mAlbumsAdapter);
                }
                break ;
            default :
                break;
        }
    }

    @Override
    public void onCursorLoaderReset(int cursorType) {
        // switch(cursorType) {
        //     case CursorType.SONG_CURSOR:
        //         mSongsAdapter.swapCursor(null, MediaStore.Audio.Media._ID, true);
        //         break;
        //     case CursorType.ARTIST_CURSOR:
        //         mArtistsAdapter.swapCursor(null, MediaStore.Audio.Media._ID, true);
        //         break;
        //     case CursorType.ALBUM_CURSOR:
        //         mAlbumsAdapter.swapCursor(null, MediaStore.Audio.Media._ID, true);
        //         break;
        // }
    }

    public void updateAmCount(int count) {
        if (count == 0) {
            if (CURRENT_CONTENT == SONGS_CONTENT) {
                mSongsAdapter.resetOptions();
            } else if (CURRENT_CONTENT == ALBUMS_CONTENT) {
                mAlbumsAdapter.resetOptions();
            }

            hideSecondaryActionMode();
        } else {
            mAmCount.setText(String.valueOf(count));
        }
    }

    public void setSecondaryActionMode() {
        if (mInActionMode) {
            hideSecondaryActionMode();
        } else {
            showSecondaryActionMode();
        }
    }

    public void hideSecondaryActionMode() {
        mInActionMode = false;
        mAmLayout.setVisibility(View.GONE);
        mSortHeader.setVisibility(View.VISIBLE);
        mNavigationSpinner.setVisibility(View.VISIBLE);
        mNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mEdit.setVisibility(View.GONE);
        mCloseActionMode.setVisibility(View.GONE);
        resetOptions();
    }

    public void showSecondaryActionMode() {
        mInActionMode = true;
        mSortHeader.setVisibility(View.GONE);
        mNavigationSpinner.setVisibility(View.GONE);
        mNavigationDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mAmLayout.setVisibility(View.VISIBLE);
        mEdit.setVisibility(View.VISIBLE);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mCloseActionMode.setVisibility(View.VISIBLE);
        if (CURRENT_CONTENT == SONGS_CONTENT) {
            mAmTag.setVisibility(View.VISIBLE);
        } else {
            mAmTag.setVisibility(View.GONE);
        }
    }

    public void resetOptions() {
        if (CURRENT_CONTENT == SONGS_CONTENT) {
            mSongsAdapter.resetOptions();
        } else if (CURRENT_CONTENT == ALBUMS_CONTENT) {
            mAlbumsAdapter.resetOptions();
        }
    }

    public void showDeleteSnackBar(final long[] songIds) {
        Snackbar snackbar = Snackbar.make(mControlContainer, R.string.delete_snackbar_message, Snackbar.LENGTH_SHORT)
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
                            MusicUtils.deleteTracks(mContext, songIds);
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

    LoaderManager.LoaderCallbacks<ArrayList<String>> mTagsCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<String>>() {
        @Override
        public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
            return new TagsLoader(getApplicationContext(), MusicUtils.getAllSongPaths(getApplicationContext()));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
            mTagsAdapter.setData(data);
            mTagsRecyclerView.setAdapter(mTagsAdapter);
            mTagsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<String>> loader) {
        }
    };

    private void setProgress() {
        long trackPosition = mPosOverride < 0 ? MusicUtils.position() : mPosOverride;
        long mTrackLength = MusicUtils.duration();
        if ((trackPosition >= 0) && (mTrackLength > 0)) {
            mProgress.setProgress((int)trackPosition);
        } else {
            mProgress.setProgress(1000);
        }
    }
}
