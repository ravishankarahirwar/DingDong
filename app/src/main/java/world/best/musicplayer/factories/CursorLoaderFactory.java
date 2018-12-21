package world.best.musicplayer.factories;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * This is class is use for Create various loaders for loading data from Media Store.
 */
public class CursorLoaderFactory {

    private static final Uri SONG_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String SHORT_ORDER = MediaStore.Audio.Media.TITLE_KEY;
    private static final String[] SONG_COLUMNS = new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE };

    public static final Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    private static final String ALBUM_SHORT_ORDER = MediaStore.Audio.Albums.ALBUM;
    private static final String[] ALBUM_CURSOR_COLUMNS = new String[] {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS};

    public static final Uri ARTIST_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    private static final String ARTIST_SHORT_ORDER = MediaStore.Audio.Artists.ARTIST_KEY;
    private static final String[] ARTIST_CURSOR_COLUMNS = new String[] {
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
    };

    /**
     * Album Detail URI , Column and sort order
     */
    private static final Uri ALBUM_DETAIL_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String ALBUM_DETAIL_SHORT_ORDER = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

    private static final String[] ARTIST_DETAIL_ALBUM_COLUMS = new String[] {
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST,
            MediaStore.Audio.Albums.ALBUM_ART
    };

    public static final Uri ARTIST_SONGS_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private static final String[] ARTIST_DETAIL_SONGS_COLUMS = new String[] {
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.MIME_TYPE
    };

    public static CursorLoader createSongLoader(Context context) {
        StringBuilder where = new StringBuilder();
        where = where.append(MediaStore.Audio.Media.TITLE + " != ''");
        where.append(" AND " + MediaStore.Audio.Media.IS_MUSIC + "=1");
        return new CursorLoader(context, // Parent activity context
                SONG_URI, // Table to query
                SONG_COLUMNS, // Projection to return
                where.toString(), // No selection clause
                null, // No selection arguments
                SHORT_ORDER // Default sort order
        );
    }

    /**
     * @param context
     * @return All Album available in Media Store
     */
    public static CursorLoader createAlbumLoader(Context context) {
        return new CursorLoader(context, ALBUM_URI, ALBUM_CURSOR_COLUMNS, null,
                null, ALBUM_SHORT_ORDER);
    }

    /**
     * @param context
     * @param songSortOrder Order of Songs list
     * @return All Songs available in Media Store
     */
    public static CursorLoader createSongLoader(Context context, String songSortOrder) {
        String selectionStatement = "is_music=1 AND title != ''";
        return new CursorLoader(context, SONG_URI, SONG_COLUMNS, selectionStatement, null,
                songSortOrder);
    }

    /**
     * @param context
     * @return All Artist available in Media Store
     */
    public static CursorLoader createArtistLoader(Context context) {
        return new CursorLoader(context, ARTIST_URI, ARTIST_CURSOR_COLUMNS, null, null,
                ARTIST_SHORT_ORDER);
    }

    /**
     * @param context
     * @param albumId Id of the Album
     * @return All Songs belongs to particular Album
     */
    public static CursorLoader createAlbumDetailLoader(Context context, long albumId) {

        String where = MediaStore.Audio.Media.ALBUM_ID + "=" + albumId + " AND " +
                MediaStore.Audio.Media.IS_MUSIC + "=1";

        return new CursorLoader(context, ALBUM_DETAIL_URI,
                SONG_COLUMNS, where.toString(), null,
                ALBUM_DETAIL_SHORT_ORDER);
    }

    /**
     * @param context
     * @param artistId
     * @return All album belong to particular artist
     */
    public static CursorLoader createArtistDetailAlbumLoader(Context context, long artistId) {
        Uri artistUri = MediaStore.Audio.Artists.Albums.getContentUri("external", artistId);
        String selectionStatement = "is_music=1 AND title != ''";

        return new CursorLoader(context, artistUri, ARTIST_DETAIL_ALBUM_COLUMS, selectionStatement, null,
                MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
    }

    /**
     * @param context
     * @param artistId Id of particuar Artist
     * @return All Songs belong to particular artist
     */
    public static CursorLoader createArtistDetailSongLoader(Context context, long artistId) {
        String where = "is_music=1 AND title != '' AND "+MediaStore.Audio.Media.ARTIST_ID+" = " + artistId ;

        return new CursorLoader(context, ARTIST_SONGS_URI, ARTIST_DETAIL_SONGS_COLUMS, where, null,
                MediaStore.Audio.Media.TITLE_KEY);
    }
}
