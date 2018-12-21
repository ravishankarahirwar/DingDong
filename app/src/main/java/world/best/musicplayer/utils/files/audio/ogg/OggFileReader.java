package world.best.musicplayer.utils.files.audio.ogg;

import world.best.musicplayer.utils.files.audio.exceptions.CannotReadException;
import world.best.musicplayer.utils.files.audio.generic.AudioFileReader;
import world.best.musicplayer.utils.files.audio.generic.GenericAudioHeader;
import world.best.musicplayer.utils.files.audio.ogg.util.OggInfoReader;
import world.best.musicplayer.utils.files.audio.ogg.util.OggPageHeader;
import world.best.musicplayer.utils.files.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * Read Ogg File Tag and Encoding information
 *
 * Only implemented for ogg files containing a vorbis stream with vorbis comments
 */
public class OggFileReader extends AudioFileReader
{
    // Logger Object
    public static Logger logger = Logger.getLogger("world.best.musicplayer.utils.files.audio.ogg");

    private OggInfoReader ir;
    private OggVorbisTagReader vtr;

    public OggFileReader()
    {
        ir = new OggInfoReader();
        vtr = new OggVorbisTagReader();
    }

    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return ir.read(raf);
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        return vtr.read(raf);
    }

    /**
     * Return count Ogg Page header, count starts from zero
     *
     * count=0; should return PageHeader that contains Vorbis Identification Header
     * count=1; should return Pageheader that contains VorbisComment and possibly SetupHeader
     * count>=2; should return PageHeader containing remaining VorbisComment,SetupHeader and/or Audio
     *
     * @param raf
     * @param count
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    public OggPageHeader readOggPageHeader(RandomAccessFile raf, int count) throws CannotReadException, IOException
    {
        OggPageHeader pageHeader = OggPageHeader.read(raf);
        while (count > 0)
        {
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            pageHeader = OggPageHeader.read(raf);
            count--;
        }
        return pageHeader;
    }

    /**
     * Summarize all the ogg headers in a file
     *
     * A useful utility function
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void summarizeOggPageHeaders(File oggFile) throws CannotReadException, IOException
    {
        RandomAccessFile raf = new RandomAccessFile(oggFile, "r");

        while (raf.getFilePointer() < raf.length())
        {
            System.out.println("pageHeader starts at absolute file position:" + raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read(raf);
            System.out.println("pageHeader finishes at absolute file position:" + raf.getFilePointer());
            System.out.println(pageHeader + "\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
        }
        System.out.println("Raf File Pointer at:" + raf.getFilePointer() + "File Size is:" + raf.length());
        raf.close();
    }

    /**
     * Summarizes the first five pages, normally all we are interested in
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void shortSummarizeOggPageHeaders(File oggFile) throws CannotReadException, IOException
    {
        RandomAccessFile raf = new RandomAccessFile(oggFile, "r");

        int i = 0;
        while (raf.getFilePointer() < raf.length())
        {
            System.out.println("pageHeader starts at absolute file position:" + raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read(raf);
            System.out.println("pageHeader finishes at absolute file position:" + raf.getFilePointer());
            System.out.println(pageHeader + "\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            i++;
            if(i>=5)
            {
                break;
            }
        }
        System.out.println("Raf File Pointer at:" + raf.getFilePointer() + "File Size is:" + raf.length());
        raf.close();
    }
}

