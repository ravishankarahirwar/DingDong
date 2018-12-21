package world.best.musicplayer.cursorloaders;

import android.database.Cursor;

public interface CursorLoaderCallBack {
    /**
     * This interface is use for sending callback to Activity
     * after getting Cursor from Cursor Loader in onLoadFinished method
     */
    public void onCursorLoadingFinished(Cursor data, int cursorType);

    public void onCursorLoaderReset(int cursorType);
}