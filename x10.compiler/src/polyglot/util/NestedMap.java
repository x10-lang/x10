/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.util;

import java.util.*;

import polyglot.util.FilteringIterator;
import x10.util.CollectionFactory;

/**
 * A NestedMap is a map which, when it cannot find an element in itself,
 * defers to another map.  Modifications, however, are not passed on to
 * the supermap.
 *
 * A NestedMap and its backing collections and iterators support all
 * operations except 'remove' and 'clear', since operations to a
 * NestedMap must not affect the backing map.  Instead, use the 'release'
 * method.
 *
 * It is used to implement nested namespaces, such as those which store
 * local-variable bindings.
 **/
public class NestedMap<K,V> extends AbstractMap<K,V> implements Map<K,V> {
  /**
   * Creates a new nested map, which defers to <containing>.  If containing
   * is null, it defaults to a NilMap.
   **/
  public NestedMap(Map<K,V> containing) {
    this.superMap = containing;
    this.myMap = CollectionFactory.newHashMap();
    setView = new EntrySet();
    nShadowed = 0;
  }

  /////
  // For NestedMap.
  /////
  /**
   * Returns the map to which this map defers, or null for none.
   **/
  public Map<K,V> getContainingMap() {
    return superMap;
  }

  /**
   * Removes any binding in this for <key>, returning to the binding (if any)
   * from the supermap.
   **/
  public void release(Object key) {
    myMap.remove(key);
  }  

  /**
   * Returns the map containing the elements for this level of nesting.
   **/
  public Map<K,V> getInnerMap() {
    return myMap;
  }

  /////
  // Methods required for AbstractMap.
  /////

  public Set<Map.Entry<K,V>> entrySet() {
    return setView;
  }

  public int size() {
    return superMap.size() + myMap.size() - nShadowed;
  }

  public boolean containsKey(Object key) {
    return myMap.containsKey(key) || superMap.containsKey(key);
  }

  public V get(Object key) {
    if (myMap.containsKey(key))
      return myMap.get(key);
    else 
      return superMap.get(key);
  }
  
  public V put(K key, V value) {
    if (myMap.containsKey(key)) {
      return myMap.put(key,value);
    } else {
      V oldV = superMap.get(key);
      myMap.put(key,value);
      nShadowed++;
      return oldV;
    }
  }  

  public V remove(Object key) {
    throw new UnsupportedOperationException("Remove from NestedMap");
  }

  public void clear() {
    throw new UnsupportedOperationException("Clear in NestedMap");
  }

  public final class KeySet extends AbstractSet<K> {
    public Iterator<K> iterator() {
      return new ConcatenatedIterator<K>(
              myMap.keySet().iterator(),
              new FilteringIterator<K>(superMap.keySet(), keyNotInMyMap));
    }
    public int size() {
      return NestedMap.this.size();
    }
    // No add; it's not meaningful.
    public boolean contains(Object o) {
      return NestedMap.this.containsKey(o);
    }
    public boolean remove(Object o) {
      throw new UnsupportedOperationException(
               "Remove from NestedMap.keySet");
    }
  }

  private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {    
    public Iterator<Map.Entry<K,V>> iterator() {
      return new ConcatenatedIterator<Map.Entry<K,V>>(
	  myMap.entrySet().iterator(),
	  new FilteringIterator<Map.Entry<K, V>>(superMap.entrySet(), entryKeyNotInMyMap));
    }
    public int size() {
      return NestedMap.this.size();
    }
    // No add; it's not meaningful.
    @SuppressWarnings("unchecked") // Casting to a generic type
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry<?,?>)) return false;
      Map.Entry<K,V> ent = (Map.Entry<K,V>) o;
      K entKey = ent.getKey();
      V entVal = ent.getValue();
      if (entVal != null) {
	V val = NestedMap.this.get(entKey);
	return (val != null) && val.equals(entVal);
      } else {
	return NestedMap.this.containsKey(entKey) &&
	  (NestedMap.this.get(entKey) == null);
      }
    }
    public boolean remove(Object o) {
      throw new UnsupportedOperationException(
               "Remove from NestedMap.entrySet");
    }
  }
 
  private Map<K,V> myMap;
  private int nShadowed;
  private Set<Map.Entry<K,V>> setView; // the set view of this.
  private Map<K,V> superMap;
  private Predicate<Map.Entry<K,V>> entryKeyNotInMyMap = new Predicate<Map.Entry<K,V>>() {
    public boolean isTrue(Map.Entry<K,V> ent) {
      return ! myMap.containsKey(ent.getKey());
    }
  };
  private Predicate<K> keyNotInMyMap = new Predicate<K>() {
    public boolean isTrue(K o) {
      return ! myMap.containsKey(o);
    }
  };

}

