package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.InvalidDataTypeException;
import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;

public class BooleanString extends AbstractDataType
{
    /**
     * Creates a new ObjectBooleanString datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public BooleanString(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
    }

    public BooleanString(BooleanString object)
    {
        super(object);
    }

    /**
     * @return
     */
    public int getSize()
    {
        return 1;
    }

    public boolean equals(Object obj)
    {
        return obj instanceof BooleanString && super.equals(obj);

    }

    /**
     * @param offset
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readByteArray(byte[] arr, int offset) throws InvalidDataTypeException
    {
        byte b = arr[offset];
        value = b != '0';
    }

    /**
     * @return
     */
    public String toString()
    {
        return "" + value;
    }

    /**
     * @return
     */
    public byte[] writeByteArray()
    {
        byte[] booleanValue = new byte[1];
        if (value == null)
        {
            booleanValue[0] = '0';
        }
        else
        {
            if ((Boolean) value)
            {
                booleanValue[0] = '0';
            }
            else
            {
                booleanValue[0] = '1';
            }
        }
        return booleanValue;
    }
}
