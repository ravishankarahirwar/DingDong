package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Official audio source webpage URL link frames.
 * <p>The 'Official audio source webpage' frame is a URL pointing at the official webpage for the source of the audio
 * file, e.g. a movie.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyWOAS extends AbstractFrameBodyUrlLink implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyWOAS datatype.
     */
    public FrameBodyWOAS()
    {
    }

    /**
     * Creates a new FrameBodyWOAS datatype.
     *
     * @param urlLink
     */
    public FrameBodyWOAS(String urlLink)
    {
        super(urlLink);
    }

    public FrameBodyWOAS(FrameBodyWOAS body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyWOAS datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyWOAS(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_URL_SOURCE_WEB;
    }
}