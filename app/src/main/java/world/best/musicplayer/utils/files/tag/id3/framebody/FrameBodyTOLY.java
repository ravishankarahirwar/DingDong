package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Original lyricist(s)/text writer(s) Text information frame.
 * <p>The 'Original lyricist(s)/text writer(s)' frame is intended for the text writer(s) of the original recording, if for example the musicplayer in the file should be a cover of a previously released song. The text writers are seperated with the "/" character.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTOLY extends AbstractFrameBodyTextInfo implements ID3v23FrameBody,ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTOLY datatype.
     */
    public FrameBodyTOLY()
    {
    }

    public FrameBodyTOLY(FrameBodyTOLY body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTOLY datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTOLY(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTOLY datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTOLY(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ORIG_LYRICIST;
    }
}