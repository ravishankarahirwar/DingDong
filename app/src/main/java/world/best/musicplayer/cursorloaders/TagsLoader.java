package world.best.musicplayer.cursorloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import world.best.musicplayer.utils.TagUtils;

import java.util.ArrayList;
import java.util.Map;

public class TagsLoader extends AsyncTaskLoader<ArrayList<String>> {

    private Map<Long, String> mPaths;
    private Context mContext;

    public TagsLoader(Context context, Map<Long, String> paths) {
        super(context);
        mContext = context.getApplicationContext();
        mPaths = paths;
    }

    @Override
    public ArrayList<String> loadInBackground() {
        if (TagUtils.isTagRefreshNeeded()) {
            TagUtils.getAllTags(mContext, mPaths);
        }

        return TagUtils.getCachedTags();
    }
}