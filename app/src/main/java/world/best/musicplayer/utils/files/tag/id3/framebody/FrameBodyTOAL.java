package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Original album/movie/show title Text information frame.
 * <p>The 'Original album/movie/show title' frame is intended for the title of the original recording (or source of sound), if for example the musicplayer
 * in the file should be a cover of a previously released song.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTOAL extends AbstractFrameBodyTextInfo implements ID3v23FrameBody,ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTOAL datatype.
     */
    public FrameBodyTOAL()
    {
    }

    public FrameBodyTOAL(FrameBodyTOAL body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOAL datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTOAL(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOAL datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTOAL(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ORIG_TITLE;
    }
}