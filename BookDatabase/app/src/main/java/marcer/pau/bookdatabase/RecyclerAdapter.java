package marcer.pau.bookdatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import marcer.pau.bookdatabase.serializables.Book;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.BookHolder> {

    private ArrayList<Book> bookArrayList;

    public RecyclerAdapter(ArrayList<Book> books) {
        bookArrayList = books;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_row, parent, false);
        return new BookHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        holder.bindBook(bookArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    static class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView thumbnail;
        private TextView title;
        private TextView author;

        BookHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.recycleritemimg);
            title = (TextView) view.findViewById(R.id.recycleritemTitle);
            author = (TextView) view.findViewById(R.id.recycleritemAuthor);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }

        public void bindBook(Book book) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            thumbnail.setImageBitmap(BitmapFactory.decodeResource(thumbnail.getContext().getResources(),
                    R.mipmap.ic_no_image_available));
            //TODO BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_image_available);

        }
    }
}
