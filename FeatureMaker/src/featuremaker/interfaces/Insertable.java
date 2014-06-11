/*
 * Insertable.java
 *
 * Created on 4 de Outubro de 2005, 08:33
 */

package featuremaker.interfaces;

import java.awt.Dimension;

/**
 * The opposite of deletable. Must obbey to image limits
 * @author  matheus
 */
public interface Insertable {
    
    /** inserts object */
    public void insert(Dimension limits);
}
