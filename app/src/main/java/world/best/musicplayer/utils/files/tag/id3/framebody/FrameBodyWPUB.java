package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Publishers official webpage URL link frames.
 * <p>The 'Publishers official webpage' frame is a URL pointing at the official wepage for the publisher.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWPUB extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWPUB datatype.
     */
    public FrameBodyWPUB()
    {
    }

    /**
     * Creates a new FrameBodyWPUB datatype.
     *
     * @param urlLink
     */
    public FrameBodyWPUB(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWPUB(FrameBodyWPUB body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWPUB datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyWPUB(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_PUBLISHERS;
    }
}