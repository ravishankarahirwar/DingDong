package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.datatype.NumberHashMap;
import world.best.musicplayer.utils.files.tag.datatype.NumberVariableLength;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;
import world.best.musicplayer.utils.files.tag.id3.valuepair.EventTimingTimestampTypes;

import java.nio.ByteBuffer;

/**
 * Position synchronisation frame.
 *
 *
 * This frame delivers information to the listener of how far into the
 * audio stream he picked up; in effect, it states the time offset of
 * the first frame in the stream. The frame layout is:
 *
 * Head for 'Position synchronisation', ID:
 * Time stamp format $xx
 * Position          $xx (xx ...)
 *
 * Where time stamp format is:
 *
 * $01 Absolute time, 32 bit sized, using MPEG frames as unit
 * $02 Absolute time, 32 bit sized, using milliseconds as unit
 *
 * and position is where in the audio the listener starts to receive,
 * i.e. the beginning of the next frame. If this frame is used in the
 * beginning of a file the value is always 0. There may only be one
 * "POSS" frame in each tag.
 *
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 *
 */
public class FrameBodyPOSS extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyPOSS datatype.
     */
    public FrameBodyPOSS()
    {
        //        this.setObject(ObjectNumberHashMap.OBJ_TIME_STAMP_FORMAT, new Byte((byte) 0));
        //        this.setObject("Position", new Long(0));
    }

    public FrameBodyPOSS(FrameBodyPOSS body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyPOSS datatype.
     *
     * @param timeStampFormat
     * @param position
     */
    public FrameBodyPOSS(byte timeStampFormat, long position)
    {
        this.setObjectValue(DataTypes.OBJ_TIME_STAMP_FORMAT, timeStampFormat);
        this.setObjectValue(DataTypes.OBJ_POSITION, position);
    }

    /**
     * Creates a new FrameBodyPOSS datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyPOSS(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_POSITION_SYNC;
    }


    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new NumberHashMap(DataTypes.OBJ_TIME_STAMP_FORMAT, this, EventTimingTimestampTypes.TIMESTAMP_KEY_FIELD_SIZE));
        objectList.add(new NumberVariableLength(DataTypes.OBJ_POSITION, this, 1));
    }
}
