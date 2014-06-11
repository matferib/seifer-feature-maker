package featuremaker.misc;

import featuremaker.exceptions.ParseErrorException;
import java.io.*;

public class FeatureTokenizer extends StreamTokenizer {
    /** a variable telling number of comments read */
    static private int numCommentLinesRead = 0;
    
    public FeatureTokenizer(Reader r) {
        super(r); 
    }

    public void normalState(){
        lowerCaseMode(true);
        slashSlashComments(false);
        slashStarComments(false);
        eolIsSignificant(false);
        parseNumbers();
        ordinaryChar(',');
        ordinaryChar('#');
        wordChars('a', 'z');
        wordChars('A', 'Z');
        wordChars('0', '9');
        wordChars('\u00A0','\u00FF');
        whitespaceChars('\u0000','\u0020');
    }

    public void commentState(){
        this.resetSyntax();
        this.eolIsSignificant(true);
        slashSlashComments(false);
    }
    
    public void resetNumComments(){
        numCommentLinesRead = 0;
    }

    /** Reads everything until after the comment. All objects call this prior to 
     * reading an entry, and ignoring the comments.
     */
    public void skipComment() throws Exception{
        //reads until we find something useful
//        char tst;
        nextToken();
//        tst = (char)ttype;

        while (ttype == '#') {
            skipLine();
        }
        //when we get here, we got something which is not a comment =)

    }
    
    
    /** Reads until it finds a eol.
     */
    public void skipLine() throws Exception{
        eolIsSignificant(true);
        //reads until we find a EOL
        //char a = (char)ttype;        
        for (nextToken();ttype != ';';nextToken()) ;

    }
    
    /** reads an integer number from tokenizer, throws parse error if its nan */
    public int readInt() throws ParseErrorException {
        return (int)(readDouble());
    }
    
    /** reads a float number from tokenizer, throws parse error if its nan */
    public float readFloat() throws ParseErrorException {
        return (float)(readDouble());        
    }
    
    /** reads a double from tokenizer */
    public double readDouble() throws ParseErrorException {
        try {
            this.nextToken();
            if (this.ttype != FeatureTokenizer.TT_NUMBER){
                throw new ParseErrorException("Expecting a number");
            }
            return this.nval;
        }
        catch (Exception e){
            throw new ParseErrorException(e.getMessage());
        }
    }
    
    public void readComma() throws ParseErrorException {
        try {
            this.nextToken();
            if (this.ttype != ','){
                throw new ParseErrorException("Expecting comma");
            }            
        }
        catch (Exception e){
            throw new ParseErrorException(e.getMessage());
        }
    }
}