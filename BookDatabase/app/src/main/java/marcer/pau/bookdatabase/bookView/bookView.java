package marcer.pau.bookdatabase.bookView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import marcer.pau.bookdatabase.R;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.serializables.SerialBitmap;

public class BookView extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView thumbnail;
    private TextView title;
    private TextView author;
    private TextView publishedDate;
    private TextView publisher;
    private TextView category;
    private RatingBar personal_evaluation;
    private TextView removal_hint;
    private Button removeButton;
    private Book book;
    private SerialBitmap serialBitmap;
    private float rating;
    private byte bytes;
    private static final String BOOK_KEY = "BOOK";
    private static final String RESULT_KEY = "RESULT_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewbook);
        createToolbar();
        createObjects();
        createListeners();
        populate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.menuviewform_action_forward:
                checkFormForUpdates();
                finishAndReturn();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.viewbook_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);
    }

    private void createObjects() {
        bytes = 0;
        book = (Book) getIntent().getSerializableExtra(BOOK_KEY);
        serialBitmap = new SerialBitmap();
        title = (TextView) findViewById(R.id.viewbook_title);
        author = (TextView) findViewById(R.id.viewbook_author);
        publishedDate = (TextView) findViewById(R.id.viewbook_pbdate);
        publisher = (TextView) findViewById(R.id.viewbook_publisher);
        personal_evaluation = (RatingBar) findViewById(R.id.viewbook_eval);
        category = (TextView) findViewById(R.id.viewbook_category);
        thumbnail = (ImageView) findViewById(R.id.viewbook_img);
        removeButton = (Button) findViewById(R.id.viewbook_remove);
        removal_hint = (TextView) findViewById(R.id.viewbook_marked);
    }

    private void createListeners(){
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRemoval();

            }
        });
        personal_evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(title.getContext(), R.string.rating_updated, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populate(){
        if(book != null) {
            toolbar.setTitle(book.getTitle());
            rating = book.getPersonal_evaluation();
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            publishedDate.setText(book.getPublishedDate());
            publisher.setText(book.getPublisher());
            category.setText(book.getCategory());
            personal_evaluation.setRating(rating);
            thumbnail.setImageBitmap(serialBitmap.getBitmap(book.getThumbnail()));
        }
    }

    private void checkFormForUpdates(){
        if(bytes != -1 ) {
            if (personal_evaluation.getRating() != rating) {
                book.setPersonal_evaluation(personal_evaluation.getRating());
                bytes = 1;
            } else {
                bytes = 0;
            }
        }
    }

    private void finishAndReturn(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(BOOK_KEY, book);
        resultIntent.putExtra(RESULT_KEY, bytes);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void handleRemoval(){
        switch (bytes){
            case -1:
                bytes = 0;
                removal_hint.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                removeButton.setText(R.string.remove_book);
                break;
            default:
                bytes = -1;
                removal_hint.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                removeButton.setText(R.string.undo);
                break;
        }
    }
}
