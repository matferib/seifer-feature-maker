/*
 * Drawable.java
 *
 * Created on 3 de Outubro de 2005, 16:19
 */

package featuremaker.interfaces;

import java.awt.Graphics2D;

/**
 * Every object which is drawable must implement this interface.
 *
 * @author  matheus
 */

public interface Drawable {
    /** draws the object in the current graphics context */
    public void draw(Graphics2D g, float zoom);
    
    /** draws the object with a label associated */
    public void draw(Graphics2D g, float zoom, String label);    
}
