package marcer.pau.bookdatabase;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.serializables.SerialBitmap;

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
        private SerialBitmap serialBitmap;
        private ImageView thumbnail;
        private TextView title;
        private TextView author;
        private TextView year;
        private LinearLayout label;
        int colorID;

        BookHolder(View view) {
            super(view);
            serialBitmap = new SerialBitmap();
            thumbnail = (ImageView) view.findViewById(R.id.recycleritemimg);
            title = (TextView) view.findViewById(R.id.recycleritemTitle);
            author = (TextView) view.findViewById(R.id.recycleritemAuthor);
            year = (TextView) view.findViewById(R.id.recycleritemYear);
            label = (LinearLayout) view.findViewById(R.id.viewbook_label);
            view.setOnClickListener(this);
            colorID = view.getResources().getColor(R.color.blue);
        }

        @Override
        public void onClick(View view) {
            int[] coord = new int[2];
            thumbnail.getLocationOnScreen(coord);
            Toast toast = Toast.makeText(author.getContext(), "Swipe left to mark as readed", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.START, coord[0], coord[1]);
            toast.show();
            int[] coord1 = new int[2];
            title.getLocationOnScreen(coord1);
            Toast toast1 = Toast.makeText(author.getContext(), "Swipe right to view details", Toast.LENGTH_SHORT);
            toast1.setGravity(Gravity.TOP | Gravity.END, coord1[0], coord1[1]);
            toast1.show();
        }

        public void bindBook(Book book) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            year.setText(book.getPublishedDate());
            thumbnail.setImageBitmap(serialBitmap.getBitmap(book.getThumbnail()));
            if(book.getReaded().equals("TRUE")){
                label.setBackgroundColor(colorID);
            }
        }
    }
}
