package rocks.mgr.booky.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import rocks.mgr.booky.AddBookActivity;
import rocks.mgr.booky.IsbnValidator;
import rocks.mgr.booky.R;


public class AddBookFragment extends DialogFragment implements View.OnClickListener {

    private TextInputLayout isbnWrapper;
    private EditText isbnEd;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book, container, false);

        getDialog().setTitle(R.string.addNewBook);

        ImageButton scan = view.findViewById(R.id.scanBtn);
        scan.setOnClickListener(this);

        isbnWrapper = view.findViewById(R.id.isbnWrapper);
        isbnEd = isbnWrapper.getEditText();

        Button ok = view.findViewById(R.id.okBtn);
        ok.setOnClickListener(this);

        Button manuallyAdd = view.findViewById(R.id.manuallyBtn);
        manuallyAdd.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanBtn:
                IntentIntegrator.forSupportFragment(AddBookFragment.this)
                        .setOrientationLocked(false)
                        .setPrompt(getString(R.string.scanISBN))
                        .initiateScan();
                break;
            case R.id.okBtn:
                String isbn = isbnEd.getText().toString().trim();
                if (isbn.isEmpty() || !IsbnValidator.isValid(isbn)) {
                    isbnWrapper.setError(getString(R.string.invalidISBN));
                } else {
                    Intent intent = new Intent(getActivity(), AddBookActivity.class);
                    intent.putExtra(getString(R.string.isbn), isbn);
                    startActivity(intent);
                    getDialog().dismiss();
                }
                break;
            case R.id.manuallyBtn:
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
                getDialog().dismiss();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("mgr", "cancelled");
            } else {
                Log.d("mgr", "valid isbn " + IsbnValidator.isValid(result.getContents()));
                if (IsbnValidator.isValid(result.getContents())) {

                    isbnEd.setText(result.getContents());
                } else {
                    isbnWrapper.setError(getString(R.string.invalidISBN));
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
