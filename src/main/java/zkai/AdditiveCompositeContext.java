package zkai;
import java.awt.*;
import java.awt.image.*;
import java.util.Objects;

public class AdditiveCompositeContext implements CompositeContext {
    public AdditiveCompositeContext() {
    }

    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        int[] pxSrc = new int[src.getNumBands()];
        int[] pxDst = new int[dstIn.getNumBands()];
        int chans = Math.min(src.getNumBands(), dstIn.getNumBands());

        for (int x = 0; x < dstIn.getWidth(); x++) {
            for (int y = 0; y < dstIn.getHeight(); y++) {
                pxSrc = src.getPixel(x, y, pxSrc);
                pxDst = dstIn.getPixel(x, y, pxDst);



                for (int i = 0; i < chans; i++) {
                    pxDst[i] = Math.min(255, (pxSrc[i]) + (pxDst[i]));
                    dstOut.setPixel(x, y, pxDst);
                }
            }
        }
    }

    @Override public void dispose() { }
}
