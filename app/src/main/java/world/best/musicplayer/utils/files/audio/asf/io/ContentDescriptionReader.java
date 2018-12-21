package world.best.musicplayer.utils.files.audio.asf.io;

import world.best.musicplayer.utils.files.audio.asf.data.Chunk;
import world.best.musicplayer.utils.files.audio.asf.data.ContentDescription;
import world.best.musicplayer.utils.files.audio.asf.data.GUID;
import world.best.musicplayer.utils.files.audio.asf.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Reads and interprets the data of a ASF chunk containing title, author... <br>
 *
 * @see world.best.musicplayer.utils.files.audio.asf.data.ContentDescription
 */
public class ContentDescriptionReader implements ChunkReader {

    /**
     * The GUID this reader {@linkplain #getApplyingIds() applies to}
     */
    private final static GUID[] APPLYING = { GUID.GUID_CONTENTDESCRIPTION };

    /**
     * Should not be used for now.
     */
    protected ContentDescriptionReader() {
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
     * Returns the next 5 UINT16 values as an array.<br>
     * 
     * @param stream
     *            stream to read from
     * @return 5 int values read from stream.
     * @throws IOException
     *             on I/O Errors.
     */
    private int[] getStringSizes(final InputStream stream) throws IOException {
        final int[] result = new int[5];
        for (int i = 0; i < result.length; i++) {
            result[i] = Utils.readUINT16(stream);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Chunk read(final GUID guid, final InputStream stream,
            final long chunkStart) throws IOException {
        final BigInteger chunkSize = Utils.readBig64(stream);
        /*
         * Now comes 16-Bit values representing the length of the Strings which
         * follows.
         */
        final int[] stringSizes = getStringSizes(stream);

        /*
         * Now we know the String length of each occuring String.
         */
        final String[] strings = new String[stringSizes.length];
        for (int i = 0; i < strings.length; i++) {
            if (stringSizes[i] > 0) {
                strings[i] = Utils
                        .readFixedSizeUTF16Str(stream, stringSizes[i]);
            }
        }
        /*
         * Now create the result
         */
        final ContentDescription result = new ContentDescription(chunkStart,
                chunkSize);
        if (stringSizes[0] > 0) {
            result.setTitle(strings[0]);
        }
        if (stringSizes[1] > 0) {
            result.setAuthor(strings[1]);
        }
        if (stringSizes[2] > 0) {
            result.setCopyright(strings[2]);
        }
        if (stringSizes[3] > 0) {
            result.setComment(strings[3]);
        }
        if (stringSizes[4] > 0) {
            result.setRating(strings[4]);
        }
        return result;
    }
}