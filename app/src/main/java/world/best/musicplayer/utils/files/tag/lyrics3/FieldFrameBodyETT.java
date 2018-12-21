package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.StringSizeTerminated;

import java.nio.ByteBuffer;


public class FieldFrameBodyETT extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyETT datatype.
     */
    public FieldFrameBodyETT()
    {
        //        this.setObject("Title", "");
    }

    public FieldFrameBodyETT(FieldFrameBodyETT body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyETT datatype.
     *
     * @param title
     */
    public FieldFrameBodyETT(String title)
    {
        this.setObjectValue("Title", title);
    }

    /**
     * Creates a new FieldBodyETT datatype.
     *
     * @param byteBuffer
     * @throws InvalidTagException
     */
    public FieldFrameBodyETT(ByteBuffer byteBuffer) throws InvalidTagException
    {
        this.read(byteBuffer);
    }

    /**
     * @return
     */
    public String getIdentifier()
    {
        return "ETT";
    }

    /**
     * @param title
     */
    public void setTitle(String title)
    {
        setObjectValue("Title", title);
    }

    /**
     * @return
     */
    public String getTitle()
    {
        return (String) getObjectValue("Title");
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new StringSizeTerminated("Title", this));
    }
}
