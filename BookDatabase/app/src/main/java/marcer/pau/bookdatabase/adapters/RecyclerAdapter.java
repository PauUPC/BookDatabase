package marcer.pau.bookdatabase.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import marcer.pau.bookdatabase.MainActivity;
import marcer.pau.bookdatabase.R;
import marcer.pau.bookdatabase.serializables.Book;
import marcer.pau.bookdatabase.serializables.SerialBitmap;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.BookHolder>{

    private ArrayList<Book> bookArrayList;
    private ArrayList<Book> bufferBookArrayList;
    private MainActivity.OnItemTouchListener onItemTouchListener;

    public RecyclerAdapter(ArrayList<Book> books, MainActivity.OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
        bookArrayList = books;
        bufferBookArrayList = new ArrayList<>(bookArrayList);
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

    public void filter(String query, String criteria) {
        bookArrayList.clear();
        if(query.isEmpty()){
            bookArrayList.addAll(bufferBookArrayList);
        } else {
            query = query.toLowerCase();
            switch (criteria){
                case "AUTHOR":
                    for(Book book: bufferBookArrayList){
                        if(book.getAuthor().toLowerCase().contains(query)){
                            bookArrayList.add(book);
                        }
                    }
                    break;
                case "CATEGORY":
                    for(Book book: bufferBookArrayList){
                        if(book.getCategory().toLowerCase().contains(query)){
                            bookArrayList.add(book);
                        }
                    }
                    break;
                case "TITLE":
                    for(Book book: bufferBookArrayList){
                        if(book.getTitle().toLowerCase().contains(query)){
                            bookArrayList.add(book);
                        }
                    }
                    break;
            }

        }
        notifyDataSetChanged();
    }



    class BookHolder extends RecyclerView.ViewHolder {
        private SerialBitmap serialBitmap;
        private ImageView thumbnail;
        private TextView title;
        private TextView author;
        private TextView year;
        private LinearLayout label;
        private ImageButton plus;
        private int READED;
        private int UNREADED;

        BookHolder(View view) {
            super(view);
            serialBitmap = new SerialBitmap();
            thumbnail = (ImageView) view.findViewById(R.id.recycleritemimg);
            title = (TextView) view.findViewById(R.id.recycleritemTitle);
            author = (TextView) view.findViewById(R.id.recycleritemAuthor);
            year = (TextView) view.findViewById(R.id.recycleritemYear);
            label = (LinearLayout) view.findViewById(R.id.viewbook_label);
            plus = (ImageButton) view.findViewById(R.id.recycleritemPlus);
            READED = view.getResources().getColor(R.color.darkblue);
            UNREADED = view.getResources().getColor(R.color.lightGrey);

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onDetailsclicked(v, getPosition());
                }
            });
        }

        public void bindBook(Book book) {
            title.setText(book.getTitle());
            author.setText(book.getAuthor());
            year.setText(book.getPublishedDate());
            if(book.getThumbnail() != null)
                thumbnail.setImageBitmap(serialBitmap.getBitmap(book.getThumbnail()));
            else
                thumbnail.setImageResource(R.drawable.ic_book_launcher_black);
            switch (book.getReaded()) {
                case "TRUE":
                    label.setBackgroundColor(READED);
                    break;
                case "FALSE":
                    label.setBackgroundColor(UNREADED);
                    break;
            }
        }

    }
}
