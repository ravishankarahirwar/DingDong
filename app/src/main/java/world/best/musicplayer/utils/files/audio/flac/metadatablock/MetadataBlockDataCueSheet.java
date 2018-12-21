package world.best.musicplayer.utils.files.audio.flac.metadatablock;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Cuesheet Block
 *
 * <p>This block is for storing various information that can be used in a cue sheet. It supports track and index points,
 * compatible with Red Book CD digital audio discs, as well as other CD-DA metadata such as media catalog number and
 * track ISRCs. The CUESHEET block is especially useful for backing up CD-DA discs, but it can be used as a general
 * purpose cueing mechanism for playback
 */
public class MetadataBlockDataCueSheet implements MetadataBlockData
{
    private byte[] data;

    public MetadataBlockDataCueSheet(MetadataBlockHeader header, RandomAccessFile raf) throws IOException
    {
        data = new byte[header.getDataLength()];
        raf.readFully(data);
    }

    public byte[] getBytes()
    {
        return data;
    }

    public int getLength()
    {
        return data.length;
    }
}
