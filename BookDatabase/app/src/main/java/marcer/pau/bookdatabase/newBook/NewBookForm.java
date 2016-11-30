package marcer.pau.bookdatabase.newBook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import marcer.pau.bookdatabase.api.RequestThumbnail;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.R;
import marcer.pau.bookdatabase.serializables.SerialBitmap;


public class NewBookForm extends AppCompatActivity implements RequestThumbnail.AsyncResponse {

    private Toolbar toolbar;
    private ImageView thumbnail;
    private EditText title;
    private EditText author;
    private EditText publishedDate;
    private EditText publisher;
    private EditText category;
    private RatingBar personal_evaluation;
    private Book book;
    private Bitmap bitmap;
    private RequestThumbnail requestThumbnail;
    private SerialBitmap serialBitmap;
    private static final String BOOK_KEY = "BOOK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bock_form);
        createToolbar();
        createObjects();
        createListeners();
        populate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_book_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.menuaddform_action_forward:
                saveBook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createToolbar(){
        toolbar = (Toolbar) findViewById(R.id.menuadd_toolbar1);
        toolbar.setTitle(R.string.menu_add_book);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);
        //TODO change forward icon
    }

    private void createObjects(){
        book = (Book) getIntent().getSerializableExtra(BOOK_KEY);
        requestThumbnail = new RequestThumbnail(this);
        serialBitmap = new SerialBitmap();
    }

    private void createListeners(){
        title = (EditText) findViewById(R.id.addbookform_title);
        author = (EditText) findViewById(R.id.addbookform_author);
        thumbnail =(ImageView) findViewById(R.id.addbookform_img);
        publishedDate = (EditText) findViewById(R.id.addbookform_pubdate);
        publisher = (EditText) findViewById(R.id.addbookform_publisher);
        category = (EditText) findViewById(R.id.addbookform_cat);
        personal_evaluation = (RatingBar) findViewById(R.id.addbookform_eval);
        //TODO: set button listeners
    }

    private void populate(){
        if(book != null) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            publishedDate.setText(book.getPublishedDate());
            publisher.setText(book.getPublisher());
            category.setText(book.getCategory());
            personal_evaluation.setRating(book.getPersonal_evaluation());
            getBitmapFrmURL(book.getThumbnailURL());
        }
    }

    private void saveBook(){
        extractBookFromForm();
        finishAndReturn();
    }

    private void extractBookFromForm(){
        //TODO check if book is not full null
        String thumbnailURL;
        if(book != null)
            thumbnailURL =  book.getThumbnailURL();
        else
            thumbnailURL = "";
        book = new Book(
                title.getText().toString(),
                author.getText().toString(),
                publishedDate.getText().toString(),
                publisher.getText().toString(),
                category.getText().toString(),
                personal_evaluation.getNumStars(),
                thumbnailURL,
                serialBitmap.getBytes(bitmap)
                );
    }

    private void finishAndReturn(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("BOOK", book);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getBitmapFrmURL(String url) {
        requestThumbnail.execute(url);
    }

    @Override
    public void processFinish(Bitmap image) {
        thumbnail.setImageBitmap(image);
        this.bitmap = image;
    }
}
