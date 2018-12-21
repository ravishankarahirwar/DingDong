package world.best.musicplayer.utils.files.tag.lyrics3;

import world.best.musicplayer.utils.files.tag.InvalidTagException;
import world.best.musicplayer.utils.files.tag.datatype.StringSizeTerminated;

import java.nio.ByteBuffer;

public class FieldFrameBodyINF extends AbstractLyrics3v2FieldFrameBody
{
    /**
     * Creates a new FieldBodyINF datatype.
     */
    public FieldFrameBodyINF()
    {
        //        this.setObject("Additional Information", "");
    }

    public FieldFrameBodyINF(FieldFrameBodyINF body)
    {
        super(body);
    }

    /**
     * Creates a new FieldBodyINF datatype.
     *
     * @param additionalInformation
     */
    public FieldFrameBodyINF(String additionalInformation)
    {
        this.setObjectValue("Additional Information", additionalInformation);
    }

    /**
     * Creates a new FieldBodyINF datatype.
     * @param byteBuffer
     * @throws world.best.musicplayer.utils.files.tag.InvalidTagException
     */
    public FieldFrameBodyINF(ByteBuffer byteBuffer) throws InvalidTagException
    {
        this.read(byteBuffer);

    }

    /**
     * @param additionalInformation
     */
    public void setAdditionalInformation(String additionalInformation)
    {
        setObjectValue("Additional Information", additionalInformation);
    }

    /**
     * @return
     */
    public String getAdditionalInformation()
    {
        return (String) getObjectValue("Additional Information");
    }

    /**
     * @return
     */
    public String getIdentifier()
    {
        return "INF";
    }

    /**
     *
     */
    protected void setupObjectList()
    {
        objectList.add(new StringSizeTerminated("Additional Information", this));
    }
}
