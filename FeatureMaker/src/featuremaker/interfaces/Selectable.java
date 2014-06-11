/*
 * Selectable.java
 *
 * Created on 3 de Outubro de 2005, 17:39
 */

package featuremaker.interfaces;

import java.util.LinkedList;

/**
 * All objects which can be selected must implement this interface.
 * @author  matheus
 */
public interface Selectable {
    
    /** is the object selected? */
    public boolean isSelected();
    
    /** sets the object state, entering or leaving the selected objects container
     * @param selected the new state
     */
    public void setSelected(boolean selected);
}
