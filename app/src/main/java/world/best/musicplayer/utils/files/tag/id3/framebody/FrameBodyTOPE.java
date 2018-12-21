package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Original artist(s)/performer(s) Text information frame.
 * <p>The 'Original artist(s)/performer(s)' frame is intended for the performer(s) of the original recording, if for
 * example the musicplayer in the file should be a cover of a previously released song. The performers are separated with
 * the "/" character.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTOPE extends AbstractFrameBodyTextInfo implements ID3v23FrameBody, ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTOPE datatype.
     */
    public FrameBodyTOPE()
    {
    }

    public FrameBodyTOPE(FrameBodyTOPE body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOPE datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTOPE(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOPE datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTOPE(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ORIGARTIST;
    }
}