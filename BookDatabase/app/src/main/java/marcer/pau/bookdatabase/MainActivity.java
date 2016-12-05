package marcer.pau.bookdatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import marcer.pau.bookdatabase.extras.About;
import marcer.pau.bookdatabase.extras.FirstTimeSetup;
import marcer.pau.bookdatabase.extras.FirstimeRequest;
import marcer.pau.bookdatabase.extras.Help;
import marcer.pau.bookdatabase.newBook.NewBook;
import marcer.pau.bookdatabase.serializables.Book;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AsyncDbQuery.AsyncDbQueryResponse,
        AsyncDbCreate.AsyncDbCreateResponse,
        FirstTimeSetup.FirstTimeSetupResponse {

    private ArrayList<Book> bookArrayList;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private RecyclerAdapter adapter;
    private BookData bookData;
    private ShowBookHandler showBookHandler;
    private ExtrasHandler extrasHandler;
    private SharedPreferences sharedPreferences;
    private MainActivity.OnItemTouchListener onDetailsTouchListener;
    private FloatingActionButton floatingActionButtonNewBook;
    private MenuItem layoutIcon;
    private DrawerLayout drawer;
    private int lastViewPosition;
    private String CRITERIA;
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
        CRITERIA = "TITLE";
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                adapter.filter(query, CRITERIA);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_title:
                CRITERIA = "TITLE";
                break;
            case R.id.menu_search_author:
                CRITERIA = "AUTHOR";
                break;
            case R.id.menu_search_category:
                CRITERIA = "CATEGORY";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        item.setChecked(true);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.menu_change_layout:
                changeLayoutManager();
                break;
            case R.id.menu_order_author:
                changeArrayOrder("AUTHOR");
                break;
            case R.id.menu_order_category:
                changeArrayOrder("CATEGORY");
                break;
            case R.id.menu_order_title:
                changeArrayOrder("TITLE");
                break;
            case R.id.menu_about:
                extrasHandler.showAbout();
                break;
            case R.id.menu_help:
                extrasHandler.showHelp();
                break;
        }

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
//        bookData.open();
        if (sharedPreferences.getBoolean("firstrun", true)) {
            FirstTimeSetup firstTimeSetup = new FirstTimeSetup(this);
            firstTimeSetup.Start();
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        bookData.close();
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
                    showBookHandler.updateBook(book);
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
        sharedPreferences = getSharedPreferences("marcer.pau.bookdatabase", MODE_PRIVATE);
        showBookHandler = new ShowBookHandler(this);
        extrasHandler = new ExtrasHandler(this);
        floatingActionButtonNewBook = (FloatingActionButton)
                findViewById(R.id.floatingActionButtonAddBook);
    }

    private void initializeRecyclerArray(){
        bookData = new BookData(this);
        bookData.open();
        asyncDbQuery = new AsyncDbQuery(this, bookData);
        asyncDbQuery.execute("CATEGORY");
    }

    private void createListeners(){
        onDetailsTouchListener = new OnItemTouchListener() {
            @Override
            public void onDetailsclicked(View view, int position) {
                lastViewPosition = position;
                Book book = bookArrayList.get(position);
                showBookHandler.showBookDetails(book);
                recyclerView.getAdapter().notifyItemChanged(position);
            }
        };
        floatingActionButtonNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewBook();
            }
        });
    }

    private void createMenus() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);
        layoutIcon = navigationView.getMenu().findItem(R.id.menu_change_layout);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void createRecyclerview(){
        recyclerView = (RecyclerView) findViewById(R.id.content_main);
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

    @Override
    public void onFinishFirstTimeSetup(ArrayList<Book> books) {
        for(Book book:books){
            AsyncDbCreate create = new AsyncDbCreate(this,bookData);
            create.execute(book);
        }
    }


    public interface OnItemTouchListener {
        void onDetailsclicked(View view, int position);
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
        ItemTouchHelper.SimpleCallback itemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder viewHolder1) {
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
                        //swipe back the book do not del!!
                        recyclerView.getAdapter().notifyItemChanged(lastViewPosition);
                        break;
//                    //debug purposes
//                    case ItemTouchHelper.RIGHT:
//                        deleteBook(bookArrayList.get(viewHolder.getAdapterPosition()));
//                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void commitBookUpdate(Book book){
        asyncDbUpdate = new AsyncDbUpdate(this,bookData);
        asyncDbUpdate.execute(book);
    }

    private void changeArrayOrder(String code){
        asyncDbQuery = new AsyncDbQuery(this, bookData);
        switch (code){
            case "TITLE":
                asyncDbQuery.execute(code);
                break;
            case "AUTHOR":
                asyncDbQuery.execute(code);
                break;
            case "CATEGORY":
                asyncDbQuery.execute(code);
                break;
        }
    }

    @Override
    public void onFinishAsyncDbQuery(ArrayList<Book> books) {
        updateArrayAdapter(books);
    }

    @Override
    public void onFinishAsyncDbCreate(ArrayList<Book> books) {
        updateArrayAdapter(books);
    }

    private void updateArrayAdapter(ArrayList<Book> books){
        bookArrayList = null;
        recyclerView.setAdapter(null);
        bookArrayList = books;
        adapter = new RecyclerAdapter(bookArrayList, onDetailsTouchListener);
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

        void updateBook(Book book){
            bookArrayList.set(lastViewPosition,book);
            recyclerView.getAdapter().notifyItemChanged(lastViewPosition,null);
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

        void showAbout(){
            Intent intent = new Intent(context, About.class);
            startActivity(intent);
        }

        void showHelp(){
            Intent intent = new Intent(context, Help.class);
            startActivity(intent);
        }
    }

    //TODO: Handle user permissions for internet and storage
}
