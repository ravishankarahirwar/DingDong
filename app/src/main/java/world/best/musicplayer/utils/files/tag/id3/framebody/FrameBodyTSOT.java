package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


/**
 * Title Sort name
 */
public class FrameBodyTSOT extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTSOT datatype.
     */
    public FrameBodyTSOT()
    {
    }

    public FrameBodyTSOT(FrameBodyTSOT body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTSOT datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTSOT(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTSOT datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTSOT(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_TITLE_SORT_ORDER;
    }
}
