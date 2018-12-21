package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Beats Per Minute Text information frame.
 * <p>The 'BPM' frame contains the number of beats per minute in the mainpart of the audio. The BPM is an integer and represented as a numerical string.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTBPM extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTBPM datatype.
     */
    public FrameBodyTBPM()
    {
    }

    public FrameBodyTBPM(FrameBodyTBPM body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTBPM datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTBPM(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTBPM datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTBPM(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_BPM;
    }
}