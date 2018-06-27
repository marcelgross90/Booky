package rocks.marcelgross.booky.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import rocks.marcelgross.booky.R;
import rocks.marcelgross.booky.entities.Book;
import rocks.marcelgross.booky.listener.DeleteButtonClickListener;

public class ResultBookViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final TextView authors;
    private final ImageButton delete;
    private final DeleteButtonClickListener listener;

    public ResultBookViewHolder(View itemView, DeleteButtonClickListener listener) {
        super(itemView);
        this.title = itemView.findViewById(R.id.title);
        this.authors = itemView.findViewById(R.id.authors);
        this.delete = itemView.findViewById(R.id.delete);
        this.listener = listener;
    }

    public void assignData(final Book book) {

        this.title.setText(book.getTitle());

        if (book.getConcatAuthors() == null ||
                book.getConcatAuthors().isEmpty()) {
            this.authors.setVisibility(View.GONE);
        } else {
            this.authors.setVisibility(View.VISIBLE);
            this.authors.setText(book.getConcatAuthors());
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeleteButtonClicked(book);
            }
        });
    }
}
