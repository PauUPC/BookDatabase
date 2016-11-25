package marcer.pau.bookdatabase;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

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
