package marcer.pau.bookdatabase.bookView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    private FloatingActionButton floatingActionButton;
    private ToggleButton readed;
    private Book book;
    private SerialBitmap serialBitmap;
    private float rating;
    private String isRead;
    private byte bytes;
    private static final String BOOK_KEY = "BOOK";
    private static final String RESULT_KEY = "RESULT_CODE";
    private int READED;
    private int UNREADED;

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
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButtonRemoveBook);
        readed = (ToggleButton) findViewById(R.id.viewbook_read_text);
        READED = readed.getResources().getColor(R.color.darkblue);
        UNREADED = readed.getResources().getColor(R.color.lightGrey);
    }

    private void createListeners(){
        personal_evaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(title.getContext(), R.string.rating_updated, Toast.LENGTH_SHORT).show();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = ConfirmBookRemove();
                dialog.show();
            }
        });
        readed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    isRead = "TRUE";
                    readed.setBackgroundColor(READED);

                }
                else {
                    isRead = "FALSE";
                    readed.setBackgroundColor(UNREADED);
                }
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
            switch (book.getReaded()) {
                case "TRUE":
                    readed.setChecked(true);
                    readed.setBackgroundColor(READED);
                    isRead = "TRUE";
                    break;
                case "FALSE":
                    readed.setChecked(false);
                    readed.setBackgroundColor(UNREADED);
                    isRead = "FALSE";
                    break;
            }
        }
    }

    private void checkFormForUpdates(){
        if(bytes != -1 ) {
            if (personal_evaluation.getRating() != rating) {
                book.setPersonal_evaluation(personal_evaluation.getRating());
                bytes = 1;
            }
            if (!book.getReaded().equals(isRead)){
                book.setReaded(isRead);
                bytes = 1;
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
        bytes = -1;
        finishAndReturn();
    }

    private AlertDialog ConfirmBookRemove() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.remove)
                .setMessage(R.string.remoove_book_question)
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        handleRemoval();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
