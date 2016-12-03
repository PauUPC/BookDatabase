package marcer.pau.bookdatabase.async;

import android.app.Activity;
import android.os.AsyncTask;
import java.util.ArrayList;
import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.serializables.Book;

public class AsyncDbUpdate extends AsyncTask<Book, Void, ArrayList<Book>> {

    private AsyncDbUpdate.AsyncDbUpdateResponse asyncDbUpdateResponse = null;
    private BookData bookData;

    public interface AsyncDbUpdateResponse {
        void onFinishAsyncDbUpdate(ArrayList<Book> books);
    }

    public AsyncDbUpdate(Activity listeningActivity, BookData bookData) {
        asyncDbUpdateResponse = (AsyncDbUpdate.AsyncDbUpdateResponse) listeningActivity;
        this.bookData = bookData;
    }

    @Override
    protected ArrayList<Book> doInBackground(Book... books) {
        bookData.open();
        bookData.updateBook(books[0]);
        return null; //bookData.repeatLastQuery(); patch
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books) {
        bookData.close();
        asyncDbUpdateResponse.onFinishAsyncDbUpdate(books);
    }
}
