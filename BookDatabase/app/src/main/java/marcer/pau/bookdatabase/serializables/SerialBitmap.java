package marcer.pau.bookdatabase.serializables;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class SerialBitmap implements Serializable {

    public byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}