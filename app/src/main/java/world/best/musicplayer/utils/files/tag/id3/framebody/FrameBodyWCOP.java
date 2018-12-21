package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Copyright/Legal information URL link frames.
 * <p>The 'Copyright/Legal information' frame is a URL pointing at a webpage where the terms of use and ownership of the file is described.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWCOP extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWCOP datatype.
     */
    public FrameBodyWCOP()
    {
    }

    /**
     * Creates a new FrameBodyWCOP datatype.
     *
     * @param urlLink
     */
    public FrameBodyWCOP(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWCOP(FrameBodyWCOP body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWCOP datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyWCOP(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_COPYRIGHT;
    }
}