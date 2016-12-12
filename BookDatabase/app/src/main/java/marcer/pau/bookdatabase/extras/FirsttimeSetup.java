package marcer.pau.bookdatabase.extras;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import marcer.pau.bookdatabase.serializables.Book;

public class FirstTimeSetup extends AppCompatActivity
        implements FirstimeRequest.FirstimeRequestResponse {

    public interface FirstTimeSetupResponse {
        void onFinishFirstTimeSetup(ArrayList<Book> books);
    }

    private static final String ISBN1 = "9788448039806";
    private static final String ISBN2 = "9781473204287";
    private static final String ISBN3 = "9785170905386";
    private static final String ISBN4 = "9780439023528";
    private static final String ISBN5 = "9780545586177";
    private static final String ISBN6 = "9780545663267";
    private FirstTimeSetupResponse firstTimeSetupResponse;
    private ArrayList<Book> books;

    public FirstTimeSetup(Activity listeningActivity){
        firstTimeSetupResponse = (FirstTimeSetupResponse) listeningActivity;
        books = new ArrayList<>();
    }

    public void Start() {
        FirstimeRequest firstimeRequest1 = new FirstimeRequest(this, ISBN1);
        firstimeRequest1.createBook();
        FirstimeRequest firstimeRequest2 = new FirstimeRequest(this, ISBN2);
        firstimeRequest2.createBook();
        FirstimeRequest firstimeRequest3 = new FirstimeRequest(this, ISBN3);
        firstimeRequest3.createBook();
        FirstimeRequest firstimeRequest4 = new FirstimeRequest(this, ISBN4);
        firstimeRequest4.createBook();
        FirstimeRequest firstimeRequest5 = new FirstimeRequest(this, ISBN5);
        firstimeRequest5.createBook();
        FirstimeRequest firstimeRequest6 = new FirstimeRequest(this, ISBN6);
        firstimeRequest6.createBook();
    }

    @Override
    public void onFinishFirstimeRun(Book book) {
        books.add(book);
        if(books.size() == 6)
            firstTimeSetupResponse.onFinishFirstTimeSetup(books);
    }
}
