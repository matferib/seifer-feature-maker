/*
 * featuresFileFilter.java
 *
 * Created on 4 de Outubro de 2005, 10:48
 */

package fileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author  matheus
 */
public class featuresFileFilter extends FileFilter {
    
    /** Creates a new instance of featuresFileFilter */
    public featuresFileFilter() {
    }
    
    public boolean accept(File f){
        String s = f.getName().toLowerCase();
        return ((s != null) && ((f.isDirectory()) || (s.endsWith(".tdf"))));
    }
    
    public String getDescription(){
        return "Feature files(*.tdf)";
    }
}    
    

