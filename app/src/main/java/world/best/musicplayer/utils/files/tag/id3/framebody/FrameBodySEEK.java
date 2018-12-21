package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.datatype.NumberFixedLength;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


public class FrameBodySEEK extends AbstractID3v2FrameBody implements ID3v24FrameBody
{
    /**
     * Creates a new FrameBodySEEK datatype.
     */
    public FrameBodySEEK()
    {
        //        this.setObject("Minimum Offset to Next Tag", new Integer(0));
    }

    /**
     * Creates a new FrameBodySEEK datatype.
     *
     * @param minOffsetToNextTag
     */
    public FrameBodySEEK(int minOffsetToNextTag)
    {
        this.setObjectValue(DataTypes.OBJ_OFFSET, minOffsetToNextTag);
    }

    public FrameBodySEEK(FrameBodySEEK body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodySEEK datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodySEEK(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_AUDIO_SEEK_POINT_INDEX;
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new NumberFixedLength(DataTypes.OBJ_OFFSET, this, 4));
    }
}
