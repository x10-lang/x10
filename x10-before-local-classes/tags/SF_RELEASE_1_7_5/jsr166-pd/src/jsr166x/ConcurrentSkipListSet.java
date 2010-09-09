/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package jsr166x; 

import java.util.*;
import java.util.concurrent.*;

/**
 * A scalable concurrent {@link NavigableSet} implementation based on
 * a {@link ConcurrentSkipListMap}.  This class maintains a set in
 * ascending order, sorted according to the <i>natural order</i> for
 * the element's class (see {@link Comparable}), or by the comparator
 * provided at creation time, depending on which constructor is
 * used.<p>
 *
 * This implementation provides expected average <i>log(n)</i> time
 * cost for the <tt>contains</tt>, <tt>add</tt>, and <tt>remove</tt>
 * operations and their variants.  Insertion, removal, and access
 * operations safely execute concurrently by multiple
 * threads. Iterators are <i>weakly consistent</i>, returning elements
 * reflecting the state of the set at some point at or since the
 * creation of the iterator.  They do <em>not</em> throw {@link
 * ConcurrentModificationException}, and may proceed concurrently with
 * other operations.
 *
 * <p>Beware that, unlike in most collections, the <tt>size</tt>
 * method is <em>not</em> a constant-time operation. Because of the
 * asynchronous nature of these sets, determining the current number
 * of elements requires a traversal of the elements. Additionally, the
 * bulk operations <tt>addAll</tt>, <tt>removeAll</tt>,
 * <<tt>retainAll</tt>, and tt>containsAll</tt> are <em>not</em>
 * guaranteed to be performed atomically. For example, an iterator
 * operating concurrently with an <tt>addAll</tt> operation might view
 * only some of the added elements.
 *
 * <p>This class and its iterators implement all of the
 * <em>optional</em> methods of the {@link Set} and {@link Iterator}
 * interfaces. Like most other concurrent collection implementations,
 * this class does not permit the use of <tt>null</tt> elements.
 * because <tt>null</tt> arguments and return values cannot be reliably
 * distinguished from the absence of elements.
 *
 * @author Doug Lea
 * @param <E> the type of elements maintained by this set
 */
public class ConcurrentSkipListSet<E>
    extends AbstractSet<E>
    implements NavigableSet<E>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -2479143111061671589L;

    /**
     * The underlying map. Uses Boolean.TRUE as value for each
     * element.  Note that this class relies on default serialization,
     * which is a little wasteful in saving all of the useless value
     * fields of underlying map, but enables this field to be declared
     * final, which is necessary for thread safety.
     */
    private final ConcurrentSkipListMap<E,Object> m; 

    /**
     * Constructs a new, empty set, sorted according to the elements' natural
     * order. 
     */
    public ConcurrentSkipListSet() {
        m = new ConcurrentSkipListMap<E,Object>();
    }

    /**
     * Constructs a new, empty set, sorted according to the specified
     * comparator.  
     *
     * @param c the comparator that will be used to sort this set.  A
     *        <tt>null</tt> value indicates that the elements' <i>natural
     *        ordering</i> should be used.
     */
    public ConcurrentSkipListSet(Comparator<? super E> c) {
        m = new ConcurrentSkipListMap<E,Object>(c);
    }

    /**
     * Constructs a new set containing the elements in the specified
     * collection, sorted according to the elements' <i>natural order</i>.
     *
     * @param c The elements that will comprise the new set.
     *
     * @throws ClassCastException if the elements in the specified
     * collection are not comparable, or are not mutually comparable.
     * @throws NullPointerException if the specified collection is
     * <tt>null</tt>.
     */
    public ConcurrentSkipListSet(Collection<? extends E> c) {
        m = new ConcurrentSkipListMap<E,Object>();
        addAll(c);
    }

    /**
     * Constructs a new set containing the same elements as the specified
     * sorted set, sorted according to the same ordering.
     *
     * @param s sorted set whose elements will comprise the new set.
     * @throws NullPointerException if the specified sorted set is
     * <tt>null</tt>.
     */
    public ConcurrentSkipListSet(SortedSet<E> s) {
        m = new ConcurrentSkipListMap<E,Object>(s.comparator());
        addAll(s);
    }

    /**
     * Returns a shallow copy of this set. (The elements themselves
     * are not cloned.)
     *
     * @return a shallow copy of this set.
     */
    public Object clone() {
        ConcurrentSkipListSet<E> clone = null;
	try {
	    clone = (ConcurrentSkipListSet<E>) super.clone();
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}

        clone.m.initialize();
        clone.addAll(this);
        return clone;
    }

    /* ---------------- Set operations -------------- */

    /**
     * Returns the number of elements in this set.  If this set
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, it
     * returns <tt>Integer.MAX_VALUE</tt>.
     *
     * <p>Beware that, unlike in most collections, this method is
     * <em>NOT</em> a constant-time operation. Because of the
     * asynchronous nature of these sets, determining the current
     * number of elements requires traversing them all to count them.
     * Additionally, it is possible for the size to change during
     * execution of this method, in which case the returned result
     * will be inaccurate. Thus, this method is typically not very
     * useful in concurrent applications.
     *
     * @return  the number of elements in this set.
     */
    public int size() {
	return m.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     * @return <tt>true</tt> if this set contains no elements.
     */
    public boolean isEmpty() {
	return m.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     *
     * @param o the object to be checked for containment in this set.
     * @return <tt>true</tt> if this set contains the specified element.
     *
     * @throws ClassCastException if the specified object cannot be compared
     * 		  with the elements currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>.
     */
    public boolean contains(Object o) {
	return m.containsKey(o);
    }

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param o element to be added to this set.
     * @return <tt>true</tt> if the set did not already contain the specified
     *         element.
     *
     * @throws ClassCastException if the specified object cannot be compared
     * 		  with the elements currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>.
     */
    public boolean add(E o) {
	return m.putIfAbsent(o, Boolean.TRUE) == null;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o object to be removed from this set, if present.
     * @return <tt>true</tt> if the set contained the specified element.
     *
     * @throws ClassCastException if the specified object cannot be compared
     * 		  with the elements currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>.
     */
    public boolean remove(Object o) {
	return m.removep(o);
    }

    /**
     * Removes all of the elements from this set.
     */
    public void clear() {
	m.clear();
    }

    /**
     * Returns an iterator over the elements in this set.  The elements
     * are returned in ascending order.
     *
     * @return an iterator over the elements in this set.
     */
    public Iterator<E> iterator() {
	return m.keyIterator();
    }

    /**
     * Returns an iterator over the elements in this set.  The elements
     * are returned in descending order.
     *
     * @return an iterator over the elements in this set.
     */
    public Iterator<E> descendingIterator() {
	return m.descendingKeyIterator();
    }

    /* ---------------- AbstractSet Overrides -------------- */

    /**
     * Compares the specified object with this set for equality.  Returns
     * <tt>true</tt> if the specified object is also a set, the two sets
     * have the same size, and every member of the specified set is
     * contained in this set (or equivalently, every member of this set is
     * contained in the specified set).  This definition ensures that the
     * equals method works properly across different implementations of the
     * set interface.
     *
     * @param o Object to be compared for equality with this set.
     * @return <tt>true</tt> if the specified Object is equal to this set.
     */
    public boolean equals(Object o) {
        // Override AbstractSet version to avoid calling size()
	if (o == this)
	    return true;
	if (!(o instanceof Set))
	    return false;
	Collection c = (Collection) o;
        try {
            return containsAll(c) && c.containsAll(this);
        } catch(ClassCastException unused)   {
            return false;
        } catch(NullPointerException unused) {
            return false;
        }
    }
    
    /**
     * Removes from this set all of its elements that are contained in
     * the specified collection.  If the specified collection is also
     * a set, this operation effectively modifies this set so that its
     * value is the <i>asymmetric set difference</i> of the two sets.
     *
     * @param  c collection that defines which elements will be removed from
     *           this set.
     * @return <tt>true</tt> if this set changed as a result of the call.
     * 
     * @throws ClassCastException if the types of one or more elements in this
     *            set are incompatible with the specified collection
     * @throws NullPointerException if the specified collection, or any
     * of its elements are <tt>null</tt>.
     */
    public boolean removeAll(Collection<?> c) {
        // Override AbstractSet version to avoid unnecessary call to size()
        boolean modified = false;
        for (Iterator<?> i = c.iterator(); i.hasNext(); )
            if (remove(i.next()))
                modified = true;
        return modified;
    }
    
    /* ---------------- Relational operations -------------- */

    /**
     * Returns an element greater than or equal to the given element, or
     * <tt>null</tt> if there is no such element.
     * 
     * @param o the value to match
     * @return an element greater than or equal to given element, or
     * <tt>null</tt> if there is no such element.
     * @throws ClassCastException if o cannot be compared with the elements
     *            currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>
     */
    public E ceiling(E o) {
        return m.ceilingKey(o);
    }

    /**
     * Returns an element strictly less than the given element, or
     * <tt>null</tt> if there is no such element.
     * 
     * @param o the value to match
     * @return the greatest element less than the given element, or
     * <tt>null</tt> if there is no such element.
     * @throws ClassCastException if o cannot be compared with the elements
     *            currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>.
     */
    public E lower(E o) {
        return m.lowerKey(o);
    }

    /**
     * Returns an element less than or equal to the given element, or
     * <tt>null</tt> if there is no such element.
     * 
     * @param o the value to match
     * @return the greatest element less than or equal to given
     * element, or <tt>null</tt> if there is no such element.
     * @throws ClassCastException if o cannot be compared with the elements
     *            currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>.
     */
    public E floor(E o) {
        return m.floorKey(o);
    }

    /**
     * Returns an element strictly greater than the given element, or
     * <tt>null</tt> if there is no such element.
     * 
     * @param o the value to match
     * @return the least element greater than the given element, or
     * <tt>null</tt> if there is no such element.
     * @throws ClassCastException if o cannot be compared with the elements
     *            currently in the set.
     * @throws NullPointerException if o is <tt>null</tt>.
     */
    public E higher(E o) {
        return m.higherKey(o);
    }

    /**
     * Retrieves and removes the first (lowest) element.
     *
     * @return the least element, or <tt>null</tt> if empty.
     */
    public E pollFirst() {
        return m.pollFirstKey();
    }

    /**
     * Retrieves and removes the last (highest) element.
     *
     * @return the last element, or <tt>null</tt> if empty.
     */
    public E pollLast() {
        return m.pollLastKey();
    }


    /* ---------------- SortedSet operations -------------- */


    /**
     * Returns the comparator used to order this set, or <tt>null</tt>
     * if this set uses its elements natural ordering.
     *
     * @return the comparator used to order this set, or <tt>null</tt>
     * if this set uses its elements natural ordering.
     */
    public Comparator<? super E> comparator() {
        return m.comparator();
    }

    /**
     * Returns the first (lowest) element currently in this set.
     *
     * @return the first (lowest) element currently in this set.
     * @throws    NoSuchElementException sorted set is empty.
     */
    public E first() {
        return m.firstKey();
    }

    /**
     * Returns the last (highest) element currently in this set.
     *
     * @return the last (highest) element currently in this set.
     * @throws    NoSuchElementException sorted set is empty.
     */
    public E last() {
        return m.lastKey();
    }



    /**
     * Returns a view of the portion of this set whose elements range from
     * <tt>fromElement</tt>, inclusive, to <tt>toElement</tt>, exclusive.  (If
     * <tt>fromElement</tt> and <tt>toElement</tt> are equal, the returned
     * sorted set is empty.)  The returned sorted set is backed by this set,
     * so changes in the returned sorted set are reflected in this set, and
     * vice-versa. 
     * @param fromElement low endpoint (inclusive) of the subSet.
     * @param toElement high endpoint (exclusive) of the subSet.
     * @return a view of the portion of this set whose elements range from
     * 	       <tt>fromElement</tt>, inclusive, to <tt>toElement</tt>,
     * 	       exclusive.
     * @throws ClassCastException if <tt>fromElement</tt> and
     *         <tt>toElement</tt> cannot be compared to one another using
     *         this set's comparator (or, if the set has no comparator,
     *         using natural ordering).
     * @throws IllegalArgumentException if <tt>fromElement</tt> is
     * greater than <tt>toElement</tt>.
     * @throws NullPointerException if <tt>fromElement</tt> or
     *	       <tt>toElement</tt> is <tt>null</tt>.
     */
    public NavigableSet<E> subSet(E fromElement, E toElement) {
	return new ConcurrentSkipListSubSet<E>(m, fromElement, toElement);
    }

    /**
     * Returns a view of the portion of this set whose elements are strictly
     * less than <tt>toElement</tt>.  The returned sorted set is backed by
     * this set, so changes in the returned sorted set are reflected in this
     * set, and vice-versa.  
     * @param toElement high endpoint (exclusive) of the headSet.
     * @return a view of the portion of this set whose elements are strictly
     * 	       less than toElement.
     * @throws ClassCastException if <tt>toElement</tt> is not compatible
     *         with this set's comparator (or, if the set has no comparator,
     *         if <tt>toElement</tt> does not implement <tt>Comparable</tt>).
     * @throws NullPointerException if <tt>toElement</tt> is <tt>null</tt>.
     */
    public NavigableSet<E> headSet(E toElement) {
	return new ConcurrentSkipListSubSet<E>(m, null, toElement);
    }


    /**
     * Returns a view of the portion of this set whose elements are
     * greater than or equal to <tt>fromElement</tt>.  The returned
     * sorted set is backed by this set, so changes in the returned
     * sorted set are reflected in this set, and vice-versa.
     * @param fromElement low endpoint (inclusive) of the tailSet.
     * @return a view of the portion of this set whose elements are
     * greater than or equal to <tt>fromElement</tt>.
     * @throws ClassCastException if <tt>fromElement</tt> is not
     * compatible with this set's comparator (or, if the set has no
     * comparator, if <tt>fromElement</tt> does not implement
     * <tt>Comparable</tt>).
     * @throws NullPointerException if <tt>fromElement</tt> is <tt>null</tt>.
     */
    public NavigableSet<E> tailSet(E fromElement) {
	return new ConcurrentSkipListSubSet<E>(m, fromElement, null);
    }

    /**
     * Subsets returned by {@link ConcurrentSkipListSet} subset operations
     * represent a subrange of elements of their underlying
     * sets. Instances of this class support all methods of their
     * underlying sets, differing in that elements outside their range are
     * ignored, and attempts to add elements outside their ranges result
     * in {@link IllegalArgumentException}.  Instances of this class are
     * constructed only using the <tt>subSet</tt>, <tt>headSet</tt>, and
     * <tt>tailSet</tt> methods of their underlying sets.
     *
     */
    static class ConcurrentSkipListSubSet<E> 
        extends AbstractSet<E> 
        implements NavigableSet<E>, java.io.Serializable {

        private static final long serialVersionUID = -7647078645896651609L;

        /** The underlying submap  */
        private final ConcurrentSkipListMap.ConcurrentSkipListSubMap<E,Object> s;
        
        /**
         * Creates a new submap. 
         * @param fromElement inclusive least value, or <tt>null</tt> if from start
         * @param toElement exclusive upper bound or <tt>null</tt> if to end
         * @throws IllegalArgumentException if fromElement and toElement
         * nonnull and fromElement greater than toElement
         */
        ConcurrentSkipListSubSet(ConcurrentSkipListMap<E,Object> map, 
                                 E fromElement, E toElement) {
            s = new ConcurrentSkipListMap.ConcurrentSkipListSubMap<E,Object>
                (map, fromElement, toElement);
        }

        // subsubset construction

        public NavigableSet<E> subSet(E fromElement, E toElement) {
            if (!s.inOpenRange(fromElement) || !s.inOpenRange(toElement))
                throw new IllegalArgumentException("element out of range");
            return new ConcurrentSkipListSubSet<E>(s.getMap(), 
                                                   fromElement, toElement);
        }

        public NavigableSet<E> headSet(E toElement) {
            E least = s.getLeast();
            if (!s.inOpenRange(toElement))
                throw new IllegalArgumentException("element out of range");
            return new ConcurrentSkipListSubSet<E>(s.getMap(), 
                                                   least, toElement);
        }
        
        public NavigableSet<E> tailSet(E fromElement) {
            E fence = s.getFence();
            if (!s.inOpenRange(fromElement))
                throw new IllegalArgumentException("element out of range");
            return new ConcurrentSkipListSubSet<E>(s.getMap(), 
                                                   fromElement, fence);
        }

        // relays to submap methods

        public int size()                 { return s.size(); }
        public boolean isEmpty()          { return s.isEmpty(); }
        public boolean contains(Object o) { return s.containsKey(o); }
        public void clear()               { s.clear(); }
        public E first()                  { return s.firstKey(); }
        public E last()                   { return s.lastKey(); }
        public E ceiling(E o)             { return s.ceilingKey(o); }
        public E lower(E o)               { return s.lowerKey(o); }
        public E floor(E o)               { return s.floorKey(o); }
        public E higher(E o)              { return s.higherKey(o); }
        public boolean remove(Object o) { return s.remove(o)==Boolean.TRUE; }
        public boolean add(E o)       { return s.put(o, Boolean.TRUE)==null; }
        public Comparator<? super E> comparator() { return s.comparator(); }
        public Iterator<E> iterator()     { return s.keySet().iterator(); }
        public Iterator<E> descendingIterator() {
            return s.descendingKeySet().iterator();
        }
        public E pollFirst() { 
            Map.Entry<E,?> e = s.pollFirstEntry();
            return (e == null)? null : e.getKey();
        }
        public E pollLast() {
            Map.Entry<E,?> e = s.pollLastEntry();
            return (e == null)? null : e.getKey();
        }

    }
}    
