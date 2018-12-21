package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Payment URL link frames.
 * <p>The 'Payment' frame is a URL pointing at a webpage that will handle the process of paying for this file.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWPAY extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWPAY datatype.
     */
    public FrameBodyWPAY()
    {
    }

    /**
     * Creates a new FrameBodyWPAY datatype.
     *
     * @param urlLink
     */
    public FrameBodyWPAY(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWPAY(FrameBodyWPAY body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWPAY datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyWPAY(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_PAYMENT;
    }
}