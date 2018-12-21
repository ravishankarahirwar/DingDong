package world.best.musicplayer.utils.files.tag.id3;

import java.util.logging.Logger;

/**
 * This is the abstract base class for all ID3 tags.
 */
public abstract class AbstractID3Tag extends AbstractTag
{
    //Logger
    public static Logger logger = Logger.getLogger("world.best.musicplayer.utils.files.tag.id3");

    public AbstractID3Tag()
    {
    }

    protected static final String TAG_RELEASE = "ID3v";

    //The purpose of this is to provide the filename that should be used when writing debug messages
    //when problems occur reading or writing to file, otherwise it is difficult to track down the error
    //when processing many files
    private String loggingFilename = "";

    /**
     * Get full version
     */
    public String getIdentifier()
    {
        return TAG_RELEASE + getRelease() + "." + getMajorVersion() + "." + getRevision();
    }

    /**
     * Retrieve the Release
     *
     * @return
     */
    public abstract byte getRelease();


    /**
     * Retrieve the Major Version
     *
     * @return
     */
    public abstract byte getMajorVersion();

    /**
     * Retrieve the Revision
     *
     * @return
     */
    public abstract byte getRevision();


    public AbstractID3Tag(AbstractID3Tag copyObject)
    {
        super(copyObject);
    }



    /**
     * Retrieve the logging filename to be used in debugging
     *
     * @return logging filename to be used in debugging
     */
    protected String getLoggingFilename()
    {
        return loggingFilename;
    }

    /**
     * Set logging filename when construct tag for read from file
     *
     * @param loggingFilename
     */
    protected void setLoggingFilename(String loggingFilename)
    {
        this.loggingFilename = loggingFilename;
    }
}
