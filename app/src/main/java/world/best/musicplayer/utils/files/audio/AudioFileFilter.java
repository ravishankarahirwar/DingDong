package world.best.musicplayer.utils.files.audio;

import world.best.musicplayer.utils.files.audio.generic.Utils;

import java.io.FileFilter;
import java.io.File;

/**
 * <p>This is a simple FileFilter that will only allow the file supported by this library.
 * <p>It will also accept directories. An additional condition is that file must be readable (read permission) and
 * are not hidden (dot files, or hidden files)
 */
public class AudioFileFilter implements FileFilter
{
    /**
     * allows Directories
     */
    private final boolean allowDirectories;

    public AudioFileFilter( boolean allowDirectories)
    {
        this.allowDirectories=allowDirectories;
    }

    public AudioFileFilter()
    {
        this(true);
    }

    /**
     * <p>Check whether the given file meet the required conditions (supported by the library OR directory).
     * The File must also be readable and not hidden.
     *
     * @param    f    The file to test
     * @return a boolean indicating if the file is accepted or not
     */
    public boolean accept(File f)
    {
        if (f.isHidden() || !f.canRead())
        {
            return false;
        }

        if (f.isDirectory())
        {
            return allowDirectories;
        }

        String ext = Utils.getExtension(f);

        try
        {
            if (SupportedFileFormat.valueOf(ext.toUpperCase()) != null)
            {
                return true;
            }
        }
        catch(IllegalArgumentException iae)
        {
            //Not known enum value
            return false;    
        }
        return false;
	}
}
