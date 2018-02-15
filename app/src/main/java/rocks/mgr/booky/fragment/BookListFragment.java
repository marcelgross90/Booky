package rocks.mgr.booky.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import rocks.mgr.booky.FilterMode;
import rocks.mgr.booky.R;
import rocks.mgr.booky.adapter.BookListAdapter;
import rocks.mgr.booky.database.BookDbHelper;
import rocks.mgr.booky.entities.Book;
import rocks.mgr.booky.listener.BookDeleteListener;

import static rocks.mgr.booky.FilterMode.AUTHOR;
import static rocks.mgr.booky.FilterMode.TITLE;


public class BookListFragment extends Fragment implements BookListAdapter.DisplayMessage, BookDeleteListener {

    private BookListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noBooksTv;
    private RadioButton sortByTitleBtn;
    private RadioButton sortByAuthorBtn;

    private View chooseSortByMode;
    private BookDbHelper db;

    public static final String MyPREFERENCES = "Filter_Type";
    public static final String FILTER_MODE = "filter_mode";
    SharedPreferences sharedpreferences;


    @Override
    public void onResume() {
        super.onResume();
        adapter.updateDataSet(db.readAllBooks(getFilterMode()));

        if (adapter.getItemCount() == 0) {
            showMessageIfEmptyList();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noBooksTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        MenuInflater menuInflater = activity.getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);

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
        super.onCreateOptionsMenu(menu, inflater);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        Context context = getContext();
        if (context == null) {
            return null;
        }

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        db = BookDbHelper.getInstance(context);
        adapter = new BookListAdapter(db.readAllBooks(getFilterMode()), this, this);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        noBooksTv = view.findViewById(R.id.noBooks);
        chooseSortByMode = view.findViewById(R.id.sortByChooser);

        filter(view);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    new AddBookFragment().show(fragmentManager, "scan");
                }
            }
        });

        return view;
    }

    private void filter(View view) {
        sortByTitleBtn = view.findViewById(R.id.titleRadio);
        sortByAuthorBtn = view.findViewById(R.id.authorRadio);
        setRadioButtons();

        sortByAuthorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAuthorBtn.setChecked(true);
                internalSortClick(AUTHOR, R.string.authors);
            }
        });

        sortByTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByTitleBtn.setChecked(true);
                internalSortClick(TITLE, R.string.title);
            }
        });
    }

    private void internalSortClick(FilterMode filterMode, int message) {
        sharedpreferences.edit().putInt(FILTER_MODE, message).apply();
        adapter.updateDataSet(db.readAllBooks(filterMode));
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
    public void showMessageIfEmptyList() {
        recyclerView.setVisibility(View.GONE);
        noBooksTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBookDeleteListener(final Book book) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
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
        db.deleteBook(book);
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
