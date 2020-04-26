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
public class Pathable implements IDrawer {
    
    OOAICallback callback;
    zkai zkai;
    
    public Pathable(zkai callback) {

        this.callback = callback.callback;
        this.zkai = callback;
    }
    
    @Override
    public String getName(){
        return this.getClass().getSimpleName();
    }
    
    BufferedImage img = null;
    @Override
    public BufferedImage draw(){
        if (this.img != null) return this.img;
        
        int scale = getScale() / 2;
        int mwidth = callback.getMap().getWidth() / 2;
        int w = 8 * callback.getMap().getWidth() / scale;
        int h = 8 * callback.getMap().getHeight() / scale;
        float maxHP = 5000;
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
        this.img = img;
        return img;
    }
    
    
    @Override
    public MergeType getMergeType() {
        return MergeType.MAX;
    }

    @Override
    public int getScale() {
        return zkai.HIGHRES;
    }
}
