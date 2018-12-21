package world.best.musicplayer.utils.files.audio.aiff;

import world.best.musicplayer.utils.files.audio.generic.GenericTag;
import world.best.musicplayer.utils.files.audio.generic.Utils;
import world.best.musicplayer.utils.files.tag.FieldDataInvalidException;
import world.best.musicplayer.utils.files.tag.FieldKey;
import world.best.musicplayer.utils.files.tag.KeyNotFoundException;
import world.best.musicplayer.utils.files.tag.TagField;
import world.best.musicplayer.utils.files.tag.TagTextField;

public class AiffTag extends GenericTag {

    private class AiffTagTextField implements TagTextField
    {

        /**
         * Stores the string.
         */
        private String content;

        /**
         * Stores the identifier.
         */
        private final String id;

        /**
         * Creates an instance.
         *
         * @param fieldId        The identifier.
         * @param initialContent The string.
         */
        public AiffTagTextField(String fieldId, String initialContent)
        {
            this.id = fieldId;
            this.content = initialContent;
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#copyContent(world.best.musicplayer.utils.files.tag.TagField)
         */
        public void copyContent(TagField field)
        {
            if (field instanceof TagTextField)
            {
                this.content = ((TagTextField) field).getContent();
            }
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagTextField#getContent()
         */
        public String getContent()
        {
            return this.content;
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagTextField#getEncoding()
         */
        public String getEncoding()
        {
            return "ISO-8859-1";
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#getId()
         */
        public String getId()
        {
            return id;
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#getRawContent()
         */
        public byte[] getRawContent()
        {
            return this.content == null ? new byte[]{} : Utils.getDefaultBytes(this.content, getEncoding());
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#isBinary()
         */
        public boolean isBinary()
        {
            return false;
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#isBinary(boolean)
         */
        public void isBinary(boolean b)
        {
            /* not supported */
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#isCommon()
         */
        public boolean isCommon()
        {
            return true;
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagField#isEmpty()
         */
        public boolean isEmpty()
        {
            return this.content.equals("");
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagTextField#setContent(String)
         */
        public void setContent(String s)
        {
            this.content = s;
        }

        /**
         * (overridden)
         *
         * @see world.best.musicplayer.utils.files.tag.TagTextField#setEncoding(String)
         */
        public void setEncoding(String s)
        {
            /* Not allowed */
        }

        /**
         * (overridden)
         *
         * @see Object#toString()
         */
        public String toString()
        {
            return getContent();
        }
    } // End of AiffTagTextField
    
    
    public boolean hasField(AiffTagFieldKey fieldKey)
    {
        return hasField(fieldKey.name());
    }
    
    /**
     * Create new AIFF-specific field and set it in the tag
     *
     * @param genericKey
     * @param value
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    public void setField(AiffTagFieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        TagField tagfield = createField(genericKey,value);
        setField(tagfield);
    }
    
    public TagField createField(AiffTagFieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
            return new AiffTagTextField(genericKey.name(),value);
    }

    public TagField createCompilationField(boolean value) throws KeyNotFoundException, FieldDataInvalidException
    {
        return createField(FieldKey.IS_COMPILATION,String.valueOf(value));
    }

}
