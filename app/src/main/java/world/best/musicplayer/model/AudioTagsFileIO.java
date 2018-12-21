package world.best.musicplayer.model;

import java.io.File;

import world.best.musicplayer.utils.files.audio.AudioFileIO;
import world.best.musicplayer.utils.files.audio.exceptions.CannotWriteException;
import world.best.musicplayer.utils.files.tag.FieldDataInvalidException;
import world.best.musicplayer.utils.files.tag.FieldKey;
import world.best.musicplayer.utils.files.tag.KeyNotFoundException;
import world.best.musicplayer.utils.files.tag.Tag;
import world.best.musicplayer.utils.files.audio.AudioFile;

public class AudioTagsFileIO {

    private final String TAG = "AudioTagsFileIO";
    private static final String delimiter = ",";

    /***
     *
     * @param tagsToWrite
     * @param filePath
     * @return 1 for success and -1 for failure
     */
    public static int writeTagToAudioFile(String tagsToWrite, String filePath) {
        int writeOperationResult = 1;
        AudioFile audioFile = null;
        Tag tag = null;

        try {
            File file = new File(filePath);
            try {
                audioFile = AudioFileIO.read(file);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            tag = audioFile.getTag();
            // AudioHeader audioHeader = audioFile.getAudioHeader();
            String existingTags = tag.getFirst(FieldKey.TAGS);
            if (existingTags.length() > 0) {
                //tagsToWrite = existingTags + delimiter + tagsToWrite;
                tagsToWrite = existingTags + tagsToWrite;
            }
            tag.setField(FieldKey.TAGS, tagsToWrite);
            audioFile.commit();

        } catch (KeyNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            writeOperationResult = -1;
        } catch (FieldDataInvalidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            writeOperationResult = -1;
        } catch (CannotWriteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            writeOperationResult = -1;
        } /*
           * catch (CannotReadException e) { // TODO Auto-generated catch block
           * e.printStackTrace(); writeOperationResult =-1; } catch (IOException
           * e) { // TODO Auto-generated catch block e.printStackTrace();
           * writeOperationResult =-1; } catch (TagException e) { // TODO
           * Auto-generated catch block e.printStackTrace();
           * writeOperationResult =-1; } catch (ReadOnlyFileException e) { //
           * TODO Auto-generated catch block e.printStackTrace();
           * writeOperationResult =-1; } catch (InvalidAudioFrameException e) {
           * // TODO Auto-generated catch block e.printStackTrace();
           * writeOperationResult =-1; }
           */

        return writeOperationResult;
    }

}
