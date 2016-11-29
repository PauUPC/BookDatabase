package marcer.pau.bookdatabase.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestThumbnail extends AsyncTask<String, Void, Bitmap> {
    private AsyncResponse responseHandler = null;

    public interface AsyncResponse {
        void processFinish(Bitmap image);
    }

    public RequestThumbnail(Activity listeningActivity) {
        responseHandler = (RequestThumbnail.AsyncResponse) listeningActivity;
    }

    @Override
    protected Bitmap doInBackground(String ... urls) {
        InputStream input = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urls[0]).openConnection();
            connection.connect();
            input = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(input);


    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        responseHandler.processFinish(bitmap);
    }
}



