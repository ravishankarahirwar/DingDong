package world.best.musicplayer.utils.files.audio.exceptions;

/**
 * This exception is thrown if a
 * {@link world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener} wants to
 * prevent; from actually finishing its
 * operation.<br>
 * This exception can be used in all methods but
 * {@link world.best.musicplayer.utils.files.audio.generic.AudioFileModificationListener#fileOperationFinished(java.io.File)}.
 */
public class ModifyVetoException extends Exception
{

    /**
     * (overridden)
     */
    public ModifyVetoException()
    {
        super();
    }

    /**
     * (overridden)
     *
     * @param message
     * @see Exception#Exception(String)
     */
    public ModifyVetoException(String message)
    {
        super(message);
    }

    /**
     * (overridden)
     *
     * @param message
     * @param cause
     * @see Exception#Exception(String,Throwable)
     */
    public ModifyVetoException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * (overridden)
     *
     * @param cause
     * @see Exception#Exception(Throwable)
     */
    public ModifyVetoException(Throwable cause)
    {
        super(cause);
    }

}
