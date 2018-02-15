package rocks.mgr.booky;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import rocks.mgr.booky.database.BookDbHelper;
import rocks.mgr.booky.entities.Book;
import rocks.mgr.booky.network.IsbnNetworkRequest;

public class AddBookActivity extends AppCompatActivity implements View.OnClickListener{

    private View progressBar;
    private View inputs;
    private FloatingActionButton saveBtn;

    private TextInputLayout titleWrapper;
    private EditText titleEd;
    private EditText subtitleEd;
    private EditText authorsEd;
    private EditText publishedDateEd;
    private EditText pageCountEd;
    private EditText isbnEd;
    private EditText coverEd;

    private String isbn;

    private BookDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        dbHelper = BookDbHelper.getInstance(this);

        initViews();

        setTitle(R.string.addNewBook);

        Intent intent = getIntent();
        isbn = intent.getStringExtra(getString(R.string.isbn));

        if (isbn == null) {
            progressBar.setVisibility(View.GONE);
            inputs.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            inputs.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            loadBook();
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        inputs = findViewById(R.id.inputs);
        saveBtn = findViewById(R.id.save);
        saveBtn.setOnClickListener(this);

        titleWrapper = findViewById(R.id.titleWrapper);
        TextInputLayout subtitleWrapper = findViewById(R.id.subtitleWrapper);
        TextInputLayout authorsWrapper = findViewById(R.id.authorsWrapper);
        TextInputLayout publishedDateWrapper = findViewById(R.id.publishedDateWrapper);
        TextInputLayout pageCountWrapper = findViewById(R.id.pageCountWrapper);
        TextInputLayout isbnWrapper = findViewById(R.id.isbnWrapper);
        TextInputLayout coverWrapper = findViewById(R.id.coverWrapper);

        titleEd = titleWrapper.getEditText();
        subtitleEd = subtitleWrapper.getEditText();
        authorsEd = authorsWrapper.getEditText();
        publishedDateEd = publishedDateWrapper.getEditText();
        pageCountEd = pageCountWrapper.getEditText();
        isbnEd = isbnWrapper.getEditText();
        coverEd = coverWrapper.getEditText();
    }

    private void loadBook() {
        IsbnNetworkRequest request = new IsbnNetworkRequest();
        request.requestAsync(isbn, new IsbnNetworkRequest.OnResultListener() {
            @Override
            public void onResultListener(List<Book> book) {
                inputs.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (book != null) {
                    assignData(book.get(0));
                } else {
                    Snackbar snackbar = Snackbar
                            .make(inputs, R.string.bookNotFound, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }

    private void assignData(Book book) {
        titleEd.setText(book.getTitle());
        subtitleEd.setText(book.getSubtitle());
        authorsEd.setText(book.getConcatAuthors());
        publishedDateEd.setText(book.getPublishedDate());
        pageCountEd.setText(String.valueOf(book.getPageCount()));
        isbnEd.setText(isbn);
        coverEd.setText(book.getThumbnail());
    }

    private void saveBook() {
        if (titleEd.getText().toString().isEmpty()) {
            titleWrapper.setError(getString(R.string.titleIsMissing));
        } else {
            Book book = new Book();
            book.setTitle(titleEd.getText().toString());
            book.setISNB(isbnEd.getText().toString());
            book.setSubtitle(subtitleEd.getText().toString());
            book.extractAuthors(authorsEd.getText().toString());
            book.setPublishedDate(publishedDateEd.getText().toString());
            book.setPageCount(Integer.valueOf(pageCountEd.getText().toString()));
            book.setThumbnail(coverEd.getText().toString());

            dbHelper.createBook(book);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                saveBook();
                break;
        }
    }
}
