/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * Predicate.java
 */

package polyglot.util;

/**
 * Predicate
 *
 * Overview:
 *     This interface provides a general means for describing predicates
 *     about objects.
 **/
public interface Predicate2<T> { 
  public boolean isTrue(T o, T p);  
}


