package rocks.marcelgross.booky.fragment;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rocks.marcelgross.booky.R;
import rocks.marcelgross.booky.adapter.BookListAdapter;
import rocks.marcelgross.booky.database.BookDbHelper;
import rocks.marcelgross.booky.entities.Book;
import rocks.marcelgross.booky.listener.BookClickListener;
import rocks.marcelgross.booky.network.NetworkRequest;

public class SearchResultFragment extends Fragment implements BookListAdapter.DisplayMessage, BookClickListener {

    private BookListAdapter adapter;
    private RecyclerView recyclerView;
    private TextView noBooksTv;
    private View progressbar;
    private View searchList;
    private List<Book> selectedBooks = new ArrayList<>();

    private BookDbHelper db;
    private int totalItems;

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

        final Bundle bundle = getArguments();
        if (bundle == null) {
            //todo open edit fragment?
            return null;
        }

        searchList = view.findViewById(R.id.searchList);
        adapter = new BookListAdapter(new ArrayList<Book>(), this, this);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        /*recyclerView.addOnScrollListener(new ScrollListener(layoutManager, new ScrollListener.OnScrollListener() {
            @Override
            public void loadNextBooks() {
                if (adapter.getItemCount() < totalItems) {
                    progressbar.setVisibility(View.VISIBLE);
                    searchBooks(bundle, adapter.getItemCount());
                }
            }
        }));*/

        noBooksTv = view.findViewById(R.id.noBooks);
        progressbar = view.findViewById(R.id.progressBar);

        searchBooks(bundle, 0);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        final FragmentManager fragmentManager = getFragmentManager();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBooks.size() == 0) {
                    final Snackbar mySnackbar = Snackbar.make(searchList,
                            R.string.noSelectedBooks, Snackbar.LENGTH_LONG);
                    mySnackbar.show();
                } else {
                    for (Book selectedBook : selectedBooks) {
                        db.createBook(selectedBook);
                    }
                    activity.onBackPressed();
                }
            }
        });

        return view;
    }

    private void searchBooks(Bundle bundle, int startIndex) {
        String isbn = bundle.getString(getString(R.string.isbn));
        String title = bundle.getString(getString(R.string.title));

        if (isbn == null && title == null) {
            showMessageIfEmptyList();
        } else {
            NetworkRequest request = new NetworkRequest();

            if (isbn != null) {
                request.searchISBNAsync(isbn, startIndex, getOnResultListener());
            }

            if (title != null) {
                request.searchTitleAsync(title, startIndex, getOnResultListener());
            }
        }

    }

    private NetworkRequest.OnResultListener getOnResultListener() {
        return new NetworkRequest.OnResultListener() {
            @Override
            public void onResultListener(NetworkRequest.BookRequest.ResponseObject responseObject) {
                progressbar.setVisibility(View.GONE);
                selectedBooks.clear();

                List<Book> books = responseObject.getBooks();
                totalItems = responseObject.getTotalItems();
                if (books.size() == 0 && adapter.getItemCount() == 0) {
                    noBooksTv.setVisibility(View.VISIBLE);
                } else {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.addNewBooks(books);

                    activity.setTitle(activity.getResources().getQuantityString(R.plurals.searchResults, totalItems, totalItems));
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
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            view.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void onBookLongClickListener(View view, Book book) {
        //not needed
    }
}
