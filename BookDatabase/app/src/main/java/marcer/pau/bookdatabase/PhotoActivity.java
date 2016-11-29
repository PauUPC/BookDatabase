//
//package marcer.pau.bookdatabase;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//public class PhotoActivity extends AppCompatActivity {
//
//  private ImageView mPhotoImageView;
//  private TextView mDescriptionTextView;
//  private Photo mSelectedPhoto;
//  private static final String PHOTO_KEY = "PHOTO";
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//
//    setContentView(R.layout.activity_photo);
//
//    mPhotoImageView = (ImageView) findViewById(R.id.photoImageView);
//    mSelectedPhoto = (Photo) getIntent().getSerializableExtra(PHOTO_KEY);
//    Picasso.with(this).load(mSelectedPhoto.getUrl()).into(mPhotoImageView);
//
//    mDescriptionTextView = (TextView) findViewById(R.id.photoDescription);
//
//    if (mDescriptionTextView != null) {
//      mDescriptionTextView.setText(mSelectedPhoto.getExplanation());
//    }
//  }
//}
