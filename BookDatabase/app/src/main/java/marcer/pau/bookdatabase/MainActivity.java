package marcer.pau.bookdatabase;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;

import marcer.pau.bookdatabase.adapters.RecyclerAdapter;
import marcer.pau.bookdatabase.bookView.BookView;
import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.newBook.NewBook;
import marcer.pau.bookdatabase.newBook.NewBookForm;
import marcer.pau.bookdatabase.serializables.Book;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Book> bookArrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private RecyclerAdapter adapter;
    private BookData bookData;
    private SearchView searchView;
    private newBookHandler handleNewBook;
    private ShowBookHandler showBookHandler;
    private MainActivity.OnItemTouchListener onItemTouchListener;
    private MenuItem layoutIcon;
    private int lastSwipePosition;
    private final static int CODE_CHILD_NEW = 1;
    private final static int CODE_CHILD_VIEW = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMenus();
        createObjects();
        createRecyclerview();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                Toast.makeText(getApplicationContext(), "SearchOnQueryTextSubmit: " + query, Toast.LENGTH_SHORT).show();
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Toast.makeText(getApplicationContext(), "SearchOnQueryTextChanged: " + s, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_change_layout) {
            changeLayoutManager();
        }
        else if (id == R.id.menu_add_book) {
            handleNewBook.startNewBook();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        bookData.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //bookData.close();
        super.onPause();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode == CODE_CHILD_NEW) {
            Book book = (Book) data.getExtras().getSerializable("BOOK");
            handleNewBook.newBook(book);
        }
        if (resultCode==RESULT_OK && requestCode == CODE_CHILD_VIEW) {
            Book book = (Book) data.getExtras().getSerializable("BOOK");
            byte bytes = (byte) data.getExtras().getSerializable("RESULT_CODE");
            switch (bytes){
                case 1:
                    showBookHandler.updateBookRating(book);
                    break;
                case -1:
                    deleteBook(book);
                    break;
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, 3);
            layoutIcon.setVisible(false);
            setGridLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, 2);
            layoutIcon.setVisible(true);
            setLinearLayout();
        }
    }

    private void createObjects(){
        handleNewBook = new newBookHandler(this);
        bookArrayList = new ArrayList<>();
        bookData = new BookData(this);
        bookData.open();
        bookArrayList = bookData.orderByCategoryQuery();
        showBookHandler = new ShowBookHandler(this);
        onItemTouchListener = new OnItemTouchListener() {
            @Override
            public void onPlusclicked(View view, int position) {
                Book book = bookArrayList.get(position);
                showBookHandler.showBookDetails(book);
                recyclerView.getAdapter().notifyItemChanged(position);
            }
        };
    }

    private void createMenus(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layoutIcon = navigationView.getMenu().findItem(R.id.menu_change_layout);
    }

    private void createRecyclerview(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(bookArrayList,  onItemTouchListener);
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        recyclerView.setItemAnimator(itemAnimator);

        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();
    }

    private void setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                Book book;
                switch (swipeDir) {
                    case ItemTouchHelper.LEFT:
                        lastSwipePosition = viewHolder.getAdapterPosition();
                        book = bookArrayList.get(lastSwipePosition);
                        showBookHandler.updateReadStatus(book);
                        recyclerView.getAdapter().notifyItemChanged(lastSwipePosition);
                        break;
//                    case ItemTouchHelper.RIGHT:
//                        lastSwipePosition = viewHolder.getAdapterPosition();
//                        book = bookArrayList.get(lastSwipePosition);
//                        deleteBook(book);
//                        break;
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteBook(Book book){
        book = bookArrayList.get(lastSwipePosition);
        bookArrayList.remove(lastSwipePosition);
        recyclerView.getAdapter().notifyItemRemoved(lastSwipePosition);
        //delete from db
        bookData.deleteBook(book);
    }

    private void changeLayoutManager() {
        if (recyclerView.getLayoutManager().equals(linearLayoutManager)) {
            setGridLayout();
        } else {
            setLinearLayout();
        }
    }

    private void setGridLayout(){
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        layoutIcon.setIcon(R.drawable.ic_menu_view_list_black_24dp);
        layoutIcon.setTitle(R.string.menu_layout_list);
    }

    private void setLinearLayout(){
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        layoutIcon.setIcon(R.drawable.ic_menu_view_grid_black_24dp);
        layoutIcon.setTitle(R.string.menu_layout_grid);
    }

    private void reloadArrayAdapter(){
        recyclerView.setAdapter(null);
        adapter = new RecyclerAdapter(bookArrayList, onItemTouchListener);
        recyclerView.setAdapter(adapter);
    }

    private void commitBookUpdate(Book book){
        bookData.updateBook(book);
    }

    private class newBookHandler {
        private Context context;

        newBookHandler(Context context){
            this.context = context;

        }

        private void startNewBook(){
            Intent intent = new Intent(context, NewBook.class);
            startActivityForResult(intent, CODE_CHILD_NEW);
        }

        private void newBook(Book book){
            bookData.createBook(book);
            bookArrayList = bookData.repeatLastQuery();
            reloadArrayAdapter();
        }


    }

    private class ShowBookHandler {

        private Context context;

        ShowBookHandler(Context context) {
            this.context = context;
        }

        void showBookDetails(Book book){
            Intent intent = new Intent(context, BookView.class);
            intent.putExtra("BOOK",book);
            startActivityForResult(intent, CODE_CHILD_VIEW);
        }

        private void updateBookRating(Book book){
                //TODO do something with the rating
                commitBookUpdate(book);
        }

        private void updateReadStatus(Book book){
            switch (book.getReaded()){
                case "TRUE":
                    book.setReaded("FALSE");
                    break;
                case "FALSE":
                    book.setReaded("TRUE");
                    break;
            }
            commitBookUpdate(book);
        }
    }

    public interface OnItemTouchListener {
        void onPlusclicked(View view, int position);
    }

    //TODO: Handle user permissions for internet and storage
}
