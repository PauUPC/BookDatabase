package marcer.pau.bookdatabase.serializables;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import marcer.pau.bookdatabase.R;

public class Book implements Serializable {
    private long id;
    //TODO id managed by database autoincrement
    private String title;
    private String author;
    private String publishedDate;
    private String publisher;
    private String category;
    private float personal_evaluation;
    private String thumbnailURL;
    private byte[] thumbnail;
    //private BookImageHandler bookImageHandler;

    public Book(){
        this.title = "";
        this.author = "";
        this.publishedDate = "";
        this.publisher = "";
        this.category = "";
        this.personal_evaluation = 0;
        this.thumbnailURL = "";
        this.thumbnail = null;
    }

    public Book(String title, String author, String publishedDate, String publisher,
                String category, float personal_evaluation, String thumbnailURL, byte[] bitmap) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.category = category;
        this.personal_evaluation = personal_evaluation;
        this.thumbnailURL = thumbnailURL;
        this.thumbnail = bitmap;
    }

    public Book(JSONObject jsonObject){
        try {
            JSONArray array = jsonObject.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                this.title = volumeInfo.getString("title");
                JSONArray authors = volumeInfo.getJSONArray("authors");
                this.author = authors.getString(0);
                this.publishedDate = volumeInfo.getString("publishedDate");
                JSONArray category = volumeInfo.getJSONArray("categories");
                this.category = category.getString(0);
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                this.thumbnailURL = imageLinks.getString("smallThumbnail");
                this.personal_evaluation = 0;
                this.publisher = "unknown";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public  void setID(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCategory() {
        return category;
    }

    public float getPersonal_evaluation() {
        return personal_evaluation;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] bitmap){
        this.thumbnail = bitmap;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", title, author);
    }

}