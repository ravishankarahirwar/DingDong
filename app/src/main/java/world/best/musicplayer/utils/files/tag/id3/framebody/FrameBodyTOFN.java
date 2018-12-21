package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Original filename Text information frame.
 * <p>The 'Original filename' frame contains the preferred filename for the file, since some media doesn't allow the
 * desired length of the filename. The filename is case sensitive and includes its suffix.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTOFN extends AbstractFrameBodyTextInfo implements ID3v23FrameBody, ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTOFN datatype.
     */
    public FrameBodyTOFN()
    {
    }

    public FrameBodyTOFN(FrameBodyTOFN body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOFN datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTOFN(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOFN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTOFN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ORIG_FILENAME;
    }
}