package marcer.pau.bookdatabase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.serializables.SerialBitmap;

public class BookData {

    private String lastQuery;
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE,
            MySQLiteHelper.COLUMN_AUTHOR,
            MySQLiteHelper.COLUMN_YEAR,
            MySQLiteHelper.COLUMN_PUBLISHER,
            MySQLiteHelper.COLUMN_CATEGORY,
            MySQLiteHelper.COLUMN_PERSONAL_EVALUATION,
            MySQLiteHelper.COLUMN_THUMBNAIL_PATH,
            MySQLiteHelper.COLUMN_THUMBNAIL_BMP
    };

    public BookData(Context context) {
        dbHelper = new MySQLiteHelper(context);
        lastQuery = "TITLE";
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createBook(Book book) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_TITLE, book.getTitle());
        values.put(MySQLiteHelper.COLUMN_AUTHOR, book.getAuthor());
        values.put(MySQLiteHelper.COLUMN_YEAR, book.getPublishedDate());
        values.put(MySQLiteHelper.COLUMN_PUBLISHER, book.getPublisher());
        values.put(MySQLiteHelper.COLUMN_CATEGORY, book.getCategory());
        values.put(MySQLiteHelper.COLUMN_PERSONAL_EVALUATION,
                book.getPersonal_evaluation());
        values.put(MySQLiteHelper.COLUMN_THUMBNAIL_PATH, book.getThumbnailURL());
        values.put(MySQLiteHelper.COLUMN_THUMBNAIL_BMP, book.getThumbnail());
        long insertId = database.insert(MySQLiteHelper.TABLE_BOOKS, null, values);
    }

    public void deleteBook(Book book) {
        long id = book.getId();
        database.delete(MySQLiteHelper.TABLE_BOOKS,
                MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    public ArrayList<Book> getAllBooksQuery() {
        ArrayList<Book> books = new ArrayList<>();
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
        lastQuery = "ALL";
        return books;
    }

    public ArrayList<Book> orderByTitleQuery(){
        ArrayList<Book> books = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TITLE+"DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Book book = cursorToBook(cursor);
            books.add(book);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        lastQuery = "TITLE";
        return books;
    }

    public ArrayList<Book> orderByCategoryQuery(){
        ArrayList<Book> books = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_BOOKS,
                allColumns, null, null, null, null, MySQLiteHelper.COLUMN_CATEGORY+"DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Book book = cursorToBook(cursor);
            books.add(book);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        lastQuery = "CATEGORY";
        return books;
    }

    public ArrayList<Book> repeatLastQuery(){
        switch (lastQuery){
            case "ALL":
                return getAllBooksQuery();
            case "TITLE":
                return orderByTitleQuery();
            case "CATEGORY":
                return orderByCategoryQuery();
        }
        return null;
    }

//    public ArrayList<Book> filterByCategory(String category){
//
//    }
//
//    public ArrayList<Book> filterByAuthor(String category){
//
//    }


    private Book cursorToBook(Cursor cursor) {
        Book book = new Book(cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getFloat(6),
                cursor.getString(7),
                cursor.getBlob(8)
        );
        book.setID(cursor.getLong(0));
        return book;
    }
}