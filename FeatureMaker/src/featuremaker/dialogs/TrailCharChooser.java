/*
 * TrailCharChooser.java
 *
 * Created on 8 de Outubro de 2005, 13:30
 */

package featuremaker.dialogs;
import java.awt.event.*;
import javax.swing.*;

/**
 * A dialog used to select a character, the trailing char.
 * @author  matheus
 */
public class TrailCharChooser extends JDialog implements ActionListener {
    // the char to return
    private char tChar = '0';
    
    //button OK
    private JButton okBut = new JButton("OK");
    //char field
    private JTextField charField = new JTextField(2);
    
    /** Creates a new instance of TrailCharChooser over a frame*/
    public TrailCharChooser(JFrame parent) {
        setProperties();
    }
    
    /** Creates a new instance of TrailCharChooser over a dialog*/
    public TrailCharChooser(JDialog parent) {
        setProperties();
    }

    /** sets dialog properties */
    private void setProperties(){
        this.add(charField);
        this.add(okBut);
        this.setSize(250, 150);
        this.setTitle("Enter trailing char");
        this.setModal(true);        
    }
    
    /** opens dialog, return the selcted char on ok press */
    public char getTrailChar(){
        return tChar;
    }
    
    public void actionPerformed(ActionEvent ae){
        if (ae.getSource() == okBut){
            String s = charField.getText();
            if (s.length() != 1){
                JOptionPane.showMessageDialog(this, "Invalid trail char", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                tChar = s.toCharArray()[0];
                this.setVisible(false);
            }            
        }
    }
    
}
