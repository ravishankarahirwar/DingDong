package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;
import world.best.musicplayer.utils.files.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;


public class FrameBodyTMOO extends AbstractFrameBodyTextInfo implements ID3v24FrameBody
{
    /**
     * Creates a new FrameBodyTMOO datatype.
     */
    public FrameBodyTMOO()
    {
    }

    public FrameBodyTMOO(FrameBodyTMOO body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTMOO datatype.
     *
     * @param textEncoding
     * @param text
     */
    public FrameBodyTMOO(byte textEncoding, String text)
    {
        super(textEncoding, text);
    }

    public FrameBodyTMOO(FrameBodyTXXX body)
    {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, body.getTextEncoding());
        this.setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        this.setObjectValue(DataTypes.OBJ_TEXT, body.getText());
    }
    
    /**
     * Creates a new FrameBodyTMOO datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws java.io.IOException
     * @throws InvalidTagException
     */
    public FrameBodyTMOO(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
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
        return ID3v24Frames.FRAME_ID_MOOD;
    }

}