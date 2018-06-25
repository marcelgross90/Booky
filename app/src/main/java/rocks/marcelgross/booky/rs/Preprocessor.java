package rocks.marcelgross.booky.rs;

import rocks.marcelgross.booky.renderscript.ScriptC_rotator;

import android.content.Context;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicResize;
import android.support.v8.renderscript.Type;

public class Preprocessor {
    public int outWidth = 0;
    public int outHeight = 0;

    private RenderScript rs;
    private ScriptIntrinsicResize resizeScript;
    private ScriptC_rotator rotatorScript;
    private Type yuvType;
    private Allocation yuvAlloc;
    private Type resizedType;
    private Allocation resizedAlloc;
    private Type rotatedType;
    private Allocation rotatedAlloc;
    private int frameOrientation = 0;

    public Preprocessor(
            Context context,
            int width,
            int height,
            int orientation) {
        rs = RenderScript.create(context);
        resizeScript = ScriptIntrinsicResize.create(rs);
        rotatorScript = new ScriptC_rotator(rs);
        yuvType = Type.createXY(
                rs,
                Element.U8(rs),
                width,
                // use only grayscale part
                height);
        yuvAlloc = Allocation.createTyped(
                rs,
                yuvType,
                Allocation.USAGE_SCRIPT);

        float f = .75f;
        int w = Math.round(width * f);
        int h = Math.round(height * f);

        resizedType = Type.createXY(rs, Element.U8(rs), w, h);
        resizedAlloc = Allocation.createTyped(
                rs,
                resizedType,
                Allocation.USAGE_SCRIPT);

        if (orientation == 90 || orientation == 270) {
            int tmp = w;
            w = h;
            h = tmp;
        }

        outWidth = w;
        outHeight = h;
        frameOrientation = orientation;

        if (orientation != 0) {
            rotatedType = Type.createXY(
                    rs,
                    Element.U8(rs),
                    w,
                    h);
            rotatedAlloc = Allocation.createTyped(
                    rs,
                    rotatedType,
                    Allocation.USAGE_SCRIPT);
        }
    }

    public void destroy() {
        yuvType.destroy();
        yuvType = null;
        yuvAlloc.destroy();
        yuvAlloc = null;
        resizedType.destroy();
        resizedType = null;
        resizedAlloc.destroy();
        resizedAlloc = null;
        rotatedType.destroy();
        rotatedType = null;
        rotatedAlloc.destroy();
        rotatedAlloc = null;
        resizeScript.destroy();
        rotatorScript.destroy();
        rs.destroy();
    }

    public void process(byte[] frame) {
        yuvAlloc.copyFrom(frame);

        resizeScript.setInput(yuvAlloc);
        resizeScript.forEach_bicubic(resizedAlloc);

        Type t = resizedType;
        if (t == null || frameOrientation == 0) {
            resizedAlloc.copyTo(frame);
        } else {
            rotatorScript.set_inImage(resizedAlloc);
            rotatorScript.set_inWidth(t.getX());
            rotatorScript.set_inHeight(t.getY());
            switch (frameOrientation) {
            default:
                break;
            case 90:
                rotatorScript.forEach_rotate90(rotatedAlloc, rotatedAlloc);
                break;
            case 180:
                rotatorScript.forEach_rotate180(rotatedAlloc, rotatedAlloc);
                break;
            case 270:
                rotatorScript.forEach_rotate270(rotatedAlloc, rotatedAlloc);
                break;
            }
            rotatedAlloc.copyTo(frame);
        }
    }
}
