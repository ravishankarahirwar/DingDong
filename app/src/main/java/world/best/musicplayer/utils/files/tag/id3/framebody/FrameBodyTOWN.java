package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * File owner/licensee Text information frame.
 * <p>The 'File owner/licensee' frame contains the name of the owner or licensee of the file and it's contents.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTOWN extends AbstractFrameBodyTextInfo implements ID3v23FrameBody, ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTOWN datatype.
     */
    public FrameBodyTOWN()
    {
    }

    public FrameBodyTOWN(FrameBodyTOWN body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOWN datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTOWN(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOWN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTOWN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_FILE_OWNER;
    }
}
