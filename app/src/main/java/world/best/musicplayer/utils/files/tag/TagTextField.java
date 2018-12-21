package world.best.musicplayer.utils.files.tag;

/**
 * This interface extends the default field definition by methods for working
 * with human readable text.<br>
 * A TagTextField does not store binary data.
 */
public interface TagTextField extends TagField
{

    /**
     * Returns the content of the field.
     *
     * @return Content
     */
    public String getContent();

    /**
     * Returns the current used charset encoding.
     *
     * @return Charset encoding.
     */
    public String getEncoding();

    /**
     * Sets the content of the field.
     *
     * @param content fields content.
     */
    public void setContent(String content);

    /**
     * Sets the charset encoding used by the field.
     *
     * @param encoding charset.
     */
    public void setEncoding(String encoding);
}