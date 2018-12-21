package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * <p>The 'Tagging time' frame contains a timestamp describing then the
 *  audio was tagged. Timestamp format is described in the ID3v2
 *  structure document
 */
public class FrameBodyTDTG extends AbstractFrameBodyTextInfo implements ID3v24FrameBody
{

    /**
     * Creates a new FrameBodyTDTG datatype.
     */
    public FrameBodyTDTG()
    {
    }

    public FrameBodyTDTG(FrameBodyTDTG body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDTG datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTDTG(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDTG datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTDTG(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * @return the frame identifier
     */
    public String getIdentifier()
    {
        return ID3v24Frames.FRAME_ID_TAGGING_TIME;
    }


}
