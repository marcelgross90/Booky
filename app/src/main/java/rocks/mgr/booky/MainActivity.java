package rocks.mgr.booky;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import rocks.mgr.booky.fragment.BookListFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 1)
            super.onBackPressed();
        else
            finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        replaceFragment(fm, new BookListFragment());
    }

    private void replaceFragment(FragmentManager fm, Fragment fragment) {
        fm.beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment, fragment.getClass().getName())
                .addToBackStack(null)
                .commit();
    }

}
