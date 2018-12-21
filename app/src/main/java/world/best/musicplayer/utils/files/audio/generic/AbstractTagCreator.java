package world.best.musicplayer.utils.files.audio.generic;

import world.best.musicplayer.utils.files.tag.Tag;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Abstract class for creating the raw content that represents the tag so it can be written
 * to file.
 */
public abstract class AbstractTagCreator
{
    /**
     * Convert tagdata to rawdata ready for writing to file with no additional padding
     *
     * @param tag
     * @return
     * @throws UnsupportedEncodingException
     */
    public ByteBuffer convert(Tag tag) throws UnsupportedEncodingException
    {
        return convert(tag, 0);
    }

    /**
     * Convert tagdata to rawdata ready for writing to file
     *
     * @param tag
     * @param padding TODO is this padding or additional padding
     * @return
     * @throws UnsupportedEncodingException
     */
    public abstract ByteBuffer convert(Tag tag, int padding) throws UnsupportedEncodingException;
}
