package rocks.marcelgross.booky.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

abstract class BaseDao extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "booky";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_BOOK_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + BookEntry.TABLE_NAME + " (" +
                    BookEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_ISBN + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_TITLE + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_AUTHORS + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_PUBLISHED_DATE + TEXT_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_PAGE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    BookEntry.COLUMN_BOOK_THUMBNAIL + TEXT_TYPE +
            ");";

    final SQLiteDatabase readDb;
    final SQLiteDatabase writeDb;

    static abstract class BookEntry implements BaseColumns {

        static final String TABLE_NAME = "book";
        static final String COLUMN_BOOK_ISBN = "isbn";
        static final String COLUMN_BOOK_TITLE = "title";
        static final String COLUMN_BOOK_SUBTITLE = "subtitle";
        static final String COLUMN_BOOK_AUTHORS = "authors";
        static final String COLUMN_BOOK_PUBLISHED_DATE = "publishedDate";
        static final String COLUMN_BOOK_PAGE_COUNT = "pageCount";
        static final String COLUMN_BOOK_THUMBNAIL = "thumbnail";
    }

    BaseDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.readDb = getReadableDatabase();
        this.writeDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BOOK_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //do nothing
    }
}
