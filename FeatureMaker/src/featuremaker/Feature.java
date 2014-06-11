/*
 * Feature.java
 *
 * Created on 3 de Outubro de 2005, 08:46
 */

package featuremaker;

import featuremaker.dialogs.TrailCharChooser;
import featuremaker.exceptions.ParseErrorException;
import featuremaker.interfaces.*;
import featuremaker.misc.FeatureTokenizer;
import featuremaker.misc.RAFile;
import fileFilters.featuresFileFilter;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * A feature consists of a type and a list of waypoints
 * @author  matheus
 */
public class Feature 
    implements 
        Clickable, Drawable, Movable, Deletable, Selectable, Saveable, Loadable<Feature> //, Insertable
{

    /** according to Qaz, we need a trailing char. According to twister, no.
     * This is the trailing char used 
     */
    static private char trailChar = '0';
    /** controls weather or not use the trailing char at save */
    static private boolean useTrailChar = false;
    
    
    /** a list with all features */
    static public LinkedList<Feature> fList = new LinkedList<Feature>();
    
    /** the file */
    static public File featuresFile = null;
    
    /** is the object selected ? */
    private boolean selected = false   ;
    
    /** number of features we have */
    public static final int F_N_TYPES = 2;
    
    /** every feature can be of a different type,
     * we define river and road
     */
    static public final int F_RIVER = 0, F_ROAD = 1;
    /** features in string format */
    static public String featureNames[] = {"RIVER", "ROAD"};

    /** feature colors, following each feature type plus yellow for selected feature */
    static private Color darkBlue = new Color(0, 0, 128);
    static private Color colors[][] = {{Color.blue, Color.orange}, {darkBlue, Color.red}};
    
    /** the list of waypoints */
    private LinkedList<Waypoint> wpList = new LinkedList<Waypoint>();
    /** reference counter for selected waypoints */
    private int selectedRefCount = 0;
    /** the selected features list */
    static private LinkedList<Feature> sfList = new LinkedList<Feature>();
    
    /** the feature type */
    private int type;
    
    /** Creates a new instance of Feature. The feature type is defined as F_RIVER
     * or F_ROAD
     */
    public Feature(int type) {
        this.type = type;
    }
    
    /** toggle all selected features */
    static public void toggleSelected(){
        ListIterator<Feature> li = sfList.listIterator();
        while (li.hasNext()){
            li.next().toggle();
        }
    }
    
    /** toggle the feature between modes */
    public void toggle(){
        this.type = (type + 1) % F_N_TYPES;
    }

    /** given an waypoint, gets the next one 
     * @param w the reference waypoint
     * @return the next one, null if its the last or waypoint is invalid
     */
    public Waypoint getNext(Waypoint w){
        int ind = wpList.indexOf(w);
        if ((ind == -1) || (w == null) || (ind == (wpList.size()-1))){
            return null;
        }
        return wpList.get(ind+1);
    }
    
    /** given an waypoint, gets the previous one 
     * @param w the reference waypoint
     * @return the previous one, null if its the firstt or waypoint is invalid
     */
    public Waypoint getPrevious(Waypoint w){
        int ind = wpList.indexOf(w);
        if ((ind <= 0) || (w == null)){
            return null;
        }
        return wpList.get(ind-1);
    }
    
    
    /** adds a waypoint to the waypoint list of this feature
     * @param wp the new waypoint
     */
    public void addWP(Waypoint wp){
        wpList.add(wp);
        wp.setParent(this);
    }
    
    /** does the feature contain a waypoint */
    public boolean contains(Waypoint w){
        return wpList.contains(w);
    }
    
    /** creates a waypoint based on another one. There are 2 possibilities:
     * create new waypoint after reference and next one or
     * create new waypoint after reference in some direction
     *
     * @param w the reference waypoint
     * @param limits the image limits, to create new waypoint inside
     */
    public void addWaypointAfter(Waypoint w, Dimension limits){
        //index of waypoint
        int indW = wpList.indexOf(w);
        if (indW == -1){
            //waypoint doesnt belong to us
            return;
        }
        
        //are we the last point
        int size = wpList.size();
        if (indW == size - 1){
            //create a new waypoint in the same place as w
            Waypoint nw = new Waypoint(w.x, w.y);

            //if we have more than 1 waypoint, translate w so that the previous
            //waypoint is midway between w and the other
            if (size > 1){
                Waypoint pw = getPrevious(w);
                Point off = new Point((w.x - pw.x), (w.y - pw.y));
                nw.translate(off.x, off.y, limits);
            }
            //add new waypoint
            this.addWP(nw);
        }
        else {
            Waypoint w2 = wpList.get(indW + 1);
            //mean
            Waypoint w3 = new Waypoint((w.x + w2.x) / 2, (w.y + w2.y) / 2);
            wpList.add(indW + 1, w3);
            w3.setParent(this);
        }
        
    }
    
    //------------ movable interface ---------------//
    /** move all selected features. The offset are given by the method paramethers.
     * A feature is selected if it has a waypoint selected.
     * @param xoff x offset
     * @param yoff y offset
     * @param limits screen limits. A feature cant leave this region.
     */
    static public void translateSelected(int xoff, int yoff, Dimension limits){
        ListIterator<Feature> li = sfList.listIterator();
        while (li.hasNext()){
            li.next().translate(xoff, yoff, limits);
        }
    }
    
    
    public void translate(int xoff, int yoff, Dimension limits){
        Iterator<Waypoint> it = wpList.iterator();
        while (it.hasNext()) {
            Waypoint w = it.next();
            w.translate(xoff, yoff, limits);
        }         
    }
    //-------------end movable interface------------//

    
    // ------- clickable interface ----------//
    public boolean clicked(Point p){
        //check all waypoints
        Iterator<Waypoint> it = wpList.iterator();
        while (it.hasNext()) {
            Waypoint w = it.next();
            if (w.clicked(p)){
                return true;
            }
        }
        return false;
    }
    // ------ end clickable ------------//
    
    //create a random feature, test purposes
    public static Feature createRandomFeature(Dimension screenSize){
        //test features
        Feature f = new Feature((int)(Math.random()*2));
        // 1 - 10 waypoints
        int numWpts = (int)(Math.random() * 10 + 1);
        
        for (int i=0;i<numWpts;i++){
            Waypoint w = Waypoint.createRandomWaypoint(screenSize);
            f.addWP(w);
        }
        
        return f;
    }
    
    // --------drawing functions -----------------------//
    
    /** draw the feature */
    public static void drawFeatures(Graphics2D g, float zoom){
        Iterator<Feature> it = fList.iterator();
        while (it.hasNext()){
            it.next().draw(g, zoom);
        }        
    }
    
    /** same as draw */
    public void draw(Graphics2D g, float zoom, String label){
        draw(g, zoom);
    }
    
    public void draw(Graphics2D g, float zoom){
        //attributes depending on selected state
        Color c;
        BasicStroke stroke;
        if (selected){
            c = colors[1][type];
            stroke = new BasicStroke(2 / zoom);
        }
        else {
            c = colors[0][type];
            
            stroke = new BasicStroke(1 / zoom);            
        }        
        g.setColor(c);
        g.setStroke(stroke);
        

        
        //wpt 1
        Iterator<Waypoint> it = wpList.iterator();
        if (!it.hasNext()){
            return;
        }
        
        Waypoint w = it.next();        
        Waypoint oldW = w;
        boolean isSel = isSelected();
        if (isSel){
            oldW.draw(g, zoom, "0");
        }
        else {
            oldW.draw(g, zoom);
        }
        
        for (int i=1;it.hasNext();i++) {
            //get next
            w = it.next();            
            //draw it
            if (isSel){
                w.draw(g, zoom, Integer.toString(i));
            }
            else {
                w.draw(g, zoom);
            }
            //draw line between this and old
            g.drawLine(oldW.x, oldW.y, w.x, w.y);
            //update old
            oldW = w;
        }         
    }
    // ------- end draw -------------------//  
    
    
    //------ insertable stuff ---------//
    static public void insertNew(Dimension limits){
        Point p = new Point(limits.width / 2, limits.height / 2);
        insertNew(p, limits);
    }

    /** inserts a new feature at the given point 
     * @param p the point of insertion
     * @param limits screen limits
     */
    static public void insertNew(Point p, Dimension limits){
        Feature f = new Feature(Feature.F_RIVER);
        Waypoint w = new Waypoint(p.x, p.y);
        f.addWP(w);
        fList.addLast(f);
        Waypoint.unselectAll();
        w.setSelected(true);
    }    
    // -------- end insertable -------------//
    
    //----------- selectable iface --------//
    public boolean isSelected(){
        return selected;
    }
    
    /** doesnt mean we are unselected. It notifies that a child was unselected.
     * When all of them are, we are unselected too.
     *
     */
    public void setSelected(boolean s){        
        if (s){
            if (selectedRefCount == 0){
                selected = true;
                sfList.add(this);
            }
            selectedRefCount++;
        }
        else {
            selectedRefCount--;
            if (selectedRefCount == 0){
                selected = false;
                sfList.remove(this);
            }
        }
    }    
    //-----------end selecteable ------------//
    
    //---- deletable stuff ------------//
    /** deletes a selected Feature */
    static public void deleteSelected(){
        //dont use itarator cause of damn concurrent stuff
        int size = sfList.size();
        for (int i=0;i<size;i++){
            sfList.getFirst().autoDelete();
        }
    }
    
    /** deletes a given waypoint from feature. If no more waypoints are left,
     * feature autodeletes itself
     *@param w the waypoint to be deleted
     */
    public void deleteWaypoint(Waypoint w){
        wpList.remove(w);
        if (wpList.size() == 0){
            autoDelete();
        }
    }
        
    public void autoDelete(){
        //remove our waypoints
        int size = wpList.size();
        for (int i=0;i<size;i++){
            wpList.getFirst().autoDelete();
        }
        //remove ourselves
        fList.remove(this);
    }
    // ------ end deletable ------------//
    
    // -------- saveable -----------//
    static public void saveAllTo(RAFile raf, Dimension limits, Component parent) throws IOException {
        ListIterator<Feature> li = fList.listIterator();
        while (li.hasNext()){
            li.next().saveTo(raf, limits);
        }
        String saveMsg = (useTrailChar) ? 
            "Save successful(trail char " + trailChar + " used)" : 
            "Save successfull(no trail char)";
        JOptionPane.showMessageDialog(
            parent,
            saveMsg, 
            "File saved",
            JOptionPane.INFORMATION_MESSAGE
        );        
    }
    
    public String toString(Dimension limits){
        String s = new String(featureNames[type] + " " + wpList.size());
        ListIterator<Waypoint> li = wpList.listIterator();
        while (li.hasNext()){
            s += " " + li.next().toString(limits);
        }
        if (useTrailChar){
            s += " " + trailChar;
        }
        return s;
    }
    
    public void saveTo(RAFile output, Dimension limits) throws IOException{
        output.writeln(toString(limits));
    }
    // ----------- end saveable -----------///
    
    //------------ feature file stuff --------------//
    /** selects a file to be the feature file. It is selected only once, other
     * saves will use it afterwards.
     * @param parent the component parent, for modal operation
     */
    static public void pickFeaturesFile(Component parent){
        if (featuresFile == null){
            //open save dialog
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select file to be saved");
            fc.setFileFilter(new featuresFileFilter());
            fc.showSaveDialog(parent);
            if ((featuresFile = fc.getSelectedFile()) == null){
                return;
            }
            String s = featuresFile.getName().toLowerCase();
            if (!s.endsWith(".tdf")){
                s = Feature.featuresFile.getPath() + ".tdf";
                Feature.featuresFile = new File(s);
            }
        }
    }

    //----------- loadable stuff -------------------------//
    /** loads a feature file, creating features and waypoints. The file is loaded
     * but its is not set as the features file. This is done by saving.
     * @param parent the parent window, for modal operation
     */
    static public void loadFeaturesFile(Component parent, Dimension limits){
        File file;
        //open dialog
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select features file");
        fc.setFileFilter(new featuresFileFilter());
        fc.showOpenDialog(parent);
        if ((file = fc.getSelectedFile()) == null){
            return;
        }
        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(file));
            FeatureTokenizer ft = new FeatureTokenizer(bufReader);
            ft.nextToken();
            while (ft.ttype != FeatureTokenizer.TT_EOF) {
                ft.pushBack();
                Feature f = loadFromTokenizer(ft, limits);
                //insert feature
                fList.add(f);
                ft.nextToken();
            }
            bufReader.close();
        }
        catch (Exception e){
            //we should show a message here
            JOptionPane.showMessageDialog(parent, 
                "Cant load file: " +  e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    /** reads a feature type from tokenizer. If its not valid, throws 
     * ParseErrorException. Valid features are RIVER and ROAD
     */
    static private int readFeatureType(FeatureTokenizer ft) throws ParseErrorException {
        try {
            ft.nextToken();
            if (ft.ttype != FeatureTokenizer.TT_WORD){
                String s = new String("Expecting ");
                for (int i=0;i<F_N_TYPES-1;i++){
                    s += featureNames[i] + ", ";
                }
                s += featureNames[F_N_TYPES-1];
                throw new ParseErrorException(s);
            }
            
            //get feature type
            String fs = ft.sval;
            for (int i=0;i<F_N_TYPES;i++){
                if (fs.equalsIgnoreCase(featureNames[i])){
                    return i;
                }
            }
            throw new ParseErrorException("Invalid feature type: " + fs);
        }
        catch (Exception e){
            throw new ParseErrorException(e.getMessage());
        }
    }
    
    static public Feature loadFromTokenizer(FeatureTokenizer ft, Dimension limits) throws ParseErrorException {
        Feature f;
        //read feature type
        f = new Feature(readFeatureType(ft));
        //read number of waypoints
        int nw = ft.readInt();
        //read waypoints
        for (int i=0;i<nw;i++){
            Waypoint w = Waypoint.loadFromTokenizer(ft, limits);
            f.addWP(w);
        }
        //optional traling char, we dont use it
        try {
            ft.readInt();
        }
        catch (ParseErrorException pee){
            ft.pushBack();
        }
        
        return f;
    }
    
    static public Feature loadFromString(String s, Dimension limits) throws ParseErrorException {
        FeatureTokenizer ft = new FeatureTokenizer(new StringReader(s));
        ft.normalState();
        return loadFromTokenizer(ft, limits);
    }
    //----------- end loadable -------------------------//
        
    
    
    /** creates a backup file of the features file. If the file bla.tdf exists, 
     * it is renamed to bla.bak. If it also exists, it is renamed to bla.bak.2
     * until a free name is found
     */
    static public void createFeaturesFileBackup(){
        if (featuresFile.exists()){
            String s = featuresFile.getPath();
            File f = new File(s + ".bak");
            //we try at most 100 backups
            int i=0;
            while (true) {
                f = new File(s + ".bak." + i);
                if (!f.exists()){
                    //backup name found
                    break;
                }
                i++;
            }
            
            //couldnt find it!
            if (i == 100){
                System.out.println("Too many backups, delete some(save wont generate backup)");
                featuresFile.delete();
            }
            else {
                //backup features file
                featuresFile.renameTo(f);
            }
        }
    }

    // ---------- trail character stuff -----------------//
    
    /** toggles trailing char usage at save time. 
     * @return if trail will be used or not after the call
     */
    static public boolean toggleTrailChar(){
        useTrailChar = !useTrailChar;
        return useTrailChar;
    }
    
    /** sets the trailing character.
     * @param c the trailing character
     */
    static public void setTrailChar(char c){
        trailChar = c;
    }
    
    /** open a dialog to select trail char 
     *@param parent the dialog parent for modal op
     */
    static public void selectTrailChar(Component parent){
        TrailCharChooser tcd;
        if (parent instanceof JDialog){
            tcd = new TrailCharChooser((JDialog)parent);
        }
        else {
            tcd = new TrailCharChooser((JFrame)parent);
        }
        tcd.setVisible(true);
        trailChar = tcd.getTrailChar();
        tcd.dispose();
        
        //if we selected one, we will use it
        useTrailChar = true;
    }
    
    /** gets the trailing character 
     * @return the character
     */
    static public char getTrailChar(){
        return trailChar;
    }
    //-------------end trail char--------------//
     
}

















