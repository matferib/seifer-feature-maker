/*
 * Deletable.java
 *
 * Created on 4 de Outubro de 2005, 07:48
 */

package featuremaker.interfaces;

/**
 * An object which can be deleted. A deleted object must leave all its containers
 * approprieately.
 * @author  matheus
 */
public interface Deletable {

    /** auto destruction */
    public void autoDelete();
}
