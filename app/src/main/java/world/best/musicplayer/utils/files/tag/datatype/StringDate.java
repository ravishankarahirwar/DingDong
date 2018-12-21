package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;
import world.best.musicplayer.utils.files.tag.id3.ID3Tags;

/**
 * Represents a timestamp field
 */
public class StringDate extends StringFixedLength
{
    /**
     * Creates a new ObjectStringDate datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public StringDate(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody, 8);
    }

    public StringDate(StringDate object)
    {
        super(object);
    }

    /**
     * @param value
     */
    public void setValue(Object value)
    {
        if (value != null)
        {
            this.value = ID3Tags.stripChar(value.toString(), '-');
        }
    }

    /**
     * @return
     */
    public Object getValue()
    {
        if (value != null)
        {
            return ID3Tags.stripChar(value.toString(), '-');
        }
        else
        {
            return null;
        }
    }

    public boolean equals(Object obj)
    {
        return obj instanceof StringDate && super.equals(obj);

    }
}
