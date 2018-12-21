package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;
import world.best.musicplayer.utils.files.tag.reference.MusicalKey;

import java.nio.ByteBuffer;

/**
 * Initial key Text information frame.
 * <p>The 'Initial key' frame contains the musical key in which the sound starts. It is represented as a string with
 * a maximum length of three characters. The ground keys are represented with "A","B","C","D","E", "F" and "G" and halfkeys represented
 * with "b" and "#". Minor is represented as "m". Example "Cbm". Off key is represented with an "o" only.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTKEY extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTKEY datatype.
     */
    public FrameBodyTKEY()
    {
    }

    public FrameBodyTKEY(FrameBodyTKEY body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTKEY datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTKEY(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTKEY datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTKEY(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_INITIAL_KEY;
    }

    /**
     *
     * @return true if text value is valid musical key notation
     */
    public boolean isValid()
    {
        return MusicalKey.isValid(getFirstTextValue());
    }
}