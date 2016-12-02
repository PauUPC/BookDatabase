package marcer.pau.bookdatabase.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import java.util.ArrayList;

import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.serializables.Book;

public class AsyncDbQuery extends AsyncTask<String, Void, ArrayList<Book>> {
    private AsyncDbQueryResponse asyncDbQueryResponse = null;
    private BookData bookData;
    private Context context;

    public interface AsyncDbQueryResponse {
        void QueryResult(ArrayList<Book> books);
    }

    public AsyncDbQuery(Activity listeningActivity, Context context) {
        bookData = new BookData(context);
        asyncDbQueryResponse = (AsyncDbQuery.AsyncDbQueryResponse) listeningActivity;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... command) {
        bookData.open();
        switch (command[0]){
            case "TITLE":
                return bookData.orderByTitleQuery();
            case "CATEGORY":
                return bookData.orderByCategoryQuery();
            default:
                return bookData.getAllBooksQuery();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        super.onPostExecute(books);
        bookData.close();
        asyncDbQueryResponse.QueryResult(books);
    }
}
