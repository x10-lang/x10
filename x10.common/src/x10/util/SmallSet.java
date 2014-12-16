package x10.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public final class SmallSet<K> extends AbstractSet<K> {
    // elems[0] .. elems[curIndex-1] contains distinct elements, and there might be additional elements in overflow
    private K[] elems; // we must support null entries
    private int curIndex;
    private LinkedHashSet<K> overflow = null;

    public SmallSet() {
        this(CollectionFactory.DEFAULT_SIZE);
    }
    @SuppressWarnings("unchecked")
    public SmallSet(int size) {
        elems = (K[])new Object[size];
    }
    public SmallSet(Collection<K> c) {
        this(c.size());
        addAll(c);
    }
    @Override
    public boolean add(K k) {
        // X10TypeEnv_c.bounds adds a null entry: if (k==null) throw new RuntimeException("SmallSet doesn't support null entries");
        if (contains(k)) return false;
        if (curIndex<elems.length) {
            elems[curIndex++] = k;
        } else {
            if (overflow==null) overflow = new LinkedHashSet<K>();
            overflow.add(k);
        }
        return true;
    }
    private void removeIndex(int i) {
        for (int j=i; j<curIndex-1; j++)
            elems[j] = elems[j+1];
        elems[--curIndex] = null; // for garbage collection
    }
    @Override
    public boolean remove(Object o) {
        for (int i=0; i<curIndex; i++)
            if (CollectionFactory.equals(elems[i],o)) {
                removeIndex(i);
                return true;
            }
        return overflow==null ? false : overflow.remove(o);
    }


    // no need to override since the implementation in AbstractSet is efficient.
    @Override
    public String toString() {
        return super.toString(); // uses an iterator, and we do not care about efficiency of toString() anyway
    }
    @Override
    public boolean equals(Object o) {
        return super.equals(o); // uses containsAll
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        if (c instanceof SmallSet) {
            @SuppressWarnings("unchecked")
            SmallSet<K> s = (SmallSet<K>) c;
            for (int i=0; i<s.curIndex; i++)
                if (!contains(s.elems[i])) return false;
            if (s.overflow!=null)
                for (K o : s.overflow)
                    if (!contains(o)) return false;
            return true;
        }
        return super.containsAll(c); // uses c.iterator() and contains
    }
    @Override
    public boolean addAll(Collection<? extends K> c) {
        if (c instanceof SmallSet) {
            boolean modified = false;
            @SuppressWarnings("unchecked")
            SmallSet<K> s = (SmallSet<K>) c;
            for (int i=0; i<s.curIndex; i++)
                if (add(s.elems[i])) modified = true;
            if (s.overflow!=null)
                for (K o : s.overflow)
                    if (add(o)) modified = true;
            return modified;
        }
        return super.addAll(c);    // uses c.iterator() and add
    }
    @Override
     public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);    // iterates over the smaller collection
    }
    @Override
    public boolean isEmpty() {
        return super.isEmpty();    // uses size
    }
    @Override
    public Object[] toArray() {
        if (overflow==null) {
            return Arrays.copyOf(elems, curIndex);
        }
        return super.toArray(); // uses an iterator
    }

	@Override
    public <T> T[] toArray(T[] a) {
        if (overflow==null) {
            if (a.length>=curIndex) {
                System.arraycopy(elems,0,a,0,curIndex);
                return a;
            }
            Arrays.copyOf(elems, curIndex, a.getClass());
        }
        return super.toArray(a); 
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (int i=0; i<curIndex; i++)
            if (!c.contains(elems[i])) {
                modified = true;
                removeIndex(i);
                i--;
            }
        if (overflow!=null) modified |=overflow.retainAll(c);
        return modified;
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i=0; i<curIndex; i++)
            h += CollectionFactory.hashCode(elems[i]);
        if (overflow!=null) h+=overflow.hashCode();
        return h;
    }

    @Override
    public boolean contains(Object o) {
        for (int i=0; i<curIndex; i++)
            if (CollectionFactory.equals(elems[i],o)) return true;
        return overflow==null ? false : overflow.contains(o);
    }


    @Override
    public void clear() {
        for (int i=0; i<curIndex; i++)
            elems[i] = null;
        overflow = null;
        curIndex = 0;
    }

    public Iterator<K> iterator() {
        return new Iterator<K>() {
            int pos;
            Iterator<K> overflowIt;
            public boolean hasNext() {
                return pos<curIndex || (
                        overflowIt==null ? (overflow!=null && !overflow.isEmpty())
                            : overflowIt.hasNext());
            }

            public K next() {
                if (pos<curIndex) {
                    return elems[pos++];
                } else {
                    if (overflowIt==null) overflowIt = overflow.iterator();
                    return overflowIt.next();
                }
            }

            public void remove() {
                if (overflowIt==null) {
                    removeIndex(--pos);
                } else
                    overflowIt.remove();
            }
        }; 
    }

    public int size() {
        return curIndex+(overflow==null?0:overflow.size());
    }
}
