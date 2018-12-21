package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Subtitle/Description refinement Text information frame.
 * <p>The 'Subtitle/Description refinement' frame is used for information directly related to the contents title (e.g. "Op. 16" or "Performed live at Wembley").
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTIT3 extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTIT3 datatype.
     */
    public FrameBodyTIT3()
    {
    }

    public FrameBodyTIT3(FrameBodyTIT3 body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTIT3 datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTIT3(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTIT3 datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTIT3(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_TITLE_REFINEMENT;
    }
}