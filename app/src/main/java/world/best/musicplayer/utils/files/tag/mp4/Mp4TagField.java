package world.best.musicplayer.utils.files.tag.mp4;

import world.best.musicplayer.utils.files.audio.generic.Utils;
import world.best.musicplayer.utils.files.audio.mp4.atom.Mp4BoxHeader;
import world.best.musicplayer.utils.files.tag.TagField;
import world.best.musicplayer.utils.files.tag.mp4.atom.Mp4DataBox;
import world.best.musicplayer.utils.files.tag.mp4.field.Mp4FieldType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * This abstract class represents a link between piece of data, and how it is stored as an mp4 atom
 *
 * Note there isnt a one to one correspondance between a tag field and a box because some fields are represented
 * by multiple boxes, for example many of the MusicBrainz fields use the '----' box, which in turn uses one of mean,
 * name and data box. So an instance of a tag field maps to one item of data such as 'Title', but it may have to read
 * multiple boxes to do this.   
 *
 * There are various subclasses that represent different types of fields               
 */
public abstract class Mp4TagField implements TagField
{
    // Logger Object
    public static Logger logger = Logger.getLogger("world.best.musicplayer.utils.files.tag.mp4");


    protected String id;

    //Just used by reverese dns class, so it knows the size of its aprent so it can detect end correctly
    protected Mp4BoxHeader parentHeader;

    protected Mp4TagField(String id)
    {
        this.id = id;
    }

    /**
     * Used by subclasses that canot identify their id until after they have been built such as ReverseDnsField
     *
     * @param data
     * @throws UnsupportedEncodingException
     */
    protected Mp4TagField(ByteBuffer data) throws UnsupportedEncodingException
    {
        build(data);
    }

    /**
     * Used by reverese dns when reading from file, so can identify when there is a data atom
     *
     * @param parentHeader
     * @param data
     * @throws UnsupportedEncodingException
     */
    protected Mp4TagField(Mp4BoxHeader parentHeader, ByteBuffer data) throws UnsupportedEncodingException
    {
        this.parentHeader = parentHeader;
        build(data);
    }

    protected Mp4TagField(String id, ByteBuffer data) throws UnsupportedEncodingException
    {
        this(id);
        build(data);
    }

    /**
     * @return field identifier
     */
    public String getId()
    {
        return id;
    }

    public void isBinary(boolean b)
    {
        /* One cannot choose if an arbitrary block can be binary or not */
    }

    public boolean isCommon()
    {
        return id.equals(Mp4FieldKey.ARTIST.getFieldName()) || id.equals(Mp4FieldKey.ALBUM.getFieldName()) || id.equals(Mp4FieldKey.TITLE.getFieldName()) || id.equals(Mp4FieldKey.TRACK.getFieldName()) || id.equals(Mp4FieldKey.DAY.getFieldName()) || id.equals(Mp4FieldKey.COMMENT.getFieldName()) || id.equals(Mp4FieldKey.GENRE.getFieldName());
    }

    /**
     * @return field identifier as it will be held within the file
     */
    protected byte[] getIdBytes()
    {
        return Utils.getDefaultBytes(getId(), "ISO-8859-1");
    }

    /**
     * @return the data as it is held on file
     * @throws UnsupportedEncodingException
     */
    protected abstract byte[] getDataBytes() throws UnsupportedEncodingException;


    /**
     * @return the field type of this field
     */
    public abstract Mp4FieldType getFieldType();

    /**
     * Processes the data and sets the position of the data buffer to just after the end of this fields data
     * ready for processing next field.
     *
     * @param data
     * @throws UnsupportedEncodingException
     */
    protected abstract void build(ByteBuffer data) throws UnsupportedEncodingException;

    /**
     * Convert back to raw content, includes parent and data atom as views as one thing externally
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public byte[] getRawContent() throws UnsupportedEncodingException
    {
        logger.fine("Getting Raw data for:" + getId());
        try
        {
            //Create Data Box
            byte[] databox = getRawContentDataOnly();

            //Wrap in Parent box
            ByteArrayOutputStream outerbaos = new ByteArrayOutputStream();
            outerbaos.write(Utils.getSizeBEInt32(Mp4BoxHeader.HEADER_LENGTH + databox.length));
            outerbaos.write(Utils.getDefaultBytes(getId(), "ISO-8859-1"));
            outerbaos.write(databox);
            return outerbaos.toByteArray();
        }
        catch (IOException ioe)
        {
            //This should never happen as were not actually writing to/from a file
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Get raw content for the data component only
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public byte[] getRawContentDataOnly() throws UnsupportedEncodingException
    {
        logger.fine("Getting Raw data for:" + getId());
        try
        {
            //Create Data Box
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = getDataBytes();
            baos.write(Utils.getSizeBEInt32(Mp4DataBox.DATA_HEADER_LENGTH + data.length));
            baos.write(Utils.getDefaultBytes(Mp4DataBox.IDENTIFIER, "ISO-8859-1"));
            baos.write(new byte[]{0});
            baos.write(new byte[]{0, 0, (byte) getFieldType().getFileClassId()});
            baos.write(new byte[]{0, 0, 0, 0});
            baos.write(data);
            return baos.toByteArray();
        }
        catch (IOException ioe)
        {
            //This should never happen as were not actually writing to/from a file
            throw new RuntimeException(ioe);
        }
    }
}
