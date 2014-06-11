/*
 * Movable.java
 *
 * Created on 3 de Outubro de 2005, 18:39
 */

package featuremaker.interfaces;

import java.awt.Dimension;

/**
 * An object implementing this can be moved
 * @author  matheus
 */
public interface Movable {
    /** Translates the object by a given offset
     * @param xoff x offset
     * @param yoff y offset
     */
    public void translate(int xoff, int yoff, Dimension limits);
}
