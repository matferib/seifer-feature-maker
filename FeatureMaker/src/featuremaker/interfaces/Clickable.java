/*
 * Clickable.java
 *
 * Created on 3 de Outubro de 2005, 17:53
 */

package featuremaker.interfaces;

import java.awt.Point;

/**
 * An object can be clicked if it implements this interface
 * @author  matheus
 */
public interface Clickable {
    
    /** indicates the object has been clicked at point p. All actions related
     * to the clicked must be taken: interface the object must be set selected,
     * added to the selected container if possible etc
     *
     * @param p the point clicked
     * @return true if the object has been clicked
     */
    public boolean clicked(Point p);
    
}
