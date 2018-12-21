package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


/**
 * Content group description Text information frame.
 * <p>The 'Content group description' frame is used if the sound belongs to a larger category of sounds/musicplayer.
 * For example, classical musicplayer is often sorted in different musical sections (e.g. "Piano Concerto", "Weather - Hurricane").
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTIT1 extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTIT1 datatype.
     */
    public FrameBodyTIT1()
    {
    }

    public FrameBodyTIT1(FrameBodyTIT1 body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIT1 datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTIT1(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIT1 datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTIT1(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_CONTENT_GROUP_DESC;
    }
}