package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.ByteArraySizeTerminated;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.id3.ID3v23Frames;

import java.nio.ByteBuffer;

/**
 * Relative volume adjustment frame.
 *
 * Only partially implemented.
 */
public class FrameBodyRVAD extends AbstractID3v2FrameBody implements ID3v23FrameBody
{

    /**
     * Creates a new FrameBodyRVAD datatype.
     */
    public FrameBodyRVAD()
    {

    }

    public FrameBodyRVAD(FrameBodyRVAD copyObject)
    {
        super(copyObject);

    }


    /**
     * Convert from V4 to V3 Frame
     * @param body
     */
    public FrameBodyRVAD(FrameBodyRVA2 body)
    {
        setObjectValue(DataTypes.OBJ_DATA, body.getObjectValue(DataTypes.OBJ_DATA));
    }

    /**
     * Creates a new FrameBodyRVAD datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyRVAD(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v23Frames.FRAME_ID_V3_RELATIVE_VOLUME_ADJUSTMENT;
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
