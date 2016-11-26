package marcer.pau.bookdatabase;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class NewBookForm extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView thumbnail;
    private EditText title;
    private EditText author;
    private EditText publishedDate;
    private EditText publisher;
    private EditText category;
    private EditText personal_evaluation;
    private EditText imagePath;
    private Book book;
    onNewBookAdded onNewBookAdded;
    private static final String BOOK_KEY = "BOOK";

    public interface onNewBookAdded{
        void needToUpdateView(Book book);
    }
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
                Toast.makeText(getApplicationContext(), "back pressed", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
                return true;
            case R.id.menuaddform_action_forward:
                Toast.makeText(getApplicationContext(), "forward pressed", Toast.LENGTH_SHORT).show();
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
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_no_image_available);
        //thumbnail.setImageDrawable(drawable);

        if(book != null) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
        }
        //TODO: set button listeners
    }

    private void saveBook(){
        //TODO check if book is not full null
        //extractBookFromForm();
        saveToDB();
        addToView();
    }

    private void saveToDB(){
        //TODO add book to database
    }

    private void addToView(){
        onNewBookAdded.needToUpdateView(book);
    }

//    private void extractBookFromForm(){
//        book = new Book(
//                title.getText().toString(),
//                author.getText().toString(),
//                );
//    }
}
