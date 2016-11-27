package marcer.pau.bookdatabase.serializables;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {
    private long id;
    //TODO id managed by database autoincrement
    private String title;
    private String author;
    private String publishedDate;
    private String publisher;
    private String category;
    private float personal_evaluation;
    private String thumbnailPath;
    //private BookImageHandler bookImageHandler;

    public Book(){
        this.title = "";
        this.author = "";
        this.publishedDate = "";
        this.publisher = "";
        this.category = "";
        this.personal_evaluation = 0;
        this.thumbnailPath = "";
    }

    public Book(String title, String author, String publishedDate, String publisher,
                String category, int personal_evaluation, String thumbnailPath) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.category = category;
        this.personal_evaluation = personal_evaluation;
        this.thumbnailPath = thumbnailPath;
        //bookImageHandler = new BookImageHandler(context);
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
                this.thumbnailPath = imageLinks.getString("smallThumbnail");
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

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", title, author);
    }

}

//private class BookImageHandler {
//    //http://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
//        Context context;
//
//        BookImageHandler(Context context){
//            this.context = context;
//        }
//
//        private String saveToInternalStorage(Bitmap bitmapImage){
//            ContextWrapper cw = new ContextWrapper(context);
//            // path to /data/data/yourapp/app_data/imageDir
//            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//            // Create imageDir
//            File mypath=new File(directory,"profile.jpg");
//
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(mypath);
//                // Use the compress method on the BitMap object to write image to the OutputStream
//                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    fos.close();
//                } catch (NullPointerException | IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return directory.getAbsolutePath();
//        }
//        private void loadImageFromStorage(String path)
//        {
//            try {
//                File f=new File(path, "profile.jpg");
//                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//                ImageView img=(ImageView)findViewById(R.id.imgPicker);
//                img.setImageBitmap(b);
//            }
//            catch (FileNotFoundException e)
//            {
//                e.printStackTrace();
//            }
//
//        }
//    }
