package rocks.marcelgross.booky.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rocks.marcelgross.booky.R;
import rocks.marcelgross.booky.entities.Book;
import rocks.marcelgross.booky.listener.BookClickListener;
import rocks.marcelgross.booky.viewholder.BookViewHolder;


public class BookListAdapter extends RecyclerView.Adapter<BookViewHolder> implements Filterable {

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    private final DisplayMessage displayMessage;
    private final BookClickListener listener;
    private final Set<Book> dataSet = new HashSet<>();
    private List<Book> filteredDataSet;

    public BookListAdapter(List<Book> books, BookClickListener listener, DisplayMessage displayMessage) {
        this.listener = listener;
        this.displayMessage = displayMessage;
        this.dataSet.clear();
        this.dataSet.addAll(books);
        this.filteredDataSet = new LinkedList<>(dataSet);
    }


    @Override
    public Filter getFilter() {
        return new FilterBooks(this, dataSet);
    }

    public void updateDataSet(List<Book> books) {
        this.dataSet.clear();
        addNewBooks(books);
    }

    public void addNewBooks(List<Book> books) {
        this.dataSet.addAll(books);
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

    public static class FilterBooks extends Filter {
        private final BookListAdapter adapter;
        private Set<Book> bookList = new HashSet<>();
        final List<Book> filteredBookList;

        FilterBooks(BookListAdapter adapter, Set<Book> bookList) {
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
