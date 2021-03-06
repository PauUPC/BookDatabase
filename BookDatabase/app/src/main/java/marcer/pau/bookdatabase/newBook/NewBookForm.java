package marcer.pau.bookdatabase.newBook;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import marcer.pau.bookdatabase.api.RequestThumbnail;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.R;
import marcer.pau.bookdatabase.serializables.SerialBitmap;


public class NewBookForm extends AppCompatActivity implements RequestThumbnail.AsyncResponse {

    Toolbar toolbar;
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
    private ToggleButton readed;
    private String isRead;
    private static final String BOOK_KEY = "BOOK";
    private int READED;
    private int UNREADED;

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
        getMenuInflater().inflate(R.menu.add_book_form, menu);
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
            case R.id.menuaddform_action_forward:
                extractBookFromForm();
                return true;
            case R.id.menuaddform_action_help:
                showHelp();
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
    }

    private void createObjects(){
        book = (Book) getIntent().getSerializableExtra(BOOK_KEY);
        requestThumbnail = new RequestThumbnail(this);
        serialBitmap = new SerialBitmap();

        title = (EditText) findViewById(R.id.addbookform_title);
        author = (EditText) findViewById(R.id.addbookform_author);
        thumbnail =(ImageView) findViewById(R.id.addbookform_img);
        publishedDate = (EditText) findViewById(R.id.addbookform_pubdate);
        publisher = (EditText) findViewById(R.id.addbookform_publisher);
        category = (EditText) findViewById(R.id.addbookform_cat);
        personal_evaluation = (RatingBar) findViewById(R.id.addbookform_eval);

        readed = (ToggleButton) findViewById(R.id.add_book_form_read);
        READED = readed.getResources().getColor(R.color.darkblue);
        UNREADED = readed.getResources().getColor(R.color.lightGrey);
    }

    private void createListeners(){
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

        readed.setChecked(false);
        readed.setBackgroundColor(UNREADED);
        isRead = "FALSE";
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

    private void extractBookFromForm(){
        if(validate()) {
            String thumbnailURL;
            if (book != null)
                thumbnailURL = book.getThumbnailURL();
            else
                thumbnailURL = "";
            byte[] bytes;
            if (bitmap != null)
                bytes = serialBitmap.getBytes(bitmap);
            else
                bytes = null;
            book = new Book(
                    title.getText().toString(),
                    author.getText().toString(),
                    publishedDate.getText().toString(),
                    publisher.getText().toString(),
                    category.getText().toString(),
                    book.getIsbn(),
                    personal_evaluation.getRating(),
                    thumbnailURL,
                    bytes,
                    isRead
            );
            finishAndReturn();
        }
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

    private boolean validate(){
        boolean eval = true;
        if(title.getText().toString().equals("")) {
            title.getBackground().setColorFilter(getResources().getColor(R.color.red),
                    PorterDuff.Mode.SRC_IN);
            title.setHintTextColor(getResources().getColor(R.color.red));
            eval = false;
        } else {
            title.getBackground().clearColorFilter();
        }
        if(author.getText().toString().equals("")) {
            author.getBackground().setColorFilter(getResources().getColor(R.color.red),
                    PorterDuff.Mode.SRC_IN);
            author.setHintTextColor(getResources().getColor(R.color.red));
            eval = false;
        } else {
            author.getBackground().clearColorFilter();
        }
        if(category.getText().toString().equals("")) {
            category.getBackground().setColorFilter(getResources().getColor(R.color.red),
                    PorterDuff.Mode.SRC_IN);
            category.setHintTextColor(getResources().getColor(R.color.red));
            eval = false;
        } else {
            category.getBackground().clearColorFilter();
        }
        if(!eval){
            ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(200);
            Toast.makeText(getBaseContext(), R.string.missing_fields,Toast.LENGTH_LONG).show();
        }
        return eval;
    }

    @Override
    public void processFinish(Bitmap image) {
        thumbnail.setImageBitmap(image);
        this.bitmap = image;
    }

    private void showHelp() {
        AlertDialog alertDialog = help();
        alertDialog.show();
    }

    private AlertDialog help() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.help)
                .setMessage(R.string.help_addbookform)
                .setIcon(R.drawable.ic_menu_help_black_24dp)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
