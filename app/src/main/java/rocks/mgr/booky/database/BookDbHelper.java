package rocks.mgr.booky.database;

import android.content.Context;

import java.util.List;

import rocks.mgr.booky.FilterMode;
import rocks.mgr.booky.entities.Book;

public class BookDbHelper {

    private static BookDbHelper instance;

    private final BookDao bookDao;

    private BookDbHelper(Context context) {
        this.bookDao = BookDao.getInstance(context);
    }

    public static BookDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new BookDbHelper(context);
        }

        return instance;
    }

    /*public Book readBook(int isbn) {
        String selection = BaseDao.BookEntry.COLUMN_BOOK_ISBN + " = ?";
        String[] selectionArgs = {Long.toString(isbn)};

        List<Book> books = bookDao.read(selection, selectionArgs, null);

        return books.size() == 0 ? null : books.get(0);
    }*/

    public List<Book> readAllBooks(FilterMode filterMode) {
        String orderBy;
        switch (filterMode) {
            case TITLE:
                orderBy = BaseDao.BookEntry.COLUMN_BOOK_TITLE;
                break;
            case AUTHOR:
                orderBy = BaseDao.BookEntry.COLUMN_BOOK_AUTHORS;
                break;
            default:
                orderBy = BaseDao.BookEntry.COLUMN_BOOK_TITLE;
                break;
        }
        return bookDao.read(null, null, orderBy);
    }

    public void createBook(Book book) {
        bookDao.create(book);
    }

    public void deleteBook(Book book) {
        bookDao.delete(book.getISNB());
    }
}
