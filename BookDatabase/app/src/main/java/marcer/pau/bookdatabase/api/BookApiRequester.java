package marcer.pau.bookdatabase.api;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.R;

public class BookApiRequester extends AsyncTask<String, Void, Book> {

    private BookApiRequesterResponse bookApiRequesterResponse;
    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
//    private static final String API_AUTH = ":keyes&key=" + R.string.google_api_key;
    private HttpURLConnection urlConnection;

    public interface BookApiRequesterResponse  {
        void receivedNewBook(Book book);
    }

    public BookApiRequester(Activity listeningActivity) {
        bookApiRequesterResponse = (BookApiRequester.BookApiRequesterResponse) listeningActivity;
    }

    @Override
    protected Book doInBackground(String... urls) {
        Book book = new Book();
        StringBuilder responseJSONBuffer = new StringBuilder();
        String urlString = API_BASE_URL + urls[0];

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseJSONBuffer.append(line);
            }
            book = new Book(new JSONObject(responseJSONBuffer.toString()));
        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return book;
    }

    @Override
    protected void onPostExecute(Book book) {
        super.onPostExecute(book);
        bookApiRequesterResponse.receivedNewBook(book);
    }
}
