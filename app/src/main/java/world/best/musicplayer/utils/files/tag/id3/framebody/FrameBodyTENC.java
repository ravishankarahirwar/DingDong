package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Encoded by Text information frame.
 * <p>The 'Encoded by' frame contains the name of the person or organisation that encoded the audio file.
 *  This field may contain a copyright message, if the audio file also is copyrighted by the encoder.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTENC extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTENC dataType.
     */
    public FrameBodyTENC()
    {
    }

    public FrameBodyTENC(FrameBodyTENC body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTENC dataType.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTENC(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTENC dataType.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTENC(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ENCODEDBY;
    }
}