package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.BooleanString;

import java.nio.ByteBuffer;


public class FieldFrameBodyIND extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyIND datatype.
     */
    public FieldFrameBodyIND()
    {
        //        this.setObject("Lyrics Present", new Boolean(false));
        //        this.setObject("Timestamp Present", new Boolean(false));
    }

    public FieldFrameBodyIND(FieldFrameBodyIND body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyIND datatype.
     *
     * @param lyricsPresent
     * @param timeStampPresent
     */
    public FieldFrameBodyIND(boolean lyricsPresent, boolean timeStampPresent)
    {
        this.setObjectValue("Lyrics Present", lyricsPresent);
        this.setObjectValue("Timestamp Present", timeStampPresent);
    }

    /**
     * Creates a new FieldBodyIND datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyIND(ByteBuffer byteBuffer) throws InvalidTagException
    {
        this.read(byteBuffer);
    }

    /**
     * @param author
     */
    public void setAuthor(String author)
    {
        setObjectValue("Author", author);
    }

    /**
     * @return
     */
    public String getAuthor()
    {
        return (String) getObjectValue("Author");
    }

    /**
     * @return
     */
    public String getIdentifier()
    {
        return "IND";
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new BooleanString("Lyrics Present", this));
        objectList.add(new BooleanString("Timestamp Present", this));
    }
}
