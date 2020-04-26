/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import com.springrts.ai.oo.AIFloat3;
import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author user
 */
public class UnitHandler {

    final boolean enableMove = true;
    
    zkai zkai;
    OOAICallback callback;
    Unit unit;
    List<DrawerHandler> drawers;
    Map<String, DebugWindow> wins = new TreeMap();
    static Client client = null;

    public UnitHandler(zkai zkai, Unit unit) throws Exception {

        if (client == null) {
            client = new Client(zkai);
        }
        this.zkai = zkai;
        this.callback = zkai.callback;
        this.unit = unit;
        this.drawers = zkai.drawers;
        for (DrawerHandler drawer : drawers) {
            wins.put(drawer.drawer.getName(), new DebugWindow(drawer.drawer.getName() + ": " + unit.getUnitId()));
        }
        zkai.debug("Got " + unit.getUnitId());
    }

    public static BufferedImage scale(BufferedImage before, int fac) {

        int w = before.getWidth() * fac;
        int h = before.getHeight() * fac;
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        AffineTransform at = new AffineTransform();
        at.scale(fac, fac);
        AffineTransformOp scaleOp
                = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return scaleOp.filter(before, after);
    }

    List<Float> lastparams = new ArrayList();
    int lastframe = -1000;
    float oldangle = 0;
    TreeMap<String, BufferedImage> imgs = new TreeMap();

    static boolean hasdbg = false;
    public String move() {
        try {

            int width = imgs.values().iterator().next().getWidth();
            int height = imgs.values().iterator().next().getHeight();
            float[][][][] inpFloat = new float[1][imgs.size()][width][height];
            List<BufferedImage> imageList = new ArrayList();
            for (String name : this.imgs.keySet()){
                imageList.add(this.imgs.get(name));
                if (!hasdbg) zkai.debug(name);
            }
            hasdbg = true;
            
            for (int img = 0; img < imageList.size(); img++) {
                WritableRaster raster = imageList.get(img).getRaster();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        inpFloat[0][img][y][x] = raster.getSample(x, y, 0) / 255.0f;
                    }
                }
            }
            String ret = client.send(inpFloat);
            return ret;
        } catch (Exception ex) {
            zkai.debug("Error running inference", ex);
        }
        return "1,0";
    }

    int lastMove = 0;
    public void update(int frame) {
        if (unit.getCurrentCommands().isEmpty()) {
            //check for move command
            return;
        }
        if (unit.getCurrentCommands().get(0).getId() != 10 && unit.getCurrentCommands().get(0).getId() != 31109) {
            //check for move command
            return;
        }
        if (unit.getCurrentCommands().get(0).getParams().equals(lastparams) && frame - lastframe < 30) {
            return;
        }
        float x = unit.getPos().x;
        float y = unit.getPos().z;
        float tx = unit.getCurrentCommands().get(0).getParams().get(0) - x;
        float ty = unit.getCurrentCommands().get(0).getParams().get(2) - y;
        float theta = (float) Math.atan2(ty, tx);
        float dx = (float) Math.cos(oldangle - theta);
        float dy = (float) Math.sin(oldangle - theta);
        if (frame - lastframe < 45) {
            zkai.saveFrame(imgs, dx, dy);
        }
        oldangle = theta;
        lastframe = frame;
        lastparams = unit.getCurrentCommands().get(0).getParams();
        imgs = new TreeMap();
        for (DrawerHandler drawer : drawers) {
            BufferedImage img = drawer.render(x, y, theta, true);
            imgs.put(drawer.drawer.getName(), img);
            wins.get(drawer.drawer.getName()).updateImage(scale(img, 32));
        }
        if (enableMove && frame - lastMove > 5) {
            lastMove = frame;
            String ret = move();
            float mx = Float.valueOf(ret.split(",")[0]);
            float my = Float.valueOf(ret.split(",")[1]);
            float targetangle = theta + (float) Math.atan2(my, mx);

            mx = (float) Math.cos(targetangle) * 300;
            my = (float) Math.sin(targetangle) * 300;
            //zkai.debug("mx: " + mx + " | my: " + my);
            AIFloat3 move = new AIFloat3(unit.getPos());
            move.x += mx;
            move.z += my;
            unit.moveTo(move, (short) 0, Integer.MAX_VALUE);
        }
    }
}
