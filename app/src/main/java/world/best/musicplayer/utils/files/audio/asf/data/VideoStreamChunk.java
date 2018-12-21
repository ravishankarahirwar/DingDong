package world.best.musicplayer.utils.files.audio.asf.data;

import world.best.musicplayer.utils.files.audio.asf.util.Utils;

import java.math.BigInteger;

public class VideoStreamChunk extends StreamChunk {

    /**
     * Stores the codecs id. Normally the Four-CC (4-Bytes).
     */
    private byte[] codecId = new byte[0];

    /**
     * This field stores the height of the video stream.
     */
    private long pictureHeight;

    /**
     * This field stores the width of the video stream.
     */
    private long pictureWidth;

    /**
     * Creates an instance.
     * 
     * @param chunkLen
     *            Length of the entire chunk (including guid and size)
     */
    public VideoStreamChunk(final BigInteger chunkLen) {
        super(GUID.GUID_VIDEOSTREAM, chunkLen);
    }

    /**
     * @return Returns the codecId.
     */
    public byte[] getCodecId() {
        return this.codecId.clone();
    }

    /**
     * Returns the {@link #getCodecId()}, as a String, where each byte has been
     * converted to a <code>char</code>.
     * 
     * @return Codec Id as String.
     */
    public String getCodecIdAsString() {
        String result;
        if (this.codecId == null) {
            result = "Unknown"; 
        } else {
            result = new String(getCodecId());
        }
        return result;
    }

    /**
     * @return Returns the pictureHeight.
     */
    public long getPictureHeight() {
        return this.pictureHeight;
    }

    /**
     * @return Returns the pictureWidth.
     */
    public long getPictureWidth() {
        return this.pictureWidth;
    }

    /**
     * (overridden)
     * 
     * @see world.best.musicplayer.utils.files.audio.asf.data.StreamChunk#prettyPrint(String)
     */
    @Override
    public String prettyPrint(final String prefix) {
        final StringBuilder result = new StringBuilder(super.prettyPrint(prefix));
        result.insert(0, Utils.LINE_SEPARATOR + prefix + "|->VideoStream");
        result.append(prefix).append("Video info:")
                .append(Utils.LINE_SEPARATOR);
        result.append(prefix).append("      |->Width  : ").append(
                getPictureWidth()).append(Utils.LINE_SEPARATOR);
        result.append(prefix).append("      |->Heigth : ").append(
                getPictureHeight()).append(Utils.LINE_SEPARATOR);
        result.append(prefix).append("      |->Codec  : ").append(
                getCodecIdAsString()).append(Utils.LINE_SEPARATOR);
        return result.toString();
    }

    /**
     * @param codecIdentifier
     *            The codecId to set.
     */
    public void setCodecId(final byte[] codecIdentifier) {
        this.codecId = codecIdentifier.clone();
    }

    /**
     * @param picHeight
     */
    public void setPictureHeight(final long picHeight) {
        this.pictureHeight = picHeight;
    }

    /**
     * @param picWidth
     */
    public void setPictureWidth(final long picWidth) {
        this.pictureWidth = picWidth;
    }
}