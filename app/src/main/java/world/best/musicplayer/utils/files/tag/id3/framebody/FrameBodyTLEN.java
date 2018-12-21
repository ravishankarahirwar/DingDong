package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Length Text information frame.
 * <p>The 'Length' frame contains the length of the audiofile in milliseconds, represented as a numeric string.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTLEN extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTLEN datatype.
     */
    public FrameBodyTLEN()
    {
    }

    public FrameBodyTLEN(FrameBodyTLEN body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTLEN datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTLEN(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTLEN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTLEN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_LENGTH;
    }
}