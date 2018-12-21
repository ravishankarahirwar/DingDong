package world.best.musicplayer.adapters;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import world.best.musicplayer.activity.AlbumActivity;
import world.best.musicplayer.activity.ArtistActivity;
import world.best.musicplayer.activity.MainActivity;
import world.best.musicplayer.activity.SearchActivity;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumsAdapter extends BaseCursorAdapter<RecyclerView.ViewHolder> {

    private int mAlbumIdx;
    private int mArtistIdx;
    private int mAlbumIdIdx;
    private int mNoOfSongs;

    private final String mUnknownAlbum;
    private final String mUnknownArtist;

    private boolean mInActionMode = false;
    private Map<Long, Boolean> mCheckedItems = new HashMap<>();
    private SparseBooleanArray mSelectedItems;

    private final Resources mResources;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public AlbumsAdapter (Context context, Activity activity, int layout, Cursor cursor) {
        super(context, layout, cursor);
        mActivity = activity;
        findColumns(cursor);

        mUnknownArtist = context.getString(R.string.unknown_artist_name);
        mUnknownAlbum = context.getString(R.string.unknown_album_name);
        mSelectedItems = new SparseBooleanArray();
        mResources = context.getResources();
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
    public void onBindViewHolder (final RecyclerView.ViewHolder baseHolder, Cursor cursor,final int position) {
        if (baseHolder instanceof ContentViewHolder) {
            final ContentViewHolder holder = (ContentViewHolder) baseHolder;
            holder.overFlowMenu.setVisibility(View.GONE);
            final String albumTitle = cursor.getString(mAlbumIdx);
            final String albumArtist = cursor.getString(mArtistIdx);
            final long albumId = cursor.getLong(mAlbumIdIdx);

            holder.layout.setActivated(mSelectedItems.get(position, false));
            holder.checked.setVisibility(mSelectedItems.get(position, false) ? View.VISIBLE : View.INVISIBLE);

            holder.title.setText(albumTitle);
            try {
                int numberOfSongs = Integer.parseInt(cursor.getString(mNoOfSongs));
                String songsKey = mResources.getQuantityString(R.plurals.songs_count, numberOfSongs, numberOfSongs);
                holder.artistAlbum.setText(songsKey + ", " + albumArtist);
            } catch (Exception e) {

            }

            mPicasso.load(ContentUris.withAppendedId(sArtworkUri, albumId))
                    .placeholder(R.drawable.ic_default_art)
                    .error(R.drawable.ic_default_art)
                    .resize(120, 120)
                    .into(holder.artwork);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mInActionMode) {
                        Intent intent = new Intent(mContext, AlbumActivity.class);
                        intent.putExtra(Constants.EXTRA_ALBUM_ID, albumId);
                        intent.putExtra(Constants.EXTRA_ALBUM_TITLE, albumTitle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    } else {
                        toggleSelection(position, albumId);
                    }
                }
            });

            if (mActivity instanceof MainActivity) {
                holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!mInActionMode) {
                            if (mActivity instanceof MainActivity) {
                                ((MainActivity) mActivity).setSecondaryActionMode();
                                mInActionMode = true;
                                //Starting Action Mode
                                toggleSelection(position, albumId);
//                                notifyDataSetChanged();
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        } else if (baseHolder instanceof FooterViewHolder) {
            final FooterViewHolder holder = (FooterViewHolder) baseHolder;

            int numberOfAlbum = mCursor.getCount();
            String albumKey = mContext.getResources().getQuantityString(R.plurals.albums_count, numberOfAlbum, numberOfAlbum);

            holder.footerText.setText(albumKey.toLowerCase());
        }
    }

    @Override
    public int getItemViewType (int position) {
        if (isPositionFooter (position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    public void toggleSelection(int pos, long albumId) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
            mCheckedItems.remove(albumId);
        }
        else {
            mSelectedItems.put(pos, true);
            mCheckedItems.put(albumId, true);
        }

        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).updateAmCount(getSelectedItemCount());
        }
        notifyDataSetChanged();
//        notifyItemChanged(pos);
    }

    int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    private void findColumns(Cursor c) {
        if (c != null) {
            if (mActivity instanceof MainActivity) {
                mAlbumIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                try {
                    mAlbumIdIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ID);
                } catch (IllegalArgumentException e) {
                    mAlbumIdIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
                }

                try {
                    mNoOfSongs = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
                    mArtistIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);
                } catch (IllegalArgumentException e) {

                }
            } else if (mActivity instanceof SearchActivity) {
                mAlbumIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                mAlbumIdIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            } else if (mActivity instanceof ArtistActivity) {
                mAlbumIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                mNoOfSongs = c.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
            }
        }
    }

    @Override
    public Cursor swapCursor(Cursor c, String idField, boolean animate) {
        findColumns(c);
        return super.swapCursor(c, idField, animate);
    }

    public void deleteAlbums() {
        if (!mCheckedItems.isEmpty()) {
            List<Long> albumIds = new ArrayList<>(mCheckedItems.keySet());
            long[] songIds = new long[0];
            long[] cachedIds = new long[0];
            for (int i = 0; i < albumIds.size(); i++) {
                long[] ids = MusicUtils.getSongListForAlbum(mContext, albumIds.get(i));
                cachedIds = songIds;
                songIds = new long[cachedIds.length + ids.length];
                for (int j = 0; j < cachedIds.length; j++) {
                    songIds[j] = cachedIds[j];
                }
                for (int k = 0; k < ids.length; k++) {
                    songIds[k] = ids[k];
                }
            }
//            mCheckedItems.clear();
            resetOptions();
            ((MainActivity) mActivity).updateAmCount(mCheckedItems.size());
            ((MainActivity) mActivity).showDeleteSnackBar(songIds);
        }
    }

    public void resetOptions() {
        mInActionMode = false;
        mCheckedItems.clear();
        mSelectedItems.clear();
        notifyDataSetChanged();
    }
}