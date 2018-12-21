package world.best.musicplayer.utils.files.tag;

/**
 * An <code>InvalidTagException</code> is thrown if a parse error occurs while
 * a tag is being read from a file. This is different from a
 * <code>TagNotFoundException</code>. Each tag (or MP3 Frame Header) has an ID
 * string or some way saying that it simply exists. If this string is missing,
 * <code>TagNotFoundException</code> is thrown. If the ID string exists, then
 * any other error while reading throws an <code>InvalidTagException</code>.
 *
 * @version $Revision$
 */
public class InvalidTagException extends TagException
{
    /**
     * Creates a new InvalidTagException datatype.
     */
    public InvalidTagException()
    {
    }

    /**
     * Creates a new InvalidTagException datatype.
     *
     * @param ex the cause.
     */
    public InvalidTagException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new InvalidTagException datatype.
     *
     * @param msg the detail message.
     */
    public InvalidTagException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new InvalidTagException datatype.
     *
     * @param msg the detail message.
     * @param ex  the cause.
     */
    public InvalidTagException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}