package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v23Frames;

import java.nio.ByteBuffer;

/**
 * Size Text information frame.
 * <p>The 'Size' frame contains the size of the audiofile in bytes, excluding the ID3v2 tag, represented as a numeric string.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTSIZ extends AbstractFrameBodyTextInfo implements ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTSIZ datatype.
     */
    public FrameBodyTSIZ()
    {
    }

    public FrameBodyTSIZ(FrameBodyTSIZ body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSIZ datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTSIZ(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSIZ datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTSIZ(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v23Frames.FRAME_ID_V3_TSIZ;
    }
}