/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import java.awt.image.BufferedImage;
import java.util.function.Function;

/**
 *
 * @author user
 */
public interface IDrawer {
    
    public BufferedImage draw();
    
    public String getName();
    
    public MergeType getMergeType();
    
    public int getScale();
}
