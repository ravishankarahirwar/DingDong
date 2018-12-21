package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;


/**
 * Represents a timestamp field
 */
public class StringDateTime extends StringSizeTerminated
{
    /**
     * Creates a new ObjectStringDateTime datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public StringDateTime(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
    }

    public StringDateTime(StringDateTime object)
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
            this.value = value.toString().replace(' ', 'T');
        }
    }

    /**
     * @return
     */
    public Object getValue()
    {
        if (value != null)
        {
            return value.toString().replace(' ', 'T');
        }
        else
        {
            return null;
        }
    }

    public boolean equals(Object obj)
    {
        return obj instanceof StringDateTime && super.equals(obj);

    }
}
