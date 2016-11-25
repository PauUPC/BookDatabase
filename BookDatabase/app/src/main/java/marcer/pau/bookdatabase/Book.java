package marcer.pau.bookdatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String publishedDate;
    private String imagePath;
    //private BookImageHandler bookImageHandler;

    public Book(String title, String author, String publishDate){
        //bookImageHandler = new BookImageHandler(context);
        this.title = title;
        this.author = author;
        this.publishedDate = publishDate;
        this.imagePath = null;
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

//                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
//                String imageLink = imageLinks.getString("smallThumbnail");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getImagePath() {
        return imagePath;
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
