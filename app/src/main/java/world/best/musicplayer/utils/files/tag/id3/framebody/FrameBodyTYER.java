package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.id3.ID3v23Frames;
import world.best.musicplayer.utils.files.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;

/**
 * Year Text information frame.
 * <p>The 'Year' frame is a numeric string with a year of the recording. This frames is always four characters long (until the year 10000).
 * <p>Deprecated in v2.4.0
 *
 * <p>For more details, please refer to the ID3 specifications:
 * <ul>
 * <li><a href="http://www.id3.org/id3v2.3.0.txt">ID3 v2.3.0 Spec</a>
 * </ul>
 */
public class FrameBodyTYER extends AbstractFrameBodyTextInfo implements ID3v23FrameBody
{
    /**
     * Creates a new FrameBodyTYER datatype.
     */
    public FrameBodyTYER()
    {
    }

    public FrameBodyTYER(FrameBodyTYER body)
    {
        super(body);
    }

    /**
     * When converting v4 TDRC frame to v3 TYER
     * @param body
     */
    public FrameBodyTYER(FrameBodyTDRC body)
    {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        setObjectValue(DataTypes.OBJ_TEXT, body.getText());
    }

    /**
     * Creates a new FrameBodyTYER datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTYER(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTYER datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTYER(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v23Frames.FRAME_ID_V3_TYER;
    }
}
