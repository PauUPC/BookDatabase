package marcer.pau.bookdatabase.extras;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import marcer.pau.bookdatabase.R;

public class About extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        createToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.menuadd_toolbar);
        toolbar.setTitle(R.string.about);
        toolbar.setNavigationIcon(R.drawable.ic_menuadd_clear_24dp);
        setSupportActionBar(toolbar);
    }

}
