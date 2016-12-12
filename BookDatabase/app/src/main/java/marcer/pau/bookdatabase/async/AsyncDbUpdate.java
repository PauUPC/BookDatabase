package marcer.pau.bookdatabase.async;

import android.app.Activity;
import android.os.AsyncTask;
import java.util.ArrayList;
import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.serializables.Book;

public class AsyncDbUpdate extends AsyncTask<Book, Void, Void> {

    private BookData bookData;

    public AsyncDbUpdate(BookData bookData) {
        this.bookData = bookData;
    }

    @Override
    protected Void doInBackground(Book... books) {
        bookData.updateBook(books[0]);
        return null;
    }
}
