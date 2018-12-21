package world.best.musicplayer.cursorloaders;

/**
 * This interface is use define type of cursor we are using in application.
 */
public interface CursorType {
    int INVALID_CURSOR = -1;
    int SONG_CURSOR = 0;
    int ARTIST_CURSOR = 1;
    int ALBUM_CURSOR = 2;
    int TAG_CURSOR = 3;
    int ARTIST_DETAIL_ALBUM_CURSOR = 4;
    int ARTIST_DETAIL_SONG_CURSOR = 5;
    int ALBUM_DETAIL_CURSOR = 6;
    int SONG_CURSOR_FOR_SORTING = 11;
}
