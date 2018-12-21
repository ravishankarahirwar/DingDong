package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;
import world.best.musicplayer.utils.files.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;


/**
 *  <p>The 'Original release time' frame contains a timestamp describing
 *  when the original recording of the audio was released. Timestamp
 *  format is described in the ID3v2 structure document.
*/
public class FrameBodyTDOR extends AbstractFrameBodyTextInfo implements ID3v24FrameBody {
    /**
     * Creates a new FrameBodyTDOR datatype.
     */
    public FrameBodyTDOR()
    {
    }

    public FrameBodyTDOR(FrameBodyTDOR body)
    {
        super(body);
    }

    /**
     * When converting v3 TDAT to v4 TDRC frame
     * @param body
     */
    public FrameBodyTDOR(FrameBodyTORY body)
    {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        setObjectValue(DataTypes.OBJ_TEXT, body.getText());
    }

    /**
     * Creates a new FrameBodyTDOR datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTDOR(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    /**
     * Creates a new FrameBodyTDOR datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTDOR(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_ORIGINAL_RELEASE_TIME;
    }

}
