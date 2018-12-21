package world.best.musicplayer.utils.files.audio.asf.io;

import world.best.musicplayer.utils.files.audio.asf.data.Chunk;
import world.best.musicplayer.utils.files.audio.asf.data.GUID;
import world.best.musicplayer.utils.files.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Default reader, Reads GUID and size out of an input stream and creates a
 * {@link world.best.musicplayer.utils.files.audio.asf.data.Chunk}object, finally skips the
 * remaining chunk bytes.
 */
final class ChunkHeaderReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = { GUID.GUID_UNSPECIFIED };

    /**
     * Default instance.
     */
    private static final ChunkHeaderReader INSTANCE = new ChunkHeaderReader();

    /**
     * Returns an instance of the reader.
     * 
     * @return instance.
     */
    public static ChunkHeaderReader getInstance() {
        return INSTANCE;
    }

    /**
     * Hidden Utility class constructor.
     */
    private ChunkHeaderReader() {
        // Hidden
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
        stream.skip(chunkLen.longValue() - 24);
        return new Chunk(guid, chunkStart, chunkLen);
    }

}