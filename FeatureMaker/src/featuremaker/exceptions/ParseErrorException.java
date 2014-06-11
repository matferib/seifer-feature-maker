/*
 * ParseErrorException.java
 *
 * Created on 5 de Outubro de 2005, 09:32
 */

package featuremaker.exceptions;

/**
 * Indicates a parse error ocurred. Thrown by the parser while reading features
 * from file or clipboard.
 * @author  matheus
 */
public class ParseErrorException extends Exception {
    
    /** Creates a new instance of ParseErrorException */
    public ParseErrorException(String message) {
        super(message);
    }
    
}
