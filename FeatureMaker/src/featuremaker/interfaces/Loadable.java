/*
 * Loadable.java
 *
 * Created on 5 de Outubro de 2005, 09:13
 */

package featuremaker.interfaces;

import featuremaker.exceptions.ParseErrorException;
import featuremaker.misc.FeatureTokenizer;

/**
 * An object that can be loaded from a String or a tokenizer. This is useful for
 * loading objects from files or clipboard. 
 * @author  matheus
 */
public interface Loadable<E> {
    //public E loadFromTokenizer(FeatureTokenizer ft, Dimension limits) throws ParseErrorException;
    //public E loadFromString(String s, Dimension limits) throws ParseErrorException;
}
