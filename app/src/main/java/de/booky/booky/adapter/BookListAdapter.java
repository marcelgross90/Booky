package de.booky.booky.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.booky.booky.MainActivity;
import de.booky.booky.R;
import de.booky.booky.database.BookDbHelper;
import de.booky.booky.entities.Book;
import de.booky.booky.listener.BookDeleteListener;
import de.booky.booky.viewholder.BookViewHolder;


public class BookListAdapter extends RecyclerView.Adapter<BookViewHolder> implements Filterable {

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    private final DisplayMessage displayMessage;
    private final BookDeleteListener listener;
    private final BookDbHelper db;
    private final List<Book> dataSet = new ArrayList<>();
    private List<Book> filteredDataSet;

    public BookListAdapter(Context context, BookDeleteListener listener, DisplayMessage displayMessage, MainActivity.FilterMode filterMode) {
        this.db = BookDbHelper.getInstance(context);
        this.listener = listener;
        this.displayMessage = displayMessage;
        this.dataSet.clear();
        this.dataSet.addAll(db.readAllBooks(filterMode));
        this.filteredDataSet = new LinkedList<>(dataSet);
    }

    @Override
    public Filter getFilter() {
        return new FilterBooks(this, dataSet);
    }

    public void updateDataSet(MainActivity.FilterMode filterMode) {
        this.dataSet.clear();
        this.dataSet.addAll(db.readAllBooks(filterMode));
        this.filteredDataSet = new LinkedList<>(dataSet);
        notifyDataSetChanged();
    }

    public void deleteBook(Book book) {
        notifyItemRemoved(filteredDataSet.indexOf(book));

        dataSet.remove(book);
        filteredDataSet.remove(book);

        if (dataSet.size() == 0) {
            displayMessage.showMessageIfEmptyList();
        }
    }

    @Override
    public int getItemCount() {
        return filteredDataSet.size();
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_book, parent, false);

        return new BookViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.assignData(filteredDataSet.get(position));
    }

    public class FilterBooks extends Filter {

        private final BookListAdapter adapter;
        final List<Book> bookList;
        final List<Book> filteredBookList;

        FilterBooks(BookListAdapter adapter, List<Book> bookList) {
            this.adapter = adapter;
            this.bookList = bookList;
            this.filteredBookList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredBookList.clear();
            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredBookList.addAll(bookList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (Book book : bookList) {
                    if (book.getTitle().toLowerCase().contains(filterPattern) ||
                            book.getSubtitle().toLowerCase().contains(filterPattern) ||
                            book.getConcatAuthors().toLowerCase().contains(filterPattern)) {
                        filteredBookList.add(book);
                    }
                }
            }
            results.values = filteredBookList;
            results.count = filteredBookList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            adapter.filteredDataSet.clear();
            //noinspection unchecked
            adapter.filteredDataSet.addAll((List<Book>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
