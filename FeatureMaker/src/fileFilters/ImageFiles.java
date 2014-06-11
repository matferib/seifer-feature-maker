/*
 * ImageFiles.java
 *
 * Created on 3 de Outubro de 2005, 09:20
 */

package fileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author  matheus
 */
public class ImageFiles extends FileFilter {
    
    /** Creates a new instance of ImageFiles */
    public ImageFiles() {
    }
    
    public boolean accept(File f){
        String s = f.getName().toLowerCase();
        return ((s != null) && (f.isDirectory() || s.endsWith(".bmp") || s.endsWith(".gif")));
    }
    
    public String getDescription(){
        return "Image files";
    }
}
