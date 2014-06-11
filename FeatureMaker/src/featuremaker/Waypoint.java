/*
 * Waypoint.java
 *
 * Created on 3 de Outubro de 2005, 08:43
 */

package featuremaker;

import featuremaker.exceptions.ParseErrorException;
import featuremaker.interfaces.*;
import featuremaker.misc.FeatureTokenizer;
import featuremaker.misc.RAFile;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Represents a waypoint, a pair of X,Y coordinates. Coordinates are given as
 * a pair of integers. Falcon uses inverted coordinates for this type of features
 * 0,0 being lower left. So, at load and save time we must convert.
 *
 * @author  matheus
 */
public class Waypoint extends Point 
    implements 
        Selectable, Clickable, Movable, Drawable, Saveable, Loadable<Waypoint>//, Insertable  
{

    /** a list with all selected waypoints */
    static private LinkedList<Waypoint> swList = new LinkedList<Waypoint>();
    
    /** every waypoint belongs to a feature */
    private Feature parent;
    
    /** is this feature selected? */
    private boolean selected = false;
    
    /** show waypoint label when selected */
    static private boolean showLabel = true;
    
    /** the radius of the waypoint */
    static private float radius;

    /** the tolerance radius and its square */
    static private int tol = 4, tol2 = 16;
    
    /** Creates a new instance of Waypoint */
    public Waypoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** sets the Feature of this waypoint */
    public void setParent(Feature f){
        this.parent = f;
    }
    
    /** gets the feature of this waypoint */
    public Feature getParent(){
        return parent;
    }
    
    /** gets the waypoint real position, interface using first quadrant
     * @return the real position
     */
    public Point getRealPosition(Dimension screenSize){
        return new Point(
            this.x,
            screenSize.height - this.y - 1
        );
    }
    
    //------- insertable iface -----------//
    /** inserts a waypoint after the selected waypoints
     * @param limits the screen limits, for newly created stuff
     */
    static public void insertAfterSelected(Dimension limits){
        int size = swList.size();
        if (size == 0){
            return;
        }
        
        //feature
        Feature f;
        //reference waypoint
        Waypoint w;

        //all selected waypoints
        for (int i=0;i<size;i++){
            w = swList.getFirst();
            f = w.getParent();
            //create a waypoint based on w
            f.addWaypointAfter(w, limits);
            Waypoint nw = f.getNext(w);
            w.setSelected(false);
            nw.setSelected(true);
        }
    }
    
    // ----------- end insertable -------------------//

    //------ deletable interface ---------------//    
    /** deletes all selected waypoints. The next waypoint of the deleted ones
     * will be set as selected.
     */
    static public void deleteSelected(){
        int size = swList.size();
        if (size == 0){
            //no selected waypoints
            return;
        }

        //delete waypoints
        for (int i=0;i<size;i++){
            //waypoint to be deleted
            Waypoint w = swList.getFirst();
            //get parent and next wpt for selecting
            Feature f = w.getParent();
            Waypoint newSelected = f.getNext(w);
            //if cant get next one, try previous
            newSelected = (newSelected == null) ? f.getPrevious(w) : newSelected;
            //delete the selected waypoint
            w.autoDelete();
            //new selected waypoint, if its not null
            if (newSelected != null){
                //this is the new selected waypoint
                newSelected.setSelected(true);
            }
        }
    }    
    
    public void autoDelete(){
        //set as unselected
        this.setSelected(false);
        
        //parent feature
        Feature f = this.parent;
        //ask parent remove us from parent list
        f.deleteWaypoint(this);
    }    
    // ------- end deletable -----------//
    
    //---- drawable interface ----/
    public void draw(Graphics2D g, float zoom){
        radius = (selected) ? ((tol * 2) / (zoom)) : (tol / (zoom));
        
        Shape s = new Ellipse2D.Float(x-radius, y-radius, radius*2, radius*2);
        g.fill(s);                
    }
    
    /** we draw the label only if user settings allow it */
    public void draw(Graphics2D g, float zoom, String label){
        draw(g, zoom);
        if (!showLabel){
            return;
        }
        
        //draw label
        Font font = g.getFont();
        Rectangle2D r = font.getStringBounds(label, g.getFontRenderContext());
        int xbias = (int)(r.getWidth() / 2);
        g.drawString(label, x - xbias, y + radius + (int)(r.getHeight()) + 3);
    }
    // ----------- end drawable ------------- //
    
    //-----selectable interface ----/
    public boolean isSelected(){
        return selected;
    }
    
    public void setSelected(boolean s){
        selected = s;
        if (selected){
            if (swList.indexOf(this) == -1){
                swList.add(this);
                //notify parent we are selected
                this.parent.setSelected(true);
            }
        }
        else {
            if (swList.indexOf(this) != -1){
                swList.remove(this);
                //notify weve been unselected
                this.parent.setSelected(false);
            }
        }
    }
    // ---------- end selectable interface ---------//

    /** not from selectable, but related... unselect everything */
    static public void unselectAll(){
        int size = swList.size();
        for (int i=0;i<size;i++){
            swList.getFirst().setSelected(false);
        }
    }
    // --------- end selectable ------------//
    
    // ------- clickable interface ----------//
    public boolean clicked(Point p){
        //distance power 2
        float d2 = (p.x - x)*(p.x - x) + (p.y - y)*(p.y - y);
        
        // distance must be less equal than maximum tolerance 
        if (d2 <= (tol2+2)){
            unselectAll();
            setSelected(true);
            return true;
        }
        else{
            return false;
        }
    }
    // ------ end clickable ------------//
    
    //------------ movable interface ---------------//
    /** translate all selected waypoints 
     * @param xoff x offset
     * @param yoff y offset
     * @param limits screen limits
     */
    static public void translateSelected(int xoff, int yoff, Dimension limits){
        ListIterator<Waypoint> li = swList.listIterator();
        while (li.hasNext()){
            li.next().translate(xoff, yoff, limits);
        }
    }
    
    public void translate(int xoff, int yoff, Dimension limits){
        super.translate(xoff, yoff);
        if (x >= limits.width){
            x = limits.width - 1;
        }
        else if (x < 0){
            x = 0;
        }
        
        if (y >= limits.height){
            y = limits.height - 1;
        }
        else if (y < 0){
            y = 0;
        }
    }
    //-------------end movable interface------------//
    
    //------------ saveable stuff ------------------//
    public String toString(Dimension limits){
        //we save inverted Y
        return Integer.toString(x) + "," + Integer.toString(limits.height - y - 1);        
    }
    
    public void saveTo(RAFile output, Dimension limits) throws IOException {
        output.writeBytes(this.toString(limits));
    }
    //---------- end savable --------------------------------//
    
    //----------- loadable stuff -------------------------//
    static public Waypoint loadFromTokenizer(FeatureTokenizer ft, Dimension limits) throws ParseErrorException {
        Waypoint w;
        try {
            int xoff = ft.readInt();
            ft.readComma();
            int yoff = ft.readInt();
            w = new Waypoint(0,0);        
            w.translate(xoff, limits.height - yoff - 1);
        }
        catch (Exception e){
            throw new ParseErrorException(e.getMessage());
        }
        
        return w;
    }
    
    static public Waypoint loadFromString(String s, Dimension limits) throws ParseErrorException {
        FeatureTokenizer ft = new FeatureTokenizer(new StringReader(s));
        ft.normalState();
        return loadFromTokenizer(ft, limits);
    }
    //----------- end loadable -------------------------//
    
    /** tells if there is a waypoint selected
     */
    static public boolean existsSelected(){
        return (swList.size() != 0);
    }

    /** toggle labels on selected waypoints */
    static public void toggleLabel(){
        showLabel = !showLabel;
    }

    /** gets the position of the first selected waypoint, in real coordinates
     * @return the position, null if none
     */
    static public Point getFirstSelectedWaypointRealPosition(Dimension screenSize){
        if (swList.size() == 0){
            return null;
        }
        
        return swList.get(0).getRealPosition(screenSize);
    }
    
    // for test purposes
    static public Waypoint createRandomWaypoint(Dimension screenSize){
        int x = (int)(Math.random() * screenSize.width);
        int y = (int)(Math.random() * screenSize.height);
        return new Waypoint(x, y);
    }
    
    
}
