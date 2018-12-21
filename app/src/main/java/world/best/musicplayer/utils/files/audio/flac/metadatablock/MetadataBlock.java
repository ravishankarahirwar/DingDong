package world.best.musicplayer.utils.files.audio.flac.metadatablock;

/**
 * Metadata Block
 *
 * <p>A FLAC bitstream consists of the "fLaC" marker at the beginning of the stream,
 * followed by a mandatory metadata block (called the STREAMINFO block), any number of other metadata blocks,
 * then the audio frames.
 */
public class MetadataBlock
{
    private MetadataBlockHeader mbh;
    private MetadataBlockData mbd;

    public MetadataBlock(MetadataBlockHeader mbh, MetadataBlockData mbd)
    {
        this.mbh = mbh;
        this.mbd = mbd;
    }

    public MetadataBlockHeader getHeader()
    {
        return mbh;
    }

    public MetadataBlockData getData()
    {
        return mbd;
    }

    public int getLength()
    {
        return MetadataBlockHeader.HEADER_LENGTH + mbh.getDataLength();
    }
}
