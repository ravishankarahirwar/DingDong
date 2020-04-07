package world.best.musicplayer.cursorloaders;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.Map;

import world.best.musicplayer.utils.TagUtils;

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