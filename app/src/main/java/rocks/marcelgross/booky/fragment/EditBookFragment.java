package rocks.marcelgross.booky.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import rocks.marcelgross.booky.R;
import rocks.marcelgross.booky.database.BookDbHelper;
import rocks.marcelgross.booky.entities.Book;

public class EditBookFragment extends Fragment implements View.OnClickListener {
    private TextInputLayout titleWrapper;
    private EditText titleEd;
    private EditText subtitleEd;
    private EditText authorsEd;
    private EditText publishedDateEd;
    private EditText pageCountEd;
    private EditText isbnEd;
    private EditText coverEd;

    private BookDbHelper dbHelper;
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_book, container, false);

        activity = getActivity();
        if (activity == null) {
            return null;
        }

        dbHelper = BookDbHelper.getInstance(activity);

        initViews(view);

        activity.setTitle(R.string.addNewBook);

        return view;
    }

    private void initViews(View view) {
        FloatingActionButton saveBtn = view.findViewById(R.id.save);
        saveBtn.setOnClickListener(this);

        titleWrapper = view.findViewById(R.id.titleWrapper);
        TextInputLayout subtitleWrapper = view.findViewById(R.id.subtitleWrapper);
        TextInputLayout authorsWrapper = view.findViewById(R.id.authorsWrapper);
        TextInputLayout publishedDateWrapper = view.findViewById(R.id.publishedDateWrapper);
        TextInputLayout pageCountWrapper = view.findViewById(R.id.pageCountWrapper);
        TextInputLayout isbnWrapper = view.findViewById(R.id.isbnWrapper);
        TextInputLayout coverWrapper = view.findViewById(R.id.coverWrapper);

        titleEd = titleWrapper.getEditText();
        subtitleEd = subtitleWrapper.getEditText();
        authorsEd = authorsWrapper.getEditText();
        publishedDateEd = publishedDateWrapper.getEditText();
        pageCountEd = pageCountWrapper.getEditText();
        isbnEd = isbnWrapper.getEditText();
        coverEd = coverWrapper.getEditText();
    }

    private void saveBook() {
        if (titleEd.getText().toString().isEmpty()) {
            titleWrapper.setError(getString(R.string.titleIsMissing));
        } else {
            Book book = new Book();
            book.setTitle(titleEd.getText().toString());
            book.setIsbn(isbnEd.getText().toString());
            book.setSubtitle(subtitleEd.getText().toString());
            book.extractAuthors(authorsEd.getText().toString());
            book.setPublishedDate(publishedDateEd.getText().toString());
            book.setPageCount(Integer.valueOf(pageCountEd.getText().toString()));
            book.setThumbnail(coverEd.getText().toString());

            dbHelper.createBook(book);
            activity.onBackPressed();
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
