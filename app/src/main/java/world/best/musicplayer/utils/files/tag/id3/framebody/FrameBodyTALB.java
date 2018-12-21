package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Album/Movie/Show title Text information frame.
 * <p>The 'Album/Movie/Show title' frame is intended for the title of the recording(/source of sound) which the audio in the file is taken from.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTALB extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTALB datatype.
     */
    public FrameBodyTALB()
    {
    }

    public FrameBodyTALB(FrameBodyTALB body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTALB datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTALB(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTALB datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodyTALB(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ALBUM;
    }
}
