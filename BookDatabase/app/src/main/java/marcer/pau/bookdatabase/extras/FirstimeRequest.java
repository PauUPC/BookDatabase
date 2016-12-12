package marcer.pau.bookdatabase.extras;


import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import marcer.pau.bookdatabase.api.BookApiRequester;
import marcer.pau.bookdatabase.api.RequestThumbnail;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.serializables.SerialBitmap;

public class FirstimeRequest extends AppCompatActivity implements
        BookApiRequester.BookApiRequesterResponse,
        RequestThumbnail.AsyncResponse {

    private SerialBitmap serialBitmap;
    private String ISBN;
    private Book book;
    private FirstimeRequestResponse firstimeRequestResponse;

    @Override
    public void processFinish(Bitmap image) {
        if(image != null) {
            book.setThumbnail(serialBitmap.getBytes(image));
            firstimeRequestResponse.onFinishFirstimeRun(book);
        }
    }

    public interface FirstimeRequestResponse {
        void onFinishFirstimeRun(Book book);
    }

    public FirstimeRequest(Activity listeningActivity, String isbn) {
        ISBN = isbn;
        firstimeRequestResponse = (FirstimeRequestResponse) listeningActivity;
        serialBitmap = new SerialBitmap();
    }

    public void createBook() {
        BookApiRequester bookApiRequester = new BookApiRequester(this);
        bookApiRequester.execute(ISBN);
    }

    @Override
    public void receivedNewBook(Book book) {
        this.book = book;
        RequestThumbnail requestThumbnail = new RequestThumbnail(this);
        requestThumbnail.execute(book.getThumbnailURL());
    }
}