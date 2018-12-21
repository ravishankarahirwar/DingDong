package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Band/Orchestra/Accompaniment Text information frame.
 * <p>The 'Band/Orchestra/Accompaniment' frame is used for additional information about the performers in the recording.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTPE2 extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTPE2 datatype.
     */
    public FrameBodyTPE2()
    {
    }

    public FrameBodyTPE2(FrameBodyTPE2 body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPE2 datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTPE2(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPE2 datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTPE2(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ACCOMPANIMENT;
    }
}