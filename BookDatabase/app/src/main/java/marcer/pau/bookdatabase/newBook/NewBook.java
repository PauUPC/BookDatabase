package marcer.pau.bookdatabase.newBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.api.BookApiRequester;
import marcer.pau.bookdatabase.R;

public class NewBook extends AppCompatActivity implements BookApiRequester.BookApiRequesterResponse {

    private Toolbar toolbar;
    private EditText editText;
    private Button button_manual;
    private Button button_submit;
    private ProgressBar progressBar;
    private BookApiRequester bookApiRequester;
    private Book book;
    public final static int CODE_CHILD = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);
        createToolbar();
        createObjects();
        createListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.menuadd_action_forward:
                finishAndReturn();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void receivedNewBook(Book book) {
        if(book != null) {
            this.book = book;
            enableControls();
            finishAndReturn();
        } else {
            enableControls();
            handleBadQuery();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndReturn();
    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.menuadd_toolbar);
        toolbar.setTitle(R.string.menu_add_book);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);
    }

    private void createObjects() {
        bookApiRequester = new BookApiRequester(this);
    }

    private void createListeners(){
        editText = (EditText) findViewById(R.id.addbook_input_isbn);
        button_manual = (Button) findViewById(R.id.addbook_input_button_manual);
        button_submit = (Button) findViewById(R.id.addbook_input_button_submit);
        progressBar = (ProgressBar) findViewById(R.id.addbook_progressBar);

        editText.addTextChangedListener(new TextWatcher() {
            boolean finished = false;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 13){
                    finished = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(finished){
                    triggerBooksQuery(s.toString());
                    finished = false;
                }
            }
        });

        button_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                book = new Book();
                finishAndReturn();
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            byte behaviourAdd = 1;
            @Override
            public void onClick(View view) {
                switch (behaviourAdd){
                    case 1:
                        behaviourAdd = 0;
                        triggerBooksQuery(editText.getText().toString());
                        break;
                    case 0:
                        behaviourAdd = 1;
                        //TODO Stop query
                        enableControls();
                        break;
                }
            }
        });
    }

    private void triggerBooksQuery(final String isbn) {
        disableControls();
        bookApiRequester.execute(isbn);
    }

    private void disableControls() {
        button_submit.setText(R.string.cancel);
        progressBar.setVisibility(View.VISIBLE);
        button_manual.setEnabled(false);
        editText.setEnabled(false);
    }

    private void enableControls(){
        button_submit.setText(R.string.add);
        progressBar.setVisibility(View.INVISIBLE);
        button_manual.setEnabled(true);
        editText.setEnabled(true);
    }

    private void handleBadQuery(){
        //TODO Show Bad Query Feedback
    }

    private void finishAndReturn(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("BOOK", book);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
