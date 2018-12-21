package world.best.musicplayer.utils.files.audio.flac.metadatablock;

/**
 * The different types of metadata block
 */
public enum BlockType
{
    STREAMINFO(0),
    PADDING(1),
    APPLICATION(2),
    SEEKTABLE(3),
    VORBIS_COMMENT(4),
    CUESHEET(5),
    PICTURE(6);

    private int id;

    BlockType(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
}
