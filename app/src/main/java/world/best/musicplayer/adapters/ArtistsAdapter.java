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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import world.best.musicplayer.activity.ArtistActivity;
import world.best.musicplayer.R;
import world.best.musicplayer.utils.Constants;
import world.best.musicplayer.utils.view.ImageCircleTransform;

public class ArtistsAdapter extends BaseCursorAdapter<RecyclerView.ViewHolder> {

    boolean mIsNowPlaying;
    boolean mDisableNowPlayingIndicator;

    int mArtistIdId;

    int mAlbumId;
    int mSongId;

    int mArtistIdx;
    int mNoOfAlbumIdx;
    int mNoOfSongIdx;
    int mArtistIdIdx;

    private final StringBuilder mBuilder = new StringBuilder();
    private final String mUnknownArtist;
    private final String mUnknownAlbum;

    private final Resources mResources;

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public ArtistsAdapter (Context context, Activity activity, int layout, Cursor cursor,
            boolean isnowplaying, boolean disablenowplayingindicator) {
        super(context, layout, cursor);
        mActivity = activity;
        findColumns(cursor);

        mIsNowPlaying = isnowplaying;
        mDisableNowPlayingIndicator = disablenowplayingindicator;

        mUnknownArtist = context.getString(R.string.unknown_artist_name);
        mUnknownAlbum = context.getString(R.string.unknown_album_name);
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
    public void onBindViewHolder (RecyclerView.ViewHolder baseHolder, Cursor cursor, int position) {
        if (baseHolder instanceof ContentViewHolder) {
            final ContentViewHolder holder = (ContentViewHolder) baseHolder;
            holder.overFlowMenu.setVisibility(View.GONE);
            final long artistId = cursor.getLong(mArtistIdIdx);
            final String artistName = cursor.getString(mArtistIdx);
            holder.title.setText(artistName);
            try {
                int numberOfAlbums = Integer.parseInt(cursor.getString(mNoOfAlbumIdx));
                int numberOfSongs = Integer.parseInt(cursor.getString(mNoOfSongIdx));
                String albumsKey = mResources.getQuantityString(R.plurals.albums_count, numberOfAlbums, numberOfAlbums);
                String songsKey = mResources.getQuantityString(R.plurals.songs_count, numberOfSongs, numberOfSongs);
                holder.artistAlbum.setText(albumsKey + ", " + songsKey);
                holder.artistAlbum.setVisibility(View.VISIBLE);
            } catch (IllegalStateException e) {
                holder.artistAlbum.setVisibility(View.INVISIBLE);
            }

            mPicasso.load(ContentUris.withAppendedId(sArtworkUri, artistId))
                    .transform(new ImageCircleTransform())
                    .placeholder(R.drawable.ic_default_art)
                    .error(R.drawable.ic_default_art)
                    .resize(120, 120)
                    .into(holder.artwork);

            // final String artist = cursor.getString(mArtistIdx);
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity.getApplicationContext(), ArtistActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.EXTRA_ARTIST_ID, artistId);
                    intent.putExtra(Constants.EXTRA_ARTIST_NAME, artistName);
                    mActivity.getApplicationContext().startActivity(intent);
                }
            });
        } else if (baseHolder instanceof FooterViewHolder) {
            final FooterViewHolder holder = (FooterViewHolder) baseHolder;

            int numberOfArtist = mCursor.getCount();
            String artistKey = mContext.getResources().getQuantityString(R.plurals.artists_count, numberOfArtist, numberOfArtist);

            holder.footerText.setText(artistKey.toLowerCase());
        }
    }

    @Override
    public int getItemViewType (int position) {
        if (isPositionFooter (position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    private void findColumns(Cursor c) {
        if (c != null) {
            mArtistIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
            try {
                mNoOfAlbumIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
                mNoOfSongIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
            } catch (IllegalArgumentException e) {
            }

            try {
                mArtistIdIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
            } catch (IllegalArgumentException e) {
                mArtistIdIdx = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);
            }
        }
    }

    @Override
    public Cursor swapCursor(Cursor c, String idField, boolean animate) {
        findColumns(c);
        return super.swapCursor(c, idField, animate);
    }
}