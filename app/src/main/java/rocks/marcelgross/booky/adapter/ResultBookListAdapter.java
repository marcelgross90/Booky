package rocks.marcelgross.booky.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rocks.marcelgross.booky.R;
import rocks.marcelgross.booky.entities.Book;
import rocks.marcelgross.booky.listener.DeleteButtonClickListener;
import rocks.marcelgross.booky.viewholder.ResultBookViewHolder;

public class ResultBookListAdapter extends RecyclerView.Adapter<ResultBookViewHolder> {

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    private final DisplayMessage displayMessage;
    private final DeleteButtonClickListener listener;
    private final List<Book> dataSet = new ArrayList<>();

    public ResultBookListAdapter(DeleteButtonClickListener listener, DisplayMessage displayMessage) {
        this.listener = listener;
        this.displayMessage = displayMessage;
    }

    public void addBooks(List<Book> books) {
        for (Book book : books) {
            if (dataSet.contains(book)) {
                continue;
            }
            books.add(book);
        }

        notifyDataSetChanged();
    }

    public void addBook(Book book) {
        if (dataSet.contains(book)) {
            return;
        }
        this.dataSet.add(book);
        notifyDataSetChanged();
    }

    public void deleteBook(Book book) {
        notifyItemRemoved(dataSet.indexOf(book));

        dataSet.remove(book);

        if (dataSet.size() == 0) {
            displayMessage.showMessageIfEmptyList();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public ResultBookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_search_book, parent, false);

        return new ResultBookViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(ResultBookViewHolder holder, int position) {
        holder.assignData(dataSet.get(position));
    }
}
