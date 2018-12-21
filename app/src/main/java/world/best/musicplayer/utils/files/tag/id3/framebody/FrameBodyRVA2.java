package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.ByteArraySizeTerminated;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

public class FrameBodyRVA2 extends AbstractID3v2FrameBody implements ID3v24FrameBody
{

    /**
     * Creates a new FrameBodyRVA2 datatype.
     */
    public FrameBodyRVA2()
    {
    }

    public FrameBodyRVA2(FrameBodyRVA2 body)
    {
        super(body);
    }

    /**
     * Convert from V3 to V4 Frame
     * @param body
     */
    public FrameBodyRVA2(FrameBodyRVAD body)
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
    public FrameBodyRVA2(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_RELATIVE_VOLUME_ADJUSTMENT2;
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
