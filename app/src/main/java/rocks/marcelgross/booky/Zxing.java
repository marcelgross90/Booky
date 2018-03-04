package rocks.marcelgross.booky;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class Zxing {
    private MultiFormatReader multiFormatReader;

    public Zxing() {
        init(new BarcodeFormat[]{
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13});
    }

    public Result decodeYuv(byte data[], int width, int height) {
        return decodeLuminanceSource(new PlanarYUVLuminanceSource(
                data,
                width,
                height,
                0,
                0,
                width,
                height,
                false));
    }

    protected void init(BarcodeFormat formats[]) {
        Collection<BarcodeFormat> decodeFormats;
        Map<DecodeHintType, Object> hints =
                new EnumMap<DecodeHintType, Object>(DecodeHintType.class);

        decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(EnumSet.copyOf(
                Arrays.asList(formats)));
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    private Result decodeLuminanceSource(LuminanceSource source) {
        if (source == null) {
            return null;
        }

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            return multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException e) {
            return null;
        } finally {
            multiFormatReader.reset();
        }
    }
}
