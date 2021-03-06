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
public class FriendlyThreat implements IDrawer {
    
    OOAICallback callback;
    zkai zkai;
    
    public FriendlyThreat(zkai callback) {

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
        float maxDps = 1f;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setComposite(new AdditiveComposite());
        for (Unit u : callback.getFriendlyUnits()){
            float threat = Math.min(1.0f, getDPS(u.getDef()) / Math.max(u.getHealth(), 50f) / maxDps);
            int range = zkai.HIGHRES / 2 / scale;
            int x = Math.round(u.getPos().x / scale);
            int y = Math.round(u.getPos().z / scale);
            g.setColor(new Color(threat, threat, threat));
            g.fillOval(x - range, y - range, 2 * range, 2 * range);
        }
        return img;
    }
    
    public float getDPS(UnitDef ud) {
        float dps = 0;
        for (WeaponMount wm : ud.getWeaponMounts()) {
            if (wm.getWeaponDef().getName().toLowerCase().contains("fake")) {
                continue;
            }
            if (wm.getWeaponDef().getName().toLowerCase().contains("noweapon")) {
                continue;
            }
            float maxf = 0;
            for (int i = 1; i < wm.getWeaponDef().getDamage().getTypes().size(); i++) {
                maxf = Math.max(wm.getWeaponDef().getDamage().getTypes().get(i), maxf);
            } //You are entering a land of magic, ask Sprung for directions
            if(wm.getWeaponDef().getCustomParams().containsKey("statsdamage")){
                maxf = Float.valueOf(wm.getWeaponDef().getCustomParams().get("statsdamage"));
            }
            dps += maxf * wm.getWeaponDef().getSalvoSize() / wm.getWeaponDef().getReload();
        }
        if (ud.getName().equals("gunshipaa")) {
            return 167;
        }
        if (ud.getName().equals("planeheavyfighter")) {
            return 96;
        }
        if (ud.getCustomParams().containsKey("dynamic_comm")) { // commander
            return 200;
        }
        return dps;
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
