package world.best.musicplayer.utils.files.audio.asf.io;

import world.best.musicplayer.utils.files.audio.asf.data.Chunk;
import world.best.musicplayer.utils.files.audio.asf.data.GUID;
import world.best.musicplayer.utils.files.audio.asf.data.StreamBitratePropertiesChunk;
import world.best.musicplayer.utils.files.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This class reads the chunk containing the stream bitrate properties.<br>
 */
public class StreamBitratePropertiesReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = { GUID.GUID_STREAM_BITRATE_PROPERTIES };

    /**
     * Should not be used for now.
     */
    protected StreamBitratePropertiesReader() {
        // NOTHING toDo
    }

    /**
     * {@inheritDoc}
     */
    public boolean canFail() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public GUID[] getApplyingIds() {
        return APPLYING.clone();
    }

    /**
     * {@inheritDoc}
     */
    public Chunk read(final GUID guid, final InputStream stream,
            final long chunkStart) throws IOException {
        final BigInteger chunkLen = Utils.readBig64(stream);
        final StreamBitratePropertiesChunk result = new StreamBitratePropertiesChunk(
                chunkLen);

        /*
         * Read the amount of bitrate records
         */
        final long recordCount = Utils.readUINT16(stream);
        for (int i = 0; i < recordCount; i++) {
            final int flags = Utils.readUINT16(stream);
            final long avgBitrate = Utils.readUINT32(stream);
            result.addBitrateRecord(flags & 0x00FF, avgBitrate);
        }

        result.setPosition(chunkStart);

        return result;
    }

}