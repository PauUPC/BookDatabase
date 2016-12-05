package marcer.pau.bookdatabase.async;

import android.app.Activity;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.serializables.Book;

public class AsyncDbQuery extends AsyncTask<String, Void, ArrayList<Book>> {
    private AsyncDbQuery.AsyncDbQueryResponse asyncDbQueryResponse = null;
    private BookData bookData;

    public interface AsyncDbQueryResponse {
        void onFinishAsyncDbQuery(ArrayList<Book> books);
    }

    public AsyncDbQuery(Activity listeningActivity, BookData bookData) {
        asyncDbQueryResponse = (AsyncDbQuery.AsyncDbQueryResponse) listeningActivity;
        this.bookData = bookData;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... command) {
        switch (command[0]) {
            case "ALL":
                return bookData.getAllBooksQuery();
            case "TITLE":
                return bookData.orderByTitleQuery();
            case "AUTHOR":
                return bookData.orderByAuthorQuery();
            case "CATEGORY":
                return bookData.orderByCategoryQuery();
            case "REPEAT":
                return bookData.repeatLastQuery();
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        asyncDbQueryResponse.onFinishAsyncDbQuery(books);
    }
}
