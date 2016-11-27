package marcer.pau.bookdatabase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import marcer.pau.bookdatabase.serializables.Book;

public class BookData {

    // Database fields
    private SQLiteDatabase database;

    // Helper to manipulate table
    private MySQLiteHelper dbHelper;

    // Here we only select Title and Author, must select the appropriate columns
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_AUTHOR};

    public BookData(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createBook(Book book) {
        String title = book.getTitle();
        String author = book.getAuthor();
        ContentValues values = new ContentValues();
        Log.d("Creating", "Creating " + title + " " + author);

        // Add data: Note that this method only provides title and author
        // Must modify the method to add the full data
        values.put(MySQLiteHelper.COLUMN_TITLE, title);
        values.put(MySQLiteHelper.COLUMN_AUTHOR, author);

        // Invented data
        values.put(MySQLiteHelper.COLUMN_PUBLISHER, "Do not know");
        values.put(MySQLiteHelper.COLUMN_YEAR, 2030);
        values.put(MySQLiteHelper.COLUMN_CATEGORY, "Fantasia");
        values.put(MySQLiteHelper.COLUMN_PERSONAL_EVALUATION, "regular");

        // Actual insertion of the data using the values variable
        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS, null,
                values);

    }

    public void deleteBook(Book book) {
        long id = book.getId();
        System.out.println("Book deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_BOOKS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Book book = cursorToBook(cursor);
            books.add(book);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return books;
    }

    private Book cursorToBook(Cursor cursor) {
        Book book = new Book(cursor.getString(1), cursor.getString(2), null, null, null, null, null);
        book.setID(cursor.getLong(0));
        return book;
    }
}