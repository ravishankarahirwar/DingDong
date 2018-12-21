package world.best.musicplayer.utils.files.tag.asf;

import android.graphics.Bitmap;

import world.best.musicplayer.utils.files.audio.asf.data.MetadataDescriptor;
import world.best.musicplayer.utils.files.tag.TagField;
import world.best.musicplayer.utils.files.utils.BitmapUtils;

import java.io.IOException;

/**
 * An <code>AbstractAsfTagImageField</code> is an abstract class for representing tag
 * fields containing image data.<br>
 */
abstract class AbstractAsfTagImageField extends AsfTagField
{

    /**
     * Creates a image tag field.
     * 
     * @param field
     *            the ASF field that should be represented.
     */
    public AbstractAsfTagImageField(final AsfFieldKey field) {
        super(field);
    }

    /**
     * Creates an instance.
     * 
     * @param source
     *            The descriptor which should be represented as a
     *            {@link TagField}.
     */
    public AbstractAsfTagImageField(final MetadataDescriptor source) {
        super(source);
    }

    /**
     * Creates a tag field.
     * 
     * @param fieldKey
     *            The field identifier to use.
     */
    public AbstractAsfTagImageField(final String fieldKey) {
        super(fieldKey);
    }

    /**
     * This method returns an image instance from the
     * {@linkplain #getRawImageData() image content}.
     * 
     * @return the image instance
     * @throws IOException
     */
    public Bitmap getImage() throws IOException {
        return BitmapUtils.decodeByteArray(getRawImageData());
    }

    /**
     * Returns the size of the {@linkplain #getRawImageData() image data}.<br>
     * 
     * @return image data size in bytes.
     */
    public abstract int getImageDataSize();

    /**
     * Returns the raw data of the represented image.<br>
     * 
     * @return raw image data
     */
    public abstract byte[] getRawImageData();

}
