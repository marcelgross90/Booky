package rocks.marcelgross.booky;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;

import de.markusfisch.android.cameraview.widget.CameraView;

import rocks.marcelgross.booky.R;

public class ScanBookActivity extends AppCompatActivity {
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

    private CameraView cameraView;
    private Vibrator vibrator;
    private boolean decoding = false;
    private Thread decodingThread;
    private byte frameData[];
    private int frameWidth;
    private int frameHeight;
    private int frameOrientation;

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

    private void initCameraView() {
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
            for (int retry = 100; retry-- > 0;) {
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
        return zxing.decodeYuv(frameData, frameWidth, frameHeight);
    }

    private void found(Result result) {
        cancelDecoding();
        vibrator.vibrate(100);
    }
}
