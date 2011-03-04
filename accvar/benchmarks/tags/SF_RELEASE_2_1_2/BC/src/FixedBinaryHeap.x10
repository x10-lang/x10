/**
 * A Binary heap implemenation of PriorityQueue based on Jon Bentely's 
 * description in "Programming Pearls". The speciality of this one is that
 * you need to specify up front how the capacity of the heap. No changing 
 * once initialized.
 *
 * Note that in this implementation, we are leaving Rail position 0 untouched.
 * This is because its easier to then access the left and right child of a 
 * node using the 2*i and 2*i+1 operation.
 *
 */

import x10.util.HashMap;
import x10.util.OptionsParser;
import x10.util.Option;

public final class FixedBinaryHeap {
  /* the internal storage is a Rail */
  private val internalHeap:Rail[Int];
  private val indexMap:Rail[Int];
  private val comparator:(x:Int,y:Int)=>Int;
  private var size:Int;

  // Constructor
  public def this (comparator:(x:Int,y:Int)=>Int, N:Int) {
    this.comparator = comparator;
    this.internalHeap = Rail.make[Int] (N+1);
    this.indexMap = Rail.make[Int] (N);
    this.size = 0;
  } 
    
  // See if the data structure is empty
  public def isEmpty () = this.size==0;
      
  // A simple swap operation. Could use XOR, but dont know if that is 
  // valid on all types.
  private def swapElements (indexOne:Int, indexTwo:Int) {
    assert (indexOne<this.size && indexTwo<this.size);

    // Swap the indices
    val indexOneShadow = this.indexMap(this.internalHeap(indexOne));
    this.indexMap (this.internalHeap(indexOne)) = 
                    this.indexMap(this.internalHeap(indexTwo));
    this.indexMap (this.internalHeap(indexTwo)) = indexOneShadow;

    // Swap the elements
    val shadowElement = this.internalHeap (indexOne);
    this.internalHeap(indexOne) = this.internalHeap(indexTwo);
    this.internalHeap(indexTwo) = shadowElement;
  }
      
  // Add an element, putting it in its right order
  public def push (newElement:Int) : void {
    // check if this is still within our Rail bounds
    // length() returns an index beyond the last point we can write.
    // we are writing to (this.size+1), so when we increment it, it should
    // still be less than the length of the Rail.
    assert ((this.size+1) < this.internalHeap.length());

    // increment the size of the array
    ++this.size;

    // insert the element at the end of the current rail.
    this.indexMap (newElement) = this.size;
    this.internalHeap(this.size) = newElement;

    // sift up to put this in the right place
    for (var i:Int=this.size; 
         i>1 && (1!=comparator(this.internalHeap(i/2),this.internalHeap(i)));
         i=(i/2)) {
      this.swapElements (i, i/2);
    }
  } 

  // Remove the top element from the queue
  public def pop () : Int {
    // check that there is an element to be popped
    assert (this.size > 0);

    // The element to be returned is the first one.
    val element = this.internalHeap(1);

    // There is one less element now. Swap the first and last element,
    // reduce the size and see the magic trickle down.
    this.indexMap(this.internalHeap(this.size)) = 1;
    this.internalHeap(1) = this.internalHeap(this.size);
    --this.size;

    // Sift down operation.
    var c:Int;
    for (var i:Int=1; (c=2*i) <= this.size; i=c) {
      // If I have a right child, then see which one of these is my lesser
      if ((c+1)<=this.size && 
          (1==comparator(this.internalHeap(c+1), this.internalHeap(c)))) {
        // Set the lesser childs index
        ++c;
      }

      // If I am less than my least child, my job is done.
      if (-1!=comparator(this.internalHeap(i), this.internalHeap(c))) break;
      
      // Swap the elements
      this.swapElements (i, c);
    }

    return element;
  } 

  /** 
   * The key value for element has changed --- so update it. We assume that
   * the comparator already has the new value for the distance map. Note that 
   * decreaseKey() is similar to inserting the element again. We always assume
   * that the value will propagate up! If it can go both ways, we need a more 
   * sophisticated algorithm.
   */
  public def decreaseKey (element:Int) {
    val currentPositionInHeap = this.indexMap(element);

    // Now, note that the heap property is true for all elements below 
    // currentPositionInHeap. The only violations might be between element
    // and its ancestors --- this is ONLY because we strictly decrease.
    // sift up to put this in the right place
    for (var i:Int=currentPositionInHeap; 
         i>1 && (1!=comparator(this.internalHeap(i/2),this.internalHeap(i)));
         i=(i/2)) {
      this.swapElements (i, i/2);
    }
  }

  /** Clear everything but retain the space */
  public def clear () = this.size=0;
      
  // Output the contents of the queue in the order they are stored
  public def print () {
    Console.OUT.print ("[");
    for (var i:Int=1; i<=this.size; ++i) {
      Console.OUT.print (((i==1)?"":",") + 
          this.internalHeap(i) + ":" + this.indexMap(this.internalHeap(i)));
    }
    Console.OUT.println ("]");
  }

  public static def main (args:Array[String](1)) {
    val inputValues = new OptionsParser (args, null,
                                       [Option("s", "", "Increasing order")]);
    val isIncreasing = inputValues ("-s", 1);

    val distanceMap = new HashMap[Int, Int] ();

    val makeIncreasingComparator = (distanceMap:HashMap[Int, Int]) => {
      return (x:Int, y:Int) => {
        val dx = distanceMap.get(x).value();
        val dy = distanceMap.get(y).value();
        return (dx==dy) ? 0 : (dx<dy) ? -1 : +1;
      };
    };

    val makeNonIncreasingComparator = (distanceMap:HashMap[Int, Int]) => {
      return (x:Int, y:Int) => {
        val dx = distanceMap.get(x).value();
        val dy = distanceMap.get(y).value();
        return (dx==dy) ? 0 : (dx<dy) ? +1 : -1;
      };
    };

    val comparator = (1==isIncreasing) ? makeIncreasingComparator(distanceMap):
                                      makeNonIncreasingComparator(distanceMap);

    val myQueue = new FixedBinaryHeap[Int] (comparator, 10);
    
    distanceMap.put (0, 0);
    myQueue.push (0);

    distanceMap.put (1, 1);
    myQueue.push (1);

    distanceMap.put (2, 2);
    myQueue.push (2);

    distanceMap.put (3, 3);
    myQueue.push (3);

    distanceMap.put (4, 4);
    myQueue.push (4);

    myQueue.print ();

    myQueue.pop ();
    myQueue.print ();
    myQueue.pop ();
    myQueue.print ();

  }
}
