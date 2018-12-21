package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Commercial information URL link frames.
 * <p>The 'Commercial information' frame is a URL pointing at a webpage with information such as where the album can be
 *  bought. There may be more than one "WCOM" frame in a tag, but not with the same content.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWCOM extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWCOM datatype.
     */
    public FrameBodyWCOM()
    {
    }

    /**
     * Creates a new FrameBodyWCOM datatype.
     *
     * @param urlLink
     */
    public FrameBodyWCOM(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWCOM(FrameBodyWCOM body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWCOM datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyWCOM(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_COMMERCIAL;
    }
}