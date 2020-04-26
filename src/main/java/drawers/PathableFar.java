/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawers;

import com.springrts.ai.oo.clb.OOAICallback;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.List;
import zkai.AdditiveComposite;
import zkai.IDrawer;
import zkai.MergeType;
import zkai.zkai;

/**
 *
 * @author user
 */
public class PathableFar implements IDrawer {
    
    OOAICallback callback;
    zkai zkai;
    
    public PathableFar(zkai callback) {

        this.callback = callback.callback;
        this.zkai = callback;
    }
    
    @Override
    public String getName(){
        return this.getClass().getSimpleName();
    }
    
    
    private BufferedImage scale(BufferedImage before, int scalefac) {

        int w = before.getWidth() / scalefac;
        int h = before.getHeight() / scalefac;
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
    
    BufferedImage img = null;
    @Override
    public BufferedImage draw(){
        if (this.img != null) return this.img;
        
        int scale = zkai.HIGHRES / 2;
        int mwidth = callback.getMap().getWidth() / 2;
        int w = 8 * callback.getMap().getWidth() / scale;
        int h = 8 * callback.getMap().getHeight() / scale;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setComposite(new AdditiveComposite());
        List<Float> slopemap = callback.getMap().getSlopeMap();
        WritableRaster raster = img.getRaster();
        float maxSlope = callback.getUnitDefByName("cloakraid").getMoveData().getMaxSlope();
        for (int y= 0; y < h; y++){ 
            for (int x = 0; x < w; x++){
                int yy = y * scale / 16;
                int xx = x * scale / 16;
                float value = 1f - Math.min(1f, slopemap.get(yy * mwidth + xx) / maxSlope);
                raster.setSample(x, y, 0, Math.round(255 * value));
            }
        }
        this.img = scale(img, zkai.LOWRES / zkai.HIGHRES);
        return this.img;
    }
    
    
    @Override
    public MergeType getMergeType() {
        return MergeType.MAX;
    }

    @Override
    public int getScale() {
        return zkai.LOWRES;
    }
}
