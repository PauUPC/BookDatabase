package marcer.pau.bookdatabase;

import android.content.Context;
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
import android.widget.Toast;

public class NewBook extends AppCompatActivity implements BookApiRequester.BookApiRequesterResponse{

    Toolbar toolbar;
    EditText editText;
    Button button;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);

        toolbar = (Toolbar) findViewById(R.id.menuadd_toolbar);
        toolbar.setTitle(R.string.menu_add_book);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);

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

    }

    private void createListeners(){
        editText = (EditText) findViewById(R.id.addbook_input_isbn);
        button = (Button) findViewById(R.id.addbook_input_button);
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
                    triggerBooksQuery();
                    finished = false;
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
                nextForm(null);
            }
        });
    }

    private void triggerBooksQuery() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "new book", Toast.LENGTH_SHORT).show();
                disableControls();
                nextForm(new Book("exemple","exemple","exemple"));
                enableControls();
            }
        });
    }

    private void disableControls() {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        editText.setEnabled(false);
    }

    private void enableControls(){
        progressBar.setVisibility(View.INVISIBLE);
        button.setEnabled(true);
        editText.setEnabled(true);
    }

    private void nextForm(Book book){
        Context context = getBaseContext();
        Intent intent = new Intent(context, NewBookForm.class);
        intent.putExtra("BOOK",book);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
