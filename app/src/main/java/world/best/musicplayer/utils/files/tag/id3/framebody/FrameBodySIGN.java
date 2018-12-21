package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.ByteArraySizeTerminated;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;
import world.best.musicplayer.utils.files.tag.datatype.NumberFixedLength;
import world.best.musicplayer.utils.files.tag.id3.ID3v24Frames;

import java.nio.ByteBuffer;


public class FrameBodySIGN extends AbstractID3v2FrameBody implements ID3v24FrameBody
{
    /**
     * Creates a new FrameBodySIGN datatype.
     */
    public FrameBodySIGN()
    {
        //        this.setObject("Group Symbol", new Byte((byte) 0));
        //        this.setObject("Signature", new byte[0]);
    }

    public FrameBodySIGN(FrameBodySIGN body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodySIGN datatype.
     *
     * @param groupSymbol
     * @param signature
     */
    public FrameBodySIGN(byte groupSymbol, byte[] signature)
    {
        this.setObjectValue(DataTypes.OBJ_GROUP_SYMBOL, groupSymbol);
        this.setObjectValue(DataTypes.OBJ_SIGNATURE, signature);
    }

    /**
     * Creates a new FrameBodySIGN datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException if unable to create framebody from buffer
     */
    public FrameBodySIGN(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * @param groupSymbol
     */
    public void setGroupSymbol(byte groupSymbol)
    {
        setObjectValue(DataTypes.OBJ_GROUP_SYMBOL, groupSymbol);
    }

    /**
     * @return
     */
    public byte getGroupSymbol()
    {
        if (getObjectValue(DataTypes.OBJ_GROUP_SYMBOL) != null)
        {
            return (Byte) getObjectValue(DataTypes.OBJ_GROUP_SYMBOL);
        }
        else
        {
            return (byte) 0;
        }
    }


    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier()
    {
        return ID3v24Frames.FRAME_ID_SIGNATURE;
    }

    /**
     * @param signature
     */
    public void setSignature(byte[] signature)
    {
        setObjectValue(DataTypes.OBJ_SIGNATURE, signature);
    }

    /**
     * @return
     */
    public byte[] getSignature()
    {
        return (byte[]) getObjectValue(DataTypes.OBJ_SIGNATURE);
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new NumberFixedLength(DataTypes.OBJ_GROUP_SYMBOL, this, 1));
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_SIGNATURE, this));
    }
}
