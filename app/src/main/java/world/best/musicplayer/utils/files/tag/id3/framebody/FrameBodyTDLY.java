package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;

/**
 * Playlist delay Text information frame.
 * <p>The 'Playlist delay' defines the numbers of milliseconds of silence between every song in a playlist. The player should use the "ETC" frame, if present, to skip initial silence and silence at the end of the audio to match the 'Playlist delay' time. The time is represented as a numeric string.
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTDLY extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody {
    /**
     * Creates a new FrameBodyTDLY datatype.
     */
    public FrameBodyTDLY()
    {
    }

    public FrameBodyTDLY(FrameBodyTDLY body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTDLY datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTDLY(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDLY datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTDLY(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_PLAYLIST_DELAY;
    }
}