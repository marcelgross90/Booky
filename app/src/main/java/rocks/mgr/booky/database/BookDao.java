package rocks.mgr.booky.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import rocks.mgr.booky.entities.Book;


class BookDao extends BaseDao {

    private static BookDao instance;

    private BookDao(Context context) {
        super(context);
    }

    static BookDao getInstance(Context context) {
        if (instance == null) {
            instance = new BookDao(context);
        }

        return instance;
    }

    void create(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_ISBN, book.getISNB());
        values.put(BookEntry.COLUMN_BOOK_TITLE, book.getTitle());
        values.put(BookEntry.COLUMN_BOOK_SUBTITLE, book.getSubtitle());
        values.put(BookEntry.COLUMN_BOOK_AUTHORS, book.getConcatAuthors());
        values.put(BookEntry.COLUMN_BOOK_PUBLISHED_DATE, book.getPublishedDate());
        values.put(BookEntry.COLUMN_BOOK_PAGE_COUNT, book.getPageCount());
        values.put(BookEntry.COLUMN_BOOK_THUMBNAIL, book.getThumbnail());

        writeDb.insert(BookEntry.TABLE_NAME, null, values);
    }

    List<Book> read(String selection, String[] selectionArgs, String orderBy) {
        List<Book> books = new ArrayList<>();

        String[] projection = {
                BookEntry.COLUMN_BOOK_ISBN,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_SUBTITLE,
                BookEntry.COLUMN_BOOK_AUTHORS,
                BookEntry.COLUMN_BOOK_PUBLISHED_DATE,
                BookEntry.COLUMN_BOOK_PAGE_COUNT,
                BookEntry.COLUMN_BOOK_THUMBNAIL
        };

        Cursor c = readDb.query(
                BookEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy,
                null);

        if (c.moveToFirst()) {
            do {
                Book currentBook = new Book();
                currentBook.setISNB(c.getString(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_ISBN)));
                currentBook.setTitle(c.getString(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_TITLE)));
                currentBook.setSubtitle(c.getString(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_SUBTITLE)));
                currentBook.extractAuthors(c.getString(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_AUTHORS)));
                currentBook.setPublishedDate(c.getString(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PUBLISHED_DATE)));
                currentBook.setPageCount(c.getInt(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PAGE_COUNT)));
                currentBook.setThumbnail(c.getString(c.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_THUMBNAIL)));

                books.add(currentBook);
            } while (c.moveToNext());
        }

        c.close();
        return books;
    }

    void delete(String isbn) {
        writeDb.delete(BookEntry.TABLE_NAME, BookEntry.COLUMN_BOOK_ISBN + " = ?", new String[]{isbn});
    }
}
