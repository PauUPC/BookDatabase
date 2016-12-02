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
    private String readed;
    private static final String UNKNOWN = "UNKNOWN";

    public Book(){
        this.title = "";
        this.author = "";
        this.publishedDate = "";
        this.publisher = "";
        this.category = "";
        this.personal_evaluation = 0;
        this.thumbnailURL = "";
        this.thumbnail = null;
        this.readed = "FALSE";
    }

    public Book(String title, String author, String publishedDate, String publisher, String category,
                float personal_evaluation, String thumbnailURL, byte[] bitmap, String readed) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.category = category;
        this.personal_evaluation = personal_evaluation;
        this.thumbnailURL = thumbnailURL;
        this.thumbnail = bitmap;
        this.readed = readed;
    }

    public Book(JSONObject jsonObject){
        boolean control = false;
        try {
            JSONArray array = jsonObject.getJSONArray("items");
            JSONObject item = array.getJSONObject(0);
            JSONObject volumeInfo = item.getJSONObject("volumeInfo");

            String title_s = null;
            try {
                title_s = volumeInfo.getString("title");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(title_s != null)
                    this.title = title_s;
                else
                    this.title = UNKNOWN;
            }

            String authors_s = null;
            try {
                JSONArray authors = volumeInfo.getJSONArray("authors");
                authors_s = authors.getString(0);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (authors_s != null)
                    this.author = authors_s;
                else
                    this.author = UNKNOWN;
            }

            String publishdate_s = null;
            try {
                publishdate_s = volumeInfo.getString("publishedDate");
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (publishdate_s != null)
                    this.publishedDate = publishdate_s;
                else
                    this.publishedDate = UNKNOWN;
            }

            String category_s = null;
            try {
                JSONArray category = volumeInfo.getJSONArray("categories");
                category_s = category.getString(0);
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (category_s != null)
                    this.category = category_s;
                else
                    this.category = UNKNOWN;
            }

            String imageLinks_S = null;
            try {
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                imageLinks_S = imageLinks.getString("smallThumbnail");
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (imageLinks_S != null)
                    this.thumbnailURL  = imageLinks_S;
                else
                    this.thumbnailURL = UNKNOWN;
            }
            this.personal_evaluation = 0;
            this.publisher = UNKNOWN;
            this.readed = "FALSE";
            //if book was not correcly set, call empty constructor
            //to handle erros caused by inconsistent json data
            control = true;
        } catch (JSONException e1) {
            e1.printStackTrace();
        } finally {
            if(!control){
                this.title = "ERROR";
                this.author = "";
                this.publishedDate = "";
                this.publisher = "";
                this.category = "";
                this.personal_evaluation = 0;
                this.thumbnailURL = "";
                this.thumbnail = null;
                this.readed = "FALSE";
            }
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

    public void setPersonal_evaluation(float personal_evaluation) {
        this.personal_evaluation = personal_evaluation;
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

    public String getReaded() {
        return readed;
    }

    public void setReaded(String readed) {
        this.readed = readed;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", title, author);
    }

}