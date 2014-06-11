/*
 * Saveble.java
 *
 * Created on 4 de Outubro de 2005, 10:11
 */

package featuremaker.interfaces;

import featuremaker.misc.RAFile;
import java.awt.Dimension;
import java.io.*;

/**
 * An object that can be saved to an output stream
 * @author  matheus
 */
public interface Saveable {
    /** saves the object to the file.
     * @param output the output stream
     * @param limits the image size
     */
    public void saveTo(RAFile output, Dimension limits) throws IOException;
    
    /** converts the object to a string */
    public String toString(Dimension limits);
}
