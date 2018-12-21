package world.best.musicplayer.utils.files.audio.flac.metadatablock;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Application Block
 *
 * <p>This block is for use by third-party applications. The only mandatory field is a 32-bit identifier.
 * This ID is granted upon request to an application by the FLAC maintainers. The remainder is of the block is defined
 * by the registered application.
 */
public class MetadataBlockDataApplication implements MetadataBlockData
{

    private byte[] data;

    public MetadataBlockDataApplication(MetadataBlockHeader header, RandomAccessFile raf) throws IOException
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
