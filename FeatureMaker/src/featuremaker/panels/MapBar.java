/*
 * MapBar.java
 *
 * Created on 7 de Outubro de 2005, 07:04
 */

package featuremaker.panels;
import java.awt.*;
import javax.swing.*;

/**
 * The bottom bar of the map window, containing waypoint coordinates and mouse 
 * position.
 * @author  matheus
 */
public class MapBar extends JPanel{
    
    /** the mouse position label */
    private JLabel mousePosition = new JLabel("", JLabel.CENTER);
    /** first selected waypoint label */
    private JLabel selectedWaypointPosition = new JLabel("", JLabel.CENTER);
    /** zoom */
    private JLabel zoomLabel = new JLabel("", JLabel.CENTER);
    
    /** Creates a new instance of MapBar */
    public MapBar() {
        //2 columns
        this.setLayout(new GridLayout(1, 3));
        this.add(mousePosition);
        this.add(selectedWaypointPosition);
        this.add(zoomLabel);
        this.setMinimumSize(new Dimension(0, 20));
    }
    
    /** sets the mouse position in the bottom bar. The position must be given in
     * true coordinates, ie, using the 1 quadrant, where lower left is 0,0;
     */
    private void setMousePosition(Point p){
        String text;
        text = (p == null) ? "" : "Mouse: < " + p.x + "," + p.y + " >";
        mousePosition.setText(text);
    }

    /** sets the mouse position in the bottom bar. The position must be given in
     * true coordinates, ie, using the 1 quadrant, where lower left is 0,0;
     */    
    private void setSelectedWaypointPosition(Point p){
        String text;
        text = (p == null) ? "" : "Waypoint: < " + p.x + "," + p.y + " >";
        selectedWaypointPosition.setText(text);
    }
    
    /** sets the zoom factor at bar*/
    private void setZoomFactor(float zoomFactor){
        String s = Float.toString(zoomFactor);
        zoomLabel.setText("Zoom: <" + s + ">");
    }
    
    public void setMouseWaypointZoom(Point mouseP, Point selectedP, float zoomFactor){
        setMousePosition(mouseP);
        setSelectedWaypointPosition(selectedP);
        setZoomFactor(zoomFactor);
        revalidate();
    }    
}
