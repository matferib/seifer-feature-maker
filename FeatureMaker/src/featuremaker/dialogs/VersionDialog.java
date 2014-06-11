/*
 * VersionDialog.java
 *
 * Created on 7 de Outubro de 2005, 11:34
 */

package featuremaker.dialogs;
import java.awt.Component;
import javax.swing.*;

/**
 * A static class for showing version dialog
 * @author  matheus
 */
public class VersionDialog {
    
    static private final int 
        major = 1,
        minor = 5,
        release = 2;
    
    static private String versionString = "FeatureMaker " + 
        major + "." +
        minor + "-" +
        release;
    
    static public void showVersionDialog(Component parent){
        JOptionPane.showMessageDialog(
            parent, 
            versionString, 
            "Version", 
            JOptionPane.INFORMATION_MESSAGE);
    };
}
