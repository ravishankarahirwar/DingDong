package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.ByteArraySizeTerminated;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;

import java.nio.ByteBuffer;

/**
 * Represents a framebody for a frame identifier jaudiotagger has not implemented a framebody for.
 *
 * This is likley to be because the FrameBody is not specified in the Specification but it may just be because the code
 * has yet to be written, the library uses this framebody when it cant find an alternative. This is different to the
 * ID3v2ExtensionFrameBody Interface which should be implemented by frame bodies that are non standard such as
 * iTunes compilation frame (TCMP) but are commonly used.
 */
public class FrameBodyUnsupported extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody, ID3v22FrameBody
{
    /**
     * Because used by any unknown frame identifier varies
     */
    private String identifier = "";

    /**
     * @deprecated because no identifier set
     */
    public FrameBodyUnsupported()
    {

    }

    /**
     * Creates a new FrameBodyUnsupported
     * @param identifier
     */
    public FrameBodyUnsupported(String identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Create a new FrameBodyUnsupported
     *
     * @param identifier
     * @param value
     */
    public FrameBodyUnsupported(String identifier, byte[] value)
    {
        this.identifier = identifier;
        setObjectValue(DataTypes.OBJ_DATA, value);
    }

    /**
     * Creates a new FrameBodyUnsupported datatype.
     *
     * @param value
     * @deprecated because no identifier set
     */
    public FrameBodyUnsupported(byte[] value)
    {
        setObjectValue(DataTypes.OBJ_DATA, value);
    }

    /**
     * Copy constructor
     *
     * @param copyObject a copy is made of this
     */
    public FrameBodyUnsupported(FrameBodyUnsupported copyObject)
    {
        super(copyObject);
        this.identifier = copyObject.identifier;

    }

    /**
     * Creates a new FrameBodyUnsupported datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidFrameException if unable to create framebody from buffer
     * @throws world.best.musicplayer.utils.files.tag.InvalidTagException
     */
    public FrameBodyUnsupported(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * Return the frame identifier
     *
     * @return the identifier
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * @param obj
     * @return whether obj is equivalent to this object
     */
    public boolean equals(Object obj)
    {
        if (!(obj instanceof FrameBodyUnsupported))
        {
            return false;
        }

        FrameBodyUnsupported object = (FrameBodyUnsupported) obj;
        return this.identifier.equals(object.identifier) && super.equals(obj);
    }


    /**
     * Because the contents of this frame are an array of bytes and could be large we just
     * return the identifier.
     *
     * @return a string representation of this frame
     */
    public String toString()
    {
        return getIdentifier();
    }

    /**
     * Setup the Object List. A byte Array which will be read upto frame size
     * bytes.
     */
    protected void setupObjectList()
    {
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }

}
