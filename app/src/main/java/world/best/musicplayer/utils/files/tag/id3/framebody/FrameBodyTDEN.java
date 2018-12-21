package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


public class FrameBodyTDEN extends AbstractFrameBodyTextInfo implements ID3v24FrameBody {
    /**
     * Creates a new FrameBodyTDEN datatype.
     */
    public FrameBodyTDEN()
    {
    }

    public FrameBodyTDEN(FrameBodyTDEN body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDEN datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTDEN(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDEN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTDEN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ENCODING_TIME;
    }
}
