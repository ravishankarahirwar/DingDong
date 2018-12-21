package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Official artist/performer webpage URL link frames.
 * <p>The 'Official artist/performer webpage' frame is a URL pointing at the artists official webpage.
 * There may be more than one "WOAR" frame in a tag if the audio contains more than one performer, but not with
 * the same content.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWOAR extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWOAR datatype.
     */
    public FrameBodyWOAR()
    {
    }

    /**
     * Creates a new FrameBodyWOAR datatype.
     *
     * @param urlLink
     */
    public FrameBodyWOAR(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWOAR(FrameBodyWOAR body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWOAR datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyWOAR(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_ARTIST_WEB;
    }
}
