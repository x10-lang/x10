/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 13, 2005
 *
 * 
 */
package x10.lang;

/**
 * An interface implemented by x10.lang classes which can be accessed as arrays.
 * @author vj Jan 13, 2005
 */
public interface Indexable extends IndexableGet, IndexableSet {
    /**
     * Is this object a value object (i.e. it is immutable and
     * initialized to its fixed value upon construction).
     * @return true if this is a value
     */
    public boolean isValue();
    
    /**
     * Is this indexable value-equals to the other indexable?
     * @param other
     * @return true if these objects are value-equals
     */
    public boolean valueEquals(Indexable other);
}
