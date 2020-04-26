/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import com.springrts.ai.oo.clb.OOAICallback;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author user
 */
public class DrawerHandler {

    IDrawer drawer;
    DebugWindow win;
    BufferedImage img, scaled;
    zkai zkai;
    OOAICallback callback;

    public DrawerHandler(IDrawer d, zkai zkai) {
        drawer = d;
        win = new DebugWindow(d.getName());
        this.zkai = zkai;
        this.callback = zkai.callback;
    }

    private BufferedImage scale(BufferedImage before, int elmoPerPixel) {

        int w = callback.getMap().getWidth() * 8 / elmoPerPixel;
        int h = callback.getMap().getHeight() * 8 / elmoPerPixel;
        int scalefac = before.getWidth() / w;
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = after.getRaster();
        WritableRaster beforeRaster = before.getRaster();
        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int x0 = x * scalefac;
                int y0 = y * scalefac;
                int val = 0;
                for (int xx = 0; xx < scalefac; xx++){
                    for(int yy = 0; yy < scalefac; yy++){
                        val += beforeRaster.getSample(x0 + xx, y0 + yy, 0);
                    }
                }
                val /= scalefac * scalefac;
                raster.setSample(x, y, 0, val);
            }
        }
        return after;
    }

    public void draw() {
        img = drawer.draw();
        scaled = scale(img, drawer.getScale());
        win.updateImage(UnitHandler.scale(scaled, 600 / scaled.getWidth()));
    }

    public BufferedImage render(float x, float y, float theta, boolean highres) {
        BufferedImage img = this.scaled;
        int scale = drawer.getScale();
        int w = 21;
        int h = 21;
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        AffineTransform at = new AffineTransform();
        at.translate(w / 2.0, h / 2.0);
        at.rotate(-theta);
        at.translate(-x / scale, -y / scale);
        AffineTransformOp transform
                = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return transform.filter(img, after);
    }
}
