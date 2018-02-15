package rocks.mgr.booky;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import rocks.mgr.booky.fragment.BookListFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;

    public static void replaceFragment(FragmentManager fm, Fragment fragment) {
        fm.beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment, fragment.getClass().getName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 1)
            super.onBackPressed();
        else
            finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (fm.getBackStackEntryCount() > 1)
            fm.popBackStack();

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        initToolbar();


        if (savedInstanceState == null) {
            replaceFragment(fm, new BookListFragment());
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        canBack();
                    }
                }
        );
        canBack();
    }

    private void canBack() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(
                    fm.getBackStackEntryCount() > 1
            );
        }
    }
}
