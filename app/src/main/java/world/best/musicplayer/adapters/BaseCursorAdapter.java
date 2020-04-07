package world.best.musicplayer.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import world.best.musicplayer.MusicApplication;
import world.best.musicplayer.utils.DbContentQueryHandler;

public abstract class BaseCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final String TAG = "BaseCursorAdapter";
    protected boolean mDataValid;
    protected Cursor mCursor;
    protected int mRowIDColumn;

    protected DbContentQueryHandler mQueryHandler;

    protected Context mContext = null;
    protected Activity mActivity = null;
    protected String mConstraint = null;
    protected boolean mConstraintIsValid = false;

    protected Picasso mPicasso;

    protected int mLayout;
    protected String[] mOriginalFrom;

    private String mTagsWrittenToFile = null;
    private String mTagToDelete = null;
    private boolean mIsUpdatingTags = false;
    private boolean mIsDeletingTags = false;
    private String mAudioFilePath = null;
    private String mAllTagsForSelectedFile = null;

    public final int DB_INSERT_TOKEN = 1;
    public final int DB_UPDATE_TOKEN = 2;
    public final int DB_DELETE_TOKEN = 3;
    public final int DB_GET_ALL_TAGS_TOKEN = 4;

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;

    public BaseCursorAdapter(Context context, int layout, Cursor cursor) {
        init(cursor);
        mContext = context.getApplicationContext();
        mLayout = layout;
        mQueryHandler = new DbContentQueryHandler(mContext);
        mPicasso = ((MusicApplication) mContext).getPicassoInstance();
    }

    void init(Cursor c) {
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        setHasStableIds(true);
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        if (mCursor != null && mCursor.getCount() > 0 && position == mCursor.getCount()) {
            onBindViewHolder(holder, null, position);
        } else {
            if (!mDataValid) {
                throw new IllegalStateException("this should only be called when the cursor is valid");
            }
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            }
            onBindViewHolder(holder, mCursor, position);
        }
    }

    public abstract void onBindViewHolder(VH holder, Cursor cursor, int position);

    public Cursor getCursor() {
        return mCursor;
    }

    protected boolean isPositionFooter(int position) {
        if (mCursor != null) {
            if (position == mCursor.getCount()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            try {
                int count = mCursor.getCount();
                if (count > 0) {
                    return count + 1;
                } else {
                    return count;
                }
            } catch (Exception e) {
                return 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if(hasStableIds() && mDataValid && mCursor != null){
            if (position == -1) {
                return RecyclerView.NO_ID;
            } else if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return RecyclerView.NO_ID;
            }
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public void changeCursor(Cursor cursor, String idField) {
        Cursor old = swapCursor(cursor, idField, false);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor, String idField, boolean animate) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            mRowIDColumn = newCursor.getColumnIndexOrThrow(idField);
            mDataValid = true;
            // notify the observers about the new cursor
            if (animate) {
                notifyItemRangeInserted(0, getItemCount());
            } else {
                notifyDataSetChanged();
            }
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    public Cursor swapSingleColumnCursor(Cursor newCursor){
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null) {
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            mRowIDColumn = -1;
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;
    }

    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    public DbContentQueryHandler getQueryHandler() {
        return mQueryHandler;
    }
}