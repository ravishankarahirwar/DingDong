package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v23Frames;

import java.nio.ByteBuffer;

/**
 * Time Text information frame.
 * <p>The 'Time' frame is a numeric string in the HHMM format containing the time for the recording. This field is always four characters long.
 * <p>Deprecated in v2.4.0
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTIME extends AbstractFrameBodyTextInfo implements ID3v23FrameBody
{
    private boolean hoursOnly;
    /**
     * Creates a new FrameBodyTIME datatype.
     */
    public FrameBodyTIME()
    {
    }

    public FrameBodyTIME(FrameBodyTIME body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIME datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTIME(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIME datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTIME(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v23Frames.FRAME_ID_V3_TIME;
    }

    public boolean isHoursOnly()
    {
        return hoursOnly;
    }

    public void setHoursOnly(boolean hoursOnly)
    {
        this.hoursOnly = hoursOnly;
    }
}