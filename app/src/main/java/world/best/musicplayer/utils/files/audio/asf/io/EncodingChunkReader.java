package world.best.musicplayer.utils.files.audio.asf.io;

import world.best.musicplayer.utils.files.audio.asf.data.Chunk;
import world.best.musicplayer.utils.files.audio.asf.data.EncodingChunk;
import world.best.musicplayer.utils.files.audio.asf.data.GUID;
import world.best.musicplayer.utils.files.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This class reads the chunk containing encoding data <br>
 * <b>Warning:<b><br>
 * Implementation is not completed. More analysis of this chunk is needed.
 */
class EncodingChunkReader implements ChunkReader {
    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = { GUID.GUID_ENCODING };

    /**
     * Should not be used for now.
     */
    protected EncodingChunkReader() {
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
        final EncodingChunk result = new EncodingChunk(chunkLen);
        int readBytes = 24;
        // Can't be interpreted
        /*
         * What do I think of this data, well it seems to be another GUID. Then
         * followed by a UINT16 indicating a length of data following (by half).
         * My test files just had the length of one and a two bytes zero.
         */
        stream.skip(20);
        readBytes += 20;

        /*
         * Read the number of strings which will follow
         */
        final int stringCount = Utils.readUINT16(stream);
        readBytes += 2;

        /*
         * Now reading the specified amount of strings.
         */
        for (int i = 0; i < stringCount; i++) {
            final String curr = Utils.readCharacterSizedString(stream);
            result.addString(curr);
            readBytes += 4 + 2 * curr.length();
        }
        stream.skip(chunkLen.longValue() - readBytes);
        result.setPosition(chunkStart);
        return result;
    }

}