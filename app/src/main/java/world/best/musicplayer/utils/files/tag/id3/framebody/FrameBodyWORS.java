package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Official internet radio station homepage URL link frames.
 * <p>The 'Official internet radio station homepage' contains a URL pointing at the homepage of the internet radio station.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWORS extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWORS datatype.
     */
    public FrameBodyWORS()
    {
    }

    /**
     * Creates a new FrameBodyWORS datatype.
     *
     * @param urlLink
     */
    public FrameBodyWORS(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWORS(FrameBodyWORS body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWORS datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyWORS(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_OFFICIAL_RADIO;
    }
}