package world.best.musicplayer.utils.files.audio.real;

import world.best.musicplayer.utils.files.audio.generic.GenericTag;
import world.best.musicplayer.utils.files.tag.FieldDataInvalidException;
import world.best.musicplayer.utils.files.tag.FieldKey;
import world.best.musicplayer.utils.files.tag.KeyNotFoundException;
import world.best.musicplayer.utils.files.tag.TagField;

public class RealTag extends GenericTag
{
    public String toString()
    {
        String output = "REAL " + super.toString();
        return output;
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException
    {
        return createField(FieldKey.IS_COMPILATION,String.valueOf(value));
    }
}
