package world.best.musicplayer.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.animation.Animator;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import world.best.musicplayer.activity.ArtistActivity;
import world.best.musicplayer.activity.AlbumActivity;
import world.best.musicplayer.activity.BaseDetailActivity;
import world.best.musicplayer.activity.MainActivity;
import world.best.musicplayer.activity.SearchActivity;
import world.best.musicplayer.activity.TagActivity;;
import world.best.musicplayer.dialog.AddTagDialog;
import world.best.musicplayer.utils.MusicUtils;
import world.best.musicplayer.utils.OnUnTagListener;
import world.best.musicplayer.utils.TagUtils;
import world.best.musicplayer.factories.MenuFactory;
import world.best.musicplayer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongsAdapter extends BaseCursorAdapter<RecyclerView.ViewHolder> implements OnItemClickListener {

    private ListPopupWindow mMenuList;
    private final String TAG = "SongsAdapter";
    boolean mIsNowPlaying;
    boolean mDisableNowPlayingIndicator;

    private int mTrackIdx;
    private int mTrackTitleIdx;
    private int mArtistIdx;
    private int mArtistNameIdx;
    private int mAlbumIdIdx;
    private int mAlbumNameIdx;
    private int mDurationIdx;
    private int mDataIdx;
    private int mSizeIdx;
    private int mMimeTypeIdx;
    private int mMenuFor;

    private String mTitleOfSong;
    private String mAudioFilePath;
    private String mTagName;
    private long mSongId;
    private long mDuration;
    private long mSize;
    private String mMimeType;
    private long[] songIdsForDelete = new long[20];

    public RelativeLayout mViewHolderLayout;

    private ImageView mTagView;
    private boolean mTagsAdded;
    private boolean mTagsUpdated;

    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    private final String mUnknownAlbum;

    private boolean mInActionMode = false;
    private boolean mIsShowMenu = true;
    private Map<String, Long> mCheckedItems = new HashMap<>();
    private OnUnTagListener mOnUnTagListener;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private long mCurrentlyPlayingId = -2;
    private boolean mPlaying = false;
    private SparseBooleanArray mSelectedItems;

    public SongsAdapter (Context context, Activity activity, int layout, Cursor cursor,
            boolean isnowplaying, boolean disablenowplayingindicator, int menuFor, String tagName, OnUnTagListener onUnTagListener) {
        super(context, layout, cursor);
        mMenuFor = menuFor;
        mTagName = tagName;
        mOnUnTagListener = onUnTagListener;
        mActivity = activity;
        findColumns(cursor);

        mIsNowPlaying = isnowplaying;
        mDisableNowPlayingIndicator = disablenowplayingindicator;

        mUnknownArtist = context.getString(R.string.unknown_artist_name);
        mUnknownAlbum = context.getString(R.string.unknown_album_name);
        mSelectedItems = new SparseBooleanArray();

        init();
    }

    public SongsAdapter (Context context, Activity activity, int layout, Cursor cursor,
            boolean isnowplaying, boolean disablenowplayingindicator) {
        this(context, activity, layout, cursor, isnowplaying, disablenowplayingindicator, MenuFactory.MenuFor.SONG_LIST, null, null);
    }

    private void init() {
        MenuFactory menuFactory;

        switch (mMenuFor) {
            case MenuFactory.MenuFor.SONG_LIST :
                menuFactory = new MenuFactory(mContext, MenuFactory.MenuFor.SONG_LIST);
                break;
            case MenuFactory.MenuFor.TAG_DETAIL :
                menuFactory = new MenuFactory(mContext, MenuFactory.MenuFor.TAG_DETAIL);
                break;
            case MenuFactory.MenuFor.AUTO_TAG_DETAIL :
                menuFactory = new MenuFactory(mContext, MenuFactory.MenuFor.AUTO_TAG_DETAIL);
                break;
            default :
                menuFactory = new MenuFactory(mContext, MenuFactory.MenuFor.SONG_LIST);
                break;
        }

        Resources resources = mContext.getResources();
        mMenuList = new ListPopupWindow(mContext);
        mMenuList.setBackgroundDrawable(resources.getDrawable(R.color.colorPrimary));
        mMenuList.setAdapter(new SongMenuListAdapter(mContext, R.layout.song_menu_list_item, menuFactory.getMenus()));
        mMenuList.setOnItemClickListener(this);
        mMenuList.setWidth((int) resources.getDimension(R.dimen.menu_list_width));
        if (mMenuFor == MenuFactory.MenuFor.AUTO_TAG_DETAIL) {
            mMenuList.setHeight((int) resources.getDimension(R.dimen.tag_song_menu_list_height));
        } else {
            mMenuList.setHeight((int) resources.getDimension(R.dimen.song_menu_list_height));
        }
        mMenuList.setModal(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_footer, parent, false));
        }

        return new ContentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mLayout, parent, false));
    }

    @Override
    public int getItemViewType (int position) {
        if (isPositionFooter (position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder (final RecyclerView.ViewHolder baseHolder, final Cursor cursor, final int position) {
        if (baseHolder instanceof ContentViewHolder) {
            final ContentViewHolder holder = (ContentViewHolder) baseHolder;
            final long id = cursor.getLong(mTrackIdx);
            final String trackTitle = cursor.getString(mTrackTitleIdx);
            final String artistName = cursor.getString(mArtistNameIdx);
            final String albumName = cursor.getString(mAlbumNameIdx);
            final long albumArt = cursor.getLong(mAlbumIdIdx);
            final long duration = cursor.getLong(mDurationIdx);
            final long size = cursor.getLong(mSizeIdx);
            final String mimeType = cursor.getString(mMimeTypeIdx);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            final String filePath = cursor.getString(columnIndex);

            holder.layout.setActivated(mSelectedItems.get(position, false));
            holder.checked.setVisibility(mSelectedItems.get(position, false) ? View.VISIBLE : View.INVISIBLE);

            holder.title.setText(trackTitle);
            holder.artistAlbum.setText(artistName + " - " + albumName);
            mPicasso.load(ContentUris.withAppendedId(sArtworkUri, albumArt))
                    .placeholder(R.drawable.ic_default_art)
                    .error(R.drawable.ic_default_art)
                    .resize(120, 120)
                    .into(holder.artwork);

            if (id == mCurrentlyPlayingId) {
                holder.equalizer.setVisibility(View.VISIBLE);
                AnimationDrawable frameAnimation = (AnimationDrawable) holder.equalizer.getDrawable();
                // frameAnimation.setCallback(holder.equalizer);
                frameAnimation.setVisible(true, true);
                if (mPlaying) {
                    frameAnimation.start();
                } else {
                    if (frameAnimation.isRunning()) {
                        frameAnimation.stop();
                    }
                }
            } else {
                holder.equalizer.setVisibility(View.INVISIBLE);
            }

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mActivity instanceof MainActivity) {
                        if (!mInActionMode) {
                            showPlayingAnimation(holder.equalizer, id, position);
                        } else {
                            toggleSelection(position,filePath, id);
                        }
                    } else if (mActivity instanceof SearchActivity) {
                        MusicUtils.playAll(mActivity.getApplicationContext(), getCursor(), position);
                        ((SearchActivity) mActivity).setupPlayer(id, trackTitle, artistName);
                    } else if (mActivity instanceof TagActivity) {
                        if (!mInActionMode) {
                            showPlayingAnimation(holder.equalizer, id, position);
                        } else {
                            toggleSelection(position,filePath, id);
                        }

                    } else if (mActivity instanceof BaseDetailActivity) {
                        showPlayingAnimation(holder.equalizer, id, position);
                    }
                }
            });

            if (mActivity instanceof MainActivity || mActivity instanceof BaseDetailActivity) {
                holder.overFlowMenu.setVisibility(View.VISIBLE);
                holder.overFlowMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMenuList.setAnchorView(view);
                        mMenuList.show();
                        mAudioFilePath = filePath;
                        songIdsForDelete[0] = id;
                        mSongId = id;
                        mDuration = duration;
                        mTitleOfSong = trackTitle;
                        mSize = size;
                        mMimeType = mimeType;
                        mTagView = holder.taggedIndicator;
                    }
                });

                holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!mInActionMode) {
                            if (mActivity instanceof MainActivity) {
                                ((MainActivity) mActivity).setSecondaryActionMode();
                                mInActionMode = true;
                                toggleSelection(position,filePath, id);
                                //Autoboxing alert!!
                                ((MainActivity) mActivity).updateAmCount(getSelectedItemCount());
                                notifyDataSetChanged();
                            } else if (mActivity instanceof TagActivity && mMenuFor == MenuFactory.MenuFor.TAG_DETAIL) {
                                ((TagActivity) mActivity).setSecondaryActionMode();
                                mInActionMode = true;
                                toggleSelection(position,filePath, id);
                                notifyDataSetChanged();
//                                notifyDataSetChanged();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                if (mInActionMode) {
                    holder.overFlowMenu.setVisibility(View.GONE);
                } else {
                    holder.overFlowMenu.setVisibility(View.VISIBLE);
                }
            }
        } else if (baseHolder instanceof FooterViewHolder) {
            final FooterViewHolder holder = (FooterViewHolder) baseHolder;
            int numberOfSongs = mCursor.getCount();
            String songsKey = mContext.getResources().getQuantityString(R.plurals.songs_count, numberOfSongs, numberOfSongs);
            holder.footerText.setText(songsKey.toLowerCase());
        }
    }

    public void toggleSelection(int pos, String filePath, long id) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
            mCheckedItems.remove(filePath);
        }
        else {
            mSelectedItems.put(pos, true);
            mCheckedItems.put(filePath, id);
        }

        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).updateAmCount(getSelectedItemCount());
        } else if (mActivity instanceof TagActivity && mMenuFor == MenuFactory.MenuFor.TAG_DETAIL) {
            ((TagActivity) mActivity).updateAmCount(getSelectedItemCount());
        }

        notifyItemChanged(pos);
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    private void findColumns(Cursor cursor) {
        if (cursor != null) {
            mTrackIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            mTrackTitleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            mArtistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
            mArtistNameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            mAlbumIdIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            mAlbumNameIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            mDurationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            mDataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            mSizeIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            mMimeTypeIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
            try {
                mTrackIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            } catch (IllegalArgumentException ex) {
                mTrackIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            }
        }
    }

    @Override
    public Cursor swapCursor(Cursor cursor, String idField, boolean animate) {
        findColumns(cursor);
        return super.swapCursor(cursor, idField, animate);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        mMenuList.dismiss();
        switch (position) {
            case MenuFactory.MENU_ADD_TAG:
                List<String> filePaths = new ArrayList<>();
                filePaths.add(mAudioFilePath);
                tagSong(filePaths);
                break;
            case MenuFactory.MENU_SONG_DETAIL:
                View v = mActivity.getLayoutInflater().inflate(R.layout.dialog_details, null);
                TextView tvFormat = (TextView) v.findViewById(R.id.format_value);
                tvFormat.setText(" " + mMimeType);
                TextView tvDuration = (TextView) v.findViewById(R.id.duration_value);
                tvDuration.setText(" " + MusicUtils.humanReadableDuration(mDuration));
                TextView tvPath = (TextView) v.findViewById(R.id.path_value);
                tvPath.setText(" " + mAudioFilePath);
                TextView tvSize = (TextView) v.findViewById(R.id.size_value);
                tvSize.setText(" " + MusicUtils.humanReadableByteCount(mSize));
                AlertDialog dialog = new AlertDialog.Builder(mActivity, android.R.style.Theme_DeviceDefault_Dialog)
                        .setView(v)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setTitle(R.string.dialog_details)
                        .show();
                break;
            case MenuFactory.MENU_SONG_DELETE:
                if (mMenuFor == MenuFactory.MenuFor.SONG_LIST) {
                    if (mActivity instanceof MainActivity) {
                        ((MainActivity) mActivity).showDeleteSnackBar(new long[] {mSongId});
                    } else if (mActivity instanceof AlbumActivity || mActivity instanceof ArtistActivity) {
                        ((BaseDetailActivity) mActivity).showDeleteSnackBar(new long[] {mSongId});
                    }
                } else if (mMenuFor == MenuFactory.MenuFor.TAG_DETAIL) {
                    TagUtils.deleteTagForSong(mContext, mAudioFilePath, mTagName, mSongId);
                    Log.d("UnTag", " File path : " + mAudioFilePath + " Tag Name : " + mTagName + " SongId : " + mSongId);
                    mOnUnTagListener.onUnTagSong(mSongId);
                }
                break;
            case MenuFactory.MENU_SONG_SHARE:
                Uri uri = Uri.parse(mAudioFilePath);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                mActivity.startActivity(Intent.createChooser(share, "Share audio"));
                break;
            case MenuFactory.MENU_SONG_RINGTONE:
//                Intent intent = new Intent(Intent.ACTION_SEARCH);
//                intent.setPackage("com.google.android.youtube");
//                intent.putExtra("query", mTitleOfSong);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mActivity.startActivity(intent);
                youDesirePermissionCode(mActivity, mSongId);
                break;
        }
    }

    private void showPlayingAnimation(ImageView view, long id, int position) {
        mCurrentlyPlayingId = id;
        mPlaying = true;
        MusicUtils.playAll(mActivity.getApplicationContext(), getCursor(), position);
        view.setVisibility(View.VISIBLE);
        AnimationDrawable frameAnimation = (AnimationDrawable) view.getDrawable();
        // frameAnimation.setCallback(view);
        frameAnimation.setVisible(true, true);
        frameAnimation.start();
    }

    /**
     * @param id relevent song id
     * @param audioFilePath path of the file, for apply tag
     */
    public void tagSong(final List<String> filePaths) {
        AddTagDialog addTagsDialog = new AddTagDialog(mActivity, filePaths,
                new AddTagDialog.OnTagChangeListener() {
                    @Override
                    public void onTagDelete(String tagToDelete) {
                        TagUtils.deleteTagForSongs(mContext, filePaths, tagToDelete, mSongId);
                        notifyDataSetChanged();
                        mTagsUpdated = true;
                    }

                    @Override
                    public void onTagAdded(String tagName) {
                        mTagsAdded = TagUtils.addTagForSongs(mContext, filePaths, tagName, mSongId);
                        notifyDataSetChanged();
                        mTagsUpdated = true;
                    }
                });
        addTagsDialog.show();
        addTagsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mTagsAdded) {
                        animateTaggedItems();
                    }

                    if (mInActionMode) {
                        if (mTagsAdded || mTagsUpdated) {
                            showActionModeTagsToast(filePaths.size());
                        }
                        resetOptions();
                        if (mActivity instanceof MainActivity) {
                            ((MainActivity) mActivity).hideSecondaryActionMode();
                        } else if (mActivity instanceof TagActivity && mMenuFor == MenuFactory.MenuFor.TAG_DETAIL) {
                            ((TagActivity) mActivity).hideSecondaryActionMode();
                        }
                    } else {
                        if (mTagsAdded || mTagsUpdated) {
                            showTagsToast();
                        }
                    }
                }
            });
    }

    /**
     * This method is call when user click tag button in ActionMode
     */
    public void tagSongs() {
        if (!mCheckedItems.isEmpty()) {
            List<String> filePaths = new ArrayList<>(mCheckedItems.keySet());
            tagSong(filePaths);
        }
    }

    /**
     * This method is call when user click untag button in ActionMode
     */
    public void unTagSongs() {
        List<Long> songIds = null;
        if ((mCheckedItems != null) && (!mCheckedItems.isEmpty())) {
            songIds = new ArrayList<Long>(mCheckedItems.values());
             List<String> filePaths = new ArrayList<>(mCheckedItems.keySet());
             TagUtils.deleteTagForSongs(mContext, filePaths, mTagName, songIds);
        }
        if (mOnUnTagListener != null && songIds != null && (!songIds.isEmpty())) {
            mOnUnTagListener.onUnTagSongs(songIds);
        }
    }

    /**
     * This method is call when user click delete button in ActionMode
     */
    public void deleteSongs() {
        if (!mCheckedItems.isEmpty()) {
            List<Long> values = new ArrayList<Long>(mCheckedItems.values());
            long[] ids = new long[values.size()];
            for (int i = 0; i < values.size(); i++) {
                ids[i] = values.get(i);
            }
            mCheckedItems.clear();
            ((MainActivity) mActivity).updateAmCount(mCheckedItems.size());
            ((MainActivity) mActivity).showDeleteSnackBar(ids);
        }
    }

    public void resetOptions() {
        mInActionMode = false;
        mCheckedItems.clear();
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public void animateTaggedItems() {
        mTagsAdded = false;
        if (mTagView != null) {
            mTagView.setVisibility(View.VISIBLE);
            mTagView.animate().alpha(1f).setDuration(250).scaleX(1f).scaleY(1f).setStartDelay(350).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTagView.animate().alpha(1f).scaleX(0f).scaleY(0f).setDuration(350).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mTagView.setVisibility(View.GONE);
                                mTagView = null;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
        }
    }

    private void showTagsToast() {
        Toast.makeText(mContext.getApplicationContext(), R.string.tags_updated_toast, Toast.LENGTH_SHORT).show();
        mTagsAdded = false;
        mTagsUpdated = false;
    }

    private void showActionModeTagsToast(int count) {
        Toast.makeText(mContext.getApplicationContext(),
            String.format(mContext.getApplicationContext().getString(R.string.tags_updated_action_mode_toast), count),
            Toast.LENGTH_SHORT).show();
        mTagsAdded = false;
        mTagsUpdated = false;
    }

    public void setCurrentPlayingId(long id) {
        mCurrentlyPlayingId = id;
    }

    public long getCurrentPlayingId() {
        return mCurrentlyPlayingId;
    }

    public void setPlaying(boolean playing) {
        mPlaying = playing;
    }

    public static void youDesirePermissionCode(Activity context, long songId){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
            MusicUtils.setRingtone(context, songId);
        }  else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    }

}
