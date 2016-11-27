package marcer.pau.bookdatabase.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.R;

public class BookApiRequester {

    private AsyncHttpClient client;
    private BookApiRequesterResponse bookApiRequesterResponse;
    private boolean fetching = false;
    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private static final String API_AUTH = ":keyes&key=" + R.string.google_api_key;

    public interface BookApiRequesterResponse {
        void receivedNewBook(Book book);
    }

    public BookApiRequester(Activity listeningActivity) {
        client = new AsyncHttpClient();
        bookApiRequesterResponse = (BookApiRequester.BookApiRequesterResponse) listeningActivity;
    }

    public boolean isFetchingData() {
        return fetching;
    }

    private String getApiUrl(String isbn) {
        return API_BASE_URL + isbn; //API_AUTH;
    }

    // Method for accessing the search API
    public void getBook(final String isbn){
        final String query=getApiUrl(isbn);
        client.get(query, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Json success","succes on json response");
                bookApiRequesterResponse.receivedNewBook(new Book(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                bookApiRequesterResponse.receivedNewBook(null);
            }
        });
    }
}
