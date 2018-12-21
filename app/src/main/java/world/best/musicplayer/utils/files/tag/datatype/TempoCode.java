package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.tag.InvalidDataTypeException;
import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;
import world.best.musicplayer.utils.files.tag.id3.ID3Tags;

/**
 * Represents a {@link world.best.musicplayer.utils.files.tag.id3.framebody.FrameBodySYTC} tempo code.
 *
 * The tempo is in BPM described with one or two bytes. If the
 * first byte has the value $FF, one more byte follows, which is added
 * to the first giving a range from 2 - 510 BPM, since $00 and $01 is
 * reserved. $00 is used to describe a beat-free time period, which is
 * not the same as a musicplayer-free time period. $01 is used to indicate one
 * single beat-stroke followed by a beat-free period.
 */
public class TempoCode extends AbstractDataType
{
    private static final int MINIMUM_NO_OF_DIGITS = 1;
    private static final int MAXIMUM_NO_OF_DIGITS = 2;

    public TempoCode(final TempoCode copy)
    {
        super(copy);
    }

    public TempoCode(final String identifier, final AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody, 0);
    }

    public TempoCode(final String identifier, final AbstractTagFrameBody frameBody, final Object value) {
        super(identifier, frameBody, value);
    }


    @Override
    public int getSize()
    {
        if (value == null)
        {
            return 0;
        }
        else
        {
            return ID3Tags.getWholeNumber(value) < 0xFF ? MINIMUM_NO_OF_DIGITS : MAXIMUM_NO_OF_DIGITS;
        }
    }

    @Override
    public boolean equals(final Object that)
    {
        return that instanceof TempoCode && super.equals(that);
    }

    @Override
    public void readByteArray(final byte[] arr, final int offset) throws InvalidDataTypeException
    {
        if (arr == null)
        {
            throw new NullPointerException("Byte array is null");
        }
        if (offset < 0)
        {
            throw new IllegalArgumentException("negative offset into an array offset:" + offset);
        }
        if (offset >= arr.length)
        {
            throw new InvalidDataTypeException("Offset to byte array is out of bounds: offset = " + offset + ", array.length = " + arr.length);
        }

        long lvalue = 0;
        lvalue += (arr[offset] & 0xff);
        if (lvalue == 0xFF)
        {
            lvalue += (arr[offset+1] & 0xff);
        }
        value = lvalue;
    }

    @Override
    public byte[] writeByteArray()
    {
        final int size = getSize();
        final byte[] arr = new byte[size];
        long temp = ID3Tags.getWholeNumber(value);
        int offset = 0;
        if (temp >= 0xFF)
        {
            arr[offset] = (byte)0xFF;
            offset++;
            temp -= 0xFF;
        }
        arr[offset] = (byte) (temp & 0xFF);
        return arr;
    }

    @Override
    public String toString()
    {
        return value == null ? "" : value.toString();
    }

}
