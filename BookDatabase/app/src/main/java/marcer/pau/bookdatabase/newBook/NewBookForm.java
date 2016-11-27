package marcer.pau.bookdatabase.newBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.R;


public class NewBookForm extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView thumbnail;
    private EditText title;
    private EditText author;
    private EditText publishedDate;
    private EditText publisher;
    private EditText category;
    private EditText personal_evaluation;
    private Book book;
    private static final String BOOK_KEY = "BOOK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book_form);

        createToolbar();
        createObjects();
        createListeners();
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
    }

    private void createListeners(){
        title = (EditText) findViewById(R.id.addbookform_title);
        author = (EditText) findViewById(R.id.addbookform_author);
        thumbnail =(ImageView) findViewById(R.id.addbookform_img);
        publishedDate = (EditText) findViewById(R.id.addbookform_pubdate);
        publisher = (EditText) findViewById(R.id.addbookform_publisher);
        category = (EditText) findViewById(R.id.addbookform_cat);
        personal_evaluation = (EditText) findViewById(R.id.addbookform_eval);

        if(book != null) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
        }
        //TODO: set button listeners
    }

    private void saveBook(){
        extractBookFromForm();
        finishAndReturn();
    }

    private void extractBookFromForm(){
        //TODO check if book is not full null
        String thumbnail;
        if(book != null)
            thumbnail =  book.getThumbnailPath();
        else
            thumbnail = "";
        book = new Book(
                title.getText().toString(),
                author.getText().toString(),
                publishedDate.getText().toString(),
                publisher.getText().toString(),
                category.getText().toString(),
                personal_evaluation.getText().toString(),
                thumbnail
                );
    }

    private void finishAndReturn(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("BOOK", book);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
