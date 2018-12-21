package world.best.musicplayer.utils.files.audio.asf.data;

import world.best.musicplayer.utils.files.audio.asf.util.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class was intended to store the data of a chunk which contained the
 * encoding parameters in textual form. <br>
 * Since the needed parameters were found in other chunks the implementation of
 * this class was paused. <br>
 */
public class EncodingChunk extends Chunk {

    /**
     * The read strings.
     */
    private final List<String> strings;

    /**
     * Creates an instance.
     * 
     * @param chunkLen
     *            Length of current chunk.
     */
    public EncodingChunk(final BigInteger chunkLen) {
        super(GUID.GUID_ENCODING, chunkLen);
        this.strings = new ArrayList<String>();
    }

    /**
     * This method appends a String.
     * 
     * @param toAdd
     *            String to add.
     */
    public void addString(final String toAdd) {
        this.strings.add(toAdd);
    }

    /**
     * This method returns a collection of all {@linkplain String Strings} which
     * were added due {@link #addString(String)}.
     * 
     * @return Inserted Strings.
     */
    public Collection<String> getStrings() {
        return new ArrayList<String>(this.strings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String prettyPrint(final String prefix) {
        final StringBuilder result = new StringBuilder(super
                .prettyPrint(prefix));
        this.strings.iterator();
        for (final String string : this.strings) {
            result.append(prefix).append("  | : ").append(string).append(
                    Utils.LINE_SEPARATOR);
        }
        return result.toString();
    }
}