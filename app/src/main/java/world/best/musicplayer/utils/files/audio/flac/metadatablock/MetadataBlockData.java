package world.best.musicplayer.utils.files.audio.flac.metadatablock;

/**
 * This defines the interface required of the different metadata block types
 */
public interface MetadataBlockData
{
    /**
     * @return the rawdata as it will be written to file
     */
    public byte[] getBytes();

    /**
     * @return the length in bytes that the data uses when written to file
     */
    public int getLength();
}
