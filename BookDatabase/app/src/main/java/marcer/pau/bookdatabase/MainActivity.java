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

import java.util.ArrayList;

import marcer.pau.bookdatabase.adapters.RecyclerAdapter;
import marcer.pau.bookdatabase.async.AsyncDbCreate;
import marcer.pau.bookdatabase.async.AsyncDbDelete;
import marcer.pau.bookdatabase.async.AsyncDbQuery;
import marcer.pau.bookdatabase.async.AsyncDbUpdate;
import marcer.pau.bookdatabase.bookView.BookView;
import marcer.pau.bookdatabase.database.BookData;
import marcer.pau.bookdatabase.newBook.NewBook;
import marcer.pau.bookdatabase.serializables.Book;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AsyncDbQuery.AsyncDbQueryResponse,
        AsyncDbCreate.AsyncDbCreateResponse,
        AsyncDbUpdate.AsyncDbUpdateResponse{

    private ArrayList<Book> bookArrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private RecyclerAdapter adapter;
    private BookData bookData;
    private ShowBookHandler showBookHandler;
    private ExtrasHandler extrasHandler;
    private MainActivity.OnItemTouchListener onItemTouchListener;
    private MenuItem layoutIcon;
    private int lastViewPosition;
    private final static int CODE_CHILD_NEW = 1;
    private final static int CODE_CHILD_VIEW = 3;
    SearchView searchView;
    AsyncDbCreate asyncDbCreate;
    AsyncDbQuery asyncDbQuery;
    AsyncDbDelete asyncDbDelete;
    AsyncDbUpdate asyncDbUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMenus();
        createObjects();
        createListeners();
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
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                adapter.filter(query);
                return true;
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
            addNewBook();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addNewBook(){
        Intent intent = new Intent(this, NewBook.class);
        startActivityForResult(intent, CODE_CHILD_NEW);
    }

    private void changeLayoutManager() {
        if (recyclerView.getLayoutManager().equals(linearLayoutManager)) {
            setGridLayout();
        } else {
            setLinearLayout();
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode == CODE_CHILD_NEW) {
            newBook((Book) data.getExtras().getSerializable("BOOK"));
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

    private void newBook(Book book){
        asyncDbCreate = new AsyncDbCreate(this, bookData);
        asyncDbCreate.execute(book);
    }

    private void deleteBook(Book book){
        bookArrayList.remove(lastViewPosition);
        recyclerView.getAdapter().notifyItemRemoved(lastViewPosition);
        asyncDbDelete = new AsyncDbDelete(bookData);
        asyncDbDelete.execute(book);
    }

    private void createObjects(){
        initializeRecyclerArray();
        showBookHandler = new ShowBookHandler(this);
        extrasHandler = new ExtrasHandler(this);
    }

    private void initializeRecyclerArray(){
        bookData = new BookData(this);
        asyncDbQuery = new AsyncDbQuery(this, bookData);
        asyncDbQuery.execute("CATEGORY");
    }

    private void createListeners(){
        onItemTouchListener = new OnItemTouchListener() {
            @Override
            public void onPlusclicked(View view, int position) {
                lastViewPosition = position;
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

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        recyclerView.setItemAnimator(itemAnimator);

        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();
    }

    public interface OnItemTouchListener {
        void onPlusclicked(View view, int position);
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
                        lastViewPosition = viewHolder.getAdapterPosition();
                        book = bookArrayList.get(lastViewPosition);
                        showBookHandler.updateReadStatus(book);
                        recyclerView.getAdapter().notifyItemChanged(lastViewPosition);
                        break;
//                    case ItemTouchHelper.RIGHT:
//                        lastViewPosition = viewHolder.getAdapterPosition();
//                        book = bookArrayList.get(lastViewPosition);
//                        deleteBook(book);
//                        break;
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void reloadArrayAdapter(){
        recyclerView.setAdapter(null);
        adapter = new RecyclerAdapter(bookArrayList, onItemTouchListener);
        recyclerView.setAdapter(adapter);
    }

    private void commitBookUpdate(Book book){
        asyncDbUpdate = new AsyncDbUpdate(this,bookData);
        asyncDbUpdate.execute(book);
    }

    @Override
    public void onFinishAsyncDbQuery(ArrayList<Book> books) {
        updateArrayAdapter(books);
    }

    @Override
    public void onFinishAsyncDbCreate(ArrayList<Book> books) {
        updateArrayAdapter(books);
    }

    @Override
    public void onFinishAsyncDbUpdate(ArrayList<Book> books) {
        updateArrayAdapter(books);
    }

    private void updateArrayAdapter(ArrayList<Book> books){
        bookArrayList = null;
        recyclerView.setAdapter(null);
        bookArrayList = books;
        adapter = new RecyclerAdapter(bookArrayList,  onItemTouchListener);
        recyclerView.setAdapter(adapter);
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

        void updateBookRating(Book book){
                commitBookUpdate(book);
        }

        void updateReadStatus(Book book){
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

    private class ExtrasHandler {

        private Context context;

        ExtrasHandler(Context context) {
            this.context = context;
        }

        void startAbout(){

        }

        void startHelp(){

        }
    }

    //TODO: Handle user permissions for internet and storage
}
