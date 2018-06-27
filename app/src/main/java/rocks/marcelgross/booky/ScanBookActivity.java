package rocks.marcelgross.booky;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.List;

import de.markusfisch.android.cameraview.widget.CameraView;
import rocks.marcelgross.booky.adapter.ResultBookListAdapter;
import rocks.marcelgross.booky.entities.Book;
import rocks.marcelgross.booky.listener.DeleteButtonClickListener;
import rocks.marcelgross.booky.network.NetworkRequest;
import rocks.marcelgross.booky.rs.Preprocessor;

public class ScanBookActivity extends AppCompatActivity implements DeleteButtonClickListener, ResultBookListAdapter.DisplayMessage {
    private static final int REQUEST_CAMERA = 1;

    private final Zxing zxing = new Zxing();
    private final Runnable decodingRunnable = new Runnable() {
        @Override
        public void run() {
            while (decoding) {
                final Result result = decodeFrame();
                if (result != null) {
                    cameraView.post(new Runnable() {
                        @Override
                        public void run() {
                            found(result);
                        }
                    });
                    decoding = false;
                    break;
                }
            }
        }
    };

    private TextView infoText;
    private RecyclerView recyclerView;
    private ResultBookListAdapter adapter;
    private CameraView cameraView;
    private Vibrator vibrator;
    private boolean decoding = false;
    private Thread decodingThread;
    private Preprocessor preprocessor;
    private byte frameData[];
    private int frameWidth;
    private int frameHeight;
    private int frameOrientation;

    private String scannedISBN = "";
    private Book selectedBook = null;

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int grantResults[]) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0 &&
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_error,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scan_book);
        checkPermissions();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initToolbar();

        infoText = findViewById(R.id.infoText);
        recyclerView = findViewById(R.id.books);
        initListView();

        cameraView = findViewById(R.id.camera_view);
        initCameraView();
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.openAsync(CameraView.findCameraId(
                Camera.CameraInfo.CAMERA_FACING_BACK));
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.addNewBook);
    }

    private void checkPermissions() {
        String permission = android.Manifest.permission.CAMERA;

        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{permission},
                    REQUEST_CAMERA);
        }
    }

    private void initListView() {
        adapter = new ResultBookListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initCameraView() {
        cameraView.setUseOrientationListener(true);
        cameraView.setOnCameraListener(new CameraView.OnCameraListener() {
            @Override
            public void onConfigureParameters(Camera.Parameters parameters) {
                for (String mode : parameters.getSupportedSceneModes()) {
                    if (mode.equals(Camera.Parameters.SCENE_MODE_BARCODE)) {
                        parameters.setSceneMode(mode);
                        break;
                    }
                }
                CameraView.setAutoFocus(parameters);
            }

            @Override
            public void onCameraError() {
                Toast.makeText(ScanBookActivity.this, R.string.camera_error,
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCameraReady(Camera camera) {
                frameWidth = cameraView.getFrameWidth();
                frameHeight = cameraView.getFrameHeight();
                frameOrientation = cameraView.getFrameOrientation();
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        frameData = data;
                    }
                });
            }

            @Override
            public void onPreviewStarted(Camera camera) {
                startDecoding();
            }

            @Override
            public void onCameraStopping(Camera camera) {
                cancelDecoding();
                camera.setPreviewCallback(null);
            }
        });
    }

    private void startDecoding() {
        frameData = null;
        decoding = true;
        decodingThread = new Thread(decodingRunnable);
        decodingThread.start();
    }

    private void cancelDecoding() {
        decoding = false;
        if (decodingThread != null) {
            for (int retry = 100; retry-- > 0; ) {
                try {
                    decodingThread.join();
                    break;
                } catch (InterruptedException e) {
                    decodingThread.interrupt();
                }
            }
            decodingThread = null;
        }
    }

    private Result decodeFrame() {
        if (frameData == null) {
            return null;
        }
        if (preprocessor == null) {
            preprocessor = new Preprocessor(
                    this,
                    frameWidth,
                    frameHeight,
                    frameOrientation);
        }
        preprocessor.process(frameData);
        return zxing.decodeYuv(
                frameData,
                preprocessor.outWidth,
                preprocessor.outHeight);
    }

    private void found(Result result) {

        String isbn = result.getText();

        if (isbn.equals(scannedISBN)) {
            return;
        }

        vibrator.vibrate(100);
        searchBook(result.getText());
    }

    private void searchBook(String isbn) {
        NetworkRequest networkRequest = new NetworkRequest();

        networkRequest.searchISBNAsync(isbn, 0, getOnResultListener());
    }

    private NetworkRequest.OnResultListener getOnResultListener() {
        return new NetworkRequest.OnResultListener() {
            @Override
            public void onResultListener(NetworkRequest.BookRequest.ResponseObject responseObject) {

                List<Book> books = responseObject.getBooks();
                if (books.size() == 0 && adapter.getItemCount() == 0) {
                    Toast.makeText(ScanBookActivity.this, R.string.noBooksFound, Toast.LENGTH_SHORT).show();
                    scannedISBN = "";
                } else if (books.size() > 1) {
                    showChooserDialog(books);
                    scannedISBN = "";
                } else {
                    infoText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.addBook(books.get(0));
                }
            }
        };
    }

    private void showChooserDialog(final List<Book> books) {
        selectedBook = books.get(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a book");

        builder.setSingleChoiceItems(getTitles(books), 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedBook = books.get(i);
                    }
                });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (selectedBook != null) {
                    adapter.addBook(selectedBook);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                   dialog.cancel();
            }
        });

        builder.create().show();
    }

    @NonNull
    private CharSequence[] getTitles(List<Book> books) {
        CharSequence[] charSequences = new CharSequence[books.size()];

        for (int i = 0; i < books.size(); i++) {
            charSequences[i] = books.get(i).getTitle();
        }
        return charSequences;
    }

    @Override
    public void onDeleteButtonClicked(final Book book) {
        vibrator.vibrate(10);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setTitle(getString(R.string.deleteBook, book.getTitle()))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                adapter.deleteBook(book);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showMessageIfEmptyList() {
        infoText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
