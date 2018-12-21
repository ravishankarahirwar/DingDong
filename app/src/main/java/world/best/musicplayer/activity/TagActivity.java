package world.best.musicplayer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.adapters.SongsAdapter;
import world.best.musicplayer.cursorloaders.CursorLoaderManager;
import world.best.musicplayer.factories.MenuFactory;
import world.best.musicplayer.layoutmanagers.ContentLayoutManager;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.MusicUtils;
import world.best.musicplayer.utils.OnUnTagListener;
import world.best.musicplayer.utils.TagUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TagActivity extends BaseDetailActivity implements OnUnTagListener{

    private static final String TAG = TagActivity.class.getSimpleName();

    private int mTagType = -1;
    private String mTagName;

    private Context mContext;

    private long[] mSongIds;
    private boolean mInActionMode = false;

    private LinearLayout mAmLayout;
    private ImageView mCloseActionMode;
    private ImageView mAmBack;
    private TextView mAmCount;
    private TextView mEdit;
    private ImageView mAmUnTag;
    private TextView mCountAndDuration;
    private LinearLayout mHeader;
    private Cursor mCursor;
    private CursorLoaderManager mSongsCursorLoader;
    private static final int START_POSITION = 0;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_album);

        mActivity = this;
        mContext = getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTagName = bundle.getString(Constants.EXTRA_TAG_NAME, "Tag");
            mTagType = bundle.getInt(Constants.EXTRA_TAG_TYPE, -1);
        } else {
            Log.d(TAG, "Bundle Null");
        }

        if (mTagType == Constants.TAG_NORMAL) {
            mSongIds = bundle.getLongArray(Constants.EXTRA_SONGS_IDS);
        }
    }

    private void setupViews() {
        mAmLayout = (LinearLayout) findViewById(R.id.am_layout);
        mAmCount = (TextView) findViewById(R.id.am_count);
        mAmUnTag = (ImageView) findViewById(R.id.am_untag);

        mEdit = (TextView) findViewById(R.id.edit);
        mCloseActionMode = (ImageView) findViewById(R.id.close_action_mode);

        mCountAndDuration = (TextView) findViewById(R.id.song_count_time);
        mHeader = (LinearLayout) findViewById(R.id.header);
        mRecyclerView = (RecyclerView) findViewById(R.id.media_recycler_view);
    }

    private void setupInteractions() {
        mContentLayoutManager = new ContentLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mContentLayoutManager);

        // mSongsAdapter = new SongsAdapter(mContext, this, R.layout.content_item, null, false, false);
        // mRecyclerView.setAdapter(mSongsAdapter);
        getSupportActionBar().setTitle(mTagName);

        switch (mTagType) {
            case Constants.TAG_MOST_PLAYED :
                mSongsAdapter = new SongsAdapter(mContext,
                        mActivity,
                        R.layout.content_item,
                        null,
                        false,
                        false, MenuFactory.MenuFor.AUTO_TAG_DETAIL, mTagName, this);
                mCursor = MusicUtils.getSongsFromPaths(mContext, TagUtils.getMostPlayedPaths());
                break;
            case Constants.TAG_RECENTLY_ADDED :
                mSongsAdapter = new SongsAdapter(mContext,
                        mActivity,
                        R.layout.content_item,
                        null,
                        false,
                        false, MenuFactory.MenuFor.AUTO_TAG_DETAIL, mTagName, this);
                mCursor = MusicUtils.recentlyAddedSongs(mContext);
                break;
            case Constants.TAG_NORMAL :
                mSongsAdapter = new SongsAdapter(mContext,
                        mActivity,
                        R.layout.content_item,
                        null,
                        false,
                        false, MenuFactory.MenuFor.TAG_DETAIL, mTagName, this);
                mCursor = MusicUtils.getCursorForSongIds(mSongsAdapter.getQueryHandler(), mSongIds, null, false);

                break;
            default :
                break;
        }

        // same for both the recently added and the most played
        if (mCursor != null && mSongsAdapter != null) {
            mSongsAdapter.swapCursor(mCursor, MediaStore.Audio.Media._ID, true);
            mRecyclerView.setAdapter(mSongsAdapter);
            updateHeader();
        }

        /**
         * As soon as user click on Header view we start playing first song of
         * this album
         */
        mHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCursor != null) {
                    MusicUtils.playAll(mContext, mCursor, START_POSITION);
                }
            }
        });

        mCloseActionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSecondaryActionMode();
                }
            });

        mAmUnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSongsAdapter.unTagSongs();
                setSecondaryActionMode();
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
        setupBaseViews();
        setupBaseInteractions();
        setupViews();
        setupInteractions();
    }

    @Override
    public void onCursorLoaderReset(int cursorType) {
         mSongsAdapter.swapCursor(null, "", false);
    }

    /**
     * This method is call when user remove single tag from song in SongAdapter
     * @param songId : Id of the song, remove tag
     * Logic : we remove this song id from SongIds(mIds) belong to particular tag
     * then get the cursor for ids and swap in SongsAdapter
     */
    @Override
    public void onUnTagSong(long songId) {
        showTagRemoveToast();
        long[] songIds = mSongIds.clone();
        mSongIds = new long[mSongIds.length - 1];
        boolean removedTagPassed = false;
        for (int i = 0; i < songIds.length; i++) {
            if (songIds[i] != songId) { //compare and remove id
                if (removedTagPassed) {
                    mSongIds[i - 1] = songIds[i];
                } else {
                    mSongIds[i] = songIds[i];
                }
            } else {
                removedTagPassed = true;
            }
        }

        if (mSongIds.length > 0) {
            mCursor = MusicUtils.getCursorForSongIds(
                    mSongsAdapter.getQueryHandler(), mSongIds, null, false);
            if (mCursor != null && mCursor.getCount() > 0) {
                mSongsAdapter.swapCursor(mCursor,
                        MediaStore.Audio.Media._ID, true);
                mRecyclerView.setAdapter(mSongsAdapter);
                updateHeader();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    /**
     * From Action mode user click on unTag button then multiple/Single song removed
     * from tag blow method will call
     *
     * @param songIds
     */
    @Override
    public void onUnTagSongs(List<Long> songIds) {
        showTagRemoveToast();
        ArrayList<Long> songIdsList = new ArrayList<Long>(mSongIds.length);
        for (long n : mSongIds) {
            songIdsList.add(n);
        }

        for (Long id : songIds) {
            songIdsList.remove(id);
          }

        mSongIds = new long[songIdsList.size()];
        for (int i = 0; i < songIdsList.size(); i++) {
            mSongIds[i] = songIdsList.get(i);
        }

        if (mSongIds.length > 0) {
            mCursor = MusicUtils.getCursorForSongIds(
                    mSongsAdapter.getQueryHandler(), mSongIds, null, false);
            if (mCursor != null && mCursor.getCount() > 0) {
                mSongsAdapter.swapCursor(mCursor,
                        MediaStore.Audio.Media._ID, true);
                mRecyclerView.setAdapter(mSongsAdapter);
                updateHeader();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void updateHeader() {
        String headerText = getHeaderText(mContext, mCursor);
        mCountAndDuration.setText(headerText);
    }

    public void showTagRemoveToast() {
        Toast.makeText(mContext, R.string.tags_removed_toast, Toast.LENGTH_SHORT).show();
    }

    //*********The below methods call when user long press in Tag Detail page on song**********
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
        mHeader.setVisibility(View.VISIBLE);
        mEdit.setVisibility(View.GONE);
        mCloseActionMode.setVisibility(View.GONE);
        invalidateOptionsMenu();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        resetOptions();
    }

    public void showSecondaryActionMode() {
        mInActionMode = true;
        mHeader.setVisibility(View.GONE);
        mAmLayout.setVisibility(View.VISIBLE);
        mEdit.setVisibility(View.VISIBLE);
        mCloseActionMode.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void resetOptions() {
            mInActionMode = false;
            mSongsAdapter.resetOptions();
    }

    public void updateAmCount(int count) {
        if (count == 0) {
                mSongsAdapter.resetOptions();
            hideSecondaryActionMode();
        } else {
            mAmCount.setText("" + count);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mTagType == Constants.TAG_NORMAL && !mInActionMode) {
            getMenuInflater().inflate(R.menu.menu_tag, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.tag_delete) {
            showConfirmDialog();
            return true;
        } else if (id == R.id.tag_rename) {
            showRenameDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmDialog() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(mActivity);
        Resources res = mContext.getResources();
        confirmDialog.setMessage(R.string.delete_confirm_tag);
        confirmDialog.setPositiveButton(res.getString(R.string.delete_item).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<Long, String> pathList = MusicUtils.getAllSongPaths(mContext);
                if (pathList != null && pathList.size() > 0) {
                    List<String> list = new ArrayList<>(pathList.values());
                    TagUtils.deleteTag(mContext, list, mTagName);
                }

                // This will change to a Snackbar later so, don't need to add to strings
                Toast.makeText(mContext, "Tag deleted", Toast.LENGTH_SHORT).show();
                mActivity.finish();
            }
        });
        confirmDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        confirmDialog.show();
    }

    private void showRenameDialog() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setMessage(R.string.tag_rename);
        View view = getLayoutInflater().inflate(R.layout.dialog_tag_rename, null);
        final EditText et = (EditText) view.findViewById(R.id.tag_name);
        et.setText(mTagName);
        et.setSelection(et.getText().length());
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        confirmDialog.setView(view);
        confirmDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTagName = et.getText().toString();
                if (!mTagName.equals(newTagName) && !newTagName.isEmpty()) {
                    Map<Long, String> pathList = MusicUtils.getAllSongPaths(mContext);
                    if (pathList != null && pathList.size() > 0) {
                        List<String> list = new ArrayList<>(pathList.values());
                        TagUtils.renameTag(mContext, list, mTagName, newTagName);
                    }

                    // This will change to a Snackbar later so, don't need to add to strings
                    Toast.makeText(mContext, "Tag renamed", Toast.LENGTH_SHORT).show();
                    mActivity.finish();
                }
            }
        });
        confirmDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmDialog.show();
    }
}
