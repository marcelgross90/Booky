package rocks.mgr.booky.fragment;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rocks.mgr.booky.R;
import rocks.mgr.booky.adapter.BookListAdapter;
import rocks.mgr.booky.database.BookDbHelper;
import rocks.mgr.booky.entities.Book;
import rocks.mgr.booky.listener.BookClickListener;
import rocks.mgr.booky.network.NetworkRequest;


public class SearchResultFragment extends Fragment implements BookListAdapter.DisplayMessage, BookClickListener {

    private BookListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noBooksTv;
    private View progressbar;
    private List<Book> selectedBooks = new ArrayList<>();

    private BookDbHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Resources res = getResources();

        activity.setTitle(res.getQuantityString(R.plurals.searchResults,
                0, 0));

        db = BookDbHelper.getInstance(activity);

        Bundle bundle = getArguments();
        if (bundle == null) {
            //todo open edit fragment?
            return null;
        }

        adapter = new BookListAdapter(new ArrayList<Book>(), this, this);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        noBooksTv = view.findViewById(R.id.noBooks);
        progressbar = view.findViewById(R.id.progressBar);

        searchBooks(bundle);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBooks.size() == 0) {
                    final Snackbar mySnackbar = Snackbar.make(recyclerView,
                            R.string.noSelectedBooks, Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    for (Book selectedBook : selectedBooks) {
                        db.createBook(selectedBook);
                        activity.onBackPressed();
                    }
                }
            }
        });

        return view;
    }


    private void searchBooks(Bundle bundle) {
        String isbn = bundle.getString(getString(R.string.isbn));
        String title = bundle.getString(getString(R.string.title));

        if (isbn == null && title == null) {
            showMessageIfEmptyList();
        } else {
            NetworkRequest request = new NetworkRequest();

            if (isbn != null) {
                request.searchISBNAsync(isbn, 0, getOnResultListener());
            }

            if (title != null) {
                request.searchTitleAsync(title, 0, getOnResultListener());
            }
        }

    }

    private NetworkRequest.OnResultListener getOnResultListener() {
        return new NetworkRequest.OnResultListener() {
            @Override
            public void onResultListener(List<Book> book) {
                progressbar.setVisibility(View.GONE);
                selectedBooks.clear();

                if (book.size() == 0) {
                    noBooksTv.setVisibility(View.VISIBLE);
                } else {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.updateDataSet(book);

                    activity.setTitle(activity.getResources().getQuantityString(R.plurals.searchResults,book.size(), book.size()));
                }
            }
        };
    }

    @Override
    public void showMessageIfEmptyList() {
        recyclerView.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        noBooksTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBookClickListener(View view, Book book) {
        if (selectedBooks.contains(book)) {
            selectedBooks.remove(book);
            view.setBackgroundColor(Color.WHITE);
        } else {
            selectedBooks.add(book);
            view.setBackgroundColor(Color.LTGRAY);
        }

        Log.d("mgr", "Selected books { " + selectedBooks.size() + " }");
    }

    @Override
    public void onBookLongClickListener(View view, Book book) {
        //not needed
    }
}
