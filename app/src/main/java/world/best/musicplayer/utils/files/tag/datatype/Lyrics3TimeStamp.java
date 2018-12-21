package world.best.musicplayer.utils.files.tag.datatype;

import world.best.musicplayer.utils.files.audio.generic.Utils;
import world.best.musicplayer.utils.files.tag.InvalidDataTypeException;
import world.best.musicplayer.utils.files.tag.id3.AbstractTagFrameBody;

public class Lyrics3TimeStamp extends AbstractDataType
{
    /**
     *
     */
    private long minute = 0;

    /**
     *
     */
    private long second = 0;

    /**
     * Todo this is wrong
     * @param s
     */
    public void readString(String s)
    {
    }

    /**
     * Creates a new ObjectLyrics3TimeStamp datatype.
     *
     * @param identifier
     * @param frameBody
     */
    public Lyrics3TimeStamp(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
    }

    public Lyrics3TimeStamp(String identifier)
    {
        super(identifier, null);
    }

    public Lyrics3TimeStamp(Lyrics3TimeStamp copy)
    {
        super(copy);
        this.minute = copy.minute;
        this.second = copy.second;
    }

    public void setMinute(long minute)
    {
        this.minute = minute;
    }

    /**
     * @return
     */
    public long getMinute()
    {
        return minute;
    }

    public void setSecond(long second)
    {
        this.second = second;
    }

    /**
     * @return
     */
    public long getSecond()
    {
        return second;
    }

    /**
     * @return
     */
    public int getSize()
    {
        return 7;
    }

    /**
     * Creates a new ObjectLyrics3TimeStamp datatype.
     *
     * @param timeStamp
     * @param timeStampFormat
     */
    public void setTimeStamp(long timeStamp, byte timeStampFormat)
    {
        /**
         * @todo convert both types of formats
         */
        timeStamp = timeStamp / 1000;
        minute = timeStamp / 60;
        second = timeStamp % 60;
    }

    /**
     * @param obj
     * @return
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Lyrics3TimeStamp))
        {
            return false;
        }

        Lyrics3TimeStamp object = (Lyrics3TimeStamp) obj;

        if (this.minute != object.minute)
        {
            return false;
        }

        return this.second == object.second && super.equals(obj);

    }

    /**
     * @param timeStamp
     * @param offset
     * @throws NullPointerException
     * @throws IndexOutOfBoundsException
     */
    public void readString(String timeStamp, int offset)
    {
        if (timeStamp == null)
        {
            throw new NullPointerException("Image is null");
        }

        if ((offset < 0) || (offset >= timeStamp.length()))
        {
            throw new IndexOutOfBoundsException("Offset to timeStamp is out of bounds: offset = " + offset + ", timeStamp.length()" + timeStamp.length());
        }

        timeStamp = timeStamp.substring(offset);

        if (timeStamp.length() == 7)
        {
            minute = Integer.parseInt(timeStamp.substring(1, 3));
            second = Integer.parseInt(timeStamp.substring(4, 6));
        }
        else
        {
            minute = 0;
            second = 0;
        }
    }

    /**
     * @return
     */
    public String toString()
    {
        return writeString();
    }

    /**
     * @return
     */
    public String writeString()
    {
        String str;
        str = "[";

        if (minute < 0)
        {
            str += "00";
        }
        else
        {
            if (minute < 10)
            {
                str += '0';
            }

            str += Long.toString(minute);
        }

        str += ':';

        if (second < 0)
        {
            str += "00";
        }
        else
        {
            if (second < 10)
            {
                str += '0';
            }

            str += Long.toString(second);
        }

        str += ']';

        return str;
    }

    public void readByteArray(byte[] arr, int offset) throws InvalidDataTypeException
    {
        readString(arr.toString(), offset);
    }

    public byte[] writeByteArray()
    {
        return Utils.getDefaultBytes(writeString(), "ISO8859-1");
    }

}
