package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.StringSizeTerminated;

import java.nio.ByteBuffer;


public class FieldFrameBodyEAR extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyEAR datatype.
     */
    public FieldFrameBodyEAR()
    {
        //        this.setObject("Artist", "");
    }

    public FieldFrameBodyEAR(FieldFrameBodyEAR body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyEAR datatype.
     *
     * @param artist
     */
    public FieldFrameBodyEAR(String artist)
    {
        this.setObjectValue("Artist", artist);
    }

    /**
     * Creates a new FieldBodyEAR datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyEAR(ByteBuffer byteBuffer) throws InvalidTagException
    {

        this.read(byteBuffer);

    }

    /**
     * @param artist
     */
    public void setArtist(String artist)
    {
        setObjectValue("Artist", artist);
    }

    /**
     * @return
     */
    public String getArtist()
    {
        return (String) getObjectValue("Artist");
    }

    /**
     * @return
     */
    public String getIdentifier()
    {
        return "EAR";
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new StringSizeTerminated("Artist", this));
    }
}
