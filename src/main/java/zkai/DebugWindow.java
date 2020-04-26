/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author user
 */
public class DebugWindow {

    protected JFrame frm;
    protected DebugPanel pnl;
    final boolean enabled = false;

    public DebugWindow(String title) {
        if (enabled) {
            frm = new JFrame(title);
            pnl = new DebugPanel();
            frm.add(pnl);
            frm.setVisible(true);
            showOnScreen(1, frm);
        }
    }
    
    public void dispose(){
        if (enabled){
            frm.dispose();
        }
    }

    public static void showOnScreen(int screen, JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if (screen > -1 && screen < gd.length) {
            frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
        } else if (gd.length > 0) {
            frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
        } else {
            throw new RuntimeException("No Screens Found");
        }
    }

    public void updateImage(BufferedImage img) {
        if (enabled) {
            frm.getContentPane().setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
            frm.pack();
            pnl.img = img;
            pnl.updateUI();
        }
    }
}

class DebugPanel extends JPanel {

    public BufferedImage img;

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
}
