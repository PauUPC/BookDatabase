package marcer.pau.bookdatabase.newBook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.api.BookApiRequester;
import marcer.pau.bookdatabase.R;
import marcer.pau.bookdatabase.serializables.SerialBitmap;

public class NewBook extends AppCompatActivity implements BookApiRequester.BookApiRequesterResponse {

    private Toolbar toolbar;
    private EditText editText;
    private TextView textView;
    private Button button_manual;
    private Button button_submit;
    private ProgressBar progressBar;
    private BookApiRequester bookApiRequester;
    private Book book;
    byte behaviourAdd;
    private SerialBitmap serialBitmap;
    private final static int CODE_CHILD_FORM = 2;

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
        getMenuInflater().inflate(R.menu.default_menu, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.menuadeff_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void receivedNewBook(Book book) {
        if(book != null) {
            if (book.getTitle().equals("ERROR")) {
                enableControls();
                handleBadQuery();
            } else if (book.getTitle().equals("")) {
                enableControls();
                handleBadQuery();
                Toast.makeText(getBaseContext(), R.string.no_internet,Toast.LENGTH_LONG).show();
            } else {
                this.book = book;
                enableControls();
                launchForm();
            }
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

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode == CODE_CHILD_FORM) {
            this.book = (Book) data.getExtras().getSerializable("BOOK");
            finishAndReturn();
        }
    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.menuadd_toolbar);
        toolbar.setTitle(R.string.menu_add_book);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);
    }

    private void createObjects() {
        behaviourAdd = 1;
        serialBitmap = new SerialBitmap();
        editText = (EditText) findViewById(R.id.addbook_input_isbn);
        button_manual = (Button) findViewById(R.id.addbook_input_button_manual);
        button_submit = (Button) findViewById(R.id.addbook_input_button_submit);
        progressBar = (ProgressBar) findViewById(R.id.addbook_progressBar);
        textView = (TextView) findViewById(R.id.addbook_worngQuery);
    }

    private void createListeners() {
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
                launchForm();
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (behaviourAdd){
                    case 1:
                        behaviourAdd = 0;
                        triggerBooksQuery(editText.getText().toString());
                        break;
                    case 0:
                        behaviourAdd = 1;
                        bookApiRequester.cancel(true);
                        enableControls();
                        break;
                }
            }
        });
    }

    private void triggerBooksQuery(final String isbn) {
        hideError();
        disableControls();
        bookApiRequester = new BookApiRequester(this);
        bookApiRequester.execute(isbn);
    }

    private void disableControls() {
        button_submit.setText(R.string.cancel);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        button_manual.setEnabled(false);
        editText.setEnabled(false);
    }

    private void enableControls() {
        button_submit.setText(R.string.add);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        button_manual.setEnabled(true);
        editText.setEnabled(true);
    }

    private void handleBadQuery() {
        //TODO Show Bad Query Feedback
        showError();
    }

    private void finishAndReturn() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("BOOK", book);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void showError(){
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private void hideError(){
        textView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
    }

    private void launchForm(){
        Intent intent = new Intent(getApplicationContext(), NewBookForm.class);
        intent.putExtra("BOOK",book);
        startActivityForResult(intent, CODE_CHILD_FORM);
    }

    private void showHelp() {
        AlertDialog alertDialog = help();
        alertDialog.show();
    }

    private AlertDialog help() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.help)
                .setMessage(R.string.help_addbook)
                .setIcon(R.drawable.ic_menu_help_black_24dp)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
