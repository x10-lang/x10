/**
 * An implementation of the stack that allows us to set the initial size 
 * of the stack and that holds all the time. This also uses a Rail instead
 * of an ArrayList --- so it should be considerably faster. We only work 
 * with sizes that are a power of two.
 */
public class FixedRailQueue[T] {
  private val internalStorage:Rail[T];
  private var N:Int;
  private var head:Int;
  private var tail:Int;
  private var size:Int;

  /** return the power of 2 that is greater than N by the least */
  private def getNearestPowerOf2 (N:Int) {
    var newN:Int = 0x1;
    while (newN < N) newN = newN << 1;
    return newN;
  }

  /** Construct a fixed size stack */
  public def this(N:Int) { 
    this.N = this.getNearestPowerOf2 (N);
    this.internalStorage = Rail.make[T] (this.N);
    this.head=0;
    this.tail=0;
    this.size=0;
  }

  /** Return the size of the stack */
  public def size() = this.size;

  /** Check if the stack is empty */
  public def isEmpty() = this.size==0;
    
  /** Add the element to the front of the queue. */
  public def push(v:T) {
    // check that we are not going to overflow.
    assert ((this.size+1) < this.N);

    // Add the element and increase the size
    this.internalStorage(this.tail) = v;

    // Increment the size
    ++this.size;
    
    // addition modulo N.
    this.tail = (this.tail+1) & (this.N-1); 
  }
  
  /** Remove and return the top element of the stack. */
  public def pop():T {
    // check that we have something in the stack
    assert ((this.size) > 0); 

    // Remove the first element from the queue.
    val poppedElement = this.internalStorage(this.head);

    // Decrement the size
    --this.size;
    
    // addition modulo N.
    this.head = (this.head+1) & (this.N-1); 

    return poppedElement;
  }
  
  /** Return, but do not remove, the top element of the stack. */
  public def peek() {
    // check that we have something in the stack
    assert ((this.size) > 0); 

    // Return the position at head.
    return this.internalStorage(this.head);
  }

  /** Clear everything, but retain the size  */
  public def clear() { 
    this.size=0;
    this.head=0;
    this.tail=0;
  }

  /** Output the contents of the queue in the order they are stored */
  public def print () {
    Console.OUT.print ("[");
    for (var i:Int=0; i<this.size; ++i) {
      val index:Int = (i+this.head) & (this.N-1);
      Console.OUT.print (((i==0)?"":",") + this.internalStorage(index));
    }
    Console.OUT.println ("]");
  }

  public static def main (args:Array[String](1)) {
    val myQueue = new FixedRailQueue[Int] (4);
    
    myQueue.push (1);
    myQueue.push (2);
    myQueue.push (3);
    myQueue.push (4);
    myQueue.print ();

    myQueue.pop ();
    myQueue.pop ();
    myQueue.print ();

    myQueue.pop ();
    myQueue.pop ();
    myQueue.print ();

    myQueue.push (5);
    myQueue.print ();

    myQueue.push (2);
    myQueue.push (3);
    myQueue.push (4);
    myQueue.print ();
  }
}
