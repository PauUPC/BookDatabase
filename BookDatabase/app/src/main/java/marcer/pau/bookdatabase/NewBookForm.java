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

import static marcer.pau.bookdatabase.R.id.toolbar;


public class NewBookForm extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private EditText title;
    private EditText author;
    private Book book;
    private static final String BOOK_KEY = "BOOK";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book_form);

        toolbar = (Toolbar) findViewById(R.id.menuadd_toolbar1);
        toolbar.setTitle(R.string.menu_add_book);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);

        book = (Book) getIntent().getSerializableExtra(BOOK_KEY);

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createListeners(){
        title = (EditText) findViewById(R.id.addbookform_title);
        author = (EditText) findViewById(R.id.addbookform_author);
        imageView =(ImageView) findViewById(R.id.addbookform_img);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_no_preview_image);
        imageView.setImageDrawable(drawable);

        if(book != null) {
            title.setText(book.getAuthor());
            author.setText(book.getAuthor());
        }
        //TODO: set button listeners
    }
}
