package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.BooleanByte;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.datatype.NumberFixedLength;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


/**
 * Body of Recommended buffer size frame, generally used for streaming audio
 */
public class FrameBodyRBUF extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody
{
    private static int BUFFER_FIELD_SIZE = 3;
    private static int EMBED_FLAG_BIT_POSITION = 1;
    private static int OFFSET_FIELD_SIZE = 4;

    /**
     * Creates a new FrameBodyRBUF datatype.
     */
    public FrameBodyRBUF()
    {
        this.setObjectValue(DataTypes.OBJ_BUFFER_SIZE, (byte) 0);
        this.setObjectValue(DataTypes.OBJ_EMBED_FLAG, Boolean.FALSE);
        this.setObjectValue(DataTypes.OBJ_OFFSET, (byte) 0);
    }

    public FrameBodyRBUF(FrameBodyRBUF body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyRBUF datatype.
     *
     * @param bufferSize
     * @param embeddedInfoFlag
     * @param offsetToNextTag
     */
    public FrameBodyRBUF(byte bufferSize, boolean embeddedInfoFlag, byte offsetToNextTag)
    {
        this.setObjectValue(DataTypes.OBJ_BUFFER_SIZE, bufferSize);
        this.setObjectValue(DataTypes.OBJ_EMBED_FLAG, embeddedInfoFlag);
        this.setObjectValue(DataTypes.OBJ_OFFSET, offsetToNextTag);
    }

    /**
     * Creates a new FrameBodyRBUF datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyRBUF(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier()
    {
        return ID3v24Frames.FRAME_ID_RECOMMENDED_BUFFER_SIZE;
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new NumberFixedLength(DataTypes.OBJ_BUFFER_SIZE, this, BUFFER_FIELD_SIZE));
        objectList.add(new BooleanByte(DataTypes.OBJ_EMBED_FLAG, this, (byte) EMBED_FLAG_BIT_POSITION));
        objectList.add(new NumberFixedLength(DataTypes.OBJ_OFFSET, this, OFFSET_FIELD_SIZE));
    }
}
