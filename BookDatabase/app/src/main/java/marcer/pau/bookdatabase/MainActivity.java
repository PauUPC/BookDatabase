package marcer.pau.bookdatabase;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;
import java.util.ArrayList;

import marcer.pau.bookdatabase.newBook.NewBook;
import marcer.pau.bookdatabase.newBook.NewBookForm;
import marcer.pau.bookdatabase.serializables.Book;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Book> bookArrayList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerAdapter mAdapter;

    private SearchView searchView;
    private handleNewBook handleNewBook;
    private final static int CODE_CHILD_NEW = 1;
    private final static int CODE_CHILD_FORM = 2;

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_change_layout) {
            changeLayoutManager(item);
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
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && requestCode == CODE_CHILD_NEW) {
            Book book = (Book) data.getExtras().getSerializable("BOOK");
            handleNewBook.startNewBookForm(book);
        }
        if (resultCode==RESULT_OK && requestCode == CODE_CHILD_FORM) {
            Book book = (Book) data.getExtras().getSerializable("BOOK");
            handleNewBook.newBook(book);
        }
    }

    private void createObjects(){
        handleNewBook = new handleNewBook(this);
        bookArrayList = new ArrayList<>();
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
    }

    private void createRecyclerview(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new RecyclerAdapter(bookArrayList);
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();
    }

    private int getLastVisibleItemPosition() {
        int itemCount;
        if (mRecyclerView.getLayoutManager().equals(mLinearLayoutManager)) {
            itemCount = mLinearLayoutManager.findLastVisibleItemPosition();
        } else {
            itemCount = mGridLayoutManager.findLastVisibleItemPosition();
        }
        return itemCount;
    }

    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                // TODO getLastVisibleItemPosition()
            }
        });
    }

    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                bookArrayList.remove(position);
                mRecyclerView.getAdapter().notifyItemRemoved(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void changeLayoutManager(MenuItem item) {
        if (mRecyclerView.getLayoutManager().equals(mLinearLayoutManager)) {
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            if (bookArrayList.size() == 1) {
                //TODO more book
            }
            item.setIcon(getResources().getDrawable(R.drawable.ic_menu_view_list_black_24dp));
            item.setTitle(R.string.menu_layout_list);
        } else {
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            item.setIcon(getResources().getDrawable(R.drawable.ic_menu_view_grid_black_24dp));
            item.setTitle(R.string.menu_layout_grid);
        }
    }

//    private void requestPhoto() {
//        try {
//            mImageRequester.getPhoto();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    class handleNewBook {
        private Context context;

        handleNewBook(Context context){
            this.context = context;
        }

        private void startNewBook(){
            Intent intent = new Intent(context, NewBook.class);
            startActivityForResult(intent, CODE_CHILD_NEW);
        }

        private void startNewBookForm(Book book){
            Intent intent = new Intent(context, NewBookForm.class);
            intent.putExtra("BOOK",book);
            startActivityForResult(intent, CODE_CHILD_FORM);
        }

        private void newBook(Book book){
            Toast.makeText(context, "Added new book", Toast.LENGTH_SHORT).show();
            bookArrayList.add(book);
            mAdapter.notifyDataSetChanged();
        }
    }

    //TODO: Handle user permissions for internet and storage
}
