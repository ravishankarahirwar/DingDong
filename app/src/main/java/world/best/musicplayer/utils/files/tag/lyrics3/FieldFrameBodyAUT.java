package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.StringSizeTerminated;

import java.nio.ByteBuffer;

public class FieldFrameBodyAUT extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyAUT datatype.
     */
    public FieldFrameBodyAUT()
    {
        //        this.setObject("Author", "");
    }

    public FieldFrameBodyAUT(FieldFrameBodyAUT body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyAUT datatype.
     *
     * @param author
     */
    public FieldFrameBodyAUT(String author)
    {
        this.setObjectValue("Author", author);
    }

    /**
     * Creates a new FieldBodyAUT datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyAUT(ByteBuffer byteBuffer) throws InvalidTagException
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
        return "AUT";
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new StringSizeTerminated("Author", this));
    }
}
