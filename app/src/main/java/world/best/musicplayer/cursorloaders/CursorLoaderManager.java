package world.best.musicplayer.cursorloaders;

import world.best.musicplayer.factories.CursorLoaderFactory;
import world.best.musicplayer.utils.Constants;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * This is the class is user for Loading Song/Track Data from Media Store.
 */
public class CursorLoaderManager extends BaseCursorLoader {

    public CursorLoaderManager(Context context, CursorLoaderCallBack cursorLoaderCallBack) {
        super(context, cursorLoaderCallBack);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        Log.d(TAG, "CursorLoaderManager : onCreateLoader - loaderId : " + loaderID);
        switch (loaderID) {
        case CursorType.SONG_CURSOR :
            return CursorLoaderFactory.createSongLoader(context);

        case CursorType.ALBUM_CURSOR :
            return CursorLoaderFactory.createAlbumLoader(context);

        case CursorType.ARTIST_CURSOR :
            return CursorLoaderFactory.createArtistLoader(context);

        case CursorType.ALBUM_DETAIL_CURSOR :
            if (bundle != null && bundle.containsKey(Constants.EXTRA_ALBUM_ID)) {
                long albumId = bundle.getLong(Constants.EXTRA_ALBUM_ID , -1);
                return CursorLoaderFactory.createAlbumDetailLoader(context, albumId);
            } else {
                return null;
            }

        case CursorType.ARTIST_DETAIL_ALBUM_CURSOR :
            if (bundle != null && bundle.containsKey(Constants.EXTRA_ARTIST_ID)) {
                long artistId = bundle.getLong(Constants.EXTRA_ARTIST_ID , -1);
                return CursorLoaderFactory.createArtistDetailAlbumLoader(context, artistId);
            } else {
                return null;
            }

        case CursorType.ARTIST_DETAIL_SONG_CURSOR :
            if (bundle != null && bundle.containsKey(Constants.EXTRA_ARTIST_ID)) {
                long artistId = bundle.getLong(Constants.EXTRA_ARTIST_ID , -1);
                Log.d(TAG,
                        "CursorLoaderManager : artistId : " + artistId);
                return CursorLoaderFactory.createArtistDetailSongLoader(context, artistId);
            } else {
                return null;
            }
        default:
            // An invalid id was passed in
            Log.d(TAG,
                    "CursorLoaderManager : An invalid id was passed in: " + loaderID);
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            Log.d(TAG,
                    "CursorLoaderManager : onLoadFinished loaderId : " + loader.getId());
            mCursorLoaderCallBack.onCursorLoadingFinished(cursor,
                    loader.getId());
        } else {
            Log.d(TAG,
                    "CursorLoaderManager : onLoadFinished Cursor is null");
            mCursorLoaderCallBack.onCursorLoadingFinished(cursor,
                    CursorType.INVALID_CURSOR);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "CursorLoaderManager : onLoaderReset");
        super.onLoaderReset(loader);
    }
}
