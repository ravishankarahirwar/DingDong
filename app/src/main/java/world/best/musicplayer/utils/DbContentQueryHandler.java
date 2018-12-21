package world.best.musicplayer.utils;

import android.content.Context;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

// for getting result success status of the queries implement the DbQueryCompleteListener
// and set its value using the setmDbQueryCompleteListener method
public class DbContentQueryHandler extends AsyncQueryHandler {

    protected DbContentQueryHandler mQueryHandler;
    protected Context mContext;
    class QueryArgs {
        public Uri uri;
        public String[] projection;
        public String selection;
        public String[] selectionArgs;
        public String orderBy;
    }

    private String mTagsWrittenToFile = null;
    private String mTagToDelete = null;
    private boolean mIsUpdatingTags = false;
    private boolean mIsDeletingTags = false;
    private boolean mIsTagRenaming = false;
    private String mAudioFilePath = null;
    private String mAllTagsForSelectedFile = null;

    public final int DB_INSERT_TOKEN = 1;
    public final int DB_UPDATE_TOKEN = 2;
    public final int DB_DELETE_TOKEN = 3;
    public final int DB_GET_ALL_TAGS_TOKEN = 4;

    private DbQueryCompleteListener mDbQueryCompleteListener = null;

    public void setmDbQueryCompleteListener(DbQueryCompleteListener mDbQueryCompleteListener) {
        this.mDbQueryCompleteListener = mDbQueryCompleteListener;
    }

    public DbContentQueryHandler(Context context) {
        super(context.getContentResolver());
        mContext = context;
        mDbQueryCompleteListener = null;
    }

    public DbContentQueryHandler(Context context,DbQueryCompleteListener dbQueryCompleteListener) {
        super(context.getContentResolver());
        mContext = context;
        mDbQueryCompleteListener = dbQueryCompleteListener;
    }
    public DbContentQueryHandler(ContentResolver res, Context context) {
        super(res);
        this.mContext = context;
    }

    public Cursor doQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy,
            boolean async) {
        if (async) {
            // Get 100 results first, which is enough to allow the user to start
            // scrolling,
            // while still being very fast.
            // Uri limituri = uri.buildUpon().appendQueryParameter("limit",
            // "100").build();
            Uri limituri = uri.buildUpon().build();
            QueryArgs args = new QueryArgs();
            args.uri = uri;
            args.projection = projection;
            args.selection = selection;
            args.selectionArgs = selectionArgs;
            args.orderBy = orderBy;

            startQuery(0, args, limituri, projection, selection, selectionArgs, orderBy);
            return null;
        }
        return MusicUtils.query(mContext, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        // Log.i("@@@", "query complete: " + cursor.getCount() + " " +
        // mActivity);
        // mActivity.init(cursor, cookie != null);
        // if (token == DB_GET_ALL_TAGS_TOKEN && cookie != null && cursor != null && !cursor.isClosed()) {
        //     try {
        //         cursor.moveToFirst();
        //         mAllTagsForSelectedFile = cursor.getString(0);
        //     } catch (IllegalArgumentException e) { // no tags is associated
        //                                            // with this audio file
        //         e.printStackTrace();
        //         mAllTagsForSelectedFile = null;
        //     } catch (Exception e) { // no tags is associated with this audio
        //                             // file
        //         e.printStackTrace();
        //         mAllTagsForSelectedFile = null;
        //     }

        //     /// for creating/updating tags in the db multiple tags
        //     if (mIsUpdatingTags) {
        //         if (!TextUtils.isEmpty(mAllTagsForSelectedFile)) {
        //             mTagsWrittenToFile = mAllTagsForSelectedFile + mTagsWrittenToFile;
        //         }

        //         ContentValues values = new ContentValues();
        //         values.put(TagUtils.AUDIO_TAGS, mTagsWrittenToFile);

        //         QueryArgs args = (QueryArgs) cookie;
        //         startUpdate(DB_UPDATE_TOKEN, null, args.uri, values, args.selection, null);
        //     }
        //     /// for deleting tags from a audio file in db
        //     if (mIsDeletingTags) {
        //         if (!TextUtils.isEmpty(mAllTagsForSelectedFile)) {
        //             // mAllTagsForSelectedFile.replace(mTagToDelete +
        //             // MusicUtils.TAG_DELIMITER, "");
        //             mAllTagsForSelectedFile = mAllTagsForSelectedFile.replace(mTagToDelete, "");
        //             QueryArgs args = (QueryArgs) cookie;
        //             // rewriting the tags into the file after removing the
        //             // deleted tag from the entire string
        //             ContentValues values = new ContentValues();
        //             values.put(TagUtils.AUDIO_TAGS, mAllTagsForSelectedFile);
        //             startUpdate(DB_UPDATE_TOKEN, null, args.uri, values, args.selection, null);
        //         }
        //     }

        // } else
        if (token == 0 && cookie != null && cursor != null && !cursor.isClosed() && cursor.getCount() >= 100) {
            QueryArgs args = (QueryArgs) cookie;
            startQuery(1, null, args.uri, args.projection, args.selection, args.selectionArgs, args.orderBy);
        }
    }

    // public void createUpdateTAG(long id, String tagToAdd, String audioFilePath) {

    //     mTagsWrittenToFile = null;
    //     mTagsWrittenToFile = TagUtils.TAG_START_PATTERN + tagToAdd + TagUtils.TAG_END_PATTERN;
    //     mTagToDelete = null;
    //     getAllTagsStringForFile(id);
    //     mIsUpdatingTags = true;
    //     mAudioFilePath = audioFilePath;
    //     AudioTagsFileIO.writeTagToAudioFile(tagToAdd, audioFilePath);
    //     // startUpdate (DB_UPDATE_TOKEN, null, uri , values, where, null);
    // }

    // public void deleteTAG(long id, String tagToDelete, String audioFilePath) {
    //     mTagsWrittenToFile = null;
    //     mTagToDelete = null;
    //     mTagToDelete = TagUtils.TAG_START_PATTERN + tagToDelete + TagUtils.TAG_END_PATTERN;
    //     getAllTagsStringForFile(id);
    //     mIsDeletingTags = true;
    //     mAudioFilePath = audioFilePath;

    // }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        // Log.d(TAG, "Update Completed :" + "Token : " + token + " Result : +"
        // + result + "mAudioFilePath :"
        // + mAudioFilePath);
        super.onUpdateComplete(token, cookie, result);
        // if (result == 1) {
        //     if (mIsUpdatingTags) {
        //         try {
        //             AudioTagsFileIO.writeTagToAudioFile(mTagsWrittenToFile, mAudioFilePath);
        //         } catch (Exception e) {
        //             // e.printStackTrace();
        //         }
        //         if (mDbQueryCompleteListener != null) {
        //             mDbQueryCompleteListener.onDbQueryComplete(DbQueryCompleteListener.RESULT_SUCCESS,DbQueryCompleteListener.TAG_ADD);
        //         }

        //         mIsUpdatingTags = false;
        //     } else if (mIsDeletingTags) {
        //         try {
        //             AudioTagsFileIO.writeTagToAudioFile(mAllTagsForSelectedFile, mAudioFilePath);
        //         } catch (Exception e) {
        //             // e.printStackTrace();
        //         }
        //         if (mDbQueryCompleteListener != null) {
        //             mDbQueryCompleteListener.onDbQueryComplete(DbQueryCompleteListener.RESULT_SUCCESS,DbQueryCompleteListener.TAG_DELETE);
        //         }
        //         mIsDeletingTags = false;
        //     } else if (mIsTagRenaming) {
        //         // to be implemented
        //         if (mDbQueryCompleteListener != null) {
        //             mDbQueryCompleteListener.onDbQueryComplete(DbQueryCompleteListener.RESULT_SUCCESS,DbQueryCompleteListener.TAG_RENAME);
        //         }
        //         mIsTagRenaming = false;
        //     }
        // } else
        // if (result == 0) {
        //     if (mDbQueryCompleteListener != null) {
        //         if (mIsUpdatingTags) {
        //             mDbQueryCompleteListener.onDbQueryComplete(DbQueryCompleteListener.RESULT_FAILURE,DbQueryCompleteListener.TAG_ADD);
        //         }

        //         if (mIsDeletingTags) {
        //             mDbQueryCompleteListener.onDbQueryComplete(DbQueryCompleteListener.RESULT_FAILURE,DbQueryCompleteListener.TAG_DELETE);
        //         }

        //         if (mIsTagRenaming) {
        //             mDbQueryCompleteListener.onDbQueryComplete(DbQueryCompleteListener.RESULT_FAILURE,DbQueryCompleteListener.TAG_RENAME);
        //         }
        //     }
        // }

        // mTagsWrittenToFile = null;
    }

    // public void getAllTagsStringForFile(long id) {
    //     ContentValues values = new ContentValues();

    //     QueryArgs args = new QueryArgs();
    //     args.uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    //     args.projection = new String[] { TagUtils.AUDIO_TAGS };
    //     args.selection = MediaStore.Audio.Media._ID + "=" + id;
    //     args.selectionArgs = null;
    //     args.orderBy = null;
    //     startQuery(DB_GET_ALL_TAGS_TOKEN, args, args.uri, args.projection, args.selection, args.selectionArgs,
    //             args.orderBy);
    // }
}