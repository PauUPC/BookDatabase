package marcer.pau.bookdatabase;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookApiRequester {

    private AsyncHttpClient client;
    private BookApiRequesterResponse bookApiRequesterResponse;
    private boolean fetching = false;
    private static final String API_BASE_URL = "http://openlibrary.org/";

    public interface BookApiRequesterResponse {
        void receivedNewBook(Book book);
    }

    public BookApiRequester() {
        this.client = new AsyncHttpClient();
    }

    public boolean isFetchingData() {
        return fetching;
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    // Method for accessing the search API
    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("search.json?q=");
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
