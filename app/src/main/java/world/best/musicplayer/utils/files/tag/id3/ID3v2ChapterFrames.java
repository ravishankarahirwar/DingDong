package world.best.musicplayer.utils.files.tag.id3;

import java.util.TreeSet;

/**
 * Defines ID3 Chapter frames and collections that categorise frames.
 *
 * <p>For more details, please refer to the ID3 Chapter Frame specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2-chapters-1.0.txt">ID3 v2 Chapter Frame Spec</a>
 * </ul>
 */
public class ID3v2ChapterFrames extends ID3Frames
{
    public static final String FRAME_ID_CHAPTER = "CHAP";
    public static final String FRAME_ID_TABLE_OF_CONTENT = "CTOC";

    private static ID3v2ChapterFrames id3v2ChapterFrames;

    public static ID3v2ChapterFrames getInstanceOf()
    {
        if (id3v2ChapterFrames == null)
        {
            id3v2ChapterFrames = new ID3v2ChapterFrames();
        }
        return id3v2ChapterFrames;
    }

    private ID3v2ChapterFrames()
    {
        idToValue.put(FRAME_ID_CHAPTER, "Chapter");
        idToValue.put(FRAME_ID_TABLE_OF_CONTENT, "Table of content");
        createMaps();
        multipleFrames = new TreeSet<String>();
        discardIfFileAlteredFrames = new TreeSet<String>();
    }
}
