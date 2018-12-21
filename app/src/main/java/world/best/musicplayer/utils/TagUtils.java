package world.best.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import world.best.musicplayer.utils.files.audio.AudioFile;
import world.best.musicplayer.utils.files.audio.AudioFileIO;
import world.best.musicplayer.utils.files.audio.exceptions.CannotReadException;
import world.best.musicplayer.utils.files.audio.exceptions.CannotWriteException;
import world.best.musicplayer.utils.files.audio.exceptions.InvalidAudioFrameException;
import world.best.musicplayer.utils.files.audio.exceptions.ReadOnlyFileException;
import world.best.musicplayer.utils.files.tag.FieldDataInvalidException;
import world.best.musicplayer.utils.files.tag.FieldKey;
import world.best.musicplayer.utils.files.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagUtils {

    public final static String TAG = "TagUtils";

    private static final int MOST_USED_TAG_COUNT = 5;
    private static final int MOST_RECENT_TAG_COUNT = 5;

    private static final String PREFS_ALL_TAGS = "all_tags";
    private static final String PREFS_RECENT_TAGS = "recent_tags";
    private static final String PREFS_MOST_USED_TAGS = "most_used_tags";
    private static final String PREFS_MOST_PLAYED_SONGS = "most_played_songs";

    private static HashMap<String, List<Long>> mAllTags = new HashMap<String, List<Long>>();

    private static ArrayList<String> mTagNames = new ArrayList<String>();
    private static ArrayList<String> mRecentlyUsedTags = new ArrayList<String>();
    private static ArrayList<String> mMostUsedTags = new ArrayList<String>();

    private static HashMap<String, Integer> mMostPlayedSongs = new HashMap<String, Integer>();

    private static boolean mRefreshNeeded = true;

    public static void loadDataFromPrefs(Context context) {
        loadAllTagsData(context);
        loadRecentsData(context);
        loadMostUsedData(context);
        loadMostPlayedData(context);
    }

    public static void saveDataToPrefs(Context context) {
        saveAllTagsData(context);
        saveRecentsData(context);
        saveMostUsedData(context);
    }

    private static void loadAllTagsData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cachedTags = prefs.getStringSet(PREFS_ALL_TAGS, new HashSet<String>());
        if (!cachedTags.isEmpty()) {
            mTagNames = new ArrayList<String>(cachedTags);
        }
    }

    private static void loadRecentsData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cachedRecents = prefs.getStringSet(PREFS_RECENT_TAGS, new HashSet<String>());
        if (!cachedRecents.isEmpty()) {
            mRecentlyUsedTags = new ArrayList<String>(cachedRecents);
        }
    }

    private static void loadMostUsedData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cachedMostUsed = prefs.getStringSet(PREFS_MOST_USED_TAGS, new HashSet<String>());
        if (!cachedMostUsed.isEmpty()) {
            mMostUsedTags = new ArrayList<String>(cachedMostUsed);
        }
    }

    private static void saveAllTagsData(Context context) {
        if (!mAllTags.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putStringSet(PREFS_ALL_TAGS, new HashSet<>(mAllTags.keySet())).apply();
        }
    }

    private static void saveRecentsData(Context context) {
        if (!mRecentlyUsedTags.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putStringSet(PREFS_RECENT_TAGS, new HashSet<>(mRecentlyUsedTags)).apply();
        }
    }

    private static void saveMostUsedData(Context context) {
        if (!mMostUsedTags.isEmpty()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.edit().putStringSet(PREFS_MOST_USED_TAGS, new HashSet<>(mMostUsedTags)).apply();
        }
    }

    public static void loadTagsFromFiles(Context context, Map<Long, String> paths) {
        mRefreshNeeded = false;
        getAllTags(context, paths);
        saveAllTagsData(context);
        figureOutMostUsedTags();
    }

    /**
     * @param paths Map with SongID and SongPath
     * @return Tags and Id of Songs containing given tag
     */
    public static HashMap<String, List<Long>> getAllTags(Context context, Map<Long, String> paths) {
        HashMap<String, List<Long>> tagList = new HashMap<>();

        for (Map.Entry<Long, String> entry : paths.entrySet()) {
            List<String> tags = getTagsForSong(context, entry.getValue());
            if (tags != null) {
                for (int j = 0; j < tags.size(); j++) {
                    String tag = tags.get(j).trim();
                    if (tag != null && !tag.isEmpty()) {
                        if (tagList != null && tagList.containsKey(tag))  {
                            List<Long> songIds = tagList.get(tag);
                            songIds.add(entry.getKey());
                            tagList.put(tag, songIds);
                        } else {
                            List<Long> songIds = new ArrayList<Long>();
                            songIds.add(entry.getKey());
                            tagList.put(tag, songIds);
                        }
                    }
                }
            }
        }

        mAllTags = tagList;
        mRefreshNeeded = false;
        return tagList;
    }

    /**
     * Get all Song paths containing a given tag
     *
     * @param paths paths of the songs
     * @param tag Name of the tag
     * @return All paths to songs containing given Tag
     */
    public static List<String> getSongsForTag(Context context, List<String> paths, String tag) {
        List<String> resultPaths = new ArrayList<>();

        for (int i = 0; i < paths.size(); i++) {
            String path = paths.get(i);
            List<String> tags = getTagsForSong(context, path);
            if (tags.contains(tag)) {
                resultPaths.add(path);
            }
        }

        return resultPaths;
    }

    /**
     * Get all tags from a given song
     *
     * @param path Path to the song
     * @return All tags related to song
     */
    public static List<String> getTagsForSong(Context context, String path) {
        List<String> tags = new ArrayList<String>();
        AudioFile audioFile = getAudioFile(path);
        if (audioFile != null) {
            tags = getTagsForSong(audioFile);
        }

        if (tags == null || tags.size() < 1) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> tagsSet = prefs.getStringSet(path, new HashSet<String>());
            if (tagsSet.size() > 0) {
                tags = new ArrayList<String>(tagsSet);
            }
        }

        return tags;
    }

    /**
     * @param audioFile
     * @return list of all tags related to audioFile
     */
    private static List<String> getTagsForSong(AudioFile audioFile) {
        List<String> tags = new ArrayList<>();
        try {
            tags = audioFile.getTag().getAll(FieldKey.TAGS);
        } catch (Exception e) {
            // noop
        }

        return tags;
    }

    /**
     * Apply tags on song
     *
     * @param path Song file path
     * @param tags Tags to add on song
     */
    // public static void addTagsForSong(String path, List<String> tags) {
    //     if (!path.isEmpty()) {
    //         AudioFile audioFile = getAudioFile(path);
    //         if (audioFile != null) {
    //             addTagsForSong(audioFile, tags);
    //         }
    //     }
    // }

    private static void addTagsForSong(Context context, AudioFile audioFile, List<String> tags) {
        for (int i = 0; i < tags.size(); i++) {
            addTagForSong(context, audioFile, tags.get(i));
        }
    }

    /**
     * Add a Tag on a song
     *
     * @param path Song file path
     * @param tag Tag to apply on song
     */
    // public static void addTagForSong(String path, String tag) {
    //     if (!path.isEmpty()) {
    //         AudioFile audioFile = getAudioFile(path);
    //         if (audioFile != null) {
    //             addTagForSong(audioFile, tag);
    //         }
    //     }
    // }

    private static boolean addTagForSong(Context context, String path, String tag, long id) {
        if (!path.isEmpty()) {
            AudioFile audioFile = getAudioFile(path);
            if (audioFile != null) {
                return addTagForSong(context, audioFile, path, tag, id);
            }
        }

        return false;
    }

    public static boolean addTagForSongs(Context context, List<String> paths, String tag, long id) {
        boolean addedTag = false;
        if (!paths.isEmpty()) {
            for (int i = 0; i < paths.size(); i++) {
                boolean added = addTagForSong(context, paths.get(i), tag, id);
                if (i == 0) {
                    addedTag = added;
                }
            }
        }

        return addedTag;
    }

    private static boolean addTagForSong(Context context, AudioFile audioFile, String path, String tag, long id) {
        if (checkTagViability(audioFile, tag)) {
            try {
                updateRecentTags(context, tag);
                addToCachedTag(tag, id);
                figureOutMostUsedTags();
                saveRecentsData(context);
                saveMostUsedData(context);
                if (!mAllTags.containsKey(tag)) {
                    mAllTags.put(tag, new ArrayList<Long>());
                    mTagNames.add(tag);
                    saveAllTagsData(context);
                }

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                Set<String> tags = prefs.getStringSet(path, new HashSet<String>());
                if (!tags.contains(tag)) {
                    tags.add(tag);
                    prefs.edit().putStringSet(path, tags).apply();
                }

                audioFile.getTag().addField(FieldKey.TAGS, tag);
                audioFile.commit();
                mRefreshNeeded = true;

                return true;
            } catch (FieldDataInvalidException e1) {

            } catch (CannotWriteException e2) {

            } catch (Exception e3) {

            }

            return true;
        }

        return false;
    }

    private static void addTagForSong(Context context, AudioFile audioFile, String tag) {
        if (checkTagViability(audioFile, tag)) {
            try {
                updateRecentTags(context, tag);
                figureOutMostUsedTags();
                saveRecentsData(context);
                saveMostUsedData(context);
                if (!mAllTags.containsKey(tag)) {
                    mAllTags.put(tag, new ArrayList<Long>());
                    mTagNames.add(tag);
                    saveAllTagsData(context);
                }

                audioFile.getTag().addField(FieldKey.TAGS, tag);
                audioFile.commit();
                mRefreshNeeded = true;
            } catch (FieldDataInvalidException e1) {

            } catch (CannotWriteException e2) {

            } catch (Exception e) {

            }
        }
    }

    private static void addToCachedTag(String tag, long id) {
        if (mAllTags.containsKey(tag)) {
            List<Long> ids = mAllTags.get(tag);
            if (!ids.contains(id)) {
                ids.add(id);
                mAllTags.put(tag, ids);
            }
        }
    }

    private static void removeFromCachedTag(Context context, String tag, long id) {
        if (mAllTags.containsKey(tag)) {
            List<Long> ids = mAllTags.get(tag);
            if (ids.contains(id)) {
                ids.remove(id);
                if (ids.size() > 0) {
                    mAllTags.put(tag, ids);
                } else {
                    if (mRecentlyUsedTags.contains(tag)) {
                        mRecentlyUsedTags.remove(tag);
                    }
                    if (mTagNames.contains(tag)) {
                        mTagNames.remove(tag);
                    }
                    if (mAllTags.containsKey(tag)) {
                        mAllTags.remove(tag);
                    }
                }
                figureOutMostUsedTags();
                saveAllTagsData(context);
            }
        }
    }

    /**
     * Delete all tags from Song
     *
     * @param path Song file path
     */
    public static void deleteTagsForSong(Context context, String path) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(path)) {
            prefs.edit().remove(path).apply();
        }

        AudioFile audioFile = getAudioFile(path);
        if (audioFile != null) {
            deleteTagsForSong(audioFile);
        }
    }

    private static void deleteTagsForSong(AudioFile audioFile) {
        try {
            audioFile.getTag().deleteField(FieldKey.TAGS);
            audioFile.commit();
        } catch (CannotWriteException e1) {
            // noop
        } catch (Exception e)  {
            // noop
        }
    }

    /**
     * Delete a tag from song
     *
     * @param path
     * @param tag
     */
    // public static void deleteTagForSong(String path, String tag) {
    //     if (!path.isEmpty()) {
    //         deleteTagForSong(getAudioFile(path), tag);
    //     }
    // }

    public static void deleteTagForSong(Context context, String path, String tag, long id) {
        if (!path.isEmpty()) {
            Log.d("UnTag", "TU : deleteTagForSong : File path : " + path + " Tag Name : " + tag + " SongId : " + id);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> tags = prefs.getStringSet(path, new HashSet<String>());
            if (tags.contains((tag))) {
                tags.remove(tag);
                if (tags.size() == 0) {
                    prefs.edit().remove(path).apply();
                } else {
                    prefs.edit().putStringSet(path, tags).apply();
                }
            }
            deleteTagForSong(context, getAudioFile(path), tag, id);
        }
    }

    public static void deleteTagForSongs(Context context, List<String> paths, String tag, long id) {
        if (!paths.isEmpty()) {
            for (int i = 0; i < paths.size(); i++) {
                deleteTagForSong(context, paths.get(i), tag, id);
            }
        }
    }

    public static void deleteTagForSongs(Context context, List<String> paths, String tag, List<Long> songIds) {
        if (!paths.isEmpty()) {
            for (int i = 0; i < paths.size(); i++) {
                deleteTagForSong(context, paths.get(i), tag, songIds.get(i));
            }
        }
    }

    public static void deleteAndAddTagsForSong(Context context, String path, List<String> tags) {
        deleteAndAddTagsForSong(context, getAudioFile(path), tags);
    }

    private static void deleteAndAddTagsForSong(Context context, AudioFile audioFile, List<String> tags) {
        deleteTagsForSong(audioFile);
        addTagsForSong(context, audioFile, tags);
    }

    private static void deleteTagForSong(Context context, AudioFile audioFile, String tag) {
        if (audioFile != null && !tag.isEmpty()) {
            List<String> tags = getTagsForSong(audioFile);
            if (tags.contains(tag)) {
                tags.remove(tags.indexOf(tag));
                deleteTagsForSong(audioFile);
                addTagsForSong(context, audioFile, tags);
            }
            mRefreshNeeded = true;
        }
    }

    private static void deleteTagForSong(Context context, AudioFile audioFile, String tag, long id) {
        if (!tag.isEmpty()) {
            removeFromCachedTag(context, tag, id);
            if (audioFile != null) {
                List<String> tags = getTagsForSong(audioFile);
                if (tags.contains(tag)) {
                    tags.remove(tags.indexOf(tag));
                    deleteTagsForSong(audioFile);
                    addTagsForSong(context, audioFile, tags);
                }
                mRefreshNeeded = true;
            }
        }
    }

    // This goes through all the files and can be inefficient. Need to think of a better way to do this.
    // All tags and corresponding id's are stored in mAllTags. Maybe that is a better way to do this?
    // I am a retard. Think you idiot.
    public static void deleteTag(final Context context, final List<String> paths, final String tag) {
        if (mRecentlyUsedTags.contains(tag)) {
            mRecentlyUsedTags.remove(tag);
        }
        if (mTagNames.contains(tag)) {
            mTagNames.remove(tag);
        }
        if (mAllTags.containsKey(tag)) {
            mAllTags.remove(tag);
        }
        saveAllTagsData(context);
        figureOutMostUsedTags();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < paths.size(); i++) {
                    String path = paths.get(i);
                    List<String> tags = getTagsForSong(context, path);
                    if (tags.contains(tag)) {
                        tags.remove(tag);
                        deleteAndAddTagsForSong(context, path, tags);
                    }
                }
            }
        }).start();
    }

    public static void renameTag(final Context context, final List<String> paths, final String oldTag, final String newTag) {
        if (mRecentlyUsedTags.contains(oldTag)) {
            mRecentlyUsedTags.remove(oldTag);
            mRecentlyUsedTags.add(newTag);
        }

        if (mMostUsedTags.contains(oldTag)) {
            mMostUsedTags.remove(oldTag);
            mMostUsedTags.add(newTag);
        }

        if (mTagNames.contains(oldTag)) {
            mTagNames.remove(oldTag);
            mTagNames.add(newTag);
        }

        if(mAllTags.containsKey(oldTag)) {
            List<Long> ids = mAllTags.get(oldTag);
            mAllTags.remove(oldTag);
            mAllTags.put(newTag, ids);
        }

        saveAllTagsData(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < paths.size(); i++) {
                    String path = paths.get(i);
                    List<String> tags = getTagsForSong(context, path);
                    if (tags.contains(oldTag)) {
                        tags.remove(oldTag);
                        tags.add(newTag);
                        deleteAndAddTagsForSong(context, path, tags);
                    }
                }
            }
        }).start();
    }

    public static ArrayList<String> searchTags(String query) {
        ArrayList<String> searchResults = new ArrayList<>();
        if (!mAllTags.isEmpty()) {
            query = query.trim().toLowerCase();
            for (String key : mAllTags.keySet()) {
                if (key.contains(query)) {
                    searchResults.add(key);
                }
            }
        }

        return searchResults;
    }

    public static void updateMostPlayedData(Context context, String path) {
        if (path != null && !path.isEmpty()) {
            path = path.replace("'", "''");
            if (mMostPlayedSongs.containsKey(path)) {
                int count = mMostPlayedSongs.get(path) + 1;
                mMostPlayedSongs.put(path, count);
            } else {
                mMostPlayedSongs.put(path, 1);
            }

            sortMostPlayedSongs();
            saveMostPlayedSongs(context);
        }
    }

    private static void sortMostPlayedSongs() {
        if (mMostPlayedSongs != null && mMostPlayedSongs.size() > 1) {
            mMostPlayedSongs = sortByValues(mMostPlayedSongs);
        }
    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o2)).getValue())
                  .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private static void saveMostPlayedSongs(Context context) {
        if (mMostPlayedSongs.size() > 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> pathsSet = new HashSet<String>();
            for (Map.Entry<String, Integer> entry : mMostPlayedSongs.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();
                pathsSet.add(value + "-" + key);
            }
            prefs.edit().putStringSet(PREFS_MOST_PLAYED_SONGS, pathsSet).apply();
        }
    }

    private static void loadMostPlayedData(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> cachedMostPlayed = prefs.getStringSet(PREFS_MOST_PLAYED_SONGS, new HashSet<String>());
        if (cachedMostPlayed != null && cachedMostPlayed.size() > 0) {
            mMostPlayedSongs = new HashMap<String, Integer>();
            String[] cachedArray = cachedMostPlayed.toArray(new String[cachedMostPlayed.size()]);
            for (int i = 0; i < cachedArray.length; i++) {
                String cacheString = cachedArray[i];
                int index = cacheString.indexOf("-");
                int count = Integer.parseInt(cacheString.substring(0, index));
                String path = cacheString.substring(index + 1);
                mMostPlayedSongs.put(path, count);
            }
            sortMostPlayedSongs();
        }
    }

    public static List<String> getMostPlayedPaths() {
        List<String> paths = new ArrayList<>();
        Set<String> pathsSet = mMostPlayedSongs.keySet();
        Iterator it = pathsSet.iterator();
        int i = 0;
        while (it.hasNext() && i < 25) {
            paths.add((String) it.next());
            i++;
        }

        return paths;
    }

    public static List<String> getPathsOrderedByPlayCount() {
        List<String> paths = new ArrayList<>();
        Set<String> pathsSet = mMostPlayedSongs.keySet();
        Iterator it = pathsSet.iterator();
        int i = 0;
        while (it.hasNext()) {
            paths.add((String) it.next());
            i++;
        }

        return paths;
    }

    // Check to see if cached tags could be used.
    public static boolean isTagRefreshNeeded() {
        return mRefreshNeeded;
    }

    private static AudioFile getAudioFile(String path) {
        try {
            return AudioFileIO.read(new File(path));
        } catch (CannotReadException e1) {

        } catch (IOException e2) {

        } catch (TagException e3) {

        } catch (ReadOnlyFileException e4) {

        } catch (InvalidAudioFrameException e5) {

        } catch (Exception e6) {

        }

        return null;
    }

    private static boolean checkTagViability(AudioFile audioFile, String tag) {
        List<String> tags = getTagsForSong(audioFile);
        if (tags.size() >= 20 || tags.contains(tag)) {
            return false;
        } else {
            return true;
        }
    }

    public static ArrayList<String> getCachedTags() {
        return mTagNames;
    }

    public static List<Long> getCachedSongsForTag(String tag) {
        if (mAllTags != null && mAllTags.containsKey(tag)) {
            return mAllTags.get(tag);
        }

        return null;
    }

    public static ArrayList<String> getRecentlyUsedTags() {
        return mRecentlyUsedTags;
    }

    private static void updateRecentTags(Context context, String tag) {
        if (!mRecentlyUsedTags.contains(tag)) {
            if (mRecentlyUsedTags.size() >= MOST_RECENT_TAG_COUNT) {
                mRecentlyUsedTags.remove(0);
            }

            mRecentlyUsedTags.add(tag);
            // Using global context here is bad practice but, if context is null here,
            // it is the least of our worries
            saveRecentsData(context);
        }
    }

    private static void removeTagFromRecents(Context context, String tag) {
        if (mRecentlyUsedTags.contains(tag)) {
            mRecentlyUsedTags.remove(tag);
            // Using global context here is bad practice but, if context is null here,
            // it is the least of our worries
            saveRecentsData(context);
        }
    }

    public static List<String> getMostUsedTags() {
        return mMostUsedTags;
    }

    private static void figureOutMostUsedTags() {
        mMostUsedTags.clear();
        if (!mAllTags.isEmpty()) {
            Map<String, Integer> tagCountMap = new HashMap<>();
            for (Map.Entry<String, List<Long>> entry : mAllTags.entrySet()) {
                String tag = entry.getKey();
                int songCount = entry.getValue().size();
                tagCountMap.put(tag, songCount);
            }
            Object[] tagCountObj = tagCountMap.entrySet().toArray();
            Arrays.sort(tagCountObj, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
                            ((Map.Entry<String, Integer>) o1).getValue());
                }
            });

            for (int i = 0; i < tagCountObj.length; i++) {
                mMostUsedTags.add(((Map.Entry<String, Integer>) tagCountObj[i]).getKey());
                if (i == 4) {
                    break;
                }
            }
        }
    }
}
