package world.best.musicplayer.utils.files.tag.asf;

import world.best.musicplayer.utils.files.audio.asf.data.MetadataDescriptor;
import world.best.musicplayer.utils.files.tag.TagField;

/**
 * This class encapsulates a
 * {@link world.best.musicplayer.utils.files.audio.asf.data.MetadataDescriptor}and provides access
 * to it. <br>
 * The metadata descriptor used for construction is copied.
 */
public class AsfTagField implements TagField, Cloneable {

    /**
     * This descriptor is wrapped.
     */
    protected MetadataDescriptor toWrap;

    /**
     * Creates a tag field.
     * 
     * @param field
     *            the ASF field that should be represented.
     */
    public AsfTagField(final AsfFieldKey field) {
        assert field != null;
        this.toWrap = new MetadataDescriptor(field.getHighestContainer(), field
                .getFieldName(), MetadataDescriptor.TYPE_STRING);
    }

    /**
     * Creates an instance.
     * 
     * @param source
     *            The descriptor which should be represented as a
     *            {@link TagField}.
     */
    public AsfTagField(final MetadataDescriptor source) {
        assert source != null;
        // XXX Copy ? maybe not really.
        this.toWrap = source.createCopy();
    }

    /**
     * Creates a tag field.
     * 
     * @param fieldKey
     *            The field identifier to use.
     */
    public AsfTagField(final String fieldKey) {
        assert fieldKey != null;
        this.toWrap = new MetadataDescriptor(AsfFieldKey.getAsfFieldKey(
                fieldKey).getHighestContainer(), fieldKey,
                MetadataDescriptor.TYPE_STRING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * {@inheritDoc}
     */
    public void copyContent(final TagField field) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Returns the wrapped metadata descriptor (which actually stores the
     * values).
     * 
     * @return the wrapped metadata descriptor
     */
    public MetadataDescriptor getDescriptor() {
        return this.toWrap;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return this.toWrap.getName();
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getRawContent() {
        return this.toWrap.getRawData();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBinary() {
        return this.toWrap.getType() == MetadataDescriptor.TYPE_BINARY;
    }

    /**
     * {@inheritDoc}
     */
    public void isBinary(final boolean value) {
        if (!value && isBinary()) {
            throw new UnsupportedOperationException("No conversion supported.");
        }
        this.toWrap.setBinaryValue(this.toWrap.getRawData());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCommon() {
        // HashSet is safe against null comparison
        return AsfTag.COMMON_FIELDS.contains(AsfFieldKey
                .getAsfFieldKey(getId()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return this.toWrap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.toWrap.getString();
    }

}