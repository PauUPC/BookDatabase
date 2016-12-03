package marcer.pau.bookdatabase.async;

import android.app.Activity;
import android.os.AsyncTask;
import java.util.ArrayList;
import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.serializables.Book;

public class AsyncDbCreate extends AsyncTask<Book, Void, ArrayList<Book>> {
    private AsyncDbCreate.AsyncDbCreateResponse asyncDbCreateResponse = null;
    private BookData bookData;

    public interface AsyncDbCreateResponse {
        void onFinishAsyncDbCreate(ArrayList<Book> books);
    }

    public AsyncDbCreate(Activity listeningActivity, BookData bookData) {
        asyncDbCreateResponse = (AsyncDbCreate.AsyncDbCreateResponse) listeningActivity;
        this.bookData = bookData;
    }

    @Override
    protected ArrayList<Book> doInBackground(Book... books) {
        bookData.open();
        bookData.createBook(books[0]);
        return bookData.repeatLastQuery();
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        bookData.close();
        asyncDbCreateResponse.onFinishAsyncDbCreate(books);
    }
}