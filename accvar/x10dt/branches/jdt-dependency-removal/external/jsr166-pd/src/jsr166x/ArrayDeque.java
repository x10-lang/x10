/*
 * Written by Josh Bloch of Google Inc. and released to the public domain,
 * as explained at http://creativecommons.org/licenses/publicdomain.
 */

package jsr166x;    // XXX This belongs in java.util!!! XXX
import java.util.*; // XXX This import goes away        XXX
import java.io.*;

/**
 * Resizable-array implementation of the {@link Deque} interface.  Array
 * deques have no capacity restrictions; they grow as necessary to support
 * usage.  They are not thread-safe; in the absence of external
 * synchronization, they do not support concurrent access by multiple threads.
 * Null elements are prohibited.  This class is likely to be faster than
 * {@link Stack} when used as as a stack, and faster than {@link LinkedList}
 * when used as a queue.
 *
 * <p>Most <tt>ArrayDeque</tt> operations run in amortized constant time.
 * Exceptions include {@link #remove(Object) remove}, {@link
 * #removeFirstOccurrence removeFirstOccurrence}, {@link #removeLastOccurrence
 * removeLastOccurrence}, {@link #contains contains }, {@link #iterator
 * iterator.remove()}, and the bulk operations, all of which run in linear
 * time.
 *
 * <p>The iterators returned by this class's <tt>iterator</tt> method are
 * <i>fail-fast</i>: If the deque is modified at any time after the iterator
 * is created, in any way except through the iterator's own remove method, the
 * iterator will generally throw a {@link ConcurrentModificationException}.
 * Thus, in the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class and its iterator implement all of the
 * optional methods of the {@link Collection} and {@link
 * Iterator} interfaces.  This class is a member of the <a
 * href="{@docRoot}/../guide/collections/index.html"> Java Collections
 * Framework</a>.
 *
 * @author  Josh Bloch and Doug Lea
 * @since   1.6
 * @param <E> the type of elements held in this collection
 */
public class ArrayDeque<E> extends AbstractCollection<E>
                           implements Deque<E>, Cloneable, Serializable
{
    /**
     * The array in which the elements of in the deque are stored.
     * The capacity of the deque is the length of this array, which is
     * always a power of two. The array is never allowed to become
     * full, except transiently within an addX method where it is
     * resized (see doubleCapacity) immediately upon becoming full,
     * thus avoiding head and tail wrapping around to equal each
     * other.  We also guarantee that all array cells not holding
     * deque elements are always null.
     */
    private transient E[] elements;

    /**
     * The index of the element at the head of the deque (which is the
     * element that would be removed by remove() or pop()); or an
     * arbitrary number equal to tail if the deque is empty.
     */
    private transient int head;

    /**
     * The index at which the next element would be added to the tail
     * of the deque (via addLast(E), add(E), or push(E)).
     */
    private transient int tail;

    /**
     * The minimum capacity that we'll use for a newly created deque.
     * Must be a power of 2.
     */
    private static final int MIN_INITIAL_CAPACITY = 8;

    // ******  Array allocation and resizing utilities ******

    /**
     * Allocate empty array to hold the given number of elements.
     *
     * @param numElements  the number of elements to hold.
     */
    private void allocateElements(int numElements) {   
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>>  1);
            initialCapacity |= (initialCapacity >>>  2);
            initialCapacity |= (initialCapacity >>>  4);
            initialCapacity |= (initialCapacity >>>  8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0)   // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
        }
        elements = (E[]) new Object[initialCapacity];
    }

    /**
     * Double the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     */
    private void doubleCapacity() {
        assert head == tail; 
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("Sorry, deque too big");
        Object[] a = new Object[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = (E[])a;
        head = 0;
        tail = n;
    }

    /**
     * Copy the elements from our element array into the specified array,
     * in order (from first to last element in the deque).  It is assumed
     * that the array is large enough to hold all elements in the deque.
     *
     * @return its argument
     */
    private <T> T[] copyElements(T[] a) {
        if (head < tail) {
            System.arraycopy(elements, head, a, 0, size());
        } else if (head > tail) {
            int headPortionLen = elements.length - head;
            System.arraycopy(elements, head, a, 0, headPortionLen);
            System.arraycopy(elements, 0, a, headPortionLen, tail);
        }
        return a;
    }

    /**
     * Constructs an empty array deque with the an initial capacity
     * sufficient to hold 16 elements.
     */
    public ArrayDeque() {
        elements = (E[]) new Object[16];
    }

    /**
     * Constructs an empty array deque with an initial capacity
     * sufficient to hold the specified number of elements.
     *
     * @param numElements  lower bound on initial capacity of the deque
     */
    public ArrayDeque(int numElements) {
        allocateElements(numElements);
    }

    /**
     * Constructs a deque containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.  (The first element returned by the collection's
     * iterator becomes the first element, or <i>front</i> of the
     * deque.)
     *
     * @param c the collection whose elements are to be placed into the deque
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayDeque(Collection<? extends E> c) {
        allocateElements(c.size());
        addAll(c);
    }

    // The main insertion and extraction methods are addFirst,
    // addLast, pollFirst, pollLast. The other methods are defined in
    // terms of these.

    /**
     * Inserts the specified element to the front this deque.
     *
     * @param e the element to insert
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public void addFirst(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail) 
            doubleCapacity();
    }

    /**
     * Inserts the specified element to the end this deque.
     * This method is equivalent to {@link Collection#add} and
     * {@link #push}.
     *
     * @param e the element to insert
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public void addLast(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }

    /**
     * Retrieves and removes the first element of this deque, or
     * <tt>null</tt> if this deque is empty.
     *
     * @return the first element of this deque, or <tt>null</tt> if
     *     this deque is empty
     */
    public E pollFirst() {
        int h = head;
        E result = elements[h]; // Element is null if deque empty
        if (result == null)
            return null;
        elements[h] = null;     // Must null out slot
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    /**
     * Retrieves and removes the last element of this deque, or
     * <tt>null</tt> if this deque is empty.
     *
     * @return the last element of this deque, or <tt>null</tt> if
     *     this deque is empty
     */
    public E pollLast() {
        int t = (tail - 1) & (elements.length - 1);
        E result = elements[t];
        if (result == null)
            return null;
        elements[t] = null; 
        tail = t;
        return result;
    }

    /**
     * Inserts the specified element to the front this deque.
     *
     * @param e the element to insert
     * @return <tt>true</tt> (as per the spec for {@link Deque#offerFirst})
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element to the end this deque.
     *
     * @param e the element to insert
     * @return <tt>true</tt> (as per the spec for {@link Deque#offerLast})
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves and removes the first element of this deque.  This method
     * differs from the <tt>pollFirst</tt> method in that it throws an
     * exception if this deque is empty.
     *
     * @return the first element of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E removeFirst() {
        E x = pollFirst();
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    /**
     * Retrieves and removes the last element of this deque.  This method
     * differs from the <tt>pollLast</tt> method in that it throws an
     * exception if this deque is empty.
     *
     * @return the last element of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E removeLast() {
        E x = pollLast();
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    /**
     * Retrieves, but does not remove, the first element of this deque,
     * returning <tt>null</tt> if this deque is empty.
     *
     * @return the first element of this deque, or <tt>null</tt> if
     *     this deque is empty
     */
    public E peekFirst() {
        return elements[head]; // elements[head] is null if deque empty
    }

    /**
     * Retrieves, but does not remove, the last element of this deque,
     * returning <tt>null</tt> if this deque is empty.
     *
     * @return the last element of this deque, or <tt>null</tt> if this deque
     *     is empty
     */
    public E peekLast() {
        return elements[(tail - 1) & (elements.length - 1)];
    }

    /**
     * Retrieves, but does not remove, the first element of this
     * deque.  This method differs from the <tt>peek</tt> method only
     * in that it throws an exception if this deque is empty.
     *
     * @return the first element of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E getFirst() {
        E x = elements[head];
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    /**
     * Retrieves, but does not remove, the last element of this
     * deque.  This method differs from the <tt>peek</tt> method only
     * in that it throws an exception if this deque is empty.
     *
     * @return the last element of this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E getLast() {
        E x = elements[(tail - 1) & (elements.length - 1)];
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    /**
     * Removes the first occurrence of the specified element in this
     * deque (when traversing the deque from head to tail).  If the deque
     * does not contain the element, it is unchanged.
     *
     * @param e element to be removed from this deque, if present
     * @return <tt>true</tt> if the deque contained the specified element
     */
    public boolean removeFirstOccurrence(Object e) {
        if (e == null)
            return false;
        int mask = elements.length - 1;
        int i = head;
        E x;
        while ( (x = elements[i]) != null) {
            if (e.equals(x)) {
                delete(i);
                return true;
            }
            i = (i + 1) & mask;
        }
        return false;
    }

    /**
     * Removes the last occurrence of the specified element in this
     * deque (when traversing the deque from head to tail).  If the deque
     * does not contain the element, it is unchanged.
     *
     * @param e element to be removed from this deque, if present
     * @return <tt>true</tt> if the deque contained the specified element
     */
    public boolean removeLastOccurrence(Object e) {
        if (e == null)
            return false;
        int mask = elements.length - 1;
        int i = (tail - 1) & mask;
        E x;
        while ( (x = elements[i]) != null) {
            if (e.equals(x)) {
                delete(i);
                return true;
            }
            i = (i - 1) & mask;
        }
        return false;
    }

    // *** Queue methods ***

    /**
     * Inserts the specified element to the end of this deque.
     *
     * <p>This method is equivalent to {@link #offerLast}.
     *
     * @param e the element to insert
     * @return <tt>true</tt> (as per the spec for {@link Queue#offer})
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public boolean offer(E e) {
        return offerLast(e);
    }

    /**
     * Inserts the specified element to the end of this deque.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e the element to insert
     * @return <tt>true</tt> (as per the spec for {@link Collection#add})
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    /**
     * Retrieves and removes the head of the queue represented by
     * this deque, or <tt>null</tt> if this deque is empty.  In other words,
     * retrieves and removes the first element of this deque, or <tt>null</tt>
     * if this deque is empty.
     *
     * <p>This method is equivalent to {@link #pollFirst}.
     *
     * @return the first element of this deque, or <tt>null</tt> if
     *     this deque is empty
     */
    public E poll() {
        return pollFirst();
    }

    /**
     * Retrieves and removes the head of the queue represented by this deque.
     * This method differs from the <tt>poll</tt> method in that it throws an
     * exception if this deque is empty.
     *
     * <p>This method is equivalent to {@link #removeFirst}.
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque, returning <tt>null</tt> if this deque is empty.
     *
     * <p>This method is equivalent to {@link #peekFirst}
     *
     * @return the head of the queue represented by this deque, or
     *     <tt>null</tt> if this deque is empty
     */
    public E peek() {
        return peekFirst();
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque.  This method differs from the <tt>peek</tt> method only in
     * that it throws an exception if this deque is empty.
     *
     * <p>This method is equivalent to {@link #getFirst}
     *
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException if this deque is empty
     */
    public E element() {
        return getFirst();
    }

    // *** Stack methods ***

    /**
     * Pushes an element onto the stack represented by this deque.  In other
     * words, inserts the element to the front this deque.
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @throws NullPointerException if <tt>e</tt> is null
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this deque.  In other
     * words, removes and returns the the first element of this deque.
     *
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this deque (which is the top
     *     of the stack represented by this deque)
     * @throws NoSuchElementException if this deque is empty
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * Remove the element at the specified position in the elements array,
     * adjusting head, tail, and size as necessary.  This can result in
     * motion of elements backwards or forwards in the array.
     *
     * <p>This method is called delete rather than remove to emphasize the
     * that that its semantics differ from those of List.remove(int).
     * 
     * @return true if elements moved backwards
     */
    private boolean delete(int i) {
        // Case 1: Deque doesn't wrap
        // Case 2: Deque does wrap and removed element is in the head portion
        if ((head < tail || tail == 0) || i >= head) {
            System.arraycopy(elements, head, elements, head + 1, i - head);
            elements[head] = null;
            head = (head + 1) & (elements.length - 1);
            return false;
        }

        // Case 3: Deque wraps and removed element is in the tail portion
        tail--;
        System.arraycopy(elements, i + 1, elements, i, tail - i);
        elements[tail] = null;
        return true;
    }

    // *** Collection Methods ***

    /**
     * Returns the number of elements in this deque.
     *
     * @return the number of elements in this deque
     */
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * Returns <tt>true</tt> if this collection contains no elements.<p>
     *
     * @return <tt>true</tt> if this collection contains no elements.
     */
    public boolean isEmpty() {
        return head == tail;
    }

    /**
     * Returns an iterator over the elements in this deque.  The elements
     * will be ordered from first (head) to last (tail).  This is the same
     * order that elements would be dequeued (via successive calls to
     * {@link #remove} or popped (via successive calls to {@link #pop}).
     * 
     * @return an <tt>Iterator</tt> over the elements in this deque
     */
    public Iterator<E> iterator() {
        return new DeqIterator();
    }

    private class DeqIterator implements Iterator<E> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int cursor = head;

        /**
         * Tail recorded at construction (also in remove), to stop
         * iterator and also to check for comodification.
         */
        private int fence = tail;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if element is deleted by a call to remove.
         */
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public E next() {
            E result;
            if (cursor == fence)
                throw new NoSuchElementException();
            // This check doesn't catch all possible comodifications,
            // but does catch the ones that corrupt traversal
            if (tail != fence || (result = elements[cursor]) == null)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (delete(lastRet))
                cursor--;
            lastRet = -1;
            fence = tail;
        }
    }

    /**
     * Returns <tt>true</tt> if this deque contains the specified
     * element.  More formally, returns <tt>true</tt> if and only if this
     * deque contains at least one element <tt>e</tt> such that
     * <tt>e.equals(o)</tt>.
     *
     * @param o object to be checked for containment in this deque
     * @return <tt>true</tt> if this deque contains the specified element
     */
    public boolean contains(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = head;
        E x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x))
                return true;
            i = (i + 1) & mask;
        }
        return false;
    }

    /**
     * Removes a single instance of the specified element from this deque.
     * This method is equivalent to {@link #removeFirstOccurrence}.
     *
     * @param e element to be removed from this deque, if present
     * @return <tt>true</tt> if this deque contained the specified element
     */
    public boolean remove(Object e) {
        return removeFirstOccurrence(e);
    }

    /**
     * Removes all of the elements from this deque.
     */
    public void clear() {
        int h = head;
        int t = tail;
        if (h != t) { // clear all cells
            head = tail = 0;
            int i = h;
            int mask = elements.length - 1;
            do {
                elements[i] = null;
                i = (i + 1) & mask;
            } while(i != t);
        }
    }

    /**
     * Returns an array containing all of the elements in this list
     * in the correct order.
     *
     * @return an array containing all of the elements in this list
     * 	       in the correct order
     */
    public Object[] toArray() {
	return copyElements(new Object[size()]);
    }

    /**
     * Returns an array containing all of the elements in this deque in the
     * correct order; the runtime type of the returned array is that of the
     * specified array.  If the deque fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the runtime
     * type of the specified array and the size of this deque.
     *
     * <p>If the deque fits in the specified array with room to spare (i.e.,
     * the array has more elements than the deque), the element in the array
     * immediately following the end of the collection is set to <tt>null</tt>.
     *
     * @param a the array into which the elements of the deque are to
     *		be stored, if it is big enough; otherwise, a new array of the
     * 		same runtime type is allocated for this purpose
     * @return an array containing the elements of the deque
     * @throws ArrayStoreException if the runtime type of a is not a supertype
     *         of the runtime type of every element in this deque
     */
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
	copyElements(a);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // *** Object methods ***

    /**
     * Returns a copy of this deque.
     *
     * @return a copy of this deque
     */
    public ArrayDeque<E> clone() {
        try { 
            ArrayDeque<E> result = (ArrayDeque<E>) super.clone();
            // These two lines are currently faster than cloning the array:
            result.elements = (E[]) new Object[elements.length];
            System.arraycopy(elements, 0, result.elements, 0, elements.length);
            return result;

        } catch (CloneNotSupportedException e) { 
            throw new AssertionError();
        }
    }

    /**
     * Appease the serialization gods.
     */
    private static final long serialVersionUID = 2340985798034038923L;

    /**
     * Serialize this deque.
     *
     * @serialData The current size (<tt>int</tt>) of the deque,
     * followed by all of its elements (each an object reference) in
     * first-to-last order.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Write out size
        int size = size();
        s.writeInt(size);

        // Write out elements in order.
        int i = head;
        int mask = elements.length - 1;
        for (int j = 0; j < size; j++) {
            s.writeObject(elements[i]);
            i = (i + 1) & mask;
        }
    }

    /**
     * Deserialize this deque.
     */
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        // Read in size and allocate array
        int size = s.readInt();
        allocateElements(size);
        head = 0;
        tail = size;

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            elements[i] = (E)s.readObject();

    }
}
