package marcer.pau.bookdatabase.async;

import android.os.AsyncTask;
import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.serializables.Book;
public class AsyncDbDelete extends AsyncTask<Book, Void, Void> {
    private BookData bookData;

    public AsyncDbDelete(BookData bookData) {
        this.bookData = bookData;
    }

    @Override
    protected Void doInBackground(Book... books) {
        bookData.open();
        bookData.deleteBook(books[0]);
        bookData.close();
        return null;
    }
}