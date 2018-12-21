package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.ByteArraySizeTerminated;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.datatype.StringNullTerminated;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Private frame.
 *
 *
 * This frame is used to contain information from a software producer
 * that its program uses and does not fit into the other frames. The
 * frame consists of an 'Owner identifier' string and the binary data.
 * The 'Owner identifier' is a null-terminated string with a URL
 * containing an email address, or a link to a location where an email
 * address can be found, that belongs to the organisation responsible
 * for the frame. Questions regarding the frame should be sent to the
 * indicated email address. The tag may contain more than one "PRIV"
 * frame but only with different contents. It is recommended to keep the
 * number of "PRIV" frames as low as possible.
 *
 * Header for 'Private frame'
 * Owner identifier
 * The private data
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyPRIV extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyPRIV datatype.
     */
    public FrameBodyPRIV()
    {
        this.setObjectValue(DataTypes.OBJ_OWNER, "");
        this.setObjectValue(DataTypes.OBJ_DATA, new byte[0]);
    }

    public FrameBodyPRIV(FrameBodyPRIV body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyPRIV datatype.
     *
     * @param owner
     * @param data
     */
    public FrameBodyPRIV(String owner, byte[] data)
    {
        this.setObjectValue(DataTypes.OBJ_OWNER, owner);
        this.setObjectValue(DataTypes.OBJ_DATA, data);
    }

    /**
     * Creates a new FrameBodyPRIV datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyPRIV(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * @param data
     */
    public void setData(byte[] data)
    {
        setObjectValue(DataTypes.OBJ_DATA, data);
    }

    /**
     * @return
     */
    public byte[] getData()
    {
        return (byte[]) getObjectValue(DataTypes.OBJ_DATA);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier()
    {
        return ID3v24Frames.FRAME_ID_PRIVATE;
    }

    /**
     * @param owner
     */
    public void setOwner(String owner)
    {
        setObjectValue(DataTypes.OBJ_OWNER, owner);
    }

    /**
     * @return
     */
    public String getOwner()
    {
        return (String) getObjectValue(DataTypes.OBJ_OWNER);
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new StringNullTerminated(DataTypes.OBJ_OWNER, this));
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }
}
