package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v23Frames;

import java.nio.ByteBuffer;

/**
 * Recording dates Text information frame.
 * <p>The 'Recording dates' frame is a intended to be used as complement to the "TYER", "TDAT" and "TIME" frames. E.g. "4th-7th June, 12th June" in combination with the "TYER" frame.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTRDA extends AbstractFrameBodyTextInfo implements ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTRDA datatype.
     */
    public FrameBodyTRDA()
    {
    }

    public FrameBodyTRDA(FrameBodyTRDA body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTRDA datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTRDA(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTRDA datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTRDA(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v23Frames.FRAME_ID_V3_TRDA;
    }
}
