package world.best.musicplayer.utils.files.tag.images;

import android.graphics.Bitmap;

import java.io.IOException;

public class Images
{
    public static Bitmap getImage(Artwork artwork) throws IOException
    {
        return (Bitmap)artwork.getImage();
    }
}
