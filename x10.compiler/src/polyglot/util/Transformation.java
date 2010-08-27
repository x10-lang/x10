/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * Transformation.java
 */

package polyglot.util;


/**
 * Transformation
 *
 * Overview:
 *     This interface provides a general means for transforming objects.
 **/
public interface Transformation<S,T> { 
  public T transform(S o);  
//  public List<T> transform(List<S> l) { return new TransformingList<S, T>(l, this); }
}


