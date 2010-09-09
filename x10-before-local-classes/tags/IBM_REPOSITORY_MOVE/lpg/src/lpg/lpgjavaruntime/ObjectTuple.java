package lpg.lpgjavaruntime;


//
// This Tuple class can be used to construct a dynamic
// array of integers. The space for the array is allocated in
// blocks of size 2**LOG_BLKSIZE. In declaring a tuple the user
// may specify an estimate of how many elements he expects.
// Based on that estimate, suitable values will be calculated
// for log_blksize and base_increment. If these estimates are
// found to be off later, more space will be allocated.
//
public class ObjectTuple
{
    Object array[];
    int top;

    //
    // This function is used to reset the size of a dynamic array without
    // allocating or deallocting space. It may be invoked with an integer
    // argument n which indicates the new size or with no argument which
    // indicates that the size should be reset to 0.
    //
    void reset() { reset(0); }
    void reset(int n)
    {
        top = n;
    }

    //
    // Return size of the dynamic array.
    //
    int size() { return top; }

    //
    // Return a reference to the ith element of the dynamic array.
    //
    // Note that no check is made here to ensure that 0 <= i < top.
    // Such a check might be useful for debugging and a range exception
    // should be thrown if it yields true.
    //
    Object get(int i)
    {
        return array[i];
    }

    //
    // Insert an element in the dynamic array at the location indicated.
    //
    void set(int i, Object element)
    {
        array[i] = element;
    }

    //
    // Add an element to the dynamic array and return the top index.
    //
    int nextIndex()
    {
        int i = top++;
        if (i >= array.length)
        {
            System.arraycopy(array, 0, array = new Object[i * 2], 0, i);
        }
        return i;
    }

    //
    // Add an element to the dynamic array and return a reference to
    // that new element.
    //
    void add(Object element)
    {
        int i = nextIndex();
        array[i] = element;
    }

    //
    // Constructor of a Tuple
    //
    ObjectTuple() { this(10); }
    ObjectTuple(int estimate)
    {
        array = new Object[estimate];
    }
};
