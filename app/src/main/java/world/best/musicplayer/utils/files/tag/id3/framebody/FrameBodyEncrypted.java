package world.best.musicplayer.utils.files.tag.id3.framebody;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.ByteArraySizeTerminated;
import world.best.musicplayer.utils.files.tag.datatype.DataTypes;

import java.nio.ByteBuffer;

/**
 * Encrypted frame.
 *
 *
 * Container for an encrypted frame, we cannot decrypt encrypted frame but it may be possible
 * for the calling application to decrypt the frame if they understand how it has been encrypted,
 * information on this will be held within an ENCR frame
 */
public class FrameBodyEncrypted extends AbstractID3v2FrameBody implements ID3v24FrameBody, ID3v23FrameBody
{
    private String identifier=null;

    /**
     * Creates a new FrameBodyEncrypted dataType.
     */
    public FrameBodyEncrypted(String identifier)
    {
        this.identifier=identifier;
    }

    public FrameBodyEncrypted(FrameBodyEncrypted body)
    {
        super(body);
    }

    /**
     * Read from file
     *
     * @param identifier
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyEncrypted(String identifier,ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
        this.identifier=identifier;
    }
    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * TODO:proper mapping
     */
    protected void setupObjectList()
    {
        objectList.add(new ByteArraySizeTerminated(DataTypes.OBJ_DATA, this));
    }
}