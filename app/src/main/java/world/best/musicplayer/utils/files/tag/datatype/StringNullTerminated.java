package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;
import world.best.musicplayer.utils.files.tag.id3.valuepair.TextEncoding;

/**
 * Represents a String whose size is determined by finding of a null character at the end of the String with fixed text encoding.
 *
 * The String will be encoded using the default encoding regardless of what encoding may be specified in the framebody
 */
public class StringNullTerminated extends TextEncodedStringNullTerminated
{
    /**
     * Creates a new ObjectStringNullTerminated datatype.
     *
     * @param identifier identifies the frame type
     * @param frameBody
     */
    public StringNullTerminated(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
    }

    public StringNullTerminated(StringNullTerminated object)
    {
        super(object);
    }

    public boolean equals(Object obj)
    {
        return obj instanceof StringNullTerminated && super.equals(obj);
    }

    protected String getTextEncodingCharSet()
    {
        return TextEncoding.CHARSET_ISO_8859_1;
    }
}
