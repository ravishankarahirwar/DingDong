package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Software/Hardware and settings used for encoding Text information frame.
 * <p>The 'Software/Hardware and settings used for encoding' frame includes the used audio encoder and its settings when the file was encoded. Hardware refers to hardware encoders, not the computer on which a program was run.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTSSE extends AbstractFrameBodyTextInfo implements ID3v23FrameBody, ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTSSE datatype.
     */
    public FrameBodyTSSE()
    {
    }

    public FrameBodyTSSE(FrameBodyTSSE body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSSE datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTSSE(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSSE datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTSSE(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_HW_SW_SETTINGS;
    }
}