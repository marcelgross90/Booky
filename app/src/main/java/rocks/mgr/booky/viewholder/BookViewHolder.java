package rocks.mgr.booky.viewholder;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import rocks.mgr.booky.R;
import rocks.mgr.booky.entities.Book;
import rocks.mgr.booky.listener.BookClickListener;

public class BookViewHolder extends RecyclerView.ViewHolder {

    private final ImageView cover;
    private final TextView title;
    private final TextView subtitle;
    private final TextView author;
    private final TextView pages;
    private final Context context;
    private final View view;
    private final BookClickListener listener;

    public BookViewHolder(View itemView, BookClickListener listener) {
        super(itemView);
        this.context = itemView.getContext();
        this.cover = itemView.findViewById(R.id.cover);
        this.title = itemView.findViewById(R.id.title);
        this.subtitle = itemView.findViewById(R.id.subtitle);
        this.author = itemView.findViewById(R.id.author);
        this.pages = itemView.findViewById(R.id.pageCount);
        this.view = itemView;
        this.listener = listener;
    }

    public void assignData(final Book book) {
        if (listener != null) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onBookLongClickListener(view, book);
                    return false;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBookClickListener(view, book);
                }
            });

        }
        this.title.setText(book.getTitle());
        if (book.getSubtitle() == null || book.getSubtitle().isEmpty()) {
            this.subtitle.setVisibility(View.GONE);
        } else {
            this.subtitle.setVisibility(View.VISIBLE);
            this.subtitle.setText(book.getSubtitle());
        }
        if (book.getConcatAuthors() == null || book.getConcatAuthors().isEmpty()) {
            this.author.setVisibility(View.GONE);
        } else {
            this.author.setVisibility(View.VISIBLE);
            this.author.setText(book.getConcatAuthors());
        }

        if (book.getPageCount() < 0 ) {
            this.pages.setVisibility(View.GONE);
        } else {
            this.pages.setVisibility(View.VISIBLE);
            this.pages.setText(context.getString(R.string.pages, book.getPageCount()));
        }

        setPoster(book.getThumbnail());
    }

    private void setPoster(String coverUri) {
        Picasso.with(context).load(coverUri).resize(222, 334).error(R.mipmap.ic_launcher).into(cover);
    }
}
