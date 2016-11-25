package marcer.pau.bookdatabase;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewBook extends AppCompatActivity implements BookApiRequester.BookApiRequesterResponse{

    Toolbar toolbar;
    EditText editText;
    Button button_manual;
    Button button_submit;
    ProgressBar progressBar;
    BookApiRequester bookApiRequester;
    Runnable queryApirunnable;

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
                Toast.makeText(getApplicationContext(), "back pressed", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
                return true;
            case R.id.menuadd_action_forward:
                Toast.makeText(getApplicationContext(), "forward pressed", Toast.LENGTH_SHORT).show();
                nextForm(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void receivedNewBook(Book book) {
        if(book != null) {
            enableControls();
            nextForm(book);
        } else {
            enableControls();
            handleBadQuery();
        }
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
                Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
                nextForm(null);
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
        Runnable queryApiRunnable = new Runnable() {
            @Override
            public void run() {
                disableControls();
                bookApiRequester.getBook(isbn);
            }
        };
        runOnUiThread(queryApiRunnable );
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

    private void nextForm(Book book){
        Context context = getBaseContext();
        Intent intent = new Intent(context, NewBookForm.class);
        intent.putExtra("BOOK",book);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void handleBadQuery(){
        //TODO Show Bad Query Feedback
    }
}
