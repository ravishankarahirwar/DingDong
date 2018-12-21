package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

public class FrameBodyTPRO extends AbstractFrameBodyTextInfo implements ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTPRO datatype.
     */
    public FrameBodyTPRO()
    {
    }

    public FrameBodyTPRO(FrameBodyTPRO body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTPRO datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTPRO(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTPRO datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTPRO(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_PRODUCED_NOTICE;
    }
}