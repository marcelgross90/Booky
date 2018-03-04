package rocks.marcelgross.booky.listener;

import android.view.View;

import rocks.marcelgross.booky.entities.Book;

public interface BookClickListener {
    void onBookLongClickListener(View view, Book book);
    void onBookClickListener(View view, Book book);
}
