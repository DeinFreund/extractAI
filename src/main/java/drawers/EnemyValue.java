/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawers;

import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import com.springrts.ai.oo.clb.UnitDef;
import com.springrts.ai.oo.clb.WeaponMount;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import zkai.AdditiveComposite;
import zkai.IDrawer;
import zkai.MergeType;
import zkai.zkai;

/**
 *
 * @author user
 */
public class EnemyValue implements IDrawer {
    
    OOAICallback callback;
    zkai zkai;
    
    public EnemyValue(zkai callback) {

        this.callback = callback.callback;
        this.zkai = callback;
    }
    
    @Override
    public String getName(){
        return this.getClass().getSimpleName();
    }
    
    @Override
    public BufferedImage draw(){
        int scale = getScale() / 2;
        int w = 8 * callback.getMap().getWidth() / scale;
        int h = 8 * callback.getMap().getHeight() / scale;
        float maxValue = 1000;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setComposite(new AdditiveComposite());
        for (Unit u : callback.getEnemyUnits()){
            float value = Math.min(1.0f, u.getDef().getCost(callback.getResources().get(0)) / maxValue);
            int range = zkai.HIGHRES / 2 / scale;
            int x = Math.round(u.getPos().x / scale);
            int y = Math.round(u.getPos().z / scale);
            g.setColor(new Color(value, value, value));
            g.fillOval(x - range, y - range, 2 * range, 2 * range);
        }
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
