package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.StringSizeTerminated;

import java.nio.ByteBuffer;


public class FieldFrameBodyEAL extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyEAL datatype.
     */
    public FieldFrameBodyEAL()
    {
        //        this.setObject("Album", "");
    }

    public FieldFrameBodyEAL(FieldFrameBodyEAL body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyEAL datatype.
     *
     * @param album
     */
    public FieldFrameBodyEAL(String album)
    {
        this.setObjectValue("Album", album);
    }

    /**
     * Creates a new FieldBodyEAL datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyEAL(ByteBuffer byteBuffer) throws InvalidTagException
    {
        read(byteBuffer);

    }

    /**
     * @param album
     */
    public void setAlbum(String album)
    {
        setObjectValue("Album", album);
    }

    /**
     * @return
     */
    public String getAlbum()
    {
        return (String) getObjectValue("Album");
    }

    /**
     * @return
     */
    public String getIdentifier()
    {
        return "EAL";
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new StringSizeTerminated("Album", this));
    }
}
