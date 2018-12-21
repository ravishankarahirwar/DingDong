package world.best.musicplayer.utils.files.tag;

/**
 * Thrown if the tag o isn't found. This is different from
 * the <code>InvalidTagException</code>. Each tag  has an
 * ID string or some way saying that it simply exists. If this string is
 * missing, <code>TagNotFoundException</code> is thrown. If the ID string
 * exists, then any other error while reading throws an
 * <code>InvalidTagException</code>.
 */
public class TagNotFoundException extends TagException
{
    /**
     * Creates a new TagNotFoundException datatype.
     */
    public TagNotFoundException()
    {
    }

    /**
     * Creates a new TagNotFoundException datatype.
     *
     * @param ex the cause.
     */
    public TagNotFoundException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new TagNotFoundException datatype.
     *
     * @param msg the detail message.
     */
    public TagNotFoundException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new TagNotFoundException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public TagNotFoundException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}