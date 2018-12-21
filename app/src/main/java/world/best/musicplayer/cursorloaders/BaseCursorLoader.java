package world.best.musicplayer.cursorloaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * This is the base caass for all cursor loaders in the application
 */
public class BaseCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final String TAG = "CursorLoader";

    protected Context context;
    protected CursorLoaderCallBack mCursorLoaderCallBack;
    protected StringBuilder where;

    public BaseCursorLoader(Context context, CursorLoaderCallBack cursorLoaderCallBack) {
        this.context = context;
        this.mCursorLoaderCallBack = cursorLoaderCallBack;
        this.where = new StringBuilder();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader != null) {
            // mCursorLoaderCallBack.onCursorLoaderReset(CursorType.SONG_CURSOR);
        }
    }
}