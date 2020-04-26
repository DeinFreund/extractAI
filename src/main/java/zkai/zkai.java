/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import com.springrts.ai.oo.clb.OOAICallback;
import com.springrts.ai.oo.clb.Unit;
import drawers.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author User
 */
public class zkai extends com.springrts.ai.oo.AbstractOOAI {

    int team = -1;
    public OOAICallback callback;
    List<DrawerHandler> drawers = new ArrayList();
    HashMap<Integer, UnitHandler> handlers = new HashMap();
    public final int HIGHRES = 64;
    public final int LOWRES = 1024;

    @Override
    public int init(int teamId, OOAICallback callback) {

        this.callback = callback;
        callback.getCheats().setEnabled(true);
        callback.getCheats().setEventsEnabled(true);
        this.team = teamId;
        drawers.add(new DrawerHandler(new FriendlyDPS(this), this));
        drawers.add(new DrawerHandler(new EnemyDPS(this), this));
        drawers.add(new DrawerHandler(new FriendlyValue(this), this));
        drawers.add(new DrawerHandler(new EnemyValue(this), this));
        drawers.add(new DrawerHandler(new FriendlyValueFar(this), this));
        drawers.add(new DrawerHandler(new EnemyValueFar(this), this));
        drawers.add(new DrawerHandler(new EnemyThreat(this), this));
        drawers.add(new DrawerHandler(new FriendlyThreat(this), this));
        drawers.add(new DrawerHandler(new FriendlyHP(this), this));
        drawers.add(new DrawerHandler(new EnemyHP(this), this));
        drawers.add(new DrawerHandler(new Pathable(this), this));
        drawers.add(new DrawerHandler(new PathableFar(this), this));
        callback.getGame().sendTextMessage("/say yolo, sending stuff to " + team, team);
        return 0;
    }

    @Override
    public int unitGiven(Unit unit, int oldTeamId, int newTeamId) {
        unitFinished(unit);
        return 0;
    }

    @Override
    public int unitFinished(Unit u) {
        try {
        if ("cloakraid".equals(u.getDef().getName())) {
            handlers.put(u.getUnitId(), new UnitHandler(this, u));
        }
        } catch (Throwable e) {
            debug("Exception in unitFinished: ", e);
        }
        return 0;
    }

    @Override
    public int unitDestroyed(Unit unit, Unit attacker) {
        if (handlers.containsKey(unit.getUnitId())){
            handlers.get(unit.getUnitId()).wins.values().forEach(y  -> y.dispose());
            handlers.remove(unit.getUnitId());
        }
        return 0;
    }

    @Override
    public int update(int frame) {
        try {
            drawers.forEach(x -> x.draw());
            handlers.values().forEach(x -> x.update(frame));
        } catch (Throwable e) {
            debug("Exception in update: ", e);
        }
        return 0;
    }

    public void debug(String s, Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        debug(s + sw.toString());
    }

    public synchronized void debug(String s) {
        callback.getGame().sendTextMessage("/say " + s, team);
    }

    void saveFrame(Map<String, BufferedImage> imgs, float dx, float dy) {
        String time = System.nanoTime() + "";
        for (String name : imgs.keySet()) {
            try {
                ImageIO.write(imgs.get(name), "PNG", new File("training/" + time + "_" + name + ".png"));
            } catch (Exception ex) {
                debug("Error saving image", ex);
            }
        }
        try {
            FileWriter writer = new FileWriter("training/" + time + "_result.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write("" + dx);
            bufferedWriter.newLine();
            bufferedWriter.write("" + dy);

            bufferedWriter.close();
        } catch (Exception ex) {
            debug("Error saving image", ex);
        }

    }

    @Override
    public int release(int i) {
        drawers.forEach(x -> x.win.dispose());
        handlers.values().forEach(x -> x.wins.values().forEach(y  -> y.dispose()));
        drawers.clear();
        handlers.clear();
        return 0;
    }
}
