package de.booky.booky;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import de.booky.booky.adapter.BookListAdapter;
import de.booky.booky.database.BookDbHelper;
import de.booky.booky.entities.Book;
import de.booky.booky.fragment.AddBookFragment;
import de.booky.booky.listener.BookDeleteListener;

public class MainActivity extends AppCompatActivity implements BookListAdapter.DisplayMessage, BookDeleteListener {

    public enum FilterMode {
        TITLE, AUTHOR
    }

    private BookListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noBooksTv;
    private RadioButton sortByTitleBtn;
    private RadioButton sortByAuthorBtn;

    private View chooseSortByMode;

    public static final String MyPREFERENCES = "Filter_Type";
    public static final String FILTER_MODE = "filter_mode";
    SharedPreferences sharedpreferences;

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateDataSet(getFilterMode());

        if (adapter.getItemCount() == 0) {
            showMessageIfEmptyList();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noBooksTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        adapter = new BookListAdapter(this, this, this, getFilterMode());
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        noBooksTv = findViewById(R.id.noBooks);
        chooseSortByMode = findViewById(R.id.sortByChooser);

        filter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager != null) {
                    new AddBookFragment().show(fragmentManager, "scan");
                }
            }
        });
    }

    private void filter() {
        sortByTitleBtn = findViewById(R.id.titleRadio);
        sortByAuthorBtn = findViewById(R.id.authorRadio);
        setRadioButtons();

        sortByAuthorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAuthorBtn.setChecked(true);
                internalSortClick(FilterMode.AUTHOR, R.string.authors);
            }
        });

        sortByTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByTitleBtn.setChecked(true);
                internalSortClick(FilterMode.TITLE, R.string.title);
            }
        });
    }

    private void internalSortClick(FilterMode filterMode, int message) {
        sharedpreferences.edit().putInt(FILTER_MODE, message).apply();
        adapter.updateDataSet(filterMode);
        chooseSortByMode.setVisibility(View.GONE);
    }


    private void setRadioButtons() {
        switch (getFilterMode()) {
            case AUTHOR:
                sortByAuthorBtn.setChecked(true);
                break;
            case TITLE:
                sortByTitleBtn.setChecked(true);
                break;
            default:
                sortByTitleBtn.setChecked(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("mgr", "create  ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                chooseSortByMode.setVisibility(View.VISIBLE);


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessageIfEmptyList() {
        recyclerView.setVisibility(View.GONE);
        noBooksTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBookDeleteListener(final Book book) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder
                .setTitle(getString(R.string.deleteBook, book.getTitle()))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do something with parameter.
                        delete(book);
                    }
                })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void delete(Book book) {
        BookDbHelper.getInstance(MainActivity.this).deleteBook(book);
        adapter.deleteBook(book);
    }

    private FilterMode getFilterMode() {
        int filterMode = sharedpreferences.getInt(FILTER_MODE, R.string.title);
        switch (filterMode) {
            case R.string.title:
                return FilterMode.TITLE;
            case R.string.authors:
                return FilterMode.AUTHOR;
            default:
                return FilterMode.TITLE;
        }
    }
}
